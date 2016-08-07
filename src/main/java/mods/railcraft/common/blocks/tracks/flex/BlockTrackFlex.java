/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.tracks.TrackConstants;
import mods.railcraft.common.blocks.tracks.TrackTypes;
import net.minecraft.block.BlockRail;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackFlex extends BlockRail implements IRailcraftBlock {

    public TrackTypes trackType;

    public BlockTrackFlex(TrackTypes trackType) {
        setResistance(trackType.getResistance());
        setHardness(TrackConstants.HARDNESS);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        this.trackType = trackType;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        super.getSubBlocks(itemIn, tab, list);
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        trackType.speedController.onMinecartPass(world, cart, pos, null);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return trackType.speedController.getMaxSpeed(world, cart, pos);
    }
}
