/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.mojang.authlib.GameProfile;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.dec;

public class ItemTunnelBore extends ItemCart {

    public ItemTunnelBore(IRailcraftCartContainer cart) {
        super(cart);
        maxStackSize = 1;
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(getStack(),
                "ICI",
                "FCF",
                " S ",
                'I', "blockSteel",
                'S', Items.CHEST_MINECART,
                'F', Blocks.FURNACE,
                'C', Items.MINECART);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState existingState = WorldPlugin.getBlockState(world, pos);
        if (TrackTools.isRailBlock(existingState)) {
            if (Game.isHost(world) && !CartToolsAPI.isMinecartAt(world, pos, 0)) {
                BlockRailBase.EnumRailDirection trackShape = TrackTools.getTrackDirection(world, pos, existingState);
                if (TrackShapeHelper.isLevelStraight(trackShape)) {
                    EnumFacing playerFacing = MiscTools.getHorizontalSideFacingPlayer(player).getOpposite();

                    if (trackShape == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
                        if (playerFacing == EnumFacing.WEST)
                            playerFacing = EnumFacing.NORTH;
                        else if (playerFacing == EnumFacing.EAST)
                            playerFacing = EnumFacing.SOUTH;
                    } else if (trackShape == BlockRailBase.EnumRailDirection.EAST_WEST) {
                        if (playerFacing == EnumFacing.SOUTH)
                            playerFacing = EnumFacing.EAST;
                        else if (playerFacing == EnumFacing.NORTH)
                            playerFacing = EnumFacing.WEST;
                    }

//					System.out.println("PlayerYaw = " + playerYaw + " Yaw = " + facing + " Meta = " + meta);

                    EntityMinecart bore = new EntityTunnelBore(world, (float) pos.getX() + 0.5F, (float) pos.getY(), (float) pos.getZ() + 0.5F, playerFacing);
                    CartToolsAPI.setCartOwner(bore, player);
                    world.spawnEntity(bore);
                }
            }
            dec(stack);
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public boolean canBePlacedByNonPlayer(ItemStack cart) {
        return false;
    }

    @Nullable
    @Override
    public EntityMinecart placeCart(GameProfile owner, ItemStack cartStack, World world, BlockPos pos) {
        return null;
    }
}
