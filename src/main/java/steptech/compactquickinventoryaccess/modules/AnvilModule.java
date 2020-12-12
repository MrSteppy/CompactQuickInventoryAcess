package steptech.compactquickinventoryaccess.modules;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.AbstractQuickAccessModule;
import steptech.compactquickinventoryaccess.api.QuickAccessModule;
import steptech.compactquickinventoryaccess.api.TempItemRemover;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.*;

public class AnvilModule extends AbstractQuickAccessModule {

    public static @NotNull Material getNextAnvilDamageStateMaterial(@NotNull ItemStack anvil) throws IllegalArgumentException, IndexOutOfBoundsException {
        final AnvilDamagedEvent.DamageState currentState = AnvilDamagedEvent.DamageState.getState(anvil.getType()); //throws illegal argument when material is not anvil
        final AnvilDamagedEvent.DamageState nextLowerState = AnvilDamagedEvent.DamageState.values()[currentState.ordinal() + 1]; //throws index out of bounds when current state is air
        return nextLowerState.getMaterial();
    }

    private final Map<Player, ItemStack> anvils = new HashMap<>();
    private final Set<Player> playersWithOpenAnvilInventories = new HashSet<>();
    private double anvilGetsDamagedOnUseChance = 0.12;

    public AnvilModule(@NotNull ModuleHandler moduleHandler) {
        super(moduleHandler, "anvil");
    }

    @Override
    public boolean matchesItem(@NotNull ItemStack clickedItem) {
        final List<Material> anvilMaterials = Arrays.asList(Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL);
        return anvilMaterials.contains(clickedItem.getType());
    }

    private boolean isPickaxe(@NotNull ItemStack itemStack) {
        return itemStack.getType().name().endsWith("PICKAXE");
    }

    private int findPickaxeSlot(@NotNull InventoryView inventoryView) {
        for (int rawSlot = 0, bound = inventoryView.countSlots(); rawSlot < bound; rawSlot++) {
            final ItemStack itemStack = inventoryView.getItem(rawSlot);
            if (itemStack != null && isPickaxe(itemStack)) return rawSlot;
        }
        return -1;
    }

    @Override
    public boolean matchesOtherRequirements(@NotNull Player player) {
        //check permission
        if (!hasPermission(player)) {
            player.sendActionBar("Missing permission " + getPermission());
            return false;
        }
        //check for pickaxe
        if (!(findPickaxeSlot(player.getOpenInventory()) > -1)) {
            player.sendActionBar("Pickaxe needed!");
            return false;
        }

        return true;
    }

    @Override
    public @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot) {
        final InventoryView modificationView = player.getOpenInventory();

        //remove one health from pickaxe
        QuickAccessModule.damageItem(modificationView, findPickaxeSlot(modificationView), 1);

        //remove anvil
        final TempItemRemover tempItemRemover = new TempItemRemover(player::getOpenInventory, rawSlot);
        ItemStack anvil = tempItemRemover.getRemovedItem();
        assert anvil != null;
        this.anvils.put(player, anvil);

        return new ModuleInstructionWrapper(() -> {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
            this.playersWithOpenAnvilInventories.add(player);
            return player.openAnvil(null, true);
        }, closedView -> this.playersWithOpenAnvilInventories.remove(player), () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
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
