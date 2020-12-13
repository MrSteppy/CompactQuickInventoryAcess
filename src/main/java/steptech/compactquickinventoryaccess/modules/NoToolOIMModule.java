package steptech.compactquickinventoryaccess.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.TempItemRemover;
import steptech.compactquickinventoryaccess.api.commandModule.AbstractCommandModule;
import steptech.compactquickinventoryaccess.api.functions.OpenInventoryMethod;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.function.Function;

public class NoToolOIMModule extends AbstractCommandModule {
    private static @NotNull String getName(@NotNull Material material) {
        return material.name().toLowerCase().replace("_", "");
    }

    protected final Material matchingItemMaterial;
    protected final Function<Player, OpenInventoryMethod> openInventory;

    public NoToolOIMModule(@NotNull ModuleHandler moduleHandler,
                           @NotNull Material matchingItemMaterial,
                           @NotNull Function<@NotNull Player, @NotNull OpenInventoryMethod> openInventory) {
        super(moduleHandler,
                getName(matchingItemMaterial),
                player -> openInventory.apply(player).open(null, true),
                "You need at least one " + getName(matchingItemMaterial) + "!");
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
    @SuppressWarnings("SameReturnValue")
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
    @SuppressWarnings("EmptyMethod")
    protected void applyOtherModifications(@NotNull Player player, int rawSlot) {

    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        final TempItemRemover tempItemRemover = new TempItemRemover(player::getOpenInventory, rawSlot);
        final ItemStack removedItem = tempItemRemover.getRemovedItem();
        assert removedItem != null;

        applyOtherModifications(player, rawSlot);

        return new ModuleInstructionWrapper(() -> this.openInventory.apply(player).open(player.getLocation(), true),
                closedView -> {},
                tempItemRemover::putItemBack);
    }
}
