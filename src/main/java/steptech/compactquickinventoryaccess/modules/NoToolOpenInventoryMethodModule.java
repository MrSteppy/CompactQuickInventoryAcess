package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.AbstractQuickAccessModule;
import steptech.compactquickinventoryaccess.api.TempItemRemover;
import steptech.compactquickinventoryaccess.api.functions.OpenInventoryMethod;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class NoToolOpenInventoryMethodModule extends AbstractQuickAccessModule {
    public static @NotNull List<NoToolOpenInventoryMethodModule> createModules(
            @NotNull ModuleHandler moduleHandler,
            @NotNull Consumer<@NotNull Map<@NotNull Material, @NotNull Function<@NotNull Player, @NotNull OpenInventoryMethod>>> modulesToCreate) {
        final Map<Material, Function<Player, OpenInventoryMethod>> map = new HashMap<>();
        modulesToCreate.accept(map);
        final List<NoToolOpenInventoryMethodModule> modules = new ArrayList<>();
        map.forEach((material, playerOpenInventoryMethodFunction) -> modules.add(
                new NoToolOpenInventoryMethodModule(moduleHandler, material, playerOpenInventoryMethodFunction)
        ));
        return modules;
    }

    protected final Material matchingItemMaterial;
    protected final Function<Player, OpenInventoryMethod> openInventory;

    public NoToolOpenInventoryMethodModule(@NotNull ModuleHandler moduleHandler,
                                           @NotNull Material matchingItemMaterial,
                                           @NotNull Function<@NotNull Player, @NotNull OpenInventoryMethod> openInventory) {
        super(moduleHandler, matchingItemMaterial.name().toLowerCase());
        this.matchingItemMaterial = matchingItemMaterial;
        this.openInventory = openInventory;
    }

    @Override
    public boolean matchesItem(@NotNull ItemStack clickedItem) {
        return clickedItem.getType() == this.matchingItemMaterial;
    }

    /**
     * This is for easy modification via inheritance and will be called after a successful permission check
     * @param player The {@link Player} to check
     * @return If the given {@link Player} matches other requirements besides the permission
     */
    protected boolean checkOtherRequirementsBesidesPermission(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean matchesOtherRequirements(@NotNull Player player) {
        if (!hasPermission(player)) {
            player.sendActionBar("Missing permission " + getPermission());
            return false;
        }
        return checkOtherRequirementsBesidesPermission(player);
    }

    /**
     * This is for easy modification via inheritance and will be called after the temp removal of the clicked item
     * @param player The {@link Player} to apply the modifications to
     * @param rawSlot The raw slot from which the clicked item has been removed
     */
    protected void applyOtherModifications(@NotNull Player player, int rawSlot) {

    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        final TempItemRemover tempItemRemover = new TempItemRemover(player::getOpenInventory, rawSlot);
        final ItemStack removedItem = tempItemRemover.getRemovedItem();
        assert removedItem != null;

        applyOtherModifications(player, rawSlot);

        return new ModuleInstructionWrapper(() -> this.openInventory.apply(player).open(player.getLocation(), false),
                closedView -> {},
                tempItemRemover::putItemBack);
    }
}
