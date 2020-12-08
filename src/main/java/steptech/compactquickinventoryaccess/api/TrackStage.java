package steptech.compactquickinventoryaccess.api;

import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TrackStage {
    public final Supplier<@NotNull InventoryView> openInventory;
    public final Consumer<@NotNull InventoryView> onClose;
    public final TrackStage previous;

    public TrackStage(@NotNull Supplier<InventoryView> openInventory,
                      @NotNull Consumer<InventoryView> onClose,
                      @Nullable TrackStage trackStage) {
        this.openInventory = openInventory;
        this.onClose = onClose;
        previous = trackStage;
    }
}
