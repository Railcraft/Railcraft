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
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FirestoneTickHandler {

    private int clock;

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean shouldSpawnFire(@Nullable ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        if (RailcraftItems.FIRESTONE_RAW.isEqual(stack)) return true;
        if (RailcraftItems.FIRESTONE_CUT.isEqual(stack)) return true;
        if (RailcraftItems.FIRESTONE_CRACKED.isEqual(stack)) return true;
        return InvTools.isStackEqualToBlock(stack, EnumOreMagic.FIRESTONE.block()) && stack.getItemDamage() == EnumOreMagic.FIRESTONE.ordinal();
    }

    @SubscribeEvent
    public void tick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (Game.isClient(entity.worldObj))
            return;
        clock++;
        if (clock % 4 != 0)
            return;
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).openContainer != ((EntityPlayer) entity).inventoryContainer)
            return;
        IInventoryObject inv = InvTools.getInventory(entity);
        if (inv != null) {
            for (IInvSlot slot : InventoryIterator.getRailcraft(inv)) {
                ItemStack stack = slot.getStack();
                if (shouldSpawnFire(stack)) {
                    boolean spawnedFire = false;
                    for (int i = 0; i < stack.stackSize; i++) {
                        spawnedFire |= spawnFire(entity);
                    }
                    if (spawnedFire && stack.isItemStackDamageable() && stack.getItemDamage() < stack.getMaxDamage() - 1)
                        InvTools.damageItem(stack, 1);
                }
            }
        }
    }

    private boolean spawnFire(EntityLivingBase entity) {
        Random rnd = entity.getRNG();
        int x = (int) Math.round(entity.posX) - 5 + rnd.nextInt(12);
        int y = (int) Math.round(entity.posY) - 5 + rnd.nextInt(12);
        int z = (int) Math.round(entity.posZ) - 5 + rnd.nextInt(12);

        if (y < 1)
            y = 1;
        if (y > entity.worldObj.getActualHeight())
            y = entity.worldObj.getActualHeight() - 2;

        BlockPos pos = new BlockPos(x, y, z);
        return canBurn(entity.worldObj, pos) && entity.worldObj.setBlockState(pos, Blocks.FIRE.getDefaultState());
    }

    private boolean canBurn(World world, BlockPos pos) {
        if (!WorldPlugin.isBlockAir(world, pos))
            return false;
        for (EnumFacing side : EnumFacing.VALUES) {
            BlockPos offset = pos.offset(side);
            if (!WorldPlugin.isBlockAir(world, offset)) {
                Block block = WorldPlugin.getBlock(world, offset);
                if (block != Blocks.FIRE)
                    return true;
            }
        }
        return false;
    }

}
