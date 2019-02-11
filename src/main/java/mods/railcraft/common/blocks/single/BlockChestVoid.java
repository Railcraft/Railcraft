/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.client.render.tesr.TESRChest;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.MathTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@BlockMeta.Tile(TileChestVoid.class)
public class BlockChestVoid extends BlockChestRailcraft<TileChestVoid> {
    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        super.initializeClient();
        ClientRegistry.bindTileEntitySpecialRenderer(TileChestVoid.class, new TESRChest(this));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        for (int i = 0; i < 3; i++) {
            Vec3d start = new Vec3d(pos.getX() + MathTools.signedFloat(rand), pos.getY(), pos.getZ() + (MathTools.signedFloat(rand)));
            worldIn.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, start.x, start.y, start.z, MathTools.signedFloat(rand), MathTools.signedFloat(rand), MathTools.signedFloat(rand));
        }
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "OOO",
                "OPO",
                "OOO",
                'P', RailcraftItems.DUST.getStack(ItemDust.EnumDust.VOID),
                'O', new ItemStack(Blocks.OBSIDIAN));
    }

}
