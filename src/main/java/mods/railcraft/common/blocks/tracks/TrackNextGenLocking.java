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
import java.lang.reflect.Constructor;
import java.util.UUID;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.events.CartLockdownEvent;
import mods.railcraft.api.tracks.ITrackLockdown;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.blocks.tracks.locking.BoardingLockingProfile;
import mods.railcraft.common.blocks.tracks.locking.LockdownLockingProfile;
import mods.railcraft.common.blocks.tracks.locking.HoldingLockingProfile;
import mods.railcraft.common.blocks.tracks.locking.LockingProfile;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TrackNextGenLocking extends TrackBaseRailcraft implements ITrackLockdown, ITrackPowered {

    public enum LockType {

        CART, TRAIN
    };

    public enum LockingProfileType {

        LOCKDOWN(LockdownLockingProfile.class, LockType.CART, "lockdown"),
        LOCKDOWN_TRAIN(LockdownLockingProfile.class, LockType.TRAIN, "lockdown.train"),
        HOLDING(HoldingLockingProfile.class, LockType.CART, "holding"),
        HOLDING_TRAIN(HoldingLockingProfile.class, LockType.TRAIN, "holding.train"),
        BOARDING_A(BoardingLockingProfile.class, LockType.CART, "boarding"),
        BOARDING_B(BoardingLockingProfile.class, LockType.CART, "boarding"),
        BOARDING_A_TRAIN(BoardingLockingProfile.class, LockType.TRAIN, "boarding.train"),
        BOARDING_B_TRAIN(BoardingLockingProfile.class, LockType.TRAIN, "boarding.train");
        public static final LockingProfileType[] VALUES = values();
        private final Class<? extends LockingProfile> profileClass;
        public final LockType lockType;
        public final String tag;

        private LockingProfileType(Class<? extends LockingProfile> profileClass, LockType lockType, String tag) {
            this.profileClass = profileClass;
            this.lockType = lockType;
            this.tag = tag;
        }

        public LockingProfileType next() {
            LockingProfileType next = VALUES[(ordinal() + 1) % VALUES.length];
            return next;
        }

        public LockingProfileType previous() {
            LockingProfileType next = VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
            return next;
        }

        public LockingProfile create(TrackNextGenLocking track) {
            try {
                Constructor<? extends LockingProfile> con = profileClass.getConstructor(TrackNextGenLocking.class);
                return con.newInstance(track);
            } catch (Throwable ex) {
                Game.logThrowable("Failed to create Locking Profile!", 10, ex);
                throw new RuntimeException(ex);
            }
        }

        public static LockingProfileType fromOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal >= VALUES.length)
                return LOCKDOWN;
            return VALUES[ordinal];
        }

    }

    public static double START_BOOST = 0.04;
    public static double BOOST_FACTOR = 0.06;
    protected boolean powered = false;
    protected int prevDelay = 0;
    protected int delay = 0;
    protected byte reset = 0;
    private LockingProfileType profile = LockingProfileType.LOCKDOWN;
    private LockingProfile profileInstance = profile.create(this);
    private EntityMinecart lockedCart;
    private Train currentTrain;
    private int justLoaded = 20;
    private UUID uuid;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.LOCKING;
    }

    @Override
    public IIcon getIcon() {
        if (isPowered() || delay > 0)
            return getIcon(profile.ordinal() * 2);
        return getIcon(profile.ordinal() * 2 + 1);
    }

    public LockingProfileType getProfileType() {
        return profile;
    }

    public void setProfile(LockingProfileType type) {
        profile = type;
        profileInstance = profile.create(this);
        if (tileEntity != null && Game.isHost(getWorld()))
            sendUpdateToClient();
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (Game.isHost(getWorld())) {
            if (justLoaded > 0)
                justLoaded--;
            if (getCurrentCart() != null && getCurrentCart().isDead)
                setCurrentCartAndTrain(null);
            if (isPowered())
                delay = getDelayTime();
            else if (delay > 0) {
                delay--;
                if (delay == 0)
                    setCurrentCartAndTrain(null);
            }
            if (reset > 0)
                reset--;
            if (prevDelay == 0 ^ delay == 0)
                sendUpdateToClient();
            prevDelay = delay;
        }
    }

    @Override
    public void onNeighborBlockChange(Block blockChanged) {
        super.onNeighborBlockChange(blockChanged);
        if (isPowered())
            delay = getDelayTime();
    }

    public EntityMinecart getCurrentCart() {
        return lockedCart;
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, getX(), getY(), getZ())) {
                LockingProfileType p;
                if (player.isSneaking())
                    p = profile.previous();
                else
                    p = profile.next();
                if (Game.isHost(getWorld()))
                    setProfile(p);
                else
                    ChatPlugin.sendLocalizedChat(player, "railcraft.gui.track.locking.change", LocalizationPlugin.translate("railcraft.gui.track.locking.mode." + p.tag));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockRemoved() {
        super.onBlockRemoved();
        setCurrentCartAndTrain(null);
    }

    private UUID getUUID() {
        if (uuid == null)
            uuid = UUID.randomUUID();
        return uuid;
    }

    protected void setCurrentCartAndTrain(EntityMinecart cart) {
        if (lockedCart != cart && lockedCart != null) {
            Train train = LinkageManager.instance().getTrain(lockedCart);
            train.removeLockingTrack(getUUID());
        }
        lockedCart = cart;
        if (cart == null)
            currentTrain = null;
        else
            currentTrain = LinkageManager.instance().getTrain(cart);
    }

    protected void lockCart(EntityMinecart cart) {
        if (cart != null) {
            Train train = LinkageManager.instance().getTrain(cart);
            train.addLockingTrack(getUUID());
            MinecraftForge.EVENT_BUS.post(new CartLockdownEvent.Lock(cart, getX(), getY(), getZ()));
            profileInstance.onLock(cart);
        }
    }

    protected void releaseCart(EntityMinecart cart) {
        if (cart != null) {
            Train train = LinkageManager.instance().getTrain(cart);
            train.removeLockingTrack(getUUID());
            MinecraftForge.EVENT_BUS.post(new CartLockdownEvent.Release(cart, getX(), getY(), getZ()));
            profileInstance.onRelease(cart);
        }
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        checkIfShouldChangeCart(cart);
        if (isPowered() || delay > 0)
            releaseCart(cart);
        else {
            if (getCurrentCart() == null)
                setCurrentCartAndTrain(cart);
            if (getCurrentCart() == cart) {
                lockCart(cart);
                cart.motionX = 0.0D;
                cart.motionZ = 0.0D;
                int meta = tileEntity.getBlockMetadata();
                if (meta == 0 || meta == 4 || meta == 5)
                    cart.posZ = tileEntity.zCoord + 0.5D;
                else
                    cart.posX = tileEntity.xCoord + 0.5D;
            }
        }
    }

    protected void checkIfShouldChangeCart(EntityMinecart cart) {
        int r = reset;
        reset = 20;

        if (delay <= 0) {
            justLoaded = 0;
            return;
        }

        if (justLoaded > 0) {
            setCurrentCartAndTrain(cart);
            delay = getDelayTime();
            justLoaded = 0;
            return;
        }

        if (r <= 0
                || (profile.lockType == LockType.CART && lockedCart != cart)
                || (profile.lockType == LockType.TRAIN && currentTrain != LinkageManager.instance().getTrain(cart))) {
            delay = 0;
            setCurrentCartAndTrain(cart);
            return;
        }
        if (profile.lockType == LockType.TRAIN)
            delay = getDelayTime();
    }

    protected int getDelayTime() {
        if (profile.lockType == LockType.TRAIN)
            return TrackTools.TRAIN_LOCKDOWN_DELAY;
        return 3;
    }

    @Override
    public void releaseCart() {
        delay = 10;
    }

    @Override
    public boolean isCartLockedDown(EntityMinecart cart) {
        return !powered && lockedCart == cart && delay == 0;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("profile", (byte) profile.ordinal());
        profileInstance.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setInteger("delay", delay);
        data.setByte("reset", reset);
        data.setLong("uuidHigh", getUUID().getMostSignificantBits());
        data.setLong("uuidLow", getUUID().getLeastSignificantBits());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("profile"))
            profile = LockingProfileType.fromOrdinal(data.getByte("profile"));
        profileInstance = profile.create(this);
        profileInstance.readFromNBT(data);
        powered = data.getBoolean("powered");
        delay = data.getInteger("delay");
        reset = data.getByte("reset");

        if (data.hasKey("uuidHigh"))
            uuid = new UUID(data.getLong("uuidHigh"), data.getLong("uuidLow"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(profile.ordinal());
        data.writeBoolean(powered);
        data.writeShort(delay);

        profileInstance.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        LockingProfileType p = LockingProfileType.fromOrdinal(data.readByte());
        if (profile != p) {
            profile = p;
            profileInstance = p.create(this);
        }
        powered = data.readBoolean();
        delay = data.readShort();

        profileInstance.readPacketData(data);

        markBlockNeedsUpdate();
    }

}
