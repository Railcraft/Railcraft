/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBaseMaintenance extends CartBaseContainer {

    private static final DataParameter<Byte> BLINK = DataManagerPlugin.create(MethodHandles.lookup().lookupClass(), DataSerializers.BYTE);
    protected static final double DRAG_FACTOR = 0.9;
    protected static final float MAX_SPEED = 0.1f;
    private static final int BLINK_DURATION = 3;
    private static final int DATA_ID_BLINK = 25;

    protected CartBaseMaintenance(World world) {
        super(world);
    }

    protected CartBaseMaintenance(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(BLINK, (byte) 0);
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    protected void blink() {
        dataManager.set(BLINK, (byte) BLINK_DURATION);
    }

    protected void setBlink(byte blink) {
        dataManager.set(BLINK, blink);
    }

    protected byte getBlink() {
        return dataManager.get(BLINK);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isClient(worldObj))
            return;

        if (isBlinking())
            setBlink((byte) (getBlink() - 1));
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    protected void applyDrag() {
        super.applyDrag();
        this.motionX *= DRAG_FACTOR;
        this.motionZ *= DRAG_FACTOR;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return MAX_SPEED;
    }

    public boolean isBlinking() {
        return dataManager.get(BLINK) > 0;
    }

    protected boolean placeNewTrack(BlockPos pos, int slotStock, BlockRailBase.EnumRailDirection trackShape) {
        ItemStack trackStock = getStackInSlot(slotStock);
        if (!InvTools.isEmpty(trackStock))
            if (TrackToolsAPI.placeRailAt(trackStock, (WorldServer) getEntityWorld(), pos)) {
                decrStackSize(slotStock, 1);
                blink();
                return true;
            }
        return false;
    }

    protected BlockRailBase.EnumRailDirection removeOldTrack(BlockPos pos, Block block) {
        IBlockState state = WorldPlugin.getBlockState(getEntityWorld(), pos);
        List<ItemStack> drops = block.getDrops(worldObj, pos, state, 0);

        for (ItemStack stack : drops) {
            CartToolsAPI.transferHelper.offerOrDropItem(this, stack);
        }
        BlockRailBase.EnumRailDirection trackShape = TrackTools.getTrackDirectionRaw(state);
        getEntityWorld().setBlockToAir(pos);
        return trackShape;
    }

}
