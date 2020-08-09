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
import mods.railcraft.common.fluids.IFluidHandlerImplementor;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 8/8/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ValveLogic extends Logic implements IFluidHandlerImplementor {
    private static final int FLOW_RATE = FluidTools.BUCKET_VOLUME;
    private static final byte FILL_INCREMENT = 1;
    private final StandardTank fillTank = new StandardTank(20);

    public ValveLogic(Adapter.Tile adapter) {
        super(adapter);
        fillTank.setHidden(true);
        addSubLogic(new FluidPushLogic(adapter, StorageTankLogic.TANK_INDEX, FLOW_RATE,
                FluidPushLogic.defaultTargets(adapter)
                        .and(tile -> getLogic(StructureLogic.class)
                                .filter(logic -> logic.getMasterPos() != null)
                                .map(logic -> getY() - logic.getMasterPos().getY() <= 1).orElse(false)),
                EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST));
        addSubLogic(new FluidComparatorLogic(adapter, StorageTankLogic.TANK_INDEX));
    }

    @Override
    protected void updateServer() {
        super.updateServer();
        decrementFilling();
    }

    @Override
    public TankManager getTankManager() {
        return getLogic(StructureLogic.class)
                .flatMap(structure -> structure.getMasterLogic(FluidLogic.class))
                .map(FluidLogic::getTankManager)
                .orElse(TankManager.NIL);
    }

    private void setFilling(FluidStack resource) {
        boolean needsUpdate = fillTank.isEmpty();
        resource = resource.copy();
        resource.amount = 20;
        fillTank.fill(resource, true);

        if (needsUpdate)
            sendUpdateToClient();
    }

    private void decrementFilling() {
        if (!fillTank.isEmpty()) {
            fillTank.drain(FILL_INCREMENT, true);
            if (fillTank.isEmpty())
                sendUpdateToClient();
        }
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        TankManager tMan = getTankManager();
        if (!tMan.isEmpty()) {
            int amount = tMan.fill(resource, doFill);
            if (amount > 0 && doFill) {
                setFilling(resource.copy());
            }
            return amount;
        }
        return 0;
    }

    @Override
    public @Nullable FluidStack drain(int maxDrain, boolean doDrain) {
        TankManager tMan = getTankManager();
        if (!tMan.isEmpty()) {
            return tMan.drain(maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public @Nullable FluidStack drain(@Nullable FluidStack resource, boolean doDrain) {
        if (resource == null)
            return null;
        TankManager tMan = getTankManager();
        if (!tMan.isEmpty()) {
            return tMan.drain(resource, doDrain);
        }
        return null;
    }

    public StandardTank getFillTank() {
        return fillTank;
    }
}
