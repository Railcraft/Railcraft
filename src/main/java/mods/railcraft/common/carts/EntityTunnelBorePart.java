package mods.railcraft.common.carts;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

/**
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
        super(parent.worldObj);
        this.setSize(width, height);
        this.parent = parent;
        this.partName = partName;
        this.forwardOffset = forwardOffset;
        this.sideOffset = sideOffset;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        double x = parent.getOffsetX(parent.posX, forwardOffset, sideOffset);
        double z = parent.getOffsetZ(parent.posZ, forwardOffset, sideOffset);
        setLocationAndAngles(x, parent.posY, z, 0.0F, 0.0F);
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    protected void entityInit() {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith() {
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        return !this.isEntityInvulnerable() && this.parent.attackEntityFromPart(this, p_70097_1_, p_70097_2_);
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    public boolean isEntityEqual(Entity entity) {
        return this == entity || this.parent == entity;
    }
}
