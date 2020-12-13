package steptech.compactquickinventoryaccess.factoryModules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.FactoryHandler;
import steptech.compactquickinventoryaccess.api.functions.OpenInventoryMethod;
import steptech.compactquickinventoryaccess.api.moduleInstructionFactoryModule.AbstractModuleInstructionFactoryModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.function.Function;

public class OIMFactoryModule extends AbstractModuleInstructionFactoryModule {
    protected final InventoryType inventoryType;
    protected final Function<Player, OpenInventoryMethod> openInventory;

    public OIMFactoryModule(@NotNull FactoryHandler factoryHandler,
                            @NotNull InventoryType inventoryType,
                            @NotNull Function<Player, @NotNull OpenInventoryMethod> openInventory) {
        super(factoryHandler);
        this.inventoryType = inventoryType;
        this.openInventory = openInventory;
    }

    @Override
    public @Nullable ModuleInstructionWrapper createWrapper(@NotNull Player player, @Nullable Block lastInteractedBlock) {
        if (lastInteractedBlock != null && player.getOpenInventory().getType() == this.inventoryType) {
            final Location location = lastInteractedBlock.getLocation();
            return new ModuleInstructionWrapper(() -> this.openInventory.apply(player).open(location, false),
                    closedView -> {},
                    () -> {});
        }
        return null;
    }
}
