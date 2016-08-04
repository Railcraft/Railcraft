/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks;

import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackHighSpeed extends BlockTrackFlex {

    public BlockTrackHighSpeed() {
        super(new SpeedControllerHighSpeed());
        setResistance(3.5F);
        setHardness(0.7F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        HighSpeedTools.performBasicHighSpeedChecks(world, pos, cart);
    }
}
