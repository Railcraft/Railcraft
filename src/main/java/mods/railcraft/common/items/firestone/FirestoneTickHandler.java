/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.firestone;

import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FirestoneTickHandler {

    private int clock;

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean shouldBurn(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        if (stack.getItem() == ItemFirestoneRaw.item) return true;
        if (stack.getItem() == ItemFirestoneCut.item) return true;
        if (stack.getItem() == ItemFirestoneCracked.item) return true;
        return InvTools.isStackEqualToBlock(stack, BlockOre.getBlock()) && stack.getItemDamage() == EnumOre.FIRESTONE.ordinal();
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT)
            return;
        clock++;
        if (clock % 4 != 0)
            return;
        EntityPlayer player = event.player;
        if (player.openContainer != player.inventoryContainer) return;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (shouldBurn(stack)) {
                boolean spawnedFire = false;
                for (int i = 0; i < stack.stackSize; i++) {
                    spawnedFire |= spawnFire(player);
                }
                if (spawnedFire && stack.isItemStackDamageable() && stack.getItemDamage() < stack.getMaxDamage() - 1)
                    InvTools.damageItem(stack, 1);
            }
        }
    }

    private boolean spawnFire(EntityPlayer player) {
        Random rnd = player.getRNG();
        int x = (int) Math.round(player.posX) - 5 + rnd.nextInt(12);
        int y = (int) Math.round(player.posY) - 5 + rnd.nextInt(12);
        int z = (int) Math.round(player.posZ) - 5 + rnd.nextInt(12);

        if (y < 1)
            y = 1;
        if (y > player.worldObj.getActualHeight())
            y = player.worldObj.getActualHeight() - 2;

        BlockPos pos = new BlockPos(x, y, z);
        return canBurn(player.worldObj, pos) && player.worldObj.setBlockState(pos, Blocks.fire.getDefaultState());
    }

    private boolean canBurn(World world, BlockPos pos) {
        if (!WorldPlugin.isBlockAir(world, pos))
            return false;
        for (EnumFacing side : EnumFacing.VALUES) {
            BlockPos offset = pos.offset(side);
            if (!WorldPlugin.isBlockAir(world, offset)) {
                Block block = WorldPlugin.getBlock(world, offset);
                if (block != Blocks.fire)
                    return true;
            }
        }
        return false;
    }

}
