package steptech.compactquickinventoryaccess.api.functions;

import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public interface TempInventoryCloser {
    /**
     * Called when an {@link InventoryView} closes temporary and is opened back later
     * @param closedView The {@link InventoryView} that has been closed temporary
     */
    void onTempInventoryClose(@NotNull InventoryView closedView);
}
