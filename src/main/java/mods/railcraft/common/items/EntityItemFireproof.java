/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.misc.EntityIDs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityItemFireproof extends EntityItem {

    public static void register() {
        EntityRegistry.registerModEntity(EntityItemFireproof.class, "ItemFireproof", EntityIDs.ENTITY_ITEM_FIREPROOF, Railcraft.getMod(), 64, 20, true);
    }

    public EntityItemFireproof(World world) {
        super(world);
    }

    public EntityItemFireproof(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityItemFireproof(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    {
        isImmuneToFire = true;
        setNoDespawn();
    }

    @Override
    public void setFire(int par1) {
    }

    @Override
    protected void dealFireDamage(int par1) {
    }

    @Override
    public boolean isInLava() {
        return worldObj.isMaterialInBB(getEntityBoundingBox(), Material.LAVA);
    }

    @Override
    protected void setOnFireFromLava() {
    }

}
