package steptech.compactquickinventoryaccess.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.compactquickinventoryaccess.ModuleHandler;
import steptech.compactquickinventoryaccess.modules.EnderchestModule;
import steptech.steptechpluginframework.infrastructure.commands.PreSubCommandExecution;
import steptech.steptechpluginframework.infrastructure.commands.StepTechCommand;
import steptech.steptechpluginframework.infrastructure.commands.SubCommandRegistrar;
import steptech.steptechpluginframework.infrastructure.commands.enums.Placeholder;

public class EnderchestCommand extends StepTechCommand {
    public EnderchestCommand(@NotNull SubCommandRegistrar registrar,
                             @NotNull EnderchestModule enderchestModule,
                             @NotNull ModuleHandler moduleHandler) {
        super("enderchest", registrar);

        setAliases("ec");
        setPermission(CompactQuickInventoryAccess.COMMAND_PERMISSION_NODE + ".enderchest");
        setPermissionMessage("Missing permission " + Placeholder.PERMISSION);

        new PreSubCommandExecution(this, sender -> {
            final Player player = castSender(sender);

            if (player.getGameMode() == GameMode.CREATIVE) {
                player.openInventory(player.getEnderChest());
            } else {
                if (!moduleHandler.trackModule(enderchestModule, player)) {
                    player.sendMessage(ChatColor.RED + "You have to have at least one enderchest in your inventory!");
                }
            }
        });
    }
}
