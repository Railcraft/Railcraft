package tests;

import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Created by CovertJaguar on 12/8/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
class IInventoryCompositeTest {
    static Item item;

    @BeforeAll
    static void setUp() {
        Bootstrap.register();
        item = new Item();
    }

    @Test
    void moveOneItemTo() {
        ItemStack srcStack = new ItemStack(item, 32);
        InventoryAdvanced invSrc = new InventoryAdvanced(6);
        InventoryAdvanced invDest = new InventoryAdvanced(6);
        invSrc.setInventorySlotContents(3, srcStack);

        ItemStack resultStack = invSrc.moveOneItemTo(invDest);
        Assertions.assertTrue(InvTools.isItemEqualStrict(new ItemStack(item, 31), srcStack));
        Assertions.assertTrue(InvTools.isItemEqualStrict(new ItemStack(item, 1), resultStack));
    }

    @Test
    void addStack() {
        ItemStack stack = new ItemStack(item, 32);
        InventoryAdvanced inv = new InventoryAdvanced(6);

        ItemStack resultStack = inv.addStack(stack);
        Assertions.assertTrue(InvTools.isEmpty(resultStack));
        Assertions.assertTrue(inv.contains(stack));
    }

    @Test
    void removeItemsAbsolute() {
        ItemStack stack = new ItemStack(item, 32);
        InventoryAdvanced inv = new InventoryAdvanced(6);
        inv.setInventorySlotContents(3, stack);

        Assertions.assertTrue(inv.removeItemsAbsolute(8, StackFilters.of(item)));
        Assertions.assertEquals(24, inv.countItems());
    }
}