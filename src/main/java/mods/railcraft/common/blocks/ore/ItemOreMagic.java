/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.ItemBlockRailcraftSubtyped;
import mods.railcraft.common.items.firestone.FirestoneTools;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemOreMagic extends ItemBlockRailcraftSubtyped<BlockOreMagic> {

    public ItemOreMagic(BlockOreMagic block) {
        super(block);
    }

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
        return !InvTools.isEmpty(stack) && stack.getItemDamage() == EnumOreMagic.FIRESTONE.ordinal();
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to
     * function normally. Called when the item it placed in a world.
     *
     * @param world    The world object
     * @param original The EntityItem object, useful for getting the position of
     *                 the entity
     * @param stack    The current item stack
     * @return A new Entity object to spawn or null
     */
    @Override
    public @Nullable Entity createEntity(World world, Entity original, ItemStack stack) {
        if (!hasCustomEntity(stack))
            return null;
        return FirestoneTools.createEntityItem(world, original, stack);
    }

}
