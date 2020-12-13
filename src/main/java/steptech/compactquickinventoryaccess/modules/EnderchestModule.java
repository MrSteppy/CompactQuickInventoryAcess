package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.TempItemRemover;
import steptech.compactquickinventoryaccess.api.commandModule.AbstractCommandModule;
import steptech.compactquickinventoryaccess.api.quickAccessModule.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

public class EnderchestModule extends AbstractCommandModule {
    public EnderchestModule(@NotNull ModuleHandler moduleHandler) {
        super(moduleHandler,
                "enderchest",
                player -> player.openInventory(player.getEnderChest()),
                "You need at least one enderchest!",
                "ec");
    }

    @Override
    public boolean matchesItem(@NotNull ItemStack clickedItem) {
        return clickedItem.getType() == Material.ENDER_CHEST;
    }

    private boolean matchesPickaxeRequirements(@NotNull ItemStack itemStack) {
        //check material
        final Material material = itemStack.getType();
        final boolean checkMaterial = material == Material.DIAMOND_PICKAXE || material == Material.NETHERITE_PICKAXE;

        //check enchantments
        final boolean checkEnchantment = itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);

        return checkMaterial && checkEnchantment;
    }

    private int findMatchingPickaxeSlot(@NotNull InventoryView inventoryView) {
        for (int rawSlot = 0, bound = inventoryView.countSlots(); rawSlot < bound; rawSlot++) {
            final ItemStack itemStack = inventoryView.getItem(rawSlot);
            if (itemStack != null && matchesPickaxeRequirements(itemStack)) return rawSlot;
        }
        return -1;
    }

    @Override
    public boolean matchesOtherRequirements(@NotNull Player player) {
        //check permission
        if (!hasPermission(player)) {
            player.sendActionBar("Missing permission " + getPermission());
            return false;
        }
        //check for pickaxe
        if (!(findMatchingPickaxeSlot(player.getOpenInventory()) > -1)) {
            player.sendActionBar("Diamond or Netherite Pickaxe with SilkTouch needed!");
            return false;
        }

        return true;
    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        final InventoryView modificationView = player.getOpenInventory();

        //remove one health from pickaxe
        QuickAccessModule.damageItem(modificationView, findMatchingPickaxeSlot(modificationView), 1);

        //remove enderchest
        final TempItemRemover tempItemRemover = new TempItemRemover(player::getOpenInventory, rawSlot);
        final ItemStack enderchest = tempItemRemover.getRemovedItem();
        assert enderchest != null;

        return new ModuleInstructionWrapper(() -> {
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
            return player.openInventory(player.getEnderChest());
        }, closedView -> {}, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1, 1);
            tempItemRemover.putItemBack();
        });
    }
}
