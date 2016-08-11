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
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class RailcraftBlockSubtyped extends RailcraftBlock implements IRailcraftBlock {
    private final Class<? extends IVariantEnum> variantClass;
    private final IVariantEnum[] variantValues;

    protected RailcraftBlockSubtyped(Material materialIn, Class<? extends IVariantEnum> variantClass) {
        this(materialIn, materialIn.getMaterialMapColor(), variantClass);
    }

    protected RailcraftBlockSubtyped(Material material, MapColor mapColor, Class<? extends IVariantEnum> variantClass) {
        super(material, mapColor);
        this.variantClass = variantClass;
        this.variantValues = variantClass.getEnumConstants();
    }

    @Nullable
    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return variantClass;
    }

    @Nullable
    @Override
    public IVariantEnum[] getVariants() {
        return variantValues;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        IVariantEnum[] variants = getVariants();
        if (variants != null) {
            for (IVariantEnum variant : variants) {
                list.add(getStack(variant));
            }
        } else {
            list.add(getStack(null));
        }
    }
}
