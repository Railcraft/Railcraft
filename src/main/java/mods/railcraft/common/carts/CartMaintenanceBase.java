/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartMaintenanceBase extends CartContainerBase {

    protected static final double DRAG_FACTOR = 0.9;
    protected static final float MAX_SPEED = 0.1f;
    private static final int BLINK_DURATION = 3;
    private static final int DATA_ID_BLINK = 25;

    protected CartMaintenanceBase(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(DATA_ID_BLINK, (byte) 0);
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    protected void blink() {
        dataWatcher.updateObject(DATA_ID_BLINK, (byte) BLINK_DURATION);
    }

    protected void setBlink(byte blink) {
        dataWatcher.updateObject(DATA_ID_BLINK, blink);
    }

    protected byte getBlink() {
        return dataWatcher.getWatchableObjectByte(DATA_ID_BLINK);
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
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getCartItem());
        return items;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public double getDrag() {
        return EntityCartTrackRelayer.DRAG_FACTOR;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return MAX_SPEED;
    }

    public boolean isBlinking() {
        return dataWatcher.getWatchableObjectByte(DATA_ID_BLINK) > 0;
    }

    protected boolean placeNewTrack(BlockPos pos, int slotStock, BlockRailBase.EnumRailDirection trackShape) {
        ItemStack trackStock = getStackInSlot(slotStock);
        if (trackStock != null)
            if (TrackToolsAPI.placeRailAt(trackStock, getEntityWorld(), pos)) {
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
            CartTools.transferHelper.offerOrDropItem(this, stack);
        }
        BlockRailBase.EnumRailDirection trackShape = TrackTools.getTrackDirectionRaw(state);
        getEntityWorld().setBlockToAir(pos);
        return trackShape;
    }

}
