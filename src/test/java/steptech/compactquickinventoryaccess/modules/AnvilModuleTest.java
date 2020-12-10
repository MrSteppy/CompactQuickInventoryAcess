package steptech.compactquickinventoryaccess.modules;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class AnvilModuleTest extends TestCase {
    public void testGetNextAnvilDamageStateMaterial() {
        final BiConsumer<Material, Material> assertNextDamageMaterial = (input, result) ->
                Assert.assertEquals(AnvilModule.getNextAnvilDamageStateMaterial(new ItemStack(input)), result);

        assertNextDamageMaterial.accept(Material.ANVIL, Material.CHIPPED_ANVIL);
        assertNextDamageMaterial.accept(Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL);
        assertNextDamageMaterial.accept(Material.DAMAGED_ANVIL, Material.AIR);
    }
}