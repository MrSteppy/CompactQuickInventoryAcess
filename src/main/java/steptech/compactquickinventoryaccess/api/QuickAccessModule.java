package steptech.compactquickinventoryaccess.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface QuickAccessModule {
    static @Nullable ItemStack takeOneFromItemStack(@NotNull InventoryView inventoryView, int rawSlot) {
        final ItemStack itemStack = inventoryView.getItem(rawSlot);
        if (itemStack != null) {
            final int amount = itemStack.getAmount();
            if (amount > 1) {
                itemStack.setAmount(amount - 1);
                final ItemStack copy = new ItemStack(itemStack);
                copy.setAmount(1);
                return copy;
            } else {
                inventoryView.setItem(rawSlot, null);
                return itemStack;
            }
        }
        return null;
    }

    static @NotNull List<ItemStack> getInventoryContents(@NotNull Player player) {
        final List<ItemStack> contents = new ArrayList<>();
        final InventoryView inventoryView = player.getOpenInventory();
        for (int rawSlot = 0, bound = inventoryView.countSlots(); rawSlot < bound; rawSlot++) {
            contents.add(inventoryView.getItem(rawSlot));
        }
        return contents;
    }

    boolean matchesItem(@NotNull ItemStack clickedItem);
    boolean matchesOtherRequirements(@NotNull Player player);
    @NotNull ModificationResultWrapper modifyInventory(@NotNull Player player, int rawSlot);
}
