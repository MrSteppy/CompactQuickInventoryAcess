package steptech.compactquickinventoryaccess.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.api.commandModule.CommandModule;
import steptech.steptechpluginframework.infrastructure.commands.PreSubCommandExecution;
import steptech.steptechpluginframework.infrastructure.commands.StepTechCommand;
import steptech.steptechpluginframework.infrastructure.commands.SubCommandRegistrar;
import steptech.steptechpluginframework.infrastructure.commands.enums.Placeholder;

import java.util.Arrays;

public class CommandModuleCommand extends StepTechCommand {
    public CommandModuleCommand(@NotNull CommandModule commandModule,
                                @NotNull ModuleHandler moduleHandler,
                                @NotNull SubCommandRegistrar registrar) {
        super(commandModule.getName(), registrar);
        final String name = commandModule.getName();

        setAliases(commandModule.getAliases());
        setDescription("Opens a" + (Arrays.asList('a', 'e', 'i', 'o', 'u').contains(name.charAt(0)) ? "n" : "") + " " + name);
        setPermission(CompactQuickInventoryAccess.COMMAND_PERMISSION_NODE + "." + name);
        setPermissionMessage("Missing permission " + Placeholder.PERMISSION);

        new PreSubCommandExecution(this, sender -> {
            final Player player = castSender(sender);

            if (player.getGameMode() == GameMode.CREATIVE) {
                commandModule.onCreativeMode(player);
            } else {
                final String moduleFailedMessage = commandModule.getModuleFailedMessage();
                if (!moduleHandler.trackModule(commandModule, player) && moduleFailedMessage != null) {
                    player.sendMessage(ChatColor.RED + moduleFailedMessage);
                }
            }
        });
    }
}
