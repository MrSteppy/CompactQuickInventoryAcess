package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

public class EnderchestModule implements QuickAccessModule {
    private static final String PERMISSION = CompactQuickInventoryAccess.PERMISSION_NODE + ".enderchest";

    public EnderchestModule(@NotNull ModuleHandler moduleHandler) {
        moduleHandler.registerModule(this);
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
        final boolean checkEnchantment = itemStack.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);

        return checkMaterial && checkEnchantment;
    }

    private @Nullable ItemStack findMatchingPickaxe(@NotNull Player player) {
        return QuickAccessModule.getInventoryContents(player).stream()
                .filter(itemStack -> itemStack != null && matchesPickaxeRequirements(itemStack))
                .findFirst()
                .orElse(null);
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
        final boolean permission = player.hasPermission(PERMISSION);
        if (!permission) {
            player.sendActionBar("Missing permission " + PERMISSION);
        }
        //check for pickaxe
        final boolean pickaxe = findMatchingPickaxeSlot(player.getOpenInventory()) > -1;
        if (!pickaxe) {
            player.sendActionBar("Diamond or Netherrite Pickaxe with SilkTouch needed!");
        }

        return permission && pickaxe;
    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        final InventoryView modificationView = player.getOpenInventory();

        //remove one health from pickaxe
        QuickAccessModule.damageItem(modificationView, findMatchingPickaxeSlot(modificationView), 1);

        //remove enderchest
        final ItemStack enderchest = QuickAccessModule.takeOneFromItemStack(modificationView, rawSlot);
        assert enderchest != null;

        return new ModuleInstructionWrapper(() -> player.openInventory(player.getEnderChest()),
                closedView -> {},
                () -> QuickAccessModule.putItemBack(player.getOpenInventory(), enderchest, rawSlot));
    }
}
