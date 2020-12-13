package steptech.compactquickinventoryaccess;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.api.moduleInstructionFactoryModule.ModuleInstructionFactoryModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.*;

public class FactoryHandler {
    private final List<ModuleInstructionFactoryModule> factoryModules = new ArrayList<>();
    private final Map<Player, Block> lastInteractedBlocks = new HashMap<>();

    public void registerFactoryModule(@NotNull ModuleInstructionFactoryModule module) {
        this.factoryModules.add(0, module); //insert front, for priority
    }

    public void unregisterFactoryModule(@NotNull ModuleInstructionFactoryModule module) {
        this.factoryModules.remove(module);
    }

    public @NotNull List<ModuleInstructionFactoryModule> getFactoryModules() {
        return Collections.unmodifiableList(this.factoryModules);
    }

    public @Nullable ModuleInstructionWrapper createWrapper(@NotNull Player player) {
        return this.factoryModules.stream()
                .map(factoryModule -> factoryModule.createWrapper(player, this.lastInteractedBlocks.get(player)))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public void trackInteraction(@NotNull Player player, @Nullable Block block) {
        this.lastInteractedBlocks.put(player, block);
    }
}
