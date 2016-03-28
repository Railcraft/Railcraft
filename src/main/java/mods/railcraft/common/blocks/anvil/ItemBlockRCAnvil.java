/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.anvil;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;

public class ItemBlockRCAnvil extends ItemMultiTexture {

    public ItemBlockRCAnvil(Block block) {
        // String array copied from ItemAnvilBlock
        super(block, block, new String[] {"intact", "slightlyDamaged", "veryDamaged"});
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    @Override
    public int getMetadata(int par1) {
        return par1 << 2;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack);
    }
}
