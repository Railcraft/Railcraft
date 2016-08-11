/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.IRailcraftObject;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemBlockRailcraftSubtyped extends ItemBlockRailcraft {

    public ItemBlockRailcraftSubtyped(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Nullable
    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return ((IRailcraftObject) block).getVariantEnum();
    }

    @Nullable
    @Override
    public IVariantEnum[] getVariants() {
        return ((IRailcraftObject) block).getVariants();
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        IVariantEnum[] variants = getVariants();
        if (variants == null || damage < 0 || damage >= variants.length)
            return getUnlocalizedName();
        return getUnlocalizedName() + "." + variants[damage].getResourcePathSuffix();
    }
}
