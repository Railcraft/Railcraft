/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.IInterModMessageHandler;
import mods.railcraft.common.core.InterModMessageRegistry;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.misc.BallastRegistry;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
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
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.dec;

public class ItemTunnelBore extends ItemCart {

    public ItemTunnelBore(IRailcraftCartContainer cart) {
        super(cart);
        maxStackSize = 1;
    }

    @Override
    public void initializeDefinition() {
        super.initializeDefinition();
        InterModMessageRegistry.getInstance().register("ballast", mess -> {
            String[] tokens = Iterables.toArray(IInterModMessageHandler.SPLITTER.split(mess.getStringValue()), String.class);
            if (tokens.length != 2) {
                Game.log().msg(Level.WARN, String.format("Mod %s attempted to register a ballast, but failed: %s", mess.getSender(), mess.getStringValue()));
                return;
            }
            String blockName = tokens[0];
            Integer metadata = Ints.tryParse(tokens[1]);
            Block block;
            if (blockName == null || metadata == null || (block = Block.getBlockFromName(blockName)) == null) {
                Game.log().msg(Level.WARN, String.format("Mod %s attempted to register a ballast, but failed: %s", mess.getSender(), mess.getStringValue()));
                return;
            }
            BallastRegistry.registerBallast(block, metadata);
            Game.log().msg(Level.DEBUG, String.format("Mod %s registered %s as a valid ballast", mess.getSender(), mess.getStringValue()));
        });
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(),
                "ICI",
                "FCF",
                " S ",
                'I', "blockSteel",
                'S', Items.CHEST_MINECART,
                'F', Blocks.FURNACE,
                'C', Items.MINECART);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState existingState = WorldPlugin.getBlockState(world, pos);
        if (TrackTools.isRail(existingState)) {
            if (Game.isHost(world) && EntitySearcher.findMinecarts().around(pos).in(world).isEmpty()) {
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
            dec(player.getHeldItem(hand));
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public boolean canBePlacedByNonPlayer(ItemStack cart) {
        return false;
    }

    @Override
    public @Nullable EntityMinecart placeCart(GameProfile owner, ItemStack cartStack, World world, BlockPos pos) {
        return null;
    }
}
