package steptech.compactquickinventoryaccess.factoryModules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.FactoryHandler;
import steptech.compactquickinventoryaccess.api.ModuleInstructionFactoryModule;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BlockTypeBoundInventoriesFactoryModule extends ModuleInstructionFactoryModule {
    private interface OpenerWrapper {
        @Nullable InventoryView open(@Nullable Location location, boolean force);
    }

    private final Function<Player, Map<InventoryType, OpenerWrapper>> openersProvider = player -> {
        final Map<InventoryType, OpenerWrapper> map = new HashMap<>();

        //register inventory types here
        map.put(InventoryType.ANVIL, player::openAnvil);
        map.put(InventoryType.WORKBENCH, player::openWorkbench);
        map.put(InventoryType.GRINDSTONE, player::openGrindstone);
        map.put(InventoryType.CARTOGRAPHY, player::openCartographyTable);
        map.put(InventoryType.ENCHANTING, player::openEnchanting);
        map.put(InventoryType.LOOM, player::openLoom);
        map.put(InventoryType.SMITHING, player::openSmithingTable);
        map.put(InventoryType.STONECUTTER, player::openStonecutter);
        map.put(InventoryType.ENDER_CHEST, (location, force) -> player.openInventory(player.getEnderChest()));

        return map;
    };

    public BlockTypeBoundInventoriesFactoryModule(@NotNull FactoryHandler factoryHandler) {
        super(factoryHandler);
    }

    @Override
    public @Nullable ModuleInstructionWrapper createWrapper(@NotNull Player player, @Nullable Block lastInteractedBlock) {
        if (lastInteractedBlock != null) {
            final Location location = lastInteractedBlock.getLocation();
            final OpenerWrapper openerWrapper = this.openersProvider.apply(player).get(player.getOpenInventory().getType());
            if (openerWrapper != null) {
                return new ModuleInstructionWrapper(() -> openerWrapper.open(location, false),
                        closedView -> {},
                        () -> {});
            }
        }
        return null;
    }
}
