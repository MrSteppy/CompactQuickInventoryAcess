package steptech.compactquickinventoryaccess.factories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.functions.OpenInventoryMethod;
import steptech.compactquickinventoryaccess.modules.NoToolOIMModule;
import steptech.compactquickinventoryaccess.modules.PickaxeOIMModule;

import java.util.function.Function;

public class PickaxeOIMModuleFactory extends NoToolOIMModuleFactory {
    public PickaxeOIMModuleFactory(@NotNull ModuleHandler moduleHandler) {
        super(moduleHandler);
    }

    @Override
    public @NotNull NoToolOIMModule create(@NotNull Material matchingItemMaterial,
                                           @NotNull Function<@NotNull Player, @NotNull OpenInventoryMethod> openInventory,
                                           @NotNull String... aliases) {
        return new PickaxeOIMModule(this.moduleHandler, matchingItemMaterial, openInventory, aliases);
    }
}
