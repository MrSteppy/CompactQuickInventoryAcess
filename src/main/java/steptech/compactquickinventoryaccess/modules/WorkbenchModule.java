package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.TempItemRemover;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

public class WorkbenchModule implements QuickAccessModule {
    private static final String PERMISSION = CompactQuickInventoryAccess.PERMISSION_NODE + ".workbench";

    public WorkbenchModule(@NotNull ModuleHandler moduleHandler) {
        moduleHandler.registerModule(this);
    }

    @Override
    public boolean matchesItem(@NotNull ItemStack clickedItem) {
        return clickedItem.getType() == Material.CRAFTING_TABLE;
    }

    @Override
    public boolean matchesOtherRequirements(@NotNull Player player) {
        if (!player.hasPermission(PERMISSION)) {
            player.sendActionBar("Missing permission " + PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        final TempItemRemover tempItemRemover = new TempItemRemover(player::getOpenInventory, rawSlot);
        final ItemStack workbench = tempItemRemover.getRemovedItem();
        assert workbench != null;

        return new ModuleInstructionWrapper(() -> {
            player.playSound(player.getLocation(), Sound.BLOCK_WOOD_PLACE, 1, 1);
            return player.openWorkbench(null, true);
        }, closedView -> {

        }, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 1, 1);
            tempItemRemover.putItemBack();
        });
    }
}
