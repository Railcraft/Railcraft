/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.FluidTools.ProcessState;
import mods.railcraft.common.fluids.FluidTools.ProcessType;
import mods.railcraft.common.fluids.IFluidHandlerImplementor;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Handles the logic in processing fluid containers.
 *
 * Created by CovertJaguar on 9/4/2021 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BucketProcessorLogic extends Logic {
    private final int slotZero;
    private final ProcessType processType;
    private ProcessState state = ProcessState.RESET;

    public BucketProcessorLogic(Adapter adapter, int slotZero, ProcessType processType) {
        super(adapter);
        this.slotZero = slotZero;
        this.processType = processType;
    }

    @Override
    protected void updateServer() {
        super.updateServer();

        clock().onInterval(FluidTools.BUCKET_FILL_TIME, () -> {

            getLogic(IInventoryImplementor.class).map(inv -> InventoryMapper.make(inv, slotZero, 3)).ifPresent(inv -> {

                inv.streamSlots().forEach(slot -> {
                    if (slot.hasStack() && !FluidItemHelper.isContainer(slot.getStack())) {
                        slot.drop(theWorldAsserted(), getPos());
                    }
                });

                getLogic(IFluidHandlerImplementor.class)
                        .map(IFluidHandlerImplementor::getTankManager)
                        .ifPresent(tankManager -> state = FluidTools.processContainer(inv, tankManager, processType, state));
            });
        });
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTPlugin.writeEnumOrdinal(data, "bucketState", state);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        state = NBTPlugin.readEnumOrdinal(data, "bucketState", ProcessState.VALUES, ProcessState.RESET);
    }
}
