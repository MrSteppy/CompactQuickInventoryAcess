package steptech.compactquickinventoryaccess.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.FactoryHandler;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

public abstract class ModuleInstructionFactoryModule {
    public ModuleInstructionFactoryModule(@NotNull FactoryHandler factoryHandler) {
        factoryHandler.registerFactoryModule(this);
    }

    public abstract @Nullable ModuleInstructionWrapper createWrapper(@NotNull Player player, @Nullable Block lastInteractedBlock);
}
