/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.util.entity.EntityIDs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityItemFireproof extends EntityItem {

    public static void register() {
        EntityEntry entry = EntityEntryBuilder.create()
                .id(RailcraftConstantsAPI.locationOf("fireproof_item"),  EntityIDs.ENTITY_ITEM_FIREPROOF)
                .entity(EntityItemFireproof.class)
                .name("ItemFireproof")
                .tracker(64, 20, true)
                .factory(EntityItemFireproof::new)
                .build();
        ForgeRegistries.ENTITIES.register(entry);
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
        return world.isMaterialInBB(getEntityBoundingBox(), Material.LAVA);
    }

    @Override
    protected void setOnFireFromLava() {
    }

}
