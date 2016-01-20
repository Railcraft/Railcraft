/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.blocks.signals.MaterialStructure;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

public abstract class BlockPostBase extends Block {

    private static final float SIZE = 0.15f;
    private static final float SELECT = 4F / 16F;
    private final int renderType;

    public BlockPostBase(int renderType) {
        super(new MaterialStructure());
        this.renderType = renderType;
        setStepSound(RailcraftSound.getInstance());
        setResistance(15);
        setHardness(3);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public boolean isPlatform(int meta) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return meta;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        if (isPlatform(worldIn.getBlockMetadata(pos)))
            setBlockBounds(0.0F, 0.0F, 0.0F, 1F, 1.0F, 1F);
        else
            setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 1.0F, 0.8F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (isPlatform(worldIn.getBlockMetadata(pos)))
            return AxisAlignedBB.fromBounds(x, y, z, x + 1, y + 1, z + 1);
        if (!worldIn.isAirBlock(pos.down())
                && !(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockPostBase)
                && !TrackTools.isRailBlockAt(worldIn, pos.up()))
            return AxisAlignedBB.fromBounds(x + SIZE, y, z + SIZE, x + 1 - SIZE, y + 1.5, z + 1 - SIZE);
        return AxisAlignedBB.fromBounds(x + SIZE, y, z + SIZE, x + 1 - SIZE, y + 1, z + 1 - SIZE);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (isPlatform(worldIn.getBlockMetadata(pos)))
            return AxisAlignedBB.fromBounds(x, y, z, x + 1, y + 1, z + 1);
        return AxisAlignedBB.fromBounds(x + SELECT, y, z + SELECT, x + 1 - SELECT, y + 1.0F, z + 1 - SELECT);
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == DOWN || side == UP;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return renderType;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }
}
