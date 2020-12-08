package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.Objects;

public class ShulkerBoxModule implements QuickAccessModule {
    private static final String PERMISSION = CompactQuickInventoryAccess.PERMISSION_NODE + ".shulkerbox";

    public ShulkerBoxModule(@NotNull ModuleHandler moduleHandler) {
        moduleHandler.registerModule(this);
    }

    @Override
    public boolean matchesItem(@NotNull ItemStack clickedItem) {
        return clickedItem.getType().name().endsWith("SHULKER_BOX");
    }

    @Override
    public boolean matchesOtherRequirements(@NotNull Player player) {
        final boolean permission = player.hasPermission(PERMISSION);
        if (!permission) {
            player.sendActionBar("Missing permission " + PERMISSION);
        }
        return permission;
    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        final InventoryView openInventory = player.getOpenInventory();
        //TODO always remember to update shulker inv
        final ItemStack shulkerBoxItem = openInventory.getItem(rawSlot);
        openInventory.setItem(rawSlot, null);

        //get shulker box
        final ItemMeta itemMeta = shulkerBoxItem.getItemMeta();
        if (!(itemMeta instanceof BlockStateMeta)) throw new RuntimeException("ShulkerBox is not BlockStateMeta!");
        final BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
        final BlockState blockState = blockStateMeta.getBlockState();
        if (!(blockState instanceof ShulkerBox)) throw new RuntimeException("ShulkerBox is no ShulkerBox!");
        final ShulkerBox shulkerBox = (ShulkerBox) blockState;

        return new ModuleInstructionWrapper(() -> {
            final Inventory inventory = itemMeta.hasDisplayName() ?
                    Bukkit.createInventory(player, InventoryType.SHULKER_BOX, itemMeta.getDisplayName())
                    : Bukkit.createInventory(player, InventoryType.SHULKER_BOX);
            inventory.setContents(shulkerBox.getInventory().getContents());

            return Objects.requireNonNull(player.openInventory(inventory));
        },
                (inventoryView, closedInventoryView) -> {
            //update the inventory
            shulkerBox.getInventory().setContents(closedInventoryView.getTopInventory().getContents());
            blockStateMeta.setBlockState(shulkerBox);
            shulkerBoxItem.setItemMeta(blockStateMeta);

            final ItemStack slotItem = inventoryView.getItem(rawSlot);
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                inventoryView.setItem(rawSlot, shulkerBoxItem);
            } else {
                final Location location = player.getLocation();
                location.getWorld().dropItem(location, shulkerBoxItem);
            }
        });
    }
}
