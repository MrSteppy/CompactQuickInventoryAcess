package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.functions.OpenInventoryMethod;
import steptech.compactquickinventoryaccess.api.quickAccessModule.QuickAccessModule;

import java.util.function.Function;

public class PickaxeOpenInventoryMethodModule extends NoToolOpenInventoryMethodModule {
    public PickaxeOpenInventoryMethodModule(@NotNull ModuleHandler moduleHandler,
                                            @NotNull Material matchingItemMaterial,
                                            @NotNull Function<@NotNull Player, @NotNull OpenInventoryMethod> openInventory) {
        super(moduleHandler, matchingItemMaterial, openInventory);
    }

    public boolean isPickaxe(@NotNull ItemStack itemStack) {
        return itemStack.getType().name().endsWith("PICKAXE");
    }

    public int findPickaxeSlot(@NotNull InventoryView inventoryView) {
        for (int rawSlot = 0, bound = inventoryView.countSlots(); rawSlot < bound; rawSlot++) {
            final ItemStack itemStack = inventoryView.getItem(rawSlot);
            if (itemStack != null && isPickaxe(itemStack)) return rawSlot;
        }
        return -1;
    }

    @Override
    protected boolean checkOtherRequirementsBesidesPermission(@NotNull Player player) {
        //check for pickaxe
        if (!(findPickaxeSlot(player.getOpenInventory()) > -1)) {
            player.sendActionBar("Pickaxe needed!");
            return false;
        }
        return super.checkOtherRequirementsBesidesPermission(player);
    }

    @Override
    protected void applyOtherModifications(@NotNull Player player, int rawSlot) {
        //remove one health from pickaxe
        final InventoryView modificationView = player.getOpenInventory();
        QuickAccessModule.damageItem(modificationView, findPickaxeSlot(modificationView), 1);

        super.applyOtherModifications(player, rawSlot);
    }
}
