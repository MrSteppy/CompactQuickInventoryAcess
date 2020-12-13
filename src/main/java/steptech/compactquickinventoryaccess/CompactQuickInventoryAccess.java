package steptech.compactquickinventoryaccess;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
import steptech.compactquickinventoryaccess.commands.AnvilCommand;
import steptech.compactquickinventoryaccess.commands.EnderchestCommand;
import steptech.compactquickinventoryaccess.commands.compactQuickInventoryAccess.CompactQuickInventoryAccessCommand;
import steptech.compactquickinventoryaccess.factoryModules.BlockTypeBoundInventoriesFactoryModule;
import steptech.compactquickinventoryaccess.factoryModules.ContainerFactoryModule;
import steptech.compactquickinventoryaccess.factoryModules.DoubleChestFactoryModule;
import steptech.compactquickinventoryaccess.listener.AnvilUseListener;
import steptech.compactquickinventoryaccess.listener.InteractionListener;
import steptech.compactquickinventoryaccess.listener.QuickAccessListener;
import steptech.compactquickinventoryaccess.modules.AnvilModule;
import steptech.compactquickinventoryaccess.modules.EnderchestModule;
import steptech.compactquickinventoryaccess.modules.NoToolOpenInventoryMethodModule;
import steptech.compactquickinventoryaccess.modules.ShulkerBoxModule;
import steptech.steptechpluginframework.infrastructure.commands.commandManager.StepTechCommandManager;

public final class CompactQuickInventoryAccess extends JavaPlugin {
    //hard config
    public static final String PERMISSION_NODE = "compactquickinventoryaccess";
    public static final String COMMAND_PERMISSION_NODE = PERMISSION_NODE + ".command";
    private static final String ANVIL_DAMAGE_ON_USE_CHANCE_PATH = "anvilDamageOnUseChance";

    //handler
    private FactoryHandler factoryHandler;
    private ModuleHandler moduleHandler;


    //modules
    private AnvilModule anvilModule;

    @Override
    public void onEnable() {
        //config
        getConfig().options().copyDefaults(true);
        saveConfig();

        //handler
        this.factoryHandler = new FactoryHandler();
        this.moduleHandler = new ModuleHandler(this);

        /*TODO FactoryModules
        *  Beacon
        * */
        /*TODO Module
        *  fletching table
        *  BooksAndQuill
        *  Grindstone
        *  stone cutter
        * */

        //factory modules
        new ContainerFactoryModule(this.factoryHandler);
        new DoubleChestFactoryModule(this.factoryHandler);
        BlockTypeBoundInventoriesFactoryModule.createFactoryModules(factoryHandler, map -> {
            map.put(InventoryType.ANVIL, player -> player::openAnvil);
            map.put(InventoryType.WORKBENCH, player -> player::openWorkbench);
            map.put(InventoryType.GRINDSTONE, player -> player::openGrindstone);
            map.put(InventoryType.CARTOGRAPHY, player -> player::openCartographyTable);
            map.put(InventoryType.ENCHANTING, player -> player::openEnchanting);
            map.put(InventoryType.LOOM, player -> player::openLoom);
            map.put(InventoryType.SMITHING, player -> player::openSmithingTable);
            map.put(InventoryType.STONECUTTER, player -> player::openStonecutter);
            map.put(InventoryType.ENDER_CHEST, player -> (location, force) -> player.openInventory(player.getEnderChest()));
        });

        //modules
        NoToolOpenInventoryMethodModule.createModules(this.moduleHandler, map -> {
            map.put(Material.CRAFTING_TABLE, player -> player::openWorkbench);
            map.put(Material.CARTOGRAPHY_TABLE, player -> player::openCartographyTable);
            map.put(Material.LOOM, player -> player::openLoom);
            map.put(Material.SMITHING_TABLE, player -> player::openSmithingTable);
        });
        this.anvilModule = new AnvilModule(this.moduleHandler);
        final EnderchestModule enderchestModule = new EnderchestModule(this.moduleHandler);
        new ShulkerBoxModule(this.moduleHandler);

        //listener
        new QuickAccessListener(this);
        new AnvilUseListener(this, anvilModule);
        new InteractionListener(this);

        //commands
        final StepTechCommandManager manager = new StepTechCommandManager(this);
        new CompactQuickInventoryAccessCommand(manager, this);
        new EnderchestCommand(manager, enderchestModule, this.moduleHandler);
        new AnvilCommand(manager, this.anvilModule, this.moduleHandler);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> this.moduleHandler.trackLogout(player));
    }

    public void reloadSettings() {
        reloadConfig();
        this.anvilModule.setAnvilGetsDamagedOnUseChance(getConfig().getDouble(ANVIL_DAMAGE_ON_USE_CHANCE_PATH));
    }

    public ModuleHandler getModuleHandler() {
        return moduleHandler;
    }

    public FactoryHandler getFactoryHandler() {
        return factoryHandler;
    }
}
