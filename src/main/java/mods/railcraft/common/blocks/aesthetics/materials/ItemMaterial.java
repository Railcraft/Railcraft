/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.materials;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import static mods.railcraft.common.blocks.aesthetics.materials.Materials.MATERIAL_KEY;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemMaterial extends ItemBlockRailcraft {
    private final IMaterialBlock matBlock;

    public ItemMaterial(Block block) {
        super(block);
        this.matBlock = (IMaterialBlock) block;
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return matBlock.getTranslationKey(Materials.from(stack, MATERIAL_KEY));
    }
}
