package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.ItemBlockRailcraftSubtyped;
import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemBlockTank extends ItemBlockRailcraftSubtyped {

    public ItemBlockTank(Block block) {
        super(block);
    }

    @Override
    public IItemColor colorHandler() {
        return (stack, index) -> EnumColor.fromOrdinal(stack.getItemDamage()).getHexColor();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName(); // No different names for colors
    }
}
