package steptech.compactquickinventoryaccess.api.commandModule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.quickAccessModule.AbstractQuickAccessModule;

import java.util.function.Consumer;

public abstract class AbstractCommandModule extends AbstractQuickAccessModule implements CommandModule {
    private final String[] aliases;
    private final Consumer<Player> onCreativeMode;
    private final String moduleFailedMessage;

    public AbstractCommandModule(@NotNull ModuleHandler moduleHandler,
                                 @NotNull String name,
                                 @NotNull Consumer<Player> onCreativeMode,
                                 @Nullable String moduleFailedMessage,
                                 @NotNull String... aliases) {
        super(moduleHandler, name);

        this.aliases = aliases;
        this.onCreativeMode = onCreativeMode;
        this.moduleFailedMessage = moduleFailedMessage;
    }

    @Override
    public @NotNull String getName() {
        return getSubPermission();
    }

    @Override
    public @NotNull String[] getAliases() {
        return this.aliases;
    }

    @Override
    public void onCreativeMode(@NotNull Player player) {
        this.onCreativeMode.accept(player);
    }

    @Override
    public @Nullable String getModuleFailedMessage() {
        return this.moduleFailedMessage;
    }
}
