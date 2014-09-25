/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import cpw.mods.fml.common.registry.EntityRegistry;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.misc.EntityIDs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 *
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
        ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null)
            if (stack.getItem().onEntityItemUpdate(this))
                return;

        onEntityUpdate();

        if (this.delayBeforeCanPickup > 0)
            --this.delayBeforeCanPickup;

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.noClip = this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float f = 0.98F;

        if (this.onGround)
            f = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.98F;

        this.motionX *= (double) f;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double) f;

        if (this.onGround)
            this.motionY *= -0.5D;
    }

    @Override
    public void setFire(int par1) {
    }

    @Override
    protected void dealFireDamage(int par1) {
    }

    @Override
    public boolean handleLavaMovement() {
        return this.worldObj.isMaterialInBB(boundingBox, Material.lava);
    }

    @Override
    protected void setOnFireFromLava() {
    }

}
