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
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.common.carts.LinkageManager;
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
    protected Set<EntityMinecart> lockingCarts = new HashSet<EntityMinecart>();
    protected Set<EntityMinecart> springingCarts = new HashSet<EntityMinecart>();
    
    // temporary variables for loading from NBT
    private boolean justLoaded = true;
    private Set<UUID> lockingCartUUIDs = null;
    private Set<UUID> springingCartUUIDs = null;

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
    
    /**
     * A utility method for writing out UUID's to NBT 
     */
    private void writeUUIDToNBT(String key, UUID uuid, NBTTagCompound data) {
    	data.setLong(key+"High", uuid.getMostSignificantBits());
		data.setLong(key+"Low", uuid.getLeastSignificantBits());
    }
        
    /**
     * A utility method for reading in UUID's from NBT
     */
    private UUID readUUIDFromNBT(String key, NBTTagCompound data) {
    	 if (data.hasKey(key + "High"))
             return new UUID(data.getLong(key + "High"), data.getLong(key + "Low"));
    	 return null;
    }
    
    private void writeCartsToNBT(String key, Set<EntityMinecart> carts, NBTTagCompound data) {
    	data.setByte(key+ "Size", (byte)carts.size());
    	int i = 0;
    	for(EntityMinecart cart : carts)
    		writeUUIDToNBT(key + i++, cart.getPersistentID(), data);
    }
    
    private Set<UUID> readCartsFromNBT(String key, NBTTagCompound data) {
    	Set<UUID> cartUUIDs = new HashSet<UUID>();
    	String sizeKey = key + "Size"; 
    	if(data.hasKey(sizeKey)) {
    		byte size = data.getByte(sizeKey);
    		for(int i=0; i<size; i++) {
    			UUID id = readUUIDFromNBT(key + i, data);
    			if(id != null) 
    				cartUUIDs.add(id);
    		}
    	}
    	return cartUUIDs;
    }
    
    private Set<EntityMinecart> lookupCartsFromUUIDs(Set<UUID> cartUUIDs) {
    	Set<EntityMinecart> carts = new HashSet<EntityMinecart>();
    	if(cartUUIDs != null) {
	    	for(UUID cartUUID : cartUUIDs) {
	    		EntityMinecart cart = LinkageManager.instance().getCartFromUUID(cartUUID);
	    		if(cart != null)
	    			carts.add(cart);
	    	}
    	}
    	return carts;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("Direction", mirrored);
        data.setBoolean("Switched", switched);
        data.setByte("sprung", sprung);
        data.setByte("locked", locked);
        writeCartsToNBT("springingCarts", springingCarts, data);
        writeCartsToNBT("lockingCarts", lockingCarts, data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        mirrored = data.getBoolean("Direction");
        switched = data.getBoolean("Switched");
        sprung = data.getByte("sprung");
        locked = data.getByte("locked");        
        springingCartUUIDs = readCartsFromNBT("springingCarts", data);
        lockingCartUUIDs = readCartsFromNBT("lockingCarts", data);
        
        justLoaded = true;
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
    
    
    
	@Override
	public void updateEntity() {
		if (Game.isNotHost(getWorld()))
			return;

		// Finish loading cart sets from NBT
		if(justLoaded) {
			springingCarts = lookupCartsFromUUIDs(springingCartUUIDs);
			lockingCarts = lookupCartsFromUUIDs(lockingCartUUIDs);
			justLoaded = false;
		}
		
		boolean wasLocked = locked == 0;
		boolean springState = sprung == 0;

		// Adding carts we just found to lockingCarts
		List<EntityMinecart> lockcarts = getCartsAtLockEntrance();
		for (EntityMinecart cart : lockcarts) {
			// Carts could follow a loop track through the switch and back to
			// the other entrance so we remove it from the other list
			if (springingCarts.contains(cart))
				springingCarts.remove(cart);
			lockingCarts.add(cart);
		}

		// Same as last section except reversed
		List<EntityMinecart> springcarts = getCartsAtSpringEntrance();
		for (EntityMinecart cart : springcarts) {
			if (lockingCarts.contains(cart))
				lockingCarts.remove(cart);
			springingCarts.add(cart);
		}

		// We only set sprung/locked when a cart enters our track, this is
		// mainly for visual purposes as the subclass's getBasicRailMetadata()
		// determines which direction the carts actually take.
		List<EntityMinecart> cartsOnTrack = CartTools.getMinecartsAt(
				getWorld(), tileEntity.xCoord, tileEntity.yCoord,
				tileEntity.zCoord, 0.3f);
		for (EntityMinecart cartOnTrack : cartsOnTrack) {
			if (springingCarts.contains(cartOnTrack)) {
				sprung = SPRING_DURATION;
				locked = 0;
				break;
			} else if (lockingCarts.contains(cartOnTrack)) {
				locked = SPRING_DURATION;
				sprung = 0;
				break;
			}
		}

		if (locked > 0)
			locked--;
		if (sprung > 0)
			sprung--;

		if (locked == 0 && sprung == 0) {
			lockingCarts.clear(); // Clear out our sets so we don't keep
			springingCarts.clear(); // these carts forever
		}

		if (springState != (sprung == 0) || wasLocked != (locked == 0))
			sendUpdateToClient();
	}

	protected abstract List<EntityMinecart> getCartsAtLockEntrance();

    protected abstract List<EntityMinecart> getCartsAtSpringEntrance();

}
