package steptech.compactquickinventoryaccess.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.FactoryHandler;

public class InteractionListener implements Listener {
    private final FactoryHandler factoryHandler;

    public InteractionListener(@NotNull CompactQuickInventoryAccess compactQuickInventoryAccess) {
        this.factoryHandler = compactQuickInventoryAccess.getFactoryHandler();

        Bukkit.getPluginManager().registerEvents(this, compactQuickInventoryAccess);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInteract(@NotNull PlayerInteractEvent event) {
        final Block block = event.getAction() == Action.RIGHT_CLICK_BLOCK ? event.getClickedBlock() : null;
        this.factoryHandler.trackInteraction(event.getPlayer(), block);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        this.factoryHandler.trackInteraction(((Player) event.getPlayer()), null);
    }
}
