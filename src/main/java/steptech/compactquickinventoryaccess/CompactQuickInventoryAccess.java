package steptech.compactquickinventoryaccess;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import steptech.compactquickinventoryaccess.commands.WorkbenchCommand;
import steptech.compactquickinventoryaccess.listener.QuickAccessListener;
import steptech.compactquickinventoryaccess.modules.EnderchestModule;
import steptech.compactquickinventoryaccess.modules.ShulkerBoxModule;
import steptech.compactquickinventoryaccess.modules.WorkbenchModule;
import steptech.steptechpluginframework.infrastructure.commands.commandManager.StepTechCommandManager;

public final class CompactQuickInventoryAccess extends JavaPlugin {
    //hard config
    public static final String PERMISSION_NODE = "compactquickinventoryaccess";

    //handler
    private ModuleHandler moduleHandler;

    @Override
    public void onEnable() {
        //handler
        this.moduleHandler = new ModuleHandler(this);

        //modules
        final WorkbenchModule workbenchModule = new WorkbenchModule(this.moduleHandler);
        final EnderchestModule enderchestModule = new EnderchestModule(this.moduleHandler);
        new ShulkerBoxModule(this.moduleHandler);

        //listener
        new QuickAccessListener(this);

        //commands
        final StepTechCommandManager manager = new StepTechCommandManager(this);
        new WorkbenchCommand(manager, workbenchModule, this.moduleHandler);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> this.moduleHandler.trackLogout(player));
    }

    public ModuleHandler getModuleHandler() {
        return moduleHandler;
    }
}
