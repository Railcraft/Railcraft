/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.post;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemPostMetal extends ItemBlock {

    public ItemPostMetal(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage == -1 || damage == OreDictionary.WILDCARD_VALUE)
            return EnumPost.METAL_UNPAINTED.getIcon();
        return BlockPostMetal.textures[damage];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getItemDamage() == -1 || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
            return EnumPost.METAL_UNPAINTED.getTag();
        return super.getUnlocalizedName() + "." + EnumColor.fromId(stack.getItemDamage()).getBasicTag();
    }

}
