/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.materials;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import static mods.railcraft.common.blocks.aesthetics.materials.Materials.MATERIAL_KEY;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemMaterial extends ItemBlock {
    private final IMaterialBlock matBlock;

    public ItemMaterial(Block block) {
        super(block);
        this.matBlock = (IMaterialBlock) block;
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return matBlock.getUnlocalizedName(Materials.from(stack, MATERIAL_KEY));
    }
}
