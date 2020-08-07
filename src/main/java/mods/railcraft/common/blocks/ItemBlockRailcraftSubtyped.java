/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemBlockRailcraftSubtyped<B extends Block & IRailcraftBlock> extends ItemBlockRailcraft<B> {

    public ItemBlockRailcraftSubtyped(B block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public @Nullable Class<? extends IVariantEnum> getVariantEnumClass() {
        return block.getVariantEnumClass();
    }

    @Override
    public @Nullable IVariantEnum[] getVariants() {
        return block.getVariants();
    }

    public @Nullable IVariantEnum getVariant(ItemStack stack) {
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
