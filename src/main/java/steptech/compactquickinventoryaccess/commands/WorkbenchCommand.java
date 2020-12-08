package steptech.compactquickinventoryaccess.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.modules.WorkbenchModule;
import steptech.steptechpluginframework.infrastructure.commands.PreSubCommandExecution;
import steptech.steptechpluginframework.infrastructure.commands.StepTechCommand;
import steptech.steptechpluginframework.infrastructure.commands.SubCommandRegistrar;
import steptech.steptechpluginframework.infrastructure.commands.enums.Placeholder;

public class WorkbenchCommand extends StepTechCommand {
    public WorkbenchCommand(@NotNull SubCommandRegistrar registrar,
                            @NotNull WorkbenchModule workbenchModule,
                            @NotNull ModuleHandler moduleHandler) {
        super("workbench", registrar);

        setAliases("wb");
        setPermission(CompactQuickInventoryAccess.PERMISSION_NODE + ".command.workbench");
        setPermissionMessage("Missing permission " + Placeholder.PERMISSION);

        new PreSubCommandExecution(this, sender -> {
            final Player player = castSender(sender);

            if (player.getGameMode() == GameMode.CREATIVE) {
                player.openWorkbench(player.getLocation(), true);
            } else {
                if (!moduleHandler.trackModule(workbenchModule, player)) {
                    player.sendMessage(ChatColor.RED + "You have to have at least one workbench in your inventory!");
                }
            }
        });
    }
}
