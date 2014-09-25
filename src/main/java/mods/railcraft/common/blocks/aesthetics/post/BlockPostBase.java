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
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.signals.*;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.CreativePlugin;

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
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        if (isPlatform(world.getBlockMetadata(x, y, z)))
            setBlockBounds(0.0F, 0.0F, 0.0F, 1F, 1.0F, 1F);
        else
            setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 1.0F, 0.8F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (isPlatform(world.getBlockMetadata(x, y, z)))
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        if (!world.isAirBlock(x, y - 1, z)
                && !(world.getBlock(x, y - 1, z) instanceof BlockPostBase)
                && !TrackTools.isRailBlockAt(world, x, y + 1, z))
            return AxisAlignedBB.getBoundingBox(x + SIZE, y, z + SIZE, x + 1 - SIZE, y + 1.5, z + 1 - SIZE);
        return AxisAlignedBB.getBoundingBox(x + SIZE, y, z + SIZE, x + 1 - SIZE, y + 1, z + 1 - SIZE);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        if (isPlatform(world.getBlockMetadata(x, y, z)))
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        return AxisAlignedBB.getBoundingBox(x + SELECT, y, z + SELECT, x + 1 - SELECT, y + 1.0F, z + 1 - SELECT);
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.DOWN || side == ForgeDirection.UP;
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
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

}
