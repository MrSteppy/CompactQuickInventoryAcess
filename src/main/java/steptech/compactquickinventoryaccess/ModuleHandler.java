package steptech.compactquickinventoryaccess;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.api.ModificationResultWrapper;
import steptech.compactquickinventoryaccess.api.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.TrackStage;

import java.util.*;

public class ModuleHandler {
    private final List<QuickAccessModule> modules = new ArrayList<>();
    private final Map<Player, TrackStage> trackedStages = new HashMap<>();
    private final CompactQuickInventoryAccess compactQuickInventoryAccess;

    ModuleHandler(@NotNull CompactQuickInventoryAccess compactQuickInventoryAccess) {
        this.compactQuickInventoryAccess = compactQuickInventoryAccess;
    }

    public void registerModule(@NotNull QuickAccessModule module) {
        this.modules.add(0, module); //insert front, for priority
    }

    public void unregisterModule(@NotNull QuickAccessModule module) {
        this.modules.remove(module);
    }

    public @NotNull List<QuickAccessModule> getModules() {
        return Collections.unmodifiableList(this.modules);
    }

    public boolean checkModuleClick(@NotNull QuickAccessModule module, @NotNull ItemStack itemStack, @NotNull Player player) {
        return module.matchesItem(itemStack) && module.matchesOtherRequirements(player);
    }

    private boolean isForbiddenInventory(@NotNull Inventory inventory) {
        return inventory.getType() == InventoryType.CRAFTING; //you can't open crafting inventories
    }

    private @Nullable TrackStage getPreviousStage(@NotNull Player player) {
        TrackStage previous = this.trackedStages.get(player);
        if (previous == null) {
            //create one for current inventory
            final Inventory topInventory = player.getOpenInventory().getTopInventory();
            if (!isForbiddenInventory(topInventory)) {
                previous = new TrackStage(() -> player.openInventory(topInventory), inventoryView -> {}, null);
            }
        }
        return previous;
    }

    private @NotNull InventoryView openInventory(@NotNull TrackStage trackStage) {
        return trackStage.openInventory.get();
    }

    public boolean trackModuleClick(@NotNull QuickAccessModule module,
                                    @NotNull ItemStack itemStack,
                                    @NotNull Player player,
                                    int rawSlot) {
        final boolean checkModuleClick = checkModuleClick(module, itemStack, player);
        if (checkModuleClick) {
            //react a tick later
            Bukkit.getScheduler().runTaskLater(this.compactQuickInventoryAccess, () -> {
                //call modification
                final ModificationResultWrapper modificationResultWrapper = module.modifyInventory(player, rawSlot);

                //track stage
                final TrackStage trackStage = new TrackStage(modificationResultWrapper.openInventory,
                        modificationResultWrapper.onClose,
                        getPreviousStage(player));
                this.trackedStages.put(player, trackStage);

                //open new inventory
                openInventory(trackStage);

            }, 1);
        }
        return checkModuleClick;
    }

    public boolean trackClick(@NotNull ItemStack clickedItem, @NotNull Player player, int rawSlot) {
        for (QuickAccessModule module : getModules()) {
            if (trackModuleClick(module, clickedItem, player, rawSlot)) return true;
        }
        return false;
    }

    public boolean trackModule(@NotNull QuickAccessModule module, @NotNull Player player) {
        final List<ItemStack> inventoryContents = QuickAccessModule.getInventoryContents(player);
        for (int rawSlot = 0; rawSlot < inventoryContents.size(); rawSlot++) {
            final ItemStack itemStack = inventoryContents.get(rawSlot);
            if (itemStack != null && trackModuleClick(module, itemStack, player, rawSlot)) return true;
        }
        return false;
    }

    private void closeTrackStage(@NotNull TrackStage trackStage, @NotNull Player player) {
        this.trackedStages.put(player, trackStage.previous); //re track old stage
        final InventoryView inventoryView;
        if (trackStage.previous != null) {
            inventoryView = openInventory(trackStage.previous);
        } else {
            inventoryView = player.getOpenInventory();
        }
        trackStage.onClose.accept(inventoryView);
        player.updateInventory(); //make sure all changes are applied!
    }

    public void trackInventoryClose(@NotNull Player player) {
        Bukkit.getScheduler().runTaskLater(this.compactQuickInventoryAccess, () -> {
            final TrackStage trackStage = this.trackedStages.remove(player);
            if (trackStage != null) {
                closeTrackStage(trackStage, player);
            }
        }, 1);
    }

    public void trackLogout(@NotNull Player player) {
        //close all track stages
        do {
            final TrackStage trackStage = this.trackedStages.get(player);
            if (trackStage != null) {
                closeTrackStage(trackStage, player);
                continue;
            }
            break;
        } while (true);
    }
}
