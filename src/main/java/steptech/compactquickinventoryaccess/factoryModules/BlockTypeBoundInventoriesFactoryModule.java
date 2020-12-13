package steptech.compactquickinventoryaccess.factoryModules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.FactoryHandler;
import steptech.compactquickinventoryaccess.api.AbstractModuleInstructionFactoryModule;
import steptech.compactquickinventoryaccess.api.functions.OpenInventoryMethod;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class BlockTypeBoundInventoriesFactoryModule extends AbstractModuleInstructionFactoryModule {
    @SuppressWarnings("UnusedReturnValue")
    public static @NotNull List<BlockTypeBoundInventoriesFactoryModule> createFactoryModules(
            @NotNull FactoryHandler factoryHandler,
            @NotNull Consumer<@NotNull Map<@NotNull InventoryType, @NotNull Function<@NotNull Player, @NotNull OpenInventoryMethod>>> factoryModulesToCreate) {
        final Map<InventoryType, Function<Player, OpenInventoryMethod>> map = new HashMap<>();
        factoryModulesToCreate.accept(map);
        final List<BlockTypeBoundInventoriesFactoryModule> factoryModules = new ArrayList<>();
        map.forEach((type, playerOpenInventoryMethodFunction) -> factoryModules.add(
                new BlockTypeBoundInventoriesFactoryModule(factoryHandler, type, playerOpenInventoryMethodFunction)
        ));
        return factoryModules;
    }

    protected final InventoryType inventoryType;
    protected final Function<Player, OpenInventoryMethod> openInventory;

    public BlockTypeBoundInventoriesFactoryModule(@NotNull FactoryHandler factoryHandler,
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
