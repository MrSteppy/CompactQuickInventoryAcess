package steptech.compactquickinventoryaccess.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;

public class QuickAccessListener implements Listener {
    private final ModuleHandler moduleHandler;

    public QuickAccessListener(@NotNull CompactQuickInventoryAccess compactQuickInventoryAccess) {
        this.moduleHandler = compactQuickInventoryAccess.getModuleHandler();

        Bukkit.getPluginManager().registerEvents(this, compactQuickInventoryAccess); //register
    }

    @EventHandler (ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        final HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player) {
            final Player player = (Player) whoClicked;
            if (event.isRightClick()) { //react only on right click
                final ItemStack cursor = event.getCursor();
                if (cursor == null || cursor.getType().isAir()) { //check for empty cursor
                    final ItemStack currentItem = event.getCurrentItem();
                    if (currentItem != null && !currentItem.getType().isAir()) { //make sure, we didn't click in the air

                        //CLICK HAS BEEN RECOGNISED
                        if (this.moduleHandler.trackClick(currentItem, player, event.getRawSlot())) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        this.moduleHandler.trackInventoryOpen(event.getView());
    }

    @EventHandler (ignoreCancelled = true)
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        final InventoryView view = event.getView();
        if (event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW)
            this.moduleHandler.trackFinalInventoryClose(view);
        else
            this.moduleHandler.trackTempInventoryClose(view);
    }

    @EventHandler (ignoreCancelled = true)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        this.moduleHandler.trackLogout(event.getPlayer());
    }
}
