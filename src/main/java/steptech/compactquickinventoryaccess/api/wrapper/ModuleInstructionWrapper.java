package steptech.compactquickinventoryaccess.api.wrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.api.functions.FinalModification;
import steptech.compactquickinventoryaccess.api.functions.InventoryOpener;
import steptech.compactquickinventoryaccess.api.functions.TempInventoryCloser;

public class ModuleInstructionWrapper {
    private final InventoryOpener inventoryOpener;
    private final TempInventoryCloser tempInventoryCloser;
    private final FinalModification finalModification;
    private ModuleInstructionWrapper previous;


    public ModuleInstructionWrapper(@NotNull InventoryOpener inventoryOpener,
                                    @NotNull TempInventoryCloser tempInventoryCloser,
                                    @NotNull FinalModification finalModification) {
        this.inventoryOpener = inventoryOpener;
        this.tempInventoryCloser = tempInventoryCloser;
        this.finalModification = finalModification;
    }

    public @NotNull InventoryOpener getInventoryOpener() {
        return inventoryOpener;
    }

    public @NotNull TempInventoryCloser getTempInventoryCloser() {
        return tempInventoryCloser;
    }

    public @NotNull FinalModification getFinalInventoryCloser() {
        return finalModification;
    }

    public @Nullable ModuleInstructionWrapper getPrevious() {
        return previous;
    }

    public void setPrevious(@Nullable ModuleInstructionWrapper previous) {
        this.previous = previous;
    }
}
