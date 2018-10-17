/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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
        return ((IRailcraftBlock) block).getVariantEnum();
    }

    @Nullable
    @Override
    public IVariantEnum[] getVariants() {
        return ((IRailcraftObject) block).getVariants();
    }

    @Nullable
    public IVariantEnum getVariant(ItemStack stack) {
        int damage = stack.getItemDamage();
        IVariantEnum[] variants = getVariants();
        if (variants == null || damage < 0 || damage >= variants.length) {
            return null;
        }
        return variants[damage];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        IVariantEnum variant = getVariant(stack);
        if (variant == null)
            return getTranslationKey();
        String tag = getTranslationKey() + RailcraftConstants.SEPERATOR + variant.getResourcePathSuffix();
        return LocalizationPlugin.convertTag(tag);
    }
}
