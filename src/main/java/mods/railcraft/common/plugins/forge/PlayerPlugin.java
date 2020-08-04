/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import com.mojang.authlib.GameProfile;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.api.items.ActivationBlockingItem;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Annotations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class PlayerPlugin {

    public static void writeOwnerToNBT(NBTTagCompound nbt, GameProfile owner) {
        if (owner.getName() != null)
            nbt.setString("owner", owner.getName());
        if (owner.getId() != null)
            nbt.setString("ownerId", owner.getId().toString());
    }

    public static GameProfile readOwnerFromNBT(NBTTagCompound nbt) {
        String ownerName = RailcraftConstantsAPI.UNKNOWN_PLAYER;
        if (nbt.hasKey("owner"))
            ownerName = nbt.getString("owner");
        UUID ownerUUID = null;
        if (nbt.hasKey("ownerId"))
            ownerUUID = UUID.fromString(nbt.getString("ownerId"));
        return new GameProfile(ownerUUID, ownerName);
    }

    public static @Nullable EntityPlayer getPlayer(World world, GameProfile gameProfile) {
        UUID playerId = gameProfile.getId();
        if (playerId != null) {
            EntityPlayer player = world.getPlayerEntityByUUID(playerId);
            if (player != null)
                return player;
        }
        return null;
    }

    public static EntityPlayer getOwnerEntity(GameProfile owner, WorldServer world, BlockPos pos) {
        EntityPlayer player = null;
        if (!RailcraftConstantsAPI.UNKNOWN_PLAYER.equals(owner.getName()))
            player = PlayerPlugin.getPlayer(world, owner);
        if (player == null)
            player = RailcraftFakePlayer.get(world, pos);
        return player;
    }

    public static String getUsername(World world, GameProfile gameProfile) {
        UUID playerId = gameProfile.getId();
        if (playerId != null) {
            EntityPlayer player = world.getPlayerEntityByUUID(playerId);
            if (player != null)
                return player.getDisplayNameString();
        }
        String username = gameProfile.getName();
        if (!Strings.isEmpty(username))
            return username;
        return RailcraftConstantsAPI.UNKNOWN_PLAYER;
    }

    @SuppressWarnings("unused")
    public static String getUsername(World world, @Nullable UUID playerId) {
        if (playerId != null) {
            EntityPlayer player = world.getPlayerEntityByUUID(playerId);
            if (player != null)
                return player.getDisplayNameString();
        }
        return RailcraftConstantsAPI.UNKNOWN_PLAYER;
    }

    public static boolean isOwnerOrOp(GameProfile owner, EntityPlayer player) {
        return isOwnerOrOp(owner, player.getGameProfile());
    }

    public static boolean isOwnerOrOp(@Nullable GameProfile owner, @Nullable GameProfile accessor) {
        return !(owner == null || accessor == null) && (owner.equals(accessor) || isPlayerOp(accessor));
    }

    public static boolean isSamePlayer(GameProfile a, GameProfile b) {
        if (a.getId() != null && b.getId() != null)
            return a.getId().equals(b.getId());
        return a.getName() != null && a.getName().equals(b.getName());
    }

    public static boolean isPlayerOp(GameProfile player) {
        return getPermissionLevel(player) >= 2;
    }

    public static int getPermissionLevel(GameProfile gameProfile) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            throw new RuntimeException("You derped up! Don't call this on the client!");
        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (mcServer != null && mcServer.getPlayerList().canSendCommands(gameProfile)) {
            UserListOpsEntry opsEntry = mcServer.getPlayerList().getOppedPlayers().getEntry(gameProfile);
            //noinspection ConstantConditions
            return opsEntry != null ? opsEntry.getPermissionLevel() : 0;
        }
        return 0;
    }

    public static boolean isPlayerConnected(GameProfile player) {
        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        return mcServer != null && mcServer.getPlayerList().getPlayerByUsername(player.getName()) != null;
    }

    public static void swingArm(EntityPlayer player, EnumHand hand) {
        player.swingArm(hand);
//        if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).playerNetServerHandler != null) {
//            ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new SPacketAnimation(player, 0));
//        }
    }

    public static boolean doesItemBlockActivation(EntityPlayer player, EnumHand hand) {
        if (player.isSneaking() || hand == EnumHand.OFF_HAND)
            return true;
        ItemStack heldItem = player.getHeldItem(hand);
        if (!InvTools.isEmpty(heldItem)) {
            return TrackTools.isRail(heldItem)
                    || Annotations.isAnnotatedDeepSearch(ActivationBlockingItem.class, heldItem.getItem());
        }
        return false;
    }

    public static GameProfile fillGameProfile(GameProfile profile) {
        String name = profile.getName();
        UUID id = profile.getId();
        if (!StringUtils.isBlank(name) && id != null) {
            return profile;
        }
        PlayerProfileCache cache = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache();

        GameProfile filled = id == null ? cache.getGameProfileForUsername(name) : cache.getProfileByUUID(id);
        return filled == null ? profile : filled;
    }
}
