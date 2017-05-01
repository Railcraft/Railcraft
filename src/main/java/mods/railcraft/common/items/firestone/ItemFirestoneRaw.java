/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items.firestone;

import mods.railcraft.common.blocks.ore.EnumOreMagic;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemFirestoneRaw extends ItemRailcraft {

    /**
     * Determines if this Item has a special entity for when they are in the
     * world. Is called when a EntityItem is spawned in the world, if true and
     * Item#createCustomEntity returns non null, the EntityItem will be
     * destroyed and the new Entity will be added to the world.
     *
     * @param stack The current item stack
     * @return True of the item has a custom entity, If true,
     * Item#createCustomEntity will be called
     */
    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to
     * function normally. Called when the item it placed in a world.
     *
     * @param world    The world object
     * @param location The EntityItem object, useful for getting the position of
     *                 the entity
     * @param stack    The current item stack
     * @return A new Entity object to spawn or null
     */
    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        EntityItemFirestone entity = new EntityItemFirestone(world, location.posX, location.posY, location.posZ, stack);
        entity.motionX = location.motionX;
        entity.motionY = location.motionY;
        entity.motionZ = location.motionZ;
        entity.setDefaultPickupDelay();
        return entity;
    }

    /**
     * Called by CraftingManager to determine if an item is reparable.
     *
     * @return Always returns false for ItemFirestoneBase
     */
    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public void finalizeDefinition() {
        ItemStack ore = EnumOreMagic.FIRESTONE.getStack();
        ItemStack rawfirestone = RailcraftItems.FIRESTONE_RAW.getStack();
        if (Mod.anyLoaded(Mod.IC2, Mod.IC2_CLASSIC) && RailcraftConfig.getRecipeConfig("ic2.macerator.ores")) {
            IC2Plugin.addMaceratorRecipe(ore, rawfirestone);
        }
    }

}
