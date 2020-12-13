package steptech.compactquickinventoryaccess;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.api.quickAccessModule.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.*;

public class ModuleHandler {
    private final List<QuickAccessModule> modules = new ArrayList<>();
    private final Map<Player, ModuleInstructionWrapper> trackedModuleInstructions = new HashMap<>();
    private final Map<Player, ModuleInstructionWrapper> pendingModuleInstructions = new HashMap<>();
    private final CompactQuickInventoryAccess compactQuickInventoryAccess;
    private final FactoryHandler factoryHandler;

    ModuleHandler(@NotNull CompactQuickInventoryAccess compactQuickInventoryAccess) {
        this.compactQuickInventoryAccess = compactQuickInventoryAccess;
        this.factoryHandler = compactQuickInventoryAccess.getFactoryHandler();
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

    public boolean checkSlotType(@NotNull InventoryType.SlotType slotType) {
        final List<InventoryType.SlotType> validSlotTypes = Arrays.asList(InventoryType.SlotType.CONTAINER,
                InventoryType.SlotType.QUICKBAR);
        return validSlotTypes.contains(slotType);
    }

    public boolean checkModuleClick(@NotNull QuickAccessModule module,
                                    @NotNull ItemStack itemStack,
                                    @NotNull Player player,
                                    @NotNull InventoryType.SlotType clickedSlotType) {
        return checkSlotType(clickedSlotType) && module.matchesItem(itemStack) && module.matchesOtherRequirements(player);
    }

    private @Nullable ModuleInstructionWrapper determineCurrentInventoryView(@NotNull Player player) {
        final ModuleInstructionWrapper moduleInstructionWrapper = this.trackedModuleInstructions.get(player);
        return moduleInstructionWrapper != null ? moduleInstructionWrapper : this.factoryHandler.createWrapper(player);
    }

    @SuppressWarnings("UnusedReturnValue")
    private @Nullable InventoryView openInventory(@NotNull ModuleInstructionWrapper moduleInstructionWrapper) {
        try {
            return moduleInstructionWrapper.getInventoryOpener().openInventory();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean activateModule(@NotNull QuickAccessModule module,
                                  @NotNull ItemStack activationItem,
                                  @NotNull Player player,
                                  int clickedRawSlot,
                                  @NotNull InventoryType.SlotType clickedSlotType) {
        final boolean checkModuleClick = checkModuleClick(module, activationItem, player, clickedSlotType);
        if (checkModuleClick) {
            //react a tick later
            Bukkit.getScheduler().runTaskLater(this.compactQuickInventoryAccess, () -> {
                //call modification
                final ModuleInstructionWrapper moduleInstructionWrapper = module.modifyInventory(player, clickedRawSlot);

                //set current as previous
                moduleInstructionWrapper.setPrevious(determineCurrentInventoryView(player));

                //set pending
                this.pendingModuleInstructions.put(player, moduleInstructionWrapper);

                //open inventory
                openInventory(moduleInstructionWrapper);

            }, 1);
        }
        return checkModuleClick;
    }

    public void trackTempInventoryClose(@NotNull InventoryView tempClosedView) {
        final Player player = (Player) tempClosedView.getPlayer();
        final ModuleInstructionWrapper moduleInstructionWrapper = this.trackedModuleInstructions.get(player);
        if (moduleInstructionWrapper != null)
            moduleInstructionWrapper.getTempInventoryCloser().onTempInventoryClose(tempClosedView);
    }

    public void trackInventoryOpen(@NotNull InventoryView openingView) {
        final Player player = (Player) openingView.getPlayer();
        final ModuleInstructionWrapper pending = this.pendingModuleInstructions.remove(player);
        final ModuleInstructionWrapper current = this.trackedModuleInstructions.get(player);
        if (pending != null) {
            this.trackedModuleInstructions.put(player, pending); //track
        } else if (current != null) {
            //when an inventory is opened, which is not from a module

            final ModuleInstructionWrapper moduleInstructionWrapper = new ModuleInstructionWrapper(() -> openingView,
                    closedView -> {},
                    () -> {});
            moduleInstructionWrapper.setPrevious(current);

            this.trackedModuleInstructions.put(player, moduleInstructionWrapper); //track
        }
    }

    private void finalCloseInventory(@NotNull Player player) {
        final ModuleInstructionWrapper tracked = this.trackedModuleInstructions.remove(player);
        if (tracked != null) {
            final ModuleInstructionWrapper previous = tracked.getPrevious(); //get previous

            if (previous != null) {
                //open inventory
                openInventory(previous);

                //track
                this.trackedModuleInstructions.put(player, previous);
            }

            //call final modification
            tracked.getFinalInventoryCloser().applyFinalModification();
            player.updateInventory();
        }
    }

    public void trackFinalInventoryClose(@NotNull InventoryView closedInventoryView) {
        trackTempInventoryClose(closedInventoryView);

        //react with delay
        Bukkit.getScheduler().runTaskLater(this.compactQuickInventoryAccess,
                () -> finalCloseInventory((Player) closedInventoryView.getPlayer()),
                1);
    }

    public boolean trackClick(@NotNull ItemStack clickedItem,
                              @NotNull Player player,
                              int rawSlot,
                              @NotNull InventoryType.SlotType clickedSlotType) {
        for (QuickAccessModule module : getModules()) {
            if (activateModule(module, clickedItem, player, rawSlot, clickedSlotType)) return true;
        }
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean trackModule(@NotNull QuickAccessModule module, @NotNull Player player) {
        final List<ItemStack> inventoryContents = QuickAccessModule.getInventoryContents(player);
        final InventoryView inventoryView = player.getOpenInventory();
        for (int rawSlot = 0; rawSlot < inventoryContents.size(); rawSlot++) {
            final ItemStack itemStack = inventoryContents.get(rawSlot);
            if (itemStack != null && activateModule(module, itemStack, player, rawSlot, inventoryView.getSlotType(rawSlot)))
                return true;
        }
        return false;
    }

    public void trackLogout(@NotNull Player player) {
        //close all track stages
        do {
            finalCloseInventory(player);
        } while (this.trackedModuleInstructions.get(player) != null);
    }
}
