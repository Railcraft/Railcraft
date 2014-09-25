/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import net.minecraft.util.IIcon;
import mods.railcraft.api.tracks.TrackInstanceBase;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackDefault extends TrackInstanceBase {

    @Override
    public TrackSpec getTrackSpec() {
        return TrackRegistry.getTrackSpec("Railcraft:default");
    }

    @Override
    public IIcon getIcon() {
        int meta = tileEntity.getBlockMetadata();
        return Blocks.rail.getIcon(0, meta);
    }

    @Override
    public boolean isFlexibleRail() {
        return true;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (Game.isHost(getWorld())) {
            getWorld().setBlock(getX(), getY(), getZ(), Blocks.rail, tileEntity.getBlockMetadata(), 3);
        }
    }
}
