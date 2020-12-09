package steptech.compactquickinventoryaccess.api.functions;

import org.bukkit.inventory.InventoryView;

public interface FinalModification {
    /**
     * Will be called, when an {@link InventoryView} will be finally closed
     */
    void applyFinalModification();
}
