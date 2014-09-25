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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.entity.EntityLivingBase;

public class TrackEmbarking extends TrackBaseRailcraft implements ITrackPowered, IGuiReturnHandler {

    public static final Set<Class> excludedEntities = new HashSet<Class>();
    public static final byte MIN_AREA = 1;
    public static final byte MAX_AREA = 5;
    private boolean powered = false;
    private byte area = 2;

    static {
        excludedEntities.add(EntityIronGolem.class);
        excludedEntities.add(EntityDragon.class);
        excludedEntities.add(EntityWither.class);
        excludedEntities.add(EntityBlaze.class);
        excludedEntities.add(EntityMagmaCube.class);
        excludedEntities.add(EntitySquid.class);
        excludedEntities.add(EntityBat.class);
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.EMBARKING;
    }

    @Override
    public IIcon getIcon() {
        if (isPowered()) {
            return getIcon(0);
        }
        return getIcon(1);
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            GuiHandler.openGui(EnumGui.TRACK_EMBARKING, player, getWorld(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
            crowbar.onWhack(player, current, getX(), getY(), getZ());
            return true;
        }
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (powered && cart.canBeRidden() && cart.riddenByEntity == null && cart.getEntityData().getInteger("MountPrevention") <= 0) {
            int a = area;
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(getX(), getY(), getZ(), getX() + 1, getY() + 1, getZ() + 1);
            box = box.expand(a, a, a);
            List entities = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, box);

            if (entities.size() > 0) {
                EntityLivingBase entity = (EntityLivingBase) entities.get(MiscTools.getRand().nextInt(entities.size()));

                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    if (player.isSneaking()) {
                        return;
                    }

                    ItemStack current = player.getCurrentEquippedItem();
                    if (current != null && current.getItem() instanceof IToolCrowbar) {
                        return;
                    }
                } else if (excludedEntities.contains(entity.getClass())) {
                    return;
                } else if (entity instanceof EntitySlime) {
                    EntitySlime slime = (EntitySlime) entity;
                    if (slime.getSlimeSize() >= 100)
                        return;
                }

                if (entity.ridingEntity == null) {
                    EffectManager.instance.teleportEffect(entity, cart.posX, cart.posY, cart.posZ);
                    entity.mountEntity(cart);
                }
            }
        }
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
        data.setBoolean("powered", powered);
        data.setByte("area", area);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        area = data.getByte("area");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
        data.writeByte(area);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        boolean p = data.readBoolean();
        setArea(data.readByte());

        if (p != powered) {
            powered = p;
            markBlockNeedsUpdate();
        }
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeByte(area);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        byte a = data.readByte();
        if (area != a) {
            setArea(a);
            sendUpdateToClient();
        }
    }

    public void setArea(byte radius) {
        radius = (byte) Math.max(MIN_AREA, radius);
        radius = (byte) Math.min(MAX_AREA, radius);
        area = radius;
    }

    public byte getArea() {
        return area;
    }

}
