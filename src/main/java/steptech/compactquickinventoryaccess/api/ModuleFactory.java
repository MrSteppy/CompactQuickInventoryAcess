package steptech.compactquickinventoryaccess.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.api.functions.OpenInventoryMethod;
import steptech.compactquickinventoryaccess.api.functions.TriFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ModuleFactory<Product, ConstParameter, DependentParameter> {
    private final TriFunction<ConstParameter, DependentParameter, Function<Player, OpenInventoryMethod>, Product> createOne;

    public ModuleFactory(@NotNull TriFunction<ConstParameter, DependentParameter, @NotNull Function<Player, @NotNull OpenInventoryMethod>, Product> createOne) {
        this.createOne = createOne;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull List<Product> createModules(
            ConstParameter constParameter,
            @NotNull Consumer<Map<DependentParameter, @NotNull Function<Player, @NotNull OpenInventoryMethod>>> modulesToCreate) {
        final Map<DependentParameter, Function<Player, OpenInventoryMethod>> map = new HashMap<>();
        modulesToCreate.accept(map);
        final List<Product> products = new ArrayList<>();
        map.forEach((dependentParameter, playerOpenInventoryMethodFunction) -> products.add(
                this.createOne.apply(constParameter, dependentParameter, playerOpenInventoryMethodFunction)
        ));
        return products;
    }
}
