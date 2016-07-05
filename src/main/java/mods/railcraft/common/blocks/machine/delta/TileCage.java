/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.delta;

import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileCage extends TileMachineBase {

    private boolean isOpen = false;
    private EntityLiving entity;
    private EntityLookHelper lookHelper;
    private EntityBodyHelper bodyHelper;
    private double lookX, lookZ;
    private Entity lookTarget;
    private int lookCounter;

    @Override
    public EnumMachineDelta getMachineType() {
        return EnumMachineDelta.CAGE;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
//        if (player.isSneaking()) {
        if (Game.isHost(worldObj)) {
            isOpen = !isOpen;
            sendUpdateToClient();
        }
        return true;
//        }
//        return super.blockActivated(player, side);
    }

    @SideOnly(Side.CLIENT)
    public Entity getEntity() {
        if (entity == null) {
            entity = (EntityLiving) EntityList.createEntityByName("Chicken", null);
            lookHelper = new EntityLookHelper(entity);
            bodyHelper = new EntityBodyHelper(entity);
        }
        return entity;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(worldObj))
            if (entity != null) {
//                entity.onUpdate();
                entity.setPosition(0, 0, 0);
                entity.prevRotationYawHead = entity.rotationYawHead;
                entity.prevRotationYaw = entity.rotationYaw;
                entity.prevRotationPitch = entity.rotationPitch;
                entity.prevRenderYawOffset = entity.renderYawOffset;


                if (lookCounter > 0) {
                    lookCounter--;
                    if (lookTarget == null)
                        lookHelper.setLookPosition(lookX, entity.getEyeHeight(), lookZ, 10, entity.getVerticalFaceSpeed());
                    else
                        lookHelper.setLookPosition(lookTarget.posX - (getX() + 0.5), lookTarget.posY - (getY() + entity.getEyeHeight()), lookTarget.posZ - (getZ() + 0.5), 10, entity.getVerticalFaceSpeed());
                }

                lookHelper.onUpdateLook();
                bodyHelper.updateRenderAngles();

                if (lookCounter <= 0) {
                    lookTarget = null;
                    if (entity.getRNG().nextDouble() < 0.1) {
                        List<EntityPlayer> nearby = MiscTools.getNearbyEntities(worldObj, EntityPlayer.class, getPos().getX(), getPos().getY() - 1, getPos().getY() + 3, getPos().getZ(), 5);
                        if (!nearby.isEmpty() && entity.getRNG().nextDouble() < 0.4) {
                            lookTarget = nearby.get(MiscTools.RANDOM.nextInt(nearby.size()));
                            lookCounter = 60 + entity.getRNG().nextInt(60);
                        } else {
                            double d0 = (Math.PI * 2D) * entity.getRNG().nextDouble();
                            lookX = Math.cos(d0);
                            lookZ = Math.sin(d0);
                            lookCounter = 20 + entity.getRNG().nextInt(20);
                        }
                    }
                }
            }
    }

    @Override
    public void writePacketData( RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(isOpen);
    }

    @Override
    public void readPacketData( RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        boolean o = data.readBoolean();
        if (isOpen != o) {
            isOpen = o;
            markBlockForUpdate();
        }
    }
}
