package steptech.compactquickinventoryaccess.api.functions;

import org.bukkit.Location;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

public interface OpenInventoryMethod {
    @Nullable InventoryView open(@Nullable Location location, boolean force);
}
