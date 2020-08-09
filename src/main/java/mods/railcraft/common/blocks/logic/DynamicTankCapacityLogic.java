/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

/**
 * Created by CovertJaguar on 7/28/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DynamicTankCapacityLogic extends Logic {
    private final int tankIndex;
    private final int dataIndex;
    private final int multiplier;

    public DynamicTankCapacityLogic(Adapter adapter, int tankIndex, int dataIndex) {
        this(adapter, tankIndex, dataIndex, 1);
    }

    public DynamicTankCapacityLogic(Adapter adapter, int tankIndex, int dataIndex, int multiplier) {
        super(adapter);
        this.tankIndex = tankIndex;
        this.dataIndex = dataIndex;
        this.multiplier = multiplier;
    }

    @Override
    public void onStructureChanged(boolean isComplete, boolean isMaster, Object[] data) {
        super.onStructureChanged(isComplete, isMaster, data);
        if (isComplete)
            getLogic(FluidLogic.class).ifPresent(logic -> logic.getTankManager().setCapacity(tankIndex, ((Integer) data[dataIndex]) * multiplier));
    }
}
