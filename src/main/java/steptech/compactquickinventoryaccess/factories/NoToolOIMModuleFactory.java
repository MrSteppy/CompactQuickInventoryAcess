package steptech.compactquickinventoryaccess.factories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.functions.OpenInventoryMethod;
import steptech.compactquickinventoryaccess.modules.NoToolOIMModule;

import java.util.function.Function;

public class NoToolOIMModuleFactory {
    protected final ModuleHandler moduleHandler;


    public NoToolOIMModuleFactory(@NotNull ModuleHandler moduleHandler) {
        this.moduleHandler = moduleHandler;
    }

    public @NotNull NoToolOIMModule create(@NotNull Material matchingItemMaterial,
                                           @NotNull Function<@NotNull Player, @NotNull OpenInventoryMethod> openInventory,
                                           @NotNull String... aliases) {
        return new NoToolOIMModule(moduleHandler, matchingItemMaterial, openInventory, aliases);
    }
}
