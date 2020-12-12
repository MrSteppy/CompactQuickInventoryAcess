package steptech.compactquickinventoryaccess.factoryModules;

import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.FactoryHandler;
import steptech.compactquickinventoryaccess.api.AbstractModuleInstructionFactoryModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

/**
 * Double chests can't be closed currently, so this module only returns a wrapper, which opens none
 */
public class DoubleChestFactoryModule extends AbstractModuleInstructionFactoryModule {
    public DoubleChestFactoryModule(@NotNull FactoryHandler factoryHandler) {
        super(factoryHandler);
    }

    @Override
    public @Nullable ModuleInstructionWrapper createWrapper(@NotNull Player player, @Nullable Block lastInteractedBlock) {
        //check inventory
        return player.getOpenInventory().getTopInventory().getHolder() instanceof DoubleChest ?
                new ModuleInstructionWrapper(() -> null, closedView -> {}, () -> {}) :
                null;
    }
}
