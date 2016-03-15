/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.lantern;

import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import static net.minecraft.util.EnumParticleTypes.FLAME;
import static net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL;

public class BlockLantern extends Block {

    private static final float SELECT = 2 * 0.0625f;
    public static boolean useCandleIcon = false;
    static BlockLantern stone;
    static BlockLantern metal;
    public final LanternProxy proxy;
    private final int renderId;

    public BlockLantern(int renderId, LanternProxy proxy) {
        super(Material.redstoneLight);
        this.renderId = renderId;
        this.setStepSound(Block.soundTypeStone);
        this.proxy = proxy;
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHardness(5);
        setResistance(15);
//        useNeighborBrightness[id] = false;
        opaque = false;
        lightOpacity = 0;
        setLightLevel(0.9375F);
        setHarvestLevel("pickaxe", 0);
    }

    public static BlockLantern getBlockStone() {
        return stone;
    }

    public static BlockLantern getBlockMetal() {
        return metal;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        int meta = world.getBlockMetadata(pos);
        return new ItemStack(this, 1, meta);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (LanternInfo lantern : proxy.getCreativeList()) {
            if (lantern.isEnabled())
                list.add(lantern.getItem());
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        return AxisAlignedBB.fromBounds(pos.getX() + SELECT, pos.getY() + 2 * 0.0625f, pos.getZ() + SELECT, pos.getX() + 1 - SELECT, pos.getY() + 1.0F - 1 * 0.0625f, pos.getZ() + 1 - SELECT);
    }

    @Override
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        double dx = pos.getX() + 0.5F;
        double dy = pos.getY() + 0.65F;
        double dz = pos.getZ() + 0.5F;

        worldIn.spawnParticle(SMOKE_NORMAL, dx, dy, dz, 0.0D, 0.0D, 0.0D);
        worldIn.spawnParticle(FLAME, dx, dy, dz, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return meta;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 1;
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, this, target, effectRenderer, null);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
        return ParticleHelper.addDestroyEffects(world, this, x, y, z, meta, effectRenderer, null);
    }
}
