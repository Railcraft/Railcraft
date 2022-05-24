/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.fluids.FluidTools.ProcessType;
import mods.railcraft.common.util.steam.ISteamUser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import static mods.railcraft.common.blocks.structures.BlockBoilerFirebox.BURNING;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoilerFirebox extends TileBoiler {

    public static final int SLOT_INPUT_FLUID = 0;
    public static final int SLOT_PROCESS_FLUID = 1;
    public static final int SLOT_OUTPUT_FLUID = 2;

    protected TileBoilerFirebox() {
        getLogic(StructureLogic.class).ifPresent(logic -> {
            logic.setKernel(new BoilerLogic(Logic.Adapter.of(this))
                    .addLogic(new ExploderLogic(Logic.Adapter.of(this)) {
                        @Override
                        protected void boom() {
                            world.createExplosion(null, getX(), getY(), getZ(), 5f + 0.1f * boilerData().numTanks, true);
                        }
                    })
            );
            Logic.Adapter adapter = Logic.Adapter.of(this);
            logic.addLogic(new FluidPushLogic(adapter, TANK_STEAM, TRANSFER_RATE, ISteamUser.FILTER, EnumFacing.VALUES));
            logic.getKernel(Logic.class).ifPresent(sucker ->
                    sucker.addLogic(new BucketProcessorLogic(adapter, SLOT_INPUT_FLUID, ProcessType.DRAIN_THEN_FILL)));
        });
    }

    public boolean isBurning() {
        return getLogic(BoilerLogic.class).map(BoilerLogic::isBurning).orElse(false);
    }

    @Override
    public final boolean needsFuel() {
        return getLogic(BoilerLogic.class).map(BoilerLogic::needsFuel).orElse(false);
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return base.withProperty(BURNING, hasFlames());
    }
}
