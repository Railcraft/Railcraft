/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TrackSwitchBase extends TrackBaseRailcraft implements ITrackSwitch {

    private static final int SPRING_DURATION = 30;
    protected boolean mirrored;
    protected boolean switched;
    private byte sprung;
    private byte locked;
    protected List<EntityMinecart> lockingCarts = null;
    protected List<EntityMinecart> springingCarts = null;

    @Override
    public boolean canMakeSlopes() {
        return false;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public boolean isFlexibleRail() {
        return false;
    }

    @Override
    public boolean isMirrored() {
        return mirrored;
    }

    @Override
    public boolean isSwitched() {
        return !isLocked() && (switched || isSprung());
    }

    public boolean isLocked() {
        return locked > 0;
    }

    //    @Override
    //    public boolean blockActivated(EntityPlayer player)
    //    {
    //        ItemStack current = player.getCurrentEquippedItem();
    //        if(player.isSneaking() && current != null && current.getItem() instanceof ICrowbar) {
    //            int meta = tileEntity.getBlockMetadata();
    //            getWorld().setBlockMetadata(getX(), getY(), getZ(), meta == 0 ? 1 : 0);
    //            markBlockNeedsUpdate();
    //            if(current.isItemStackDamageable()) {
    //                current.damageItem(1, player);
    //            }
    //            return true;
    //        }
    //        return super.blockActivated(player);
    //    }
    @Override
    public void onBlockPlaced() {
        determineTrackMeta();
        determineMirror();
    }

    protected void determineTrackMeta() {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        int meta = tileEntity.getBlockMetadata();
        if (TrackTools.isRailBlockAt(getWorld(), x + 1, y, z) && TrackTools.isRailBlockAt(getWorld(), x - 1, y, z)) {
            if (meta != EnumTrackMeta.EAST_WEST.ordinal())
                getWorld().setBlockMetadataWithNotify(x, y, z, EnumTrackMeta.EAST_WEST.ordinal(), 3);
        } else if (TrackTools.isRailBlockAt(getWorld(), x, y, z + 1) && TrackTools.isRailBlockAt(getWorld(), x, y, z - 1)) {
            if (meta != EnumTrackMeta.NORTH_SOUTH.ordinal())
                getWorld().setBlockMetadataWithNotify(x, y, z, EnumTrackMeta.NORTH_SOUTH.ordinal(), 3);
        } else if (meta != EnumTrackMeta.NORTH_SOUTH.ordinal())
            getWorld().setBlockMetadataWithNotify(x, y, z, EnumTrackMeta.NORTH_SOUTH.ordinal(), 3);
    }

    protected void determineMirror() {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        int meta = tileEntity.getBlockMetadata();
        boolean prevValue = isMirrored();
        if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            int ii = x;
            if (TrackTools.isRailBlockAt(getWorld(), x - 1, y, z)) {
                ii--;
                mirrored = true; // West
            } else {
                ii++;
                mirrored = false; // East
            }
            if (TrackTools.isRailBlockAt(getWorld(), ii, y, z)) {
                int otherMeta = getWorld().getBlockMetadata(ii, y, z);
                if (otherMeta == EnumTrackMeta.NORTH_SOUTH.ordinal())
                    getWorld().setBlockMetadataWithNotify(ii, y, z, EnumTrackMeta.EAST_WEST.ordinal(), 3);
            }
        } else if (meta == EnumTrackMeta.EAST_WEST.ordinal())
                mirrored = TrackTools.isRailBlockAt(getWorld(), x, y, z - 1);

        if (prevValue != isMirrored())
            sendUpdateToClient();
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        if (Game.isHost(getWorld())) {
            determineTrackMeta();
            determineMirror();
        }
        super.onNeighborBlockChange(block);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("Direction", mirrored);
        data.setBoolean("Switched", switched);
        data.setByte("sprung", sprung);
        data.setByte("locked", locked);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        mirrored = data.getBoolean("Direction");
        switched = data.getBoolean("Switched");
        sprung = data.getByte("sprung");
        locked = data.getByte("locked");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(mirrored);
        data.writeBoolean(switched);
        data.writeByte(locked);
        data.writeByte(sprung);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        mirrored = data.readBoolean();
        switched = data.readBoolean();
        locked = data.readByte();
        sprung = data.readByte();
        markBlockNeedsUpdate();
    }

    @Override
    public void setSwitched(boolean switched) {
        if (this.switched != switched) {
            this.switched = switched;
            sendUpdateToClient();
        }
    }

    public boolean isSprung() {
        return sprung > 0;
    }
    
    
    /**
     * This is useful for resetting the switch track when a new train is detected.
     */
    public void reset() {
    	locked = 0;
    	sprung = 0;
    	updateEntity();
    	// Its possible updatedEntity() won't call sendUpdateToClient() even though it should
    	// so we just call it here once more no matter what.
    	sendUpdateToClient(); 
    }

    @Override
    public void updateEntity() {
        if (Game.isNotHost(getWorld()))
            return;     
        
        boolean wasLocked = locked == 0;
        
        boolean lockcarts = shouldLockSwitch(); 
        boolean springcarts = shouldSpringSwitch();
        
        if (locked > 0) {
            locked--;
            // While we're locked but don't see any carts, check if a cart is coming in the spring direction
            if (isTrackClear() && !lockcarts && springcarts)
            	locked = 0; // reset locked
        }
                
        boolean springState = sprung == 0;
        if (sprung > 0) {
            sprung--;
            if (isTrackClear() && !springcarts && lockcarts)
            	sprung = 0; // reset sprung
        }
        
        if (lockcarts) {        	
        	locked = SPRING_DURATION;
        }        
        
        if (!isLocked())
            if (springcarts)
                sprung = SPRING_DURATION;
        
        if (springState != (sprung == 0) || wasLocked != (locked == 0))
            sendUpdateToClient();
    }

    private boolean isTrackClear() {
    	return !CartTools.isMinecartOnRailAt(getWorld(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 0.3f);
    }
    
    /**
     * Returns true iff the train(s) of new cart(s) don't match any train(s) of prev cart(s).
     * If there are no previous carts, this returns <code>!newCarts.isEmpty()</code>.  
     * @param prevCarts
     * @param newCarts
     * @return
     */
//    private boolean newCartsFound(List<EntityMinecart> prevCarts, List<EntityMinecart> newCarts) {
//    	if(newCarts == null || newCarts.isEmpty())
//    		return false;
//		if(prevCarts == null || prevCarts.isEmpty())
//			return true;
//		// Both prev and new are non-empty so we must check their trains
//		Set<Train> prevTrains = new HashSet<Train>();
//		for(EntityMinecart cart : prevCarts) {
//			prevTrains.add(LinkageManager.instance().getTrain(cart)); // Assuming getTrain() never returns null
//		}
//		for(EntityMinecart cart : newCarts) {
//			if(!prevTrains.contains(cart))
//				return true; // found a new cart
//		}
//		return false; // all new carts are accounted for
//	}

	protected abstract boolean shouldLockSwitch();

    protected abstract boolean shouldSpringSwitch();

}
