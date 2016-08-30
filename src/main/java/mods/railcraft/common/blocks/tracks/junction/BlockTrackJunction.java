/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.junction;

import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.BlockTrackStateless;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 8/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackJunction extends BlockTrackStateless {

    public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.create("shape",
            BlockRailBase.EnumRailDirection.class, EnumRailDirection.NORTH_SOUTH);

    public BlockTrackJunction(TrackType trackType) {
        super(trackType);
    }

    @Override
    public IProperty<EnumRailDirection> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
        if (cart == null) {
            return EnumRailDirection.NORTH_SOUTH;
        }
        float yaw = cart.prevRotationYaw;
        yaw = yaw % 180;
        while (yaw < 0) {
            yaw += 180;
        }
        if ((yaw >= 45) && (yaw <= 135)) {
            return EnumRailDirection.NORTH_SOUTH;
        }
        return EnumRailDirection.EAST_WEST;
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        return false;
    }
}
