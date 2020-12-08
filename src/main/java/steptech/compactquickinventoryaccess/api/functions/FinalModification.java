package steptech.compactquickinventoryaccess.api.functions;

import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public interface FinalModification {
    /**
     * Will be called, when an {@link InventoryView} will be finally closed
     * @param currentView The now freshly opened {@link InventoryView} ready for modifications
     */
    void applyFinalModification(@NotNull InventoryView currentView);
}
