/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IShearable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemSteelShears extends ItemShears {

    public ItemSteelShears() {
        super();
        setMaxDamage(500);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        String tag = "tool.steel.shears";
        setUnlocalizedName("railcraft." + tag);
        setRegistryName("Railcraft", tag);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        if (block == Blocks.web || block instanceof IShearable)
            return 15;
        if (block == Blocks.wool)
            return 5;
        return super.getStrVsBlock(stack, block);
    }

}
