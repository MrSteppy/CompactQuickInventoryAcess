package steptech.compactquickinventoryaccess.commands.compactQuickInventoryAccess;

import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.steptechpluginframework.infrastructure.commands.PreSubCommandExecution;
import steptech.steptechpluginframework.infrastructure.commands.StepTechCommand;
import steptech.steptechpluginframework.infrastructure.commands.SubCommandRegistrar;

public class ReloadCommand extends StepTechCommand {
    public ReloadCommand(@NotNull SubCommandRegistrar registrar, @NotNull CompactQuickInventoryAccess compactQuickInventoryAccess) throws IllegalArgumentException {
        super("reload", registrar);

        setAliases("rl");
        setDescription("Reloads the config");

        new PreSubCommandExecution(this, sender -> {
            compactQuickInventoryAccess.reloadSettings();
            sender.sendMessage("Config has been reloaded");
        });
    }
}
