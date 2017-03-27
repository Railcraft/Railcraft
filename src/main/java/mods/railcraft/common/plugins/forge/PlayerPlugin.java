/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class PlayerPlugin {
    public static final String UNKNOWN_PLAYER_NAME = "[Unknown]";

    public static void writeOwnerToNBT(@Nonnull NBTTagCompound nbt, @Nonnull GameProfile owner) {
        if (owner.getName() != null)
            nbt.setString("owner", owner.getName());
        if (owner.getId() != null)
            nbt.setString("ownerId", owner.getId().toString());
    }

    @Nonnull
    public static GameProfile readOwnerFromNBT(@Nonnull NBTTagCompound nbt) {
        String ownerName = UNKNOWN_PLAYER_NAME;
        if (nbt.hasKey("owner"))
            ownerName = nbt.getString("owner");
        UUID ownerUUID = null;
        if (nbt.hasKey("ownerId"))
            ownerUUID = UUID.fromString(nbt.getString("ownerId"));
        return new GameProfile(ownerUUID, ownerName);
    }

    @Nullable
    public static EntityPlayer getPlayer(@Nonnull World world, @Nonnull GameProfile gameProfile) {
        UUID playerId = gameProfile.getId();
        if (playerId != null) {
            EntityPlayer player = world.getPlayerEntityByUUID(playerId);
            if (player != null)
                return player;
        }
        return null;
    }

    public static String getUsername(@Nonnull World world, @Nonnull GameProfile gameProfile) {
        UUID playerId = gameProfile.getId();
        if (playerId != null) {
            EntityPlayer player = world.getPlayerEntityByUUID(playerId);
            if (player != null)
                return player.getDisplayNameString();
        }
        String username = gameProfile.getName();
        if (username != null && !username.equals(""))
            return username;
        return UNKNOWN_PLAYER_NAME;
    }

    @SuppressWarnings("unused")
    public static String getUsername(World world, @Nullable UUID playerId) {
        if (playerId != null) {
            EntityPlayer player = world.getPlayerEntityByUUID(playerId);
            if (player != null)
                return player.getDisplayNameString();
        }
        return UNKNOWN_PLAYER_NAME;
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
        return getPermissionLevel(player) > 2;
    }

    public static int getPermissionLevel(GameProfile gameProfile) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            throw new RuntimeException("You derped up! Don't call this on the client!");
        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (mcServer != null && mcServer.getPlayerList().canSendCommands(gameProfile)) {
            UserListOpsEntry opsEntry = mcServer.getPlayerList().getOppedPlayers().getEntry(gameProfile);
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

}
