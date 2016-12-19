/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackOutfitted;

import javax.annotation.Nonnull;

/**
 * Created by CovertJaguar on 10/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@JEIPlugin
public class RailcraftJEIPlugin extends BlankModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        if (RailcraftBlocks.TRACK_OUTFITTED.isEnabled())
            subtypeRegistry.registerSubtypeInterpreter(RailcraftBlocks.TRACK_OUTFITTED.item(), stack -> ((ItemTrackOutfitted) stack.getItem()).getSuffix(stack));
    }

}
