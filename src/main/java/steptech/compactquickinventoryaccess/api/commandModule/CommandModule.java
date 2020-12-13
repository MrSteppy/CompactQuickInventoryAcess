package steptech.compactquickinventoryaccess.api.commandModule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.api.quickAccessModule.QuickAccessModule;

public interface CommandModule extends QuickAccessModule {
    @NotNull String getName();
    @NotNull String[] getAliases();
    void onCreativeMode(@NotNull Player player);
    @Nullable String getModuleFailedMessage();
}
