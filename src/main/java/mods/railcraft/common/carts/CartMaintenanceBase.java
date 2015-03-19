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
import mods.railcraft.api.tracks.RailTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.item.ItemStack;
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

    public CartMaintenanceBase(World world) {
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
        dataWatcher.updateObject(DATA_ID_BLINK, (byte) blink);
    }

    protected byte getBlink() {
        return dataWatcher.getWatchableObjectByte(DATA_ID_BLINK);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isNotHost(worldObj))
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

    protected boolean placeNewTrack(int x, int y, int z, int slotStock, int meta) {
        ItemStack trackStock = getStackInSlot(slotStock);
        if (trackStock != null)
            if (RailTools.placeRailAt(trackStock, worldObj, x, y, z)) {
                worldObj.setBlockMetadataWithNotify(x, y, z, meta, 0x02);
                Block block = worldObj.getBlock(x, y, z);
                block.onNeighborBlockChange(worldObj, x, y, z, block);
                worldObj.markBlockForUpdate(x, y, z);
                decrStackSize(slotStock, 1);
                blink();
                return true;
            }
        return false;
    }

    protected int removeOldTrack(int x, int y, int z, Block block) {
        List<ItemStack> drops = block.getDrops(worldObj, x, y, z, 0, 0);

        for (ItemStack stack : drops) {
            CartTools.offerOrDropItem(this, stack);
        }
        int meta = worldObj.getBlockMetadata(x, y, z);
        if (((BlockRailBase) block).isPowered())
            meta = meta & 7;
        worldObj.setBlockToAir(x, y, z);
        return meta;
    }

}
