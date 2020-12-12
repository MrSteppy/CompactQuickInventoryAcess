package steptech.compactquickinventoryaccess.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

public interface ModuleInstructionFactoryModule {
    @Nullable ModuleInstructionWrapper createWrapper(@NotNull Player player, @Nullable Block lastInteractedBlock);
}
