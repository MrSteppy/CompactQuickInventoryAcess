package steptech.compactquickinventoryaccess.commands.compactQuickInventoryAccess;

import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.CompactQuickInventoryAccess;
import steptech.steptechpluginframework.infrastructure.commands.StepTechCommand;
import steptech.steptechpluginframework.infrastructure.commands.SubCommandRegistrar;

public class CompactQuickInventoryAccessCommand extends StepTechCommand {
    public CompactQuickInventoryAccessCommand(@NotNull SubCommandRegistrar registrar, @NotNull CompactQuickInventoryAccess compactQuickInventoryAccess) throws IllegalArgumentException {
        super("compactquickinventoryacess", registrar);

        setAliases("cqia");
        setPermission(CompactQuickInventoryAccess.COMMAND_PERMISSION_NODE + "." + "menu");
        setDescription("The menu for this plugin");

        new ReloadCommand(this, compactQuickInventoryAccess);
    }
}
