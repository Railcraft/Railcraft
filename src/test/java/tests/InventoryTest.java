package tests;

import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Created by CovertJaguar on 12/8/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
class InventoryTest {

    @BeforeAll
    static void setUp() {
        Bootstrap.register();
    }

    @Test
    void addStack() {
        ItemStack stack = new ItemStack(Items.APPLE, 32);
        InventoryAdvanced inv = new InventoryAdvanced(6);

        ItemStack resultStack = inv.addStack(stack);
        Assertions.assertTrue(resultStack.isEmpty());
        Assertions.assertTrue(inv.contains(stack));
    }

    @Test
    void canFit() {
        ItemStack stackFull = new ItemStack(Items.APPLE, 64);
        InventoryAdvanced invFull = new InventoryAdvanced(1);
        invFull.addStack(stackFull);

        InventoryAdvanced invRoom = new InventoryAdvanced(2);
        invRoom.addStack(stackFull);
        invRoom.addStack(new ItemStack(Items.CARROT, 63));

        ItemStack stackApple = new ItemStack(Items.APPLE);
        ItemStack stackCarrot = new ItemStack(Items.CARROT);

        Assertions.assertFalse(invFull.canFit(stackApple));
        Assertions.assertFalse(invFull.canFit(stackCarrot));

        Assertions.assertFalse(invRoom.canFit(stackApple));
        Assertions.assertTrue(invRoom.canFit(stackCarrot));
    }

    @Test
    void moveOneItemTo() {
        ItemStack srcStack = new ItemStack(Items.APPLE, 32);
        InventoryAdvanced invSrc = new InventoryAdvanced(6);
        InventoryAdvanced invDest = new InventoryAdvanced(6);
        invSrc.setInventorySlotContents(3, srcStack);

        ItemStack resultStack = invSrc.moveOneItemTo(invDest);
        Assertions.assertEquals(31, srcStack.getCount());
        Assertions.assertEquals(1, resultStack.getCount());
        Assertions.assertEquals(1, invDest.countItems());
    }

    @Test
    void removeItems() {
        ItemStack stack = new ItemStack(Items.APPLE, 32);
        InventoryAdvanced inv = new InventoryAdvanced(6);
        inv.setInventorySlotContents(3, stack);

        Assertions.assertTrue(inv.removeItems(8, StackFilters.of(Items.APPLE)));
        Assertions.assertFalse(inv.removeItems(8, StackFilters.of(Items.CARROT)));
        Assertions.assertEquals(24, inv.countItems());
    }

    @Test
    void removeOneItem() {
        ItemStack stack = new ItemStack(Items.APPLE, 32);
        InventoryAdvanced inv = new InventoryAdvanced(6);
        inv.setInventorySlotContents(3, stack);

        ItemStack result = inv.removeOneItem(StackFilters.of(Items.APPLE));
        Assertions.assertEquals(1, result.getCount());
        Assertions.assertEquals(31, inv.countItems());
    }
}