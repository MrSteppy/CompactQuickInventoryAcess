package steptech.compactquickinventoryaccess.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.modules.AnvilModule;

public class AnvilUseListener implements Listener {
    private final AnvilModule anvilModule;

    public AnvilUseListener(@NotNull CompactQuickInventoryAccess compactQuickInventoryAccess,
                            @NotNull AnvilModule anvilModule) {
        this.anvilModule = anvilModule;

        Bukkit.getPluginManager().registerEvents(this, compactQuickInventoryAccess);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAnvilUse(@NotNull InventoryClickEvent event) {
        final HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player //check if player has clicked
                && event.getInventory().getType() == InventoryType.ANVIL //check for anvil inventory
                && event.getSlot() == 2) { //check for result slot
            final ItemStack cursor = event.getCursor();
            final ItemStack currentItem = event.getCurrentItem();
            if ((cursor == null || cursor.getType() == Material.AIR) //check if item can be taken
                    && currentItem != null && currentItem.getType() != Material.AIR) { //check if result is not null
                //ANVIL HAS BEEN USED
                this.anvilModule.trackAnvilUse((Player) whoClicked);
            }
        }
    }
}
