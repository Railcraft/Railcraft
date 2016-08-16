/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.api.tracks.ITrackType;
import mods.railcraft.common.blocks.tracks.IRailcraftTrack;
import mods.railcraft.common.blocks.tracks.TrackConstants;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockRail;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackFlex extends BlockRail implements IRailcraftTrack {

    public ITrackType trackType;

    public BlockTrackFlex(ITrackType trackType) {
        setResistance(trackType.getResistance());
        setHardness(TrackConstants.HARDNESS);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        this.trackType = trackType;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        trackType.onMinecartPass(world, cart, pos, null);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (Game.isClient(world))
            return;

        trackType.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return trackType.getMaxSpeed(world, cart, pos);
    }

    @Override
    public ITrackType getTrackType(IBlockAccess world, BlockPos pos) {
        return trackType;
    }
}
