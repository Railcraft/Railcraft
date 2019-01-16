/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Created by CovertJaguar on 2/27/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackKitJunction extends TrackKitRailcraft {

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.JUNCTION;
    }

    @Override
    public EnumRailDirection getRailDirection(IBlockState state, @Nullable EntityMinecart cart) {
        if (cart == null) {
            return EnumRailDirection.NORTH_SOUTH;
        }
        float yaw = cart.prevRotationYaw;
        yaw = yaw % 180;
        while (yaw < 0) {
            yaw += 180;
        }
        if ((yaw >= 45) && (yaw <= 135))
            return EnumRailDirection.NORTH_SOUTH;
        return EnumRailDirection.EAST_WEST;
    }

    @Override
    public List<ItemStack> getDrops(int fortune) {
        return Collections.emptyList();
    }
}
