/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.misc.EntityIDs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityItemFireproof extends EntityItem {

    public static void register() {
        EntityRegistry.registerModEntity(EntityItemFireproof.class, "ItemFireproof", EntityIDs.ENTITY_ITEM_FIREPROOF, Railcraft.getMod(), 64, 20, true);
    }

    public EntityItemFireproof(World world) {
        super(world);
        init();
    }

    public EntityItemFireproof(World world, double x, double y, double z) {
        super(world, x, y, z);
        init();
    }

    public EntityItemFireproof(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
        init();
    }

    private void init() {
        isImmuneToFire = true;
    }

    @Override
    public void onUpdate() {
        ItemStack stack = getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null && stack.getItem().onEntityItemUpdate(this))
            return;
        if (getEntityItem() == null) {
            setDead();
        } else {
            onEntityUpdate();

            int delayBeforeCanPickup = ReflectionHelper.getPrivateValue(EntityItem.class, this, "field_145804_b", "delayBeforeCanPickup");
            if (cannotPickup() && delayBeforeCanPickup != 32767) {
                setPickupDelay(delayBeforeCanPickup - 1);
            }

            AxisAlignedBB bb = getEntityBoundingBox();

            this.prevPosX = posX;
            this.prevPosY = posY;
            this.prevPosZ = posZ;
            this.motionY -= 0.03999999910593033D;
            this.noClip = pushOutOfBlocks(posX, (bb.minY + bb.maxY) / 2.0D, posZ);
            moveEntity(motionX, motionY, motionZ);
            float f = 0.98F;

            if (onGround)
                f = worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(bb.minY) - 1, MathHelper.floor_double(posZ))).getBlock().slipperiness * 0.98F;

            this.motionX *= (double) f;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= (double) f;

            if (onGround)
                this.motionY *= -0.5D;

            handleWaterMovement();
        }
    }

    @Override
    public void setFire(int par1) {
    }

    @Override
    protected void dealFireDamage(int par1) {
    }

    @Override
    public boolean isInLava() {
        return worldObj.isMaterialInBB(getEntityBoundingBox(), Material.lava);
    }

    @Override
    protected void setOnFireFromLava() {
    }

}
