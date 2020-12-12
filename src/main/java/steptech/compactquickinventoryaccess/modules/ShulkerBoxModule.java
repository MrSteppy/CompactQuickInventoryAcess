package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.AbstractQuickAccessModule;
import steptech.compactquickinventoryaccess.api.TempItemRemover;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

public class ShulkerBoxModule extends AbstractQuickAccessModule {
    public ShulkerBoxModule(@NotNull ModuleHandler moduleHandler) {
        super(moduleHandler, "shulkerbox");
    }

    @Override
    public boolean matchesItem(@NotNull ItemStack clickedItem) {
        return clickedItem.getType().name().endsWith("SHULKER_BOX");
    }

    @Override
    public boolean matchesOtherRequirements(@NotNull Player player) {
        if (!hasPermission(player)) {
            player.sendActionBar("Missing permission " + getPermission());
            return false;
        }
        return true;
    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        //remove shulker box from inventory
        final TempItemRemover tempItemRemover = new TempItemRemover(player::getOpenInventory, rawSlot);
        final ItemStack shulkerBoxItem = tempItemRemover.getRemovedItem();
        assert shulkerBoxItem != null;

        //create shulkerbox from item
        final ItemMeta itemMeta = shulkerBoxItem.getItemMeta();
        final BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
        final BlockState blockState = blockStateMeta.getBlockState();
        final ShulkerBox shulkerBox = (ShulkerBox) blockState;

        return new ModuleInstructionWrapper(() -> {
            final Inventory inventory = itemMeta.hasDisplayName() ?
                    Bukkit.createInventory(player, InventoryType.SHULKER_BOX, itemMeta.getDisplayName())
                    : Bukkit.createInventory(player, InventoryType.SHULKER_BOX);
            inventory.setContents(shulkerBox.getInventory().getContents());

            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);
            return player.openInventory(inventory);
        }, closedView -> {
            shulkerBox.getInventory().setContents(closedView.getTopInventory().getContents()); //update contents
            //set block state and item meta
            blockStateMeta.setBlockState(shulkerBox);
            shulkerBoxItem.setItemMeta(blockStateMeta);
        }, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1, 1);
            tempItemRemover.setRemovedItem(shulkerBoxItem);
            tempItemRemover.putItemBack();
        });
    }
}
