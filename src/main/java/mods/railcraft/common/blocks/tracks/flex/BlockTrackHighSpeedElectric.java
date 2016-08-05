/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.common.blocks.tracks.HighSpeedTools;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackHighSpeedElectric extends BlockTrackElectric {

    public BlockTrackHighSpeedElectric() {
        super(new SpeedControllerHighSpeed());
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        HighSpeedTools.performBasicHighSpeedChecks(world, pos, cart);
    }
}
