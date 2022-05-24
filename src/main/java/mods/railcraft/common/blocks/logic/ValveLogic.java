/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.blocks.structures.TileTank;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.IFluidHandlerImplementor;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.misc.Optionals;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

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
        addLogic(new FluidPushLogic(adapter, StorageTankLogic.TANK_INDEX, FLOW_RATE,
                FluidPushLogic.defaultTargets(adapter)
                        .or(tile -> {
                            if (tile instanceof TileTank) {
                                return Optionals.notEqualOrEmpty(((TileTank) tile).getLogic(StructureLogic.class).map(StructureLogic::getMasterPos),
                                        getLogic(StructureLogic.class).map(StructureLogic::getMasterPos));
                            }
                            return false;
                        })
                        .and(tile -> canDrain()),
                EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST));
        addLogic(new FluidComparatorLogic(adapter, StorageTankLogic.TANK_INDEX));
        addLogic(new BucketInteractionLogic(adapter));
    }

    @Override
    protected void updateServer() {
        super.updateServer();
        decrementFilling();
    }

    @Override
    public TankManager getTankManager() {
        return getLogic(FluidLogic.class)
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
        if (!canFill())
            return 0;
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
        if (!canDrain())
            return null;
        TankManager tMan = getTankManager();
        if (!tMan.isEmpty()) {
            return tMan.drain(maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public @Nullable FluidStack drain(@Nullable FluidStack resource, boolean doDrain) {
        if (resource == null || !canDrain())
            return null;
        TankManager tMan = getTankManager();
        if (!tMan.isEmpty()) {
            return tMan.drain(resource, doDrain);
        }
        return null;
    }

    public boolean canFill() {
        return getLogic(StructureLogic.class)
                .filter(logic -> logic.getMasterPos() != null)
                .map(logic -> getPos().getY() - logic.getMasterPos().getY() > 0).orElse(false);
    }

    public boolean canDrain() {
        return getLogic(StructureLogic.class)
                .filter(logic -> logic.getMasterPos() != null)
                .map(logic -> getPos().getY() - logic.getMasterPos().getY() <= 1).orElse(false);
    }

    public StandardTank getFillTank() {
        return fillTank;
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeFluidStack(fillTank.getFluid());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        fillTank.setFluid(data.readFluidStack());
    }
}
