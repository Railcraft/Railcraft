/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.anvil;

import net.minecraft.item.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;

public class ItemAnvilBlock extends ItemMultiTexture {

    public ItemAnvilBlock(Block block) {
        super(block, block, BlockAnvil.anvilDamageNames);
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
