/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.carts;

import com.mojang.authlib.GameProfile;
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
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.ItemWhistleTuner;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class EntityLocomotive extends CartBaseContainer implements IDirectionalCart, IGuiReturnHandler,
        ILinkableCart, IMinecart, ISecure<LocoLockButtonState>, IPaintedCart, IRoutableCart, IEntityAdditionalSpawnData {
    private static final DataParameter<Boolean> HAS_FUEL = DataManagerPlugin.create(DataSerializers.BOOLEAN);
    private static final DataParameter<Byte> LOCOMOTIVE_MODE = DataManagerPlugin.create(DataSerializers.BYTE);
    private static final DataParameter<Byte> LOCOMOTIVE_SPEED = DataManagerPlugin.create(DataSerializers.BYTE);
    private static final DataParameter<EnumColor> PRIMARY_COLOR = DataManagerPlugin.create(DataManagerPlugin.ENUM_COLOR);
    private static final DataParameter<EnumColor> SECONDARY_COLOR = DataManagerPlugin.create(DataManagerPlugin.ENUM_COLOR);
    private static final DataParameter<String> EMBLEM = DataManagerPlugin.create(DataSerializers.STRING);
    private static final DataParameter<String> DEST = DataManagerPlugin.create(DataSerializers.STRING);
    private static final double DRAG_FACTOR = 0.9;
    private static final float HS_FORCE_BONUS = 3.5F;
    private static final byte FUEL_USE_INTERVAL = 8;
    private static final byte KNOCKBACK = 1;
    private static final int WHISTLE_INTERVAL = 256;
    private static final int WHISTLE_DELAY = 160;
    private static final int WHISTLE_CHANCE = 4;
    private final MultiButtonController<LocoLockButtonState> lockController = MultiButtonController.create(0, LocoLockButtonState.VALUES);
    public LocoMode clientMode = LocoMode.SHUTDOWN;
    public LocoSpeed clientSpeed = LocoSpeed.MAX;
    public boolean clientCanLock;
    protected float renderYaw;
    private String model = "";
    private int fuel;
    private int update = MiscTools.RANDOM.nextInt();
    private int whistleDelay;
    private int tempIdle;
    private float whistlePitch = getNewWhistlePitch();

    protected EntityLocomotive(World world) {
        super(world);
        setPrimaryColor(EnumColor.SILVER.getDye());
        setSecondaryColor(EnumColor.GRAY.getDye());
    }

    protected EntityLocomotive(World world, double x, double y, double z) {
        this(world);
        setPosition(x, y + getYOffset(), z);
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(HAS_FUEL, false);
        dataManager.register(PRIMARY_COLOR, EnumColor.WHITE);
        dataManager.register(SECONDARY_COLOR, EnumColor.WHITE);
        dataManager.register(LOCOMOTIVE_MODE, (byte) LocoMode.SHUTDOWN.ordinal());
        dataManager.register(LOCOMOTIVE_SPEED, (byte) LocoSpeed.MAX.ordinal());
        dataManager.register(EMBLEM, "");
        dataManager.register(DEST, "");
    }

    @Override
    public void initEntityFromItem(ItemStack item) {
        NBTTagCompound nbt = item.getTagCompound();
        if (nbt == null)
            return;

        setPrimaryColor(ItemLocomotive.getPrimaryColor(item).getDye());
        setSecondaryColor(ItemLocomotive.getSecondaryColor(item).getDye());
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
        return RailcraftCarts.getCartType(stack) == getCartType();
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
        if (hasCustomName())
            item.setStackDisplayName(getCustomNameTag());
        return item;
    }

    protected abstract ItemStack getCartItemBase();

    @Override
    public boolean doInteract(EntityPlayer player, @Nullable ItemStack stack, @Nullable EnumHand hand) {
        if (Game.isHost(worldObj)) {
            if (stack != null && stack.getItem() instanceof ItemWhistleTuner) {
                if (whistleDelay <= 0) {
                    whistlePitch = getNewWhistlePitch();
                    whistle();
                    stack.damageItem(1, player);
                }
                return true;
            }
            if (this instanceof IFluidHandler && FluidHelper.handleRightClick((IFluidHandler) this, null, player, true, false))
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
        return !isPrivate() || PlayerPlugin.isOwnerOrOp(getOwner(), user);
    }

    public LocoLockButtonState getSecurityState() {
        return lockController.getButtonState();
    }

    public void setSecurityState(LocoLockButtonState state) {
        lockController.setCurrentState(state);
    }

    public String getEmblem() {
        return dataManager.get(EMBLEM);
    }

    public void setEmblem(String emblem) {
        if (!getEmblem().equals(emblem))
            dataManager.set(EMBLEM, emblem);
    }

    @Nullable
    public ItemStack getDestItem() {
        return getTicketInventory().getStackInSlot(1);
    }

    @Override
    public String getDestination() {
        return StringUtils.defaultIfBlank(dataManager.get(DEST), "");
    }

    public void setDestString(String dest) {
        if (!StringUtils.equals(getDestination(), dest))
            dataManager.set(DEST, dest);
    }

    public LocoMode getMode() {
        return DataManagerPlugin.readEnum(dataManager, LOCOMOTIVE_MODE, LocoMode.VALUES);
    }

    public void setMode(LocoMode mode) {
        if (getMode() != mode)
            DataManagerPlugin.writeEnum(dataManager, LOCOMOTIVE_MODE, mode);
    }

    public LocoSpeed getSpeed() {
        return DataManagerPlugin.readEnum(dataManager, LOCOMOTIVE_SPEED, LocoSpeed.VALUES);
    }

    public void setSpeed(LocoSpeed speed) {
        if (getSpeed() != speed)
            DataManagerPlugin.writeEnum(dataManager, LOCOMOTIVE_SPEED, speed);
    }

    public void increaseSpeed() {
        LocoSpeed speed = getSpeed();
        if (speed != LocoSpeed.MAX)
            dataManager.set(LOCOMOTIVE_SPEED, (byte) (speed.ordinal() - 1));
    }

    public void decreaseSpeed() {
        LocoSpeed speed = getSpeed();
        if (speed != LocoSpeed.REVERSE)
            dataManager.set(LOCOMOTIVE_SPEED, (byte) (speed.ordinal() + 1));
    }

    public boolean hasFuel() {
        return dataManager.get(HAS_FUEL);
    }

    public void setHasFuel(boolean powered) {
        dataManager.set(HAS_FUEL, powered);
    }

    public boolean isRunning() {
        return fuel > 0 && getMode() == LocoMode.RUNNING && !(isIdle() || isShutdown());
    }

    public boolean isIdle() {
        return !isShutdown() && (tempIdle > 0 || getMode() == LocoMode.IDLE || Train.getTrain(this).isIdle());
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

    public abstract SoundEvent getWhistle();

    public final void whistle() {
        if (whistleDelay <= 0) {
            //TODO: Make moving sound
            SoundHelper.playSoundForEntity(this, getWhistle(), 1, whistlePitch);
            whistleDelay = WHISTLE_DELAY;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        update++;

        if (Game.isClient(worldObj))
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
    public boolean setDestination(@Nullable ItemStack ticket) {
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

    private boolean cartVelocityIsGreaterThan(@SuppressWarnings("SameParameterValue") float vel) {
        return Math.abs(motionX) > vel || Math.abs(motionZ) > vel;
    }

    public int getDamageToRoadKill(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            ItemStack pants = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            if (pants != null && RailcraftItems.overalls.isInstance(pants)) {
                entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, InvTools.damageItem(pants, 5));
                return 4;
            }
        }
        return 25;
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (Game.isHost(worldObj)) {
            if (!entity.isEntityAlive())
                return;
            if (!Train.getTrain(this).isPassenger(entity) && (cartVelocityIsGreaterThan(0.2f) || getEntityData().getBoolean("HighSpeed")) && MiscTools.isKillableEntity(entity)) {
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

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean collidedWithOtherLocomotive(Entity entity) {
        if (!(entity instanceof EntityLocomotive))
            return false;
        EntityLocomotive otherLoco = (EntityLocomotive) entity;
        if (getUniqueID() == entity.getUniqueID())
            return false;
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

        data.setString("dest", StringUtils.defaultIfBlank(getDestination(), ""));

        data.setByte("locoMode", (byte) getMode().ordinal());
        data.setByte("locoSpeed", (byte) getSpeed().ordinal());

        EnumColor.fromDye(getPrimaryColor()).writeToNBT(data, "primaryColor");
        EnumColor.fromDye(getSecondaryColor()).writeToNBT(data, "secondaryColor");

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

        setMode(LocoMode.values()[data.getByte("locoMode")]);
        setSpeed(LocoSpeed.values()[data.getByte("locoSpeed")]);

        setPrimaryColor(EnumColor.readFromNBT(data, "primaryColor").getDye());
        setPrimaryColor(EnumColor.readFromNBT(data, "secondaryColor").getDye());

        whistlePitch = data.getFloat("whistlePitch");

        fuel = data.getInteger("fuel");

        lockController.readFromNBT(data, "lock");
    }

    @Override
    public void writeGuiData(@Nonnull RailcraftOutputStream data) throws IOException {
        data.writeByte(clientMode.ordinal());
        data.writeByte(clientSpeed.ordinal());
        data.writeByte(lockController.getCurrentState());
    }

    @Override
    public void readGuiData(@Nonnull RailcraftInputStream data, EntityPlayer sender) throws IOException {
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
            byteStream.writeUTF(hasCustomName() ? getName() : "");
            byteStream.writeUTF(model);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        try {
            DataInputStream byteSteam = new DataInputStream(new ByteBufInputStream(data));
            String name = byteSteam.readUTF();
            if (!name.equals(""))
                setCustomNameTag(name);
            model = byteSteam.readUTF();
        } catch (IOException ignored) {
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
        if (isExemptFromLinkLimits(cart))
            return true;

        LinkageManager lm = LinkageManager.instance();

        EntityMinecart linkA = lm.getLinkedCartA(this);
        if (linkA != null && !isExemptFromLinkLimits(linkA))
            return false;

        EntityMinecart linkB = lm.getLinkedCartB(this);
        return linkB == null || isExemptFromLinkLimits(linkB);
    }

    private boolean isExemptFromLinkLimits(EntityMinecart cart) {
        return cart instanceof EntityLocomotive || cart instanceof CartBaseMaintenance;
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
    public boolean canPassItemRequests() {
        return true;
    }

    public abstract LocomotiveRenderType getRenderType();

    @Override
    public final EnumDyeColor getPrimaryColor() {
        return dataManager.get(PRIMARY_COLOR).getDye();
    }

    public final void setPrimaryColor(EnumDyeColor color) {
        dataManager.set(PRIMARY_COLOR, EnumColor.fromDye(color));
    }

    @Override
    public final EnumDyeColor getSecondaryColor() {
        return dataManager.get(SECONDARY_COLOR).getDye();
    }

    public final void setSecondaryColor(EnumDyeColor color) {
        dataManager.set(SECONDARY_COLOR, EnumColor.fromDye(color));
    }

    public final String getModel() {
        return model;
    }

    public final void setModel(String model) {
        this.model = model;
    }

    @Override
    public World theWorld() {
        return worldObj;
    }

    public enum LocoMode implements IStringSerializable {

        RUNNING, IDLE, SHUTDOWN;
        public static final LocoMode[] VALUES = values();

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum LocoSpeed implements IStringSerializable {

        MAX, SLOW, SLOWER, SLOWEST, REVERSE;
        public static final LocoSpeed[] VALUES = values();

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static LocoSpeed fromName(String name) {
            for (LocoSpeed speed : VALUES) {
                if (speed.getName().equals(name))
                    return speed;
            }
            return MAX;
        }
    }

    public enum LocoLockButtonState implements IMultiButtonState {

        UNLOCKED(new ButtonTextureSet(224, 0, 16, 16)),
        LOCKED(new ButtonTextureSet(240, 0, 16, 16)),
        PRIVATE(new ButtonTextureSet(240, 48, 16, 16));
        public static final LocoLockButtonState[] VALUES = values();
        private final IButtonTextureSet texture;

        LocoLockButtonState(IButtonTextureSet texture) {
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

        @Nullable
        @Override
        public ToolTip getToolTip() {
            return null;
        }

    }
}
