/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockRailBase;

public class ItemTunnelBore extends ItemCart implements IMinecartItem {

    public ItemTunnelBore() {
        super(EnumCart.BORE);
        maxStackSize = 1;
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int i, int j, int k, int l, float par8, float par9, float par10) {
        Block block = world.getBlock(i, j, k);
        if (TrackTools.isRailBlock(block)) {
            if (Game.isHost(world) && !CartTools.isMinecartAt(world, i, j, k, 0, null, true)) {
                int meta = ((BlockRailBase) block).getBasicRailMetadata(world, null, i, j, k);
                if (meta == 0 || meta == 1) {
                    int playerYaw = -90 - MathHelper.floor_float(player.rotationYaw);
                    for (; playerYaw > 360; playerYaw -= 360);
                    for (; playerYaw < 0; playerYaw += 360);
                    ForgeDirection facing = ForgeDirection.EAST;
                    if (Math.abs(90 - playerYaw) <= 45) {
                        facing = ForgeDirection.NORTH;
                    } else if (Math.abs(180 - playerYaw) <= 45) {
                        facing = ForgeDirection.WEST;
                    } else if (Math.abs(270 - playerYaw) <= 45) {
                        facing = ForgeDirection.SOUTH;
                    }

                    if (meta == 0 && facing == ForgeDirection.WEST) {
                        facing = ForgeDirection.NORTH;
                    } else if (meta == 0 && facing == ForgeDirection.EAST) {
                        facing = ForgeDirection.SOUTH;
                    } else if (meta == 1 && facing == ForgeDirection.SOUTH) {
                        facing = ForgeDirection.EAST;
                    } else if (meta == 1 && facing == ForgeDirection.NORTH) {
                        facing = ForgeDirection.WEST;
                    }

//					System.out.println("PlayerYaw = " + playerYaw + " Yaw = " + facing + " Meta = " + meta);

                    EntityMinecart bore = new EntityTunnelBore(world, (float) i + 0.5F, (float) j, (float) k + 0.5F, facing);
                    CartTools.setCartOwner(bore, player);
                    world.spawnEntityInWorld(bore);
                }
            }
            itemstack.stackSize--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canBePlacedByNonPlayer(ItemStack cart) {
        return false;
    }

    @Override
    public EntityMinecart placeCart(GameProfile owner, ItemStack cart, World world, int i, int j, int k) {
        return null;
    }
}
