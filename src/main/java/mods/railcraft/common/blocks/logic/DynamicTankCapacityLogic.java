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

    public DynamicTankCapacityLogic(Adapter adapter, int tankIndex, int dataIndex) {
        super(adapter);
        this.tankIndex = tankIndex;
        this.dataIndex = dataIndex;
    }

    @Override
    public void onStructureChanged(Object[] data) {
        super.onStructureChanged(data);
        getLogic(TankLogic.class).ifPresent(logic -> logic.getTankManager().setCapacity(tankIndex, (Integer) data[dataIndex]));
    }
}
