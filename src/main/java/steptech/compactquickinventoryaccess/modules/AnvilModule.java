package steptech.compactquickinventoryaccess.modules;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.TempItemRemover;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.*;

public class AnvilModule extends PickaxeOIMModule {

    public static @NotNull Material getNextAnvilDamageStateMaterial(@NotNull ItemStack anvil) throws IllegalArgumentException, IndexOutOfBoundsException {
        final AnvilDamagedEvent.DamageState currentState = AnvilDamagedEvent.DamageState.getState(anvil.getType()); //throws illegal argument when material is not anvil
        final AnvilDamagedEvent.DamageState nextLowerState = AnvilDamagedEvent.DamageState.values()[currentState.ordinal() + 1]; //throws index out of bounds when current state is air
        return nextLowerState.getMaterial();
    }

    private final Map<Player, ItemStack> anvils = new HashMap<>();
    private final Set<Player> playersWithOpenAnvilInventories = new HashSet<>();
    private double anvilGetsDamagedOnUseChance = 0.12;

    public AnvilModule(@NotNull ModuleHandler moduleHandler) {
        super(moduleHandler, Material.ANVIL, player -> player::openAnvil);
    }

    @Override
    public boolean matchesItem(@NotNull ItemStack clickedItem) {
        final List<Material> anvilMaterials = Arrays.asList(Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL);
        return anvilMaterials.contains(clickedItem.getType());
    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        //remove anvil
        final TempItemRemover tempItemRemover = new TempItemRemover(player::getOpenInventory, rawSlot);
        ItemStack anvil = tempItemRemover.getRemovedItem();
        assert anvil != null;
        this.anvils.put(player, anvil);

        applyOtherModifications(player, rawSlot);

        return new ModuleInstructionWrapper(() -> {
            this.playersWithOpenAnvilInventories.add(player);
            return player.openAnvil(null, true);
        }, closedView -> this.playersWithOpenAnvilInventories.remove(player),
                () -> {
            final ItemStack storedAnvil = this.anvils.get(player);
            if (storedAnvil != null) {
                tempItemRemover.setRemovedItem(storedAnvil);
                tempItemRemover.putItemBack();
            }
        });
    }

    public void trackAnvilUse(@NotNull Player player) {
        final ItemStack anvil = this.anvils.get(player);
        if (this.playersWithOpenAnvilInventories.contains(player) && anvil != null) {
            if (Math.random() < this.anvilGetsDamagedOnUseChance) {
                //anvil shall get damaged
                final Material nextAnvilDamageStateMaterial = getNextAnvilDamageStateMaterial(anvil);
                if (nextAnvilDamageStateMaterial.isItem()) {
                    anvil.setType(nextAnvilDamageStateMaterial);
                } else {
                    //anvil broke
                    this.anvils.remove(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                    player.closeInventory(); //close the anvil inventory
                }
            }
        }
    }

    public void setAnvilGetsDamagedOnUseChance(double anvilGetsDamagedOnUseChance) {
        this.anvilGetsDamagedOnUseChance = Math.min(1, Math.max(0, anvilGetsDamagedOnUseChance));
    }

    public double getAnvilGetsDamagedOnUseChance() {
        return anvilGetsDamagedOnUseChance;
    }
}
