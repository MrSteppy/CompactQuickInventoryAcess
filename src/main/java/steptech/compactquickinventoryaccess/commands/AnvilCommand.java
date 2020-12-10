package steptech.compactquickinventoryaccess.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.modules.AnvilModule;
import steptech.steptechpluginframework.infrastructure.commands.PreSubCommandExecution;
import steptech.steptechpluginframework.infrastructure.commands.StepTechCommand;
import steptech.steptechpluginframework.infrastructure.commands.SubCommandRegistrar;
import steptech.steptechpluginframework.infrastructure.commands.enums.Placeholder;

public class AnvilCommand extends StepTechCommand {
    public AnvilCommand(@NotNull SubCommandRegistrar registrar,
                             @NotNull AnvilModule anvilModule,
                             @NotNull ModuleHandler moduleHandler) {
        super("anvil", registrar);

        setAliases("av");
        setPermission(CompactQuickInventoryAccess.COMMAND_PERMISSION_NODE + ".anvil");
        setPermissionMessage("Missing permission " + Placeholder.PERMISSION);

        new PreSubCommandExecution(this, sender -> {
            final Player player = castSender(sender);

            if (player.getGameMode() == GameMode.CREATIVE) {
                player.openAnvil(null, true);
            } else {
                if (!moduleHandler.trackModule(anvilModule, player)) {
                    player.sendMessage(ChatColor.RED + "You have to have at least one anvil in your inventory!");
                }
            }
        });
    }
}
