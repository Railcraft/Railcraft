/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import mods.railcraft.api.carts.*;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.common.blocks.signals.ISecure;
import mods.railcraft.common.carts.EntityLocomotive.LocoLockButtonState;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.gui.buttons.ButtonTextureSet;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.ItemOveralls;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.ItemWhistleTuner;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.EnumColor;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class EntityLocomotive extends CartContainerBase implements IDirectionalCart, IGuiReturnHandler, ILinkableCart, IMinecart, ISecure<LocoLockButtonState>, IPaintedCart, IRoutableCart, IEntityAdditionalSpawnData {
    private static final byte HAS_FUEL_DATA_ID = 16;
    private static final byte PRIMARY_COLOR_DATA_ID = 25;
    private static final byte SECONDARY_COLOR_DATA_ID = 26;
    private static final byte LOCOMOTIVE_MODE_DATA_ID = 27;
    private static final byte LOCOMOTIVE_SPEED_DATA_ID = 28;
    private static final byte EMBLEM_DATA_ID = 29;
    private static final byte DEST_DATA_ID = 30;
    private static final double DRAG_FACTOR = 0.9;
    private static final float HS_FORCE_BONUS = 3.5F;
    private static final byte FUEL_USE_INTERVAL = 8;
    private static final byte KNOCKBACK = 1;
    private static final int WHISTLE_INTERVAL = 256;
    private static final int WHISTLE_DELAY = 160;
    private static final int WHISTLE_CHANCE = 4;
    private final MultiButtonController<LocoLockButtonState> lockController = new MultiButtonController(0, LocoLockButtonState.VALUES);
    public LocoMode clientMode = LocoMode.SHUTDOWN;
    public LocoSpeed clientSpeed = LocoSpeed.MAX;
    public boolean clientCanLock;
    protected float renderYaw;
    private String model = "";
    private int fuel;
    private int update = MiscTools.getRand().nextInt();
    private int whistleDelay;
    private int tempIdle;
    private float whistlePitch = getNewWhistlePitch();

    public EntityLocomotive(World world) {
        super(world);
        setPrimaryColor(EnumColor.LIGHT_GRAY.ordinal());
        setSecondaryColor(EnumColor.GRAY.ordinal());
    }

    public EntityLocomotive(World world, double x, double y, double z) {
        this(world);
        setPosition(x, y + (double) yOffset, z);
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataWatcher.addObject(HAS_FUEL_DATA_ID, (byte) 0);
        dataWatcher.addObject(PRIMARY_COLOR_DATA_ID, (byte) 0);
        dataWatcher.addObject(SECONDARY_COLOR_DATA_ID, (byte) 0);
        dataWatcher.addObject(LOCOMOTIVE_MODE_DATA_ID, (byte) LocoMode.SHUTDOWN.ordinal());
        dataWatcher.addObject(LOCOMOTIVE_SPEED_DATA_ID, (byte) 0);
        dataWatcher.addObject(EMBLEM_DATA_ID, "");
        dataWatcher.addObject(DEST_DATA_ID, "");
    }

    @Override
    public void initEntityFromItem(ItemStack item) {
        NBTTagCompound nbt = item.getTagCompound();
        if (nbt == null)
            return;

        setPrimaryColor(ItemLocomotive.getPrimaryColor(item).ordinal());
        setSecondaryColor(ItemLocomotive.getSecondaryColor(item).ordinal());
        if (nbt.hasKey("whistlePitch"))
            whistlePitch = nbt.getFloat("whistlePitch");
        if (nbt.hasKey("owner")) {
            CartTools.setCartOwner(this, PlayerPlugin.readOwnerFromNBT(nbt));
            setSecurityState(LocoLockButtonState.LOCKED);
        }
        if (nbt.hasKey("security"))
            setSecurityState(LocoLockButtonState.VALUES[nbt.getByte("security")]);
        if (nbt.hasKey("emblem"))
            setEmblem(nbt.getString("emblem"));
        if (nbt.hasKey("model"))
            model = nbt.getString("model");
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return EnumCart.getCartType(stack) == getCartType();
    }

    @Override
    public String getName() {
        return LocalizationPlugin.translate(getLocalizationTag());
    }

    @Override
    public String getLocalizationTag() {
        return getCartType().getTag();
    }

    @Override
    public MultiButtonController<LocoLockButtonState> getLockController() {
        return lockController;
    }

    @Override
    public GameProfile getOwner() {
        return CartTools.getCartOwner(this);
    }

    private float getNewWhistlePitch() {
        return 1f + (float) rand.nextGaussian() * 0.2f;
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getCartItem());
        return items;
    }

    @Override
    public ItemStack getCartItem() {
        ItemStack item = getCartItemBase();
        if (isSecure() && CartTools.doesCartHaveOwner(this))
            ItemLocomotive.setOwnerData(item, CartTools.getCartOwner(this));
        ItemLocomotive.setItemColorData(item, getPrimaryColor(), getSecondaryColor());
        ItemLocomotive.setItemWhistleData(item, whistlePitch);
        ItemLocomotive.setModel(item, getModel());
        ItemLocomotive.setEmblem(item, getEmblem());
        if (hasCustomInventoryName())
            item.setStackDisplayName(getCommandSenderName());
        return item;
    }

    protected abstract ItemStack getCartItemBase();

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(getWorld())) {
            ItemStack current = player.getCurrentEquippedItem();
            if (current != null && current.getItem() instanceof ItemWhistleTuner) {
                if (whistleDelay <= 0) {
                    whistlePitch = getNewWhistlePitch();
                    whistle();
                    current.damageItem(1, player);
                }
                return true;
            }
            if (this instanceof IFluidHandler && FluidHelper.handleRightClick((IFluidHandler) this, ForgeDirection.UNKNOWN, player, true, false))
                return true;
            if (!isPrivate() || PlayerPlugin.isOwnerOrOp(getOwner(), player.getGameProfile()))
                openGui(player);
        }
        return true;
    }

    protected abstract void openGui(EntityPlayer player);

    @Override
    public boolean isSecure() {
        return getSecurityState() == LocoLockButtonState.LOCKED || isPrivate();
    }

    public boolean isPrivate() {
        return getSecurityState() == LocoLockButtonState.PRIVATE;
    }

    public boolean canControl(GameProfile user) {
        if (!isPrivate())
            return true;
        return PlayerPlugin.isOwnerOrOp(getOwner(), user);
    }

    public LocoLockButtonState getSecurityState() {
        return lockController.getButtonState();
    }

    public void setSecurityState(LocoLockButtonState state) {
        lockController.setCurrentState(state);
    }

    public String getEmblem() {
        return dataWatcher.getWatchableObjectString(EMBLEM_DATA_ID);
    }

    public void setEmblem(String emblem) {
        if (!getEmblem().equals(emblem))
            dataWatcher.updateObject(EMBLEM_DATA_ID, emblem);
    }

    public ItemStack getDestItem() {
        return getTicketInventory().getStackInSlot(1);
    }

    @Override
    public String getDestination() {
        return dataWatcher.getWatchableObjectString(DEST_DATA_ID);
    }

    public void setDestString(String dest) {
        if (!getDestination().equals(dest))
            dataWatcher.updateObject(DEST_DATA_ID, dest);
    }

    public LocoMode getMode() {
        return LocoMode.VALUES[dataWatcher.getWatchableObjectByte(LOCOMOTIVE_MODE_DATA_ID)];
    }

    public void setMode(LocoMode mode) {
        if (getMode() != mode)
            dataWatcher.updateObject(LOCOMOTIVE_MODE_DATA_ID, (byte) mode.ordinal());
    }

    public LocoSpeed getSpeed() {
        return LocoSpeed.VALUES[dataWatcher.getWatchableObjectByte(LOCOMOTIVE_SPEED_DATA_ID)];
    }

    public void setSpeed(LocoSpeed speed) {
        if (getSpeed() != speed)
            dataWatcher.updateObject(LOCOMOTIVE_SPEED_DATA_ID, (byte) speed.ordinal());
    }

    public void increaseSpeed() {
        LocoSpeed speed = getSpeed();
        if (speed != LocoSpeed.MAX)
            dataWatcher.updateObject(LOCOMOTIVE_SPEED_DATA_ID, (byte) (speed.ordinal() - 1));
    }

    public void decreaseSpeed() {
        LocoSpeed speed = getSpeed();
        if (speed != LocoSpeed.REVERSE)
            dataWatcher.updateObject(LOCOMOTIVE_SPEED_DATA_ID, (byte) (speed.ordinal() + 1));
    }

    public boolean hasFuel() {
        return dataWatcher.getWatchableObjectByte(HAS_FUEL_DATA_ID) != 0;
    }

    public void setHasFuel(boolean powered) {
        dataWatcher.updateObject(HAS_FUEL_DATA_ID, (byte) (powered ? 1 : 0));
    }

    public boolean isRunning() {
        return fuel > 0 && getMode() == LocoMode.RUNNING && !(isIdle() || isShutdown());
    }

    public boolean isIdle() {
        if (isShutdown()) return false;
        return tempIdle > 0 || getMode() == LocoMode.IDLE || Train.getTrain(this).isIdle();
    }

    public boolean isShutdown() {
        return getMode() == LocoMode.SHUTDOWN || Train.getTrain(this).isStopped();
    }

    public void forceIdle(int ticks) {
        tempIdle = Math.max(tempIdle, ticks);
    }

    @Override
    public void reverse() {
        rotationYaw += 180;
        motionX = -motionX;
        motionZ = -motionZ;
    }

    @Override
    public void setRenderYaw(float yaw) {
        renderYaw = yaw;
    }

    public abstract String getWhistle();

    public final void whistle() {
        if (whistleDelay <= 0) {
            SoundHelper.playSoundAtEntity(this, getWhistle(), 1, whistlePitch);
            whistleDelay = WHISTLE_DELAY;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        update++;

        if (Game.isNotHost(worldObj))
            return;

        processTicket();
        updateFuel();

//        if (getEntityData().getBoolean("HighSpeed"))
//            System.out.println(CartTools.getCartSpeedUncapped(this));
        if (whistleDelay > 0)
            whistleDelay--;

        if (tempIdle > 0)
            tempIdle--;

        if (update % WHISTLE_INTERVAL == 0 && isRunning() && rand.nextInt(WHISTLE_CHANCE) == 0)
            whistle();

        if (isShutdown()) {
            double yaw = rotationYaw * Math.PI / 180D;
            double cos = Math.cos(yaw);
            double sin = Math.sin(yaw);
            float limit = 0.05f;
            if (motionX > limit && cos < 0)
                rotationYaw += 180;
            else if (motionX < -limit && cos > 0)
                rotationYaw += 180;
            else if (motionZ > limit && sin < 0)
                rotationYaw += 180;
            else if (motionZ < -limit && sin > 0)
                rotationYaw += 180;
        }
    }

    @Override
    public boolean setDestination(ItemStack ticket) {
        if (ticket != null && ticket.getItem() instanceof ItemTicket) {
            if (isSecure() && !ItemTicket.matchesOwnerOrOp(ticket, CartTools.getCartOwner(this)))
                return false;
            String dest = ItemTicket.getDestination(ticket);
            if (!dest.equals(getDestination())) {
                setDestString(dest);
                getTicketInventory().setInventorySlotContents(1, ItemTicket.copyTicket(ticket));
                return true;
            }
        }
        return false;
    }

    protected abstract IInventory getTicketInventory();

    private void processTicket() {
        IInventory invTicket = getTicketInventory();
        ItemStack stack = invTicket.getStackInSlot(0);
        if (stack != null)
            if (stack.getItem() instanceof ItemTicket) {
                if (setDestination(stack))
                    invTicket.setInventorySlotContents(0, InvTools.depleteItem(stack));
            } else
                invTicket.setInventorySlotContents(0, null);
    }

    @Override
    protected void applyDrag() {
        motionX *= getDrag();
        motionY *= 0.0D;
        motionZ *= getDrag();

        LocoSpeed speed = getSpeed();
        if (isRunning()) {
            float force = RailcraftConfig.locomotiveHorsepower() * 0.006F;
            switch (speed) {
                case REVERSE:
                    force = -force;
                    break;
                case MAX:
                    boolean highSpeed = getEntityData().getBoolean("HighSpeed");
                    if (highSpeed)
                        force *= HS_FORCE_BONUS;
                    break;
            }
            double yaw = rotationYaw * Math.PI / 180D;
            motionX += Math.cos(yaw) * force;
            motionZ += Math.sin(yaw) * force;
        }

        if (speed != LocoSpeed.MAX) {
            float limit = 0.4f;
            switch (speed) {
                case SLOWEST:
                case REVERSE:
                    limit = 0.1f;
                    break;
                case SLOWER:
                    limit = 0.2f;
                    break;
                case SLOW:
                    limit = 0.3f;
                    break;
            }
            motionX = Math.copySign(Math.min(Math.abs(motionX), limit), motionX);
            motionZ = Math.copySign(Math.min(Math.abs(motionZ), limit), motionZ);
        }
    }

    private int getFuelUse() {
        if (isRunning()) {
            LocoSpeed speed = getSpeed();
            switch (speed) {
                case SLOWEST:
                case REVERSE:
                    return 2;
                case SLOWER:
                    return 4;
                case SLOW:
                    return 6;
                default:
                    return 8;
            }
        } else if (isIdle())
            return getIdleFuelUse();
        return 0;
    }

    protected int getIdleFuelUse() {
        return 1;
    }

    protected void updateFuel() {
        if (update % FUEL_USE_INTERVAL == 0 && fuel > 0) {
            fuel -= getFuelUse();
            if (fuel < 0)
                fuel = 0;
        }
        while (fuel <= FUEL_USE_INTERVAL && !isShutdown()) {
            int newFuel = getMoreGoJuice();
            if (newFuel <= 0)
                break;
            fuel += newFuel;
        }
        setHasFuel(fuel > 0);
    }

    private boolean cartVelocityIsGreaterThan(float vel) {
        return Math.abs(motionX) > vel || Math.abs(motionZ) > vel;
    }

    public int getDamageToRoadKill(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer)
            if (ItemOveralls.isPlayerWearing((EntityPlayer) entity)) {
                ItemStack pants = ((EntityPlayer) entity).getCurrentArmor(MiscTools.ArmorSlots.LEGS.ordinal());
                ((EntityPlayer) entity).setCurrentItemOrArmor(MiscTools.ArmorSlots.LEGS.ordinal() + 1, InvTools.damageItem(pants, 5));
                return 4;
            }
        return 25;
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (Game.isHost(worldObj)) {
            if (!entity.isEntityAlive())
                return;
            if (entity != this.riddenByEntity && (cartVelocityIsGreaterThan(0.2f) || getEntityData().getBoolean("HighSpeed")) && MiscTools.isKillabledEntity(entity)) {
                EntityLivingBase living = (EntityLivingBase) entity;
                if (RailcraftConfig.locomotiveDamageMobs())
                    living.attackEntityFrom(RailcraftDamageSource.TRAIN, getDamageToRoadKill(living));
                if (living.getHealth() > 0) {
                    float yaw = (rotationYaw - 90) * (float) Math.PI / 180.0F;
                    living.addVelocity(-MathHelper.sin(yaw) * KNOCKBACK * 0.5F, 0.2D, MathHelper.cos(yaw) * KNOCKBACK * 0.5F);
                }
                return;
            }
            if (collidedWithOtherLocomotive(entity)) {
                EntityLocomotive otherLoco = (EntityLocomotive) entity;
                explode();
                if (otherLoco.isEntityAlive())
                    otherLoco.explode();
                return;
            }
        }
        super.applyEntityCollision(entity);
    }

    private boolean collidedWithOtherLocomotive(Entity entity) {
        if (!(entity instanceof EntityLocomotive))
            return false;
        EntityLocomotive otherLoco = (EntityLocomotive) entity;
        if (getUniqueID() == entity.getUniqueID())
            return false;
        LinkageManager lm = LinkageManager.instance();
        if (Train.areInSameTrain(this, otherLoco))
            return false;
        return cartVelocityIsGreaterThan(0.2f) && otherLoco.cartVelocityIsGreaterThan(0.2f)
                && (Math.abs(motionX - entity.motionX) > 0.3f || Math.abs(motionZ - entity.motionZ) > 0.3f);
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        getTicketInventory().setInventorySlotContents(1, null);
        super.killMinecart(par1DamageSource);
    }

    @Override
    public void setDead() {
        getTicketInventory().setInventorySlotContents(1, null);
        super.setDead();
    }

    protected void explode() {
        CartUtils.explodeCart(this);
        setDead();
    }

    public abstract int getMoreGoJuice();

    @Override
    public double getDrag() {
        return DRAG_FACTOR;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);

        Boolean isInReverse = ObfuscationReflectionHelper.getPrivateValue(EntityMinecart.class, this, 0);

        data.setBoolean("isInReverse", isInReverse);

        data.setString("model", model);

        data.setString("emblem", getEmblem());

        data.setString("dest", getDestination());

        data.setByte("locomode", (byte) getMode().ordinal());
        data.setByte("locospeed", (byte) getSpeed().ordinal());

        data.setByte("primaryColor", getPrimaryColor());
        data.setByte("secondaryColor", getSecondaryColor());

        data.setFloat("whistlePitch", whistlePitch);

        data.setInteger("fuel", fuel);

        lockController.writeToNBT(data, "lock");
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);

        ObfuscationReflectionHelper.setPrivateValue(EntityMinecart.class, this, data.getBoolean("isInReverse"), 0);

        model = data.getString("model");

        setEmblem(data.getString("emblem"));

        setDestString(data.getString("dest"));

        setMode(LocoMode.values()[data.getByte("locomode")]);
        setSpeed(LocoSpeed.values()[data.getByte("locospeed")]);

        setPrimaryColor(data.getByte("primaryColor"));
        setSecondaryColor(data.getByte("secondaryColor"));

        whistlePitch = data.getFloat("whistlePitch");

        fuel = data.getInteger("fuel");

        lockController.readFromNBT(data, "lock");
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeByte(clientMode.ordinal());
        data.writeByte(clientSpeed.ordinal());
        data.writeByte(lockController.getCurrentState());
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        setMode(LocoMode.VALUES[data.readByte()]);
        setSpeed(LocoSpeed.VALUES[data.readByte()]);
        byte lock = data.readByte();
        if (PlayerPlugin.isOwnerOrOp(getOwner(), sender.getGameProfile()))
            lockController.setCurrentState(lock);
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        try {
            DataOutputStream byteStream = new DataOutputStream(new ByteBufOutputStream(data));
            byteStream.writeUTF(func_95999_t() != null ? func_95999_t() : "");
            byteStream.writeUTF(model);
        } catch (IOException ex) {
        }
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        try {
            DataInputStream byteSteam = new DataInputStream(new ByteBufInputStream(data));
            String name = byteSteam.readUTF();
            if (!name.equals(""))
                setMinecartName(name);
            model = byteSteam.readUTF();
        } catch (IOException ex) {
        }
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean isPoweredCart() {
        return true;
    }

    @Override
    public boolean isLinkable() {
        return true;
    }

    @Override
    public boolean canLinkWithCart(EntityMinecart cart) {
        if (cart instanceof EntityLocomotive)
            return true;

        LinkageManager lm = LinkageManager.instance();

        EntityMinecart linkA = lm.getLinkedCartA(this);
        if (linkA != null && !(linkA instanceof EntityLocomotive))
            return false;

        EntityMinecart linkB = lm.getLinkedCartB(this);
        return linkB == null || linkB instanceof EntityLocomotive;
    }

    @Override
    public boolean hasTwoLinks() {
        return true;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return LinkageManager.LINKAGE_DISTANCE;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 0.9f;
    }

    @Override
    public boolean canBeAdjusted(EntityMinecart cart) {
        return true;
    }

    @Override
    public void onLinkCreated(EntityMinecart cart) {
    }

    @Override
    public void onLinkBroken(EntityMinecart cart) {
    }

    public abstract LocomotiveRenderType getRenderType();

    @Override
    public final byte getPrimaryColor() {
        return dataWatcher.getWatchableObjectByte(PRIMARY_COLOR_DATA_ID);
    }

    public final void setPrimaryColor(int color) {
        dataWatcher.updateObject(PRIMARY_COLOR_DATA_ID, (byte) color);
    }

    @Override
    public final byte getSecondaryColor() {
        return dataWatcher.getWatchableObjectByte(SECONDARY_COLOR_DATA_ID);
    }

    public final void setSecondaryColor(int color) {
        dataWatcher.updateObject(SECONDARY_COLOR_DATA_ID, (byte) color);
    }

    public final String getModel() {
        return model;
    }

    public final void setModel(String model) {
        this.model = model;
    }

    @Override
    public World getWorld() {
        return worldObj;
    }

    public enum LocoMode {

        RUNNING, IDLE, SHUTDOWN;
        public static final LocoMode[] VALUES = values();
    }

    public enum LocoSpeed {

        MAX, SLOW, SLOWER, SLOWEST, REVERSE;
        public static final LocoSpeed[] VALUES = values();
    }

    public enum LocoLockButtonState implements IMultiButtonState {

        UNLOCKED(new ButtonTextureSet(224, 0, 16, 16)),
        LOCKED(new ButtonTextureSet(240, 0, 16, 16)),
        PRIVATE(new ButtonTextureSet(240, 48, 16, 16));
        public static final LocoLockButtonState[] VALUES = values();
        private final IButtonTextureSet texture;

        private LocoLockButtonState(IButtonTextureSet texture) {
            this.texture = texture;
        }

        @Override
        public String getLabel() {
            return "";
        }

        @Override
        public IButtonTextureSet getTextureSet() {
            return texture;
        }

        @Override
        public ToolTip getToolTip() {
            return null;
        }

    }
}
