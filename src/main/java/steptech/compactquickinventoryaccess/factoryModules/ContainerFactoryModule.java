package steptech.compactquickinventoryaccess.factoryModules;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.FactoryHandler;
import steptech.compactquickinventoryaccess.api.moduleInstructionFactoryModule.AbstractModuleInstructionFactoryModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

public class ContainerFactoryModule extends AbstractModuleInstructionFactoryModule {
    public ContainerFactoryModule(@NotNull FactoryHandler factoryHandler) {
        super(factoryHandler);
    }

    @Override
    public @Nullable ModuleInstructionWrapper createWrapper(@NotNull Player player, @Nullable Block lastInteractedBlock) {
        if (lastInteractedBlock != null) {
            final BlockState blockState = lastInteractedBlock.getState();
            if (blockState instanceof Container) {
                final Container container = (Container) blockState;

                return new ModuleInstructionWrapper(() -> player.openInventory(container.getInventory()),
                        closedView -> {},
                        () -> {});
            }
        }
        return null;
    }
}
