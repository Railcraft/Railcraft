/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.anvil;

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockRCAnvil extends BlockAnvil implements IRailcraftObject {


    public BlockRCAnvil() {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHardness(5.0F);
        setSoundType(SoundType.ANVIL);
        setResistance(2000.0F);
    }

    @Override
    public Object getRecipeObject(@Nullable IVariantEnum variant) {
        return this;
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "BBB",
                " I ",
                "III",
                'B', "blockSteel",
                'I', "ingotSteel");
    }

    @Override
    public void initializeDefinintion() {
        ForestryPlugin.addBackpackItem("builder", this);

        HarvestPlugin.setBlockHarvestLevel("pickaxe", 2, this);
    }

    @Override
    public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, IBlockState state, @Nonnull EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
            return true;
        else {
            GuiHandler.openGui(EnumGui.ANVIL, playerIn, worldIn, pos);
            return true;
        }
    }

}
