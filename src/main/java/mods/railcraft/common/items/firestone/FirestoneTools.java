/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Predicate;

import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

/**
 * Created by CovertJaguar on 10/20/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class FirestoneTools {

    public static final Predicate<ItemStack> SPAWNS_FIRE = stack -> {
        if (InvTools.isEmpty(stack)) return false;
        if (RailcraftItems.FIRESTONE_RAW.isEqual(stack)) return true;
        if (RailcraftItems.FIRESTONE_CUT.isEqual(stack)) return true;
        if (RailcraftItems.FIRESTONE_CRACKED.isEqual(stack)) return true;
        return InvTools.isStackEqualToBlock(stack, EnumOreMagic.FIRESTONE.block()) && stack.getItemDamage() == EnumOreMagic.FIRESTONE.ordinal();
    };

    @Contract("_,_,null->false")
    public static boolean trySpawnFire(World world, BlockPos pos, @Nullable ItemStack stack) {
        if (InvTools.isEmpty(stack) || !SPAWNS_FIRE.test(stack))
            return false;
        boolean spawnedFire = false;
        for (int i = 0; i < sizeOf(stack); i++) {
            spawnedFire |= FirestoneTools.spawnFire(world, pos);
        }
        if (spawnedFire && stack.isItemStackDamageable() && stack.getItemDamage() < stack.getMaxDamage() - 1)
            InvTools.damageItem(stack, 1);
        return spawnedFire;
    }

    public static boolean spawnFire(World world, BlockPos pos) {
        Random rnd = MiscTools.RANDOM;
        int x = pos.getX() - 5 + rnd.nextInt(12);
        int y = pos.getY() - 5 + rnd.nextInt(12);
        int z = pos.getZ() - 5 + rnd.nextInt(12);

        if (y < 1)
            y = 1;
        if (y > world.getActualHeight())
            y = world.getActualHeight() - 2;

        BlockPos firePos = new BlockPos(x, y, z);
        return canBurn(world, firePos) && world.setBlockState(firePos, Blocks.FIRE.getDefaultState());
    }

    private static boolean canBurn(World world, BlockPos pos) {
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
