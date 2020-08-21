/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.fluids.FluidTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Created by CovertJaguar on 8/18/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BucketInteractionLogic extends Logic {
    public BucketInteractionLogic(Adapter adapter) {
        super(adapter);
    }

    @Override
    public boolean interact(EntityPlayer player, EnumHand hand) {
        return getLogic(IFluidHandler.class)
                .map(tank -> FluidTools.interactWithFluidHandler(player, hand, tank))
                .orElse(false) || super.interact(player, hand);
    }

}
