/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemBoreHeadDiamond extends ItemBoreHead {

    public static final ResourceLocation TEXTURE = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "tunnel_bore_diamond.png");

    public ItemBoreHeadDiamond() {
        setMaxDamage(6000);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "III",
                "IDI",
                "III",
                'I', "ingotSteel",
                'D', "blockDiamond");
    }

    @Override
    public ResourceLocation getBoreTexture() {
        return TEXTURE;
    }

    @Override
    public int getHarvestLevel() {
        return 3;
    }

    @Override
    public double getDigModifier() {
        return 1d / .6d;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return ToolMaterial.DIAMOND.getEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return OreDictPlugin.isOreType("blockDiamond", repair);
    }
}
