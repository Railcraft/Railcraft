/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IShearable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemSteelShears extends ItemShears implements IRailcraftObject {

    public ItemSteelShears() {
        setMaxDamage(500);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public void initializeDefinintion() {
        LootPlugin.addLoot(RailcraftItems.shearsSteel, 1, 1, LootPlugin.Type.TOOL);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this), false,
                " I ",
                " I ",
                " S ",
                'I', "ingotSteel",
                'S', "stickWood");
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof IShearable)
            return 15;
        return super.getStrVsBlock(stack, state);
    }

}
