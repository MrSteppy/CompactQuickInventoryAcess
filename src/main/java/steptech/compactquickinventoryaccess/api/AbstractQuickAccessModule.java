package steptech.compactquickinventoryaccess.api;

import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;

public abstract class AbstractQuickAccessModule implements QuickAccessModule {
    public static final String MODULE_PERMISSION_NODE = CompactQuickInventoryAccess.PERMISSION_NODE + ".module";
    private final String subPermission;

    public AbstractQuickAccessModule(@NotNull ModuleHandler moduleHandler, @NotNull String subPermission) {
        this.subPermission = subPermission;

        moduleHandler.registerModule(this);
    }

    public @NotNull String getPermission() {
        return MODULE_PERMISSION_NODE + "." + this.subPermission;
    }

    public boolean hasPermission(@NotNull Permissible player) {
        return player.hasPermission(getPermission());
    }
}
