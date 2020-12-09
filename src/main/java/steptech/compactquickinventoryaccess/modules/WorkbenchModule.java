package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.Objects;

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
        final boolean permission = player.hasPermission(PERMISSION);
        if (!permission) {
            player.sendActionBar("Missing permission " + PERMISSION);
        }
        return permission;
    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        final ItemStack workbench = Objects.requireNonNull(QuickAccessModule.takeOneFromItemStack(player.getOpenInventory(), rawSlot));

        return new ModuleInstructionWrapper(() -> player.openWorkbench(null, true), closedView -> {}, () -> {
            final InventoryView currentView = player.getOpenInventory();
            final ItemStack slotItem = currentView.getItem(rawSlot);
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                currentView.setItem(rawSlot, workbench);
            } else {
                final Location location = player.getLocation();
                location.getWorld().dropItem(location, workbench);
            }
        });
    }
}
