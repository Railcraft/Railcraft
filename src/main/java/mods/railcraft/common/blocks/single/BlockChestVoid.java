/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.BlockMetaTile;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@BlockMetaTile(TileChestVoid.class)
public class BlockChestVoid extends BlockChestRailcraft<TileChestVoid> {

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        BlockPos start = new BlockPos(pos.getX() - 10 + rand.nextInt(20), pos.getY(), pos.getZ() - 10 + rand.nextInt(20));
        spawnVoidFaceParticles(worldIn, start);
    }

    @SideOnly(Side.CLIENT)
    private void spawnVoidFaceParticles(World worldIn, BlockPos pos) {
        Random random = worldIn.rand;
        double pixel = 0.0625D;

        IBlockState state = WorldPlugin.getBlockState(worldIn, pos);

        for (EnumFacing facing : EnumFacing.VALUES) {
            if (!state.shouldSideBeRendered(worldIn, pos, facing)) continue;

            double px = pos.getX();
            double py = pos.getY();
            double pz = pos.getZ();

            if (facing.getAxis() == EnumFacing.Axis.X)
                px += pixel * facing.getXOffset() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 : 0.0);
            else
                px += random.nextFloat();

            if (facing.getAxis() == EnumFacing.Axis.Y)
                py += pixel * facing.getYOffset() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 : 0.0);
            else
                py += random.nextFloat();

            if (facing.getAxis() == EnumFacing.Axis.Z)
                pz += pixel * facing.getZOffset() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 : 0.0);
            else
                pz += random.nextFloat();

            worldIn.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "OOO",
                "OPO",
                "OOO",
                'P', RailcraftItems.DUST.getStack(ItemDust.EnumDust.VOID),
                'O', new ItemStack(Blocks.OBSIDIAN));
    }

}
