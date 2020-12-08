package steptech.compactquickinventoryaccess.api.functions;

import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

public interface InventoryOpener {
    /**
     * Opens an {@link InventoryView} and returns it
     * @return The opened {@link InventoryView}, preferably not null
     */
    @Nullable InventoryView openInventory();
}
