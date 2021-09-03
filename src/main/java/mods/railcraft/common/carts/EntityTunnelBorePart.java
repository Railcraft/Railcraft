/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Pseudo-Entity used to refine the Tunnel Bore collision boxes.
 *
 * Created by CovertJaguar on 11/19/2015.
 */
public class EntityTunnelBorePart extends Entity {
    public final EntityTunnelBore parent;
    public final String partName;
    public final float forwardOffset;
    public final float sideOffset;

    public EntityTunnelBorePart(EntityTunnelBore parent, String partName, float width, float height, float forwardOffset) {
        this(parent, partName, width, height, forwardOffset, 0.0F);
    }

    public EntityTunnelBorePart(EntityTunnelBore parent, String partName, float width, float height, float forwardOffset, float sideOffset) {
        super(parent.world);
        setSize(width, height);
        this.parent = parent;
        this.partName = partName;
        this.forwardOffset = forwardOffset;
        this.sideOffset = sideOffset;
        updatePosition();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        updatePosition();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return getEntityBoundingBox();
    }

    @Override
    protected void entityInit() {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float p_70097_2_) {
        return !isEntityInvulnerable(damageSource) && parent.attackEntityFromPart(this, damageSource, p_70097_2_);
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    @Override
    public boolean isEntityEqual(Entity entity) {
        return this == entity || parent == entity;
    }

    private void updatePosition() {
        double x = parent.getOffsetX(parent.posX, forwardOffset, sideOffset);
        double z = parent.getOffsetZ(parent.posZ, forwardOffset, sideOffset);
        setLocationAndAngles(x, parent.posY + 0.3F, z, 0.0F, 0.0F);
    }
}
