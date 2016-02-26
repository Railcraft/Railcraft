/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class PlayerPlugin {

    public static final GameProfile RAILCRAFT_USER_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("[Railcraft]".getBytes()), "[Railcraft]");

    public static EntityPlayer getFakePlayer(final WorldServer world, final double x, final double y, final double z) {
        EntityPlayer player = FakePlayerFactory.get(world, RAILCRAFT_USER_PROFILE);
        player.setPosition(x, y, z);
        return player;
    }

    public static void writeOwnerToNBT(NBTTagCompound nbt, GameProfile owner) {
        if (owner.getName() != null)
            nbt.setString("owner", owner.getName());
        if (owner.getId() != null)
            nbt.setString("ownerId", owner.getId().toString());
    }

    public static GameProfile readOwnerFromNBT(NBTTagCompound nbt) {
        String ownerName = "[Unknown]";
        if (nbt.hasKey("owner"))
            ownerName = nbt.getString("owner");
        UUID ownerUUID = null;
        if (nbt.hasKey("ownerId"))
            ownerUUID = UUID.fromString(nbt.getString("ownerId"));
        return new GameProfile(ownerUUID, ownerName);
    }

    public static String getUsername(World world, GameProfile gameProfile) {
        UUID playerId = gameProfile.getId();
        if (playerId != null) {
            EntityPlayer player = world.func_152378_a(playerId);
            if (player != null)
                return player.getDisplayName();
        }
        String username = gameProfile.getName();
        if (username != null && !username.equals(""))
            return username;
        return "[Unknown]";
    }

    public static String getUsername(World world, UUID playerId) {
        if (playerId != null) {
            EntityPlayer player = world.func_152378_a(playerId);
            if (player != null)
                return player.getDisplayName();
        }
        return "[Unknown]";
    }

    public static boolean isOwnerOrOp(GameProfile owner, EntityPlayer player) {
        return isOwnerOrOp(owner, player.getGameProfile());
    }

    public static boolean isOwnerOrOp(GameProfile owner, GameProfile accessor) {
        if (owner == null || accessor == null)
            return false;
        if (owner.equals(accessor))
            return true;
        return isPlayerOp(accessor);
    }

    public static boolean isSamePlayer(GameProfile a, GameProfile b) {
        if (a.getId() != null && b.getId() != null)
            return a.getId().equals(b.getId());
        return a.getName() != null && a.getName().equals(b.getName());
    }

    public static boolean isPlayerOp(GameProfile player) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            throw new RuntimeException("You derped up! Don't call this on the client!");
        return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152596_g(player);
    }

    public static boolean isPlayerConnected(GameProfile player) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152612_a(player.getName()) != null;
    }

    public static void swingItem(EntityPlayer player) {
        player.swingItem();
        if(player instanceof EntityPlayerMP && ((EntityPlayerMP) player).playerNetServerHandler != null) {
            ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S0BPacketAnimation(player, 0));
        }
    }

}
