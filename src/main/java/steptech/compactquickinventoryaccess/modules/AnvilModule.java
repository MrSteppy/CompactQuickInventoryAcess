package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.Arrays;
import java.util.List;

public class AnvilModule implements QuickAccessModule {
    private static final String PERMISSION = CompactQuickInventoryAccess.PERMISSION_NODE + ".anvil";

    public AnvilModule(@NotNull ModuleHandler moduleHandler) {
        moduleHandler.registerModule(this);
    }

    @Override
    public boolean matchesItem(@NotNull ItemStack clickedItem) {
        final List<Material> anvilMaterials = Arrays.asList(Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL);
        return anvilMaterials.contains(clickedItem.getType());
    }

    private boolean isPickaxe(@NotNull ItemStack itemStack) {
        return itemStack.getType().name().endsWith("PICKAXE");
    }

    private int findPickaxeSlot(@NotNull InventoryView inventoryView) {
        for (int rawSlot = 0, bound = inventoryView.countSlots(); rawSlot < bound; rawSlot++) {
            final ItemStack itemStack = inventoryView.getItem(rawSlot);
            if (itemStack != null && isPickaxe(itemStack)) return rawSlot;
        }
        return -1;
    }

    @Override
    public boolean matchesOtherRequirements(@NotNull Player player) {
        //check permission
        if (!player.hasPermission(PERMISSION)) {
            player.sendActionBar("Missing permission " + PERMISSION);
            return false;
        }
        //check for pickaxe
        if (!(findPickaxeSlot(player.getOpenInventory()) > -1)) {
            player.sendActionBar("Pickaxe needed!");
            return false;
        }

        return true;
    }

    //TODO somehow make sure, the anvil gets damaged -> An anvil has on use a 12% chance that it gets damaged -> Register a Listener for anvil use, maybe check out the anvil damage event first

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        final InventoryView modificationView = player.getOpenInventory();

        //remove one health from pickaxe
        QuickAccessModule.damageItem(modificationView, findPickaxeSlot(modificationView), 1);

        //remove anvil
        final ItemStack anvil = QuickAccessModule.takeOneFromItemStack(modificationView, rawSlot);
        assert anvil != null;

        return new ModuleInstructionWrapper(() -> {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
            return player.openAnvil(null, true);
        }, closedView -> {}, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
            QuickAccessModule.putItemBack(player.getOpenInventory(), anvil, rawSlot);
        });
    }
}
