package steptech.compactquickinventoryaccess;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.api.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.*;

public class ModuleHandler {
    private final List<QuickAccessModule> modules = new ArrayList<>();
    private final Map<Player, ModuleInstructionWrapper> trackedModuleInstructions = new HashMap<>();
    private final Map<Player, ModuleInstructionWrapper> pendingModuleInstructions = new HashMap<>();
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

    /*TODO
    *  onActivation add as pending
    *  onOpen check for pending ones and else create one for the current inventory view, track current
    *  onTempClose call temp close
    *  onFinalClose call temp close, track and open previous, call final mod
    *  onLogout final Close all tracked*/


    private @Nullable ModuleInstructionWrapper determineCurrentInventoryView(@NotNull Player player) {
        final ModuleInstructionWrapper moduleInstructionWrapper = this.trackedModuleInstructions.get(player);
        if (moduleInstructionWrapper != null) return moduleInstructionWrapper;
        final Inventory topInventory = player.getOpenInventory().getTopInventory();
        if (!isForbiddenInventory(topInventory))
            return new ModuleInstructionWrapper(() -> player.openInventory(topInventory),
                    closedView -> {
                    },
                    currentView -> {
                    });
        return null;
    }

    public boolean activateModule(@NotNull QuickAccessModule module,
                                  @NotNull ItemStack activationItem,
                                  @NotNull Player player,
                                  int clickedRawSlot) {
        final boolean checkModuleClick = checkModuleClick(module, activationItem, player);
        if (checkModuleClick) {
            //react a tick later
            Bukkit.getScheduler().runTaskLater(this.compactQuickInventoryAccess, () -> {
                //call modification
                final ModuleInstructionWrapper moduleInstructionWrapper = module.modifyInventory(player, clickedRawSlot);

                //set current as previous
                moduleInstructionWrapper.setPrevious(determineCurrentInventoryView(player));

                //put on pending
                this.pendingModuleInstructions.put(player, moduleInstructionWrapper);

                //open inventory
                moduleInstructionWrapper.getInventoryOpener().openInventory();

            }, 1);
        }
        return checkModuleClick;
    }

    public void trackTempInventoryClose(@NotNull InventoryView tempClosedView) {
        final Player player = (Player) tempClosedView.getPlayer();
        final ModuleInstructionWrapper moduleInstructionWrapper = this.trackedModuleInstructions.remove(player);
        if (moduleInstructionWrapper != null) {
            moduleInstructionWrapper.getTempInventoryCloser().onTempInventoryClose(tempClosedView);
        }
    }

    public void trackInventoryOpen(@NotNull Player player) {
        final ModuleInstructionWrapper moduleInstructionWrapper = this.pendingModuleInstructions.remove(player);
        if (moduleInstructionWrapper != null) {
            //track
            this.trackedModuleInstructions.put(player, moduleInstructionWrapper);
        }
    }

    private @Nullable InventoryView finalCloseInventory(@NotNull InventoryView closedInventoryView) {
        final Player player = (Player) closedInventoryView.getPlayer();
        final ModuleInstructionWrapper tracked = this.trackedModuleInstructions.remove(player);
        InventoryView currentView = null;
        if (tracked != null) {
            final ModuleInstructionWrapper previous = tracked.getPrevious(); //get previous

            if (previous != null) {
                //track previous
                this.trackedModuleInstructions.put(player, previous);

                //open inventory
                currentView = previous.getInventoryOpener().openInventory();
            }
            if (currentView == null) {
                currentView = player.getOpenInventory();
            }

            //call final modification
            tracked.getFinalInventoryCloser().applyFinalModification(currentView);
        }
        return currentView;
    }

    public void trackFinalInventoryClose(@NotNull InventoryView closedInventoryView) {
        trackTempInventoryClose(closedInventoryView);

        //react with delay
        Bukkit.getScheduler().runTaskLater(this.compactQuickInventoryAccess, () -> {
            final Player player = (Player) closedInventoryView.getPlayer();
            final ModuleInstructionWrapper tracked = this.trackedModuleInstructions.remove(player);
            if (tracked != null) {
                final ModuleInstructionWrapper previous = tracked.getPrevious(); //get previous

                InventoryView currentView = null;
                if (previous != null) {
                    //track previous
                    this.trackedModuleInstructions.put(player, previous);

                    //open inventory
                    currentView = previous.getInventoryOpener().openInventory();
                }
                if (currentView == null) {
                    currentView = player.getOpenInventory();
                }

                //call final modification
                tracked.getFinalInventoryCloser().applyFinalModification(currentView);
            }
        }, 1);
    }

    public boolean trackClick(@NotNull ItemStack clickedItem, @NotNull Player player, int rawSlot) {
        for (QuickAccessModule module : getModules()) {
            if (activateModule(module, clickedItem, player, rawSlot)) return true;
        }
        return false;
    }

    public boolean trackModule(@NotNull QuickAccessModule module, @NotNull Player player) {
        final List<ItemStack> inventoryContents = QuickAccessModule.getInventoryContents(player);
        for (int rawSlot = 0; rawSlot < inventoryContents.size(); rawSlot++) {
            final ItemStack itemStack = inventoryContents.get(rawSlot);
            if (itemStack != null && activateModule(module, itemStack, player, rawSlot)) return true;
        }
        return false;
    }

    public void trackLogout(@NotNull Player player) {
        //close all track stages
        InventoryView closedInventoryView = player.getOpenInventory();
        do {
            closedInventoryView = Objects.requireNonNull(finalCloseInventory(closedInventoryView));
        } while (this.trackedModuleInstructions.get(player) != null);
    }
}
