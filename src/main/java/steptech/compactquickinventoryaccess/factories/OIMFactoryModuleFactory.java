package steptech.compactquickinventoryaccess.factories;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.FactoryHandler;
import steptech.compactquickinventoryaccess.api.functions.OpenInventoryMethod;
import steptech.compactquickinventoryaccess.factoryModules.OIMFactoryModule;

import java.util.function.Function;

public class OIMFactoryModuleFactory {
    private final FactoryHandler factoryHandler;

    public OIMFactoryModuleFactory(@NotNull FactoryHandler factoryHandler) {
        this.factoryHandler = factoryHandler;
    }

    public @NotNull OIMFactoryModule create(
            @NotNull InventoryType inventoryType,
            @NotNull Function<Player, @NotNull OpenInventoryMethod> openInventory) {
        return new OIMFactoryModule(factoryHandler, inventoryType, openInventory);
    }
}
