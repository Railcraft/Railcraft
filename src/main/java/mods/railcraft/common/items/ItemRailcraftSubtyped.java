/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 7/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemRailcraftSubtyped extends ItemRailcraft {
    private final Class<? extends IVariantEnum> variantClass;
    private final IVariantEnum[] variantValues;

    public ItemRailcraftSubtyped(Class<? extends IVariantEnum> variantClass) {
        this.variantClass = variantClass;
        this.variantValues = variantClass.getEnumConstants();
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public Class<? extends IVariantEnum> getVariantEnumClass() {
        return variantClass;
    }

    @Override
    public IVariantEnum[] getVariants() {
        return variantValues;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (!isInCreativeTab(tab))
            return;
        IVariantEnum[] variants = getVariants();
        if (variants != null) {
            for (IVariantEnum variant : variants) {
                CreativePlugin.addToList(list, getStack(variant));
            }
        } else {
            CreativePlugin.addToList(list, getStack(null));
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int damage = stack.getItemDamage();
        IVariantEnum[] variants = getVariants();
        if (variants == null || damage < 0 || damage >= variants.length)
            return getTranslationKey();
        String tag = getTranslationKey() + RailcraftConstants.SEPERATOR + variants[damage].getResourcePathSuffix();
        return LocalizationPlugin.convertTag(tag);
    }

    @Override
    public String getOreTag(@Nullable IVariantEnum variant) {
        checkVariant(variant);
        if (variant != null)
            return variant.getOreTag();
        return null;
    }
}
