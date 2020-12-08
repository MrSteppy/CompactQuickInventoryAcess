package steptech.compactquickinventoryaccess.api;

import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModificationResultWrapper {
    public final Supplier<@NotNull InventoryView> openInventory;
    public final Consumer<@NotNull InventoryView> onClose;


    public ModificationResultWrapper(@NotNull Supplier<@NotNull InventoryView> openInventory,
                                     @NotNull Consumer<@NotNull InventoryView> onClose) {
        this.openInventory = openInventory;
        this.onClose = onClose;
    }
}
