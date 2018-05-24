/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import mods.railcraft.api.core.INetworkedObject;
import mods.railcraft.api.core.IOwnable;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class RailcraftTileEntity extends TileEntity implements INetworkedObject<RailcraftInputStream, RailcraftOutputStream>, IOwnable {

    protected final AdjacentTileCache tileCache = new AdjacentTileCache(this);
    @Nonnull
    private GameProfile owner = new GameProfile(null, RailcraftConstantsAPI.RAILCRAFT_PLAYER);
    @Nullable
    private UUID uuid;
    @Nonnull
    private String customName = "";

    public static boolean isUsableByPlayerHelper(TileEntity tile, EntityPlayer player) {
        return !tile.isInvalid() && tile.getWorld().getTileEntity(tile.getPos()) == tile && player.getDistanceSq(tile.getPos()) <= 64;
    }

    public UUID getUUID() {
        if (uuid == null)
            uuid = UUID.randomUUID();
        return uuid;
    }

//    @Nullable
    public IBlockState getBlockState() {
        if (isInvalid()) {
            if (Game.DEVELOPMENT_ENVIRONMENT) {
                Game.log(Level.ERROR, "Tried to access invalid blockstate on " + getClass() + " at " + getPos() + " hashcode " + System.identityHashCode(this));
                throw new RuntimeException();
            }
            return Blocks.AIR.getDefaultState();
        }
        return WorldPlugin.getBlockState(getWorld(), getPos());
    }

    public AdjacentTileCache getTileCache() {
        return tileCache;
    }

    @Nonnull
    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        ByteBuf byteBuf = Unpooled.buffer();
        try (ByteBufOutputStream out = new ByteBufOutputStream(byteBuf);
             RailcraftOutputStream data = new RailcraftOutputStream(out)) {
            writePacketData(data);
        } catch (IOException e) {
            Game.logThrowable("Error constructing tile packet: {0}", e, getClass());
            if (Game.DEVELOPMENT_ENVIRONMENT)
                throw new RuntimeException(e);
        }
        nbt.setByteArray("sync", byteBuf.array());
        return nbt;
    }

    @Override
    public final void handleUpdateTag(NBTTagCompound nbt) {
        byte[] bytes = nbt.getByteArray("sync");
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             RailcraftInputStream data = new RailcraftInputStream(in)) {
            readPacketData(data);
        } catch (IOException e) {
            Game.logThrowable("Error decoding tile packet: {0}", e, getClass());
            if (Game.DEVELOPMENT_ENVIRONMENT)
                throw new RuntimeException(e);
        }
    }

    @Override
    public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
//        data.writeUTF(owner);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
//        owner = data.readUTF();
    }

    public void markBlockForUpdate() {
//        System.out.println("updating");
        if (world != null) {
            IBlockState state = getBlockState();
            if (state != null)
                world.notifyBlockUpdate(getPos(), state, state, 8);
        }
    }

    public void notifyBlocksOfNeighborChange() {
        if (world != null)
            WorldPlugin.notifyBlocksOfNeighborChange(world, getPos(), getBlockType());
    }

    @Override
    public void sendUpdateToClient() {
        PacketBuilder.instance().sendTileEntityPacket(this);
    }

    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        if (placer instanceof EntityPlayer)
            owner = ((EntityPlayer) placer).getGameProfile();
        notifyBlocksOfNeighborChange();
    }

    public void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos neighborPos) {
        tileCache.resetTimers();
    }

    @Override
    public void invalidate() {
        tileCache.purge();
        super.invalidate();
    }

    @Override
    public void validate() {
        tileCache.purge();
        super.validate();
    }

    public final int getDimension() {
        if (world == null || world.provider == null)
            return 0;
        return world.provider.getDimension();
    }

    public final void clearOwner() {
        setOwner(new GameProfile(null, RailcraftConstantsAPI.RAILCRAFT_PLAYER));
    }

    protected final void setOwner(GameProfile profile) {
        owner = profile;
        sendUpdateToClient();
    }

    @Override
    public final GameProfile getOwner() {
        return owner;
    }

    public final boolean isOwner(GameProfile player) {
        return PlayerPlugin.isSamePlayer(owner, player);
    }

    @Nonnull
    public String getLocalizationTag() {
        return getBlockType().getUnlocalizedName();
    }

    public List<String> getDebugOutput() {
        List<String> debug = new ArrayList<>();
        debug.add("Railcraft Tile Entity Data Dump");
        debug.add("Object: " + this);
        if (!world.getGameRules().getBoolean("reducedDebugInfo"))
            debug.add(String.format("Coordinates: d=%d, %s", getDimension(), getPos()));
        debug.add("Owner: " + owner.getName());
        debug.addAll(tileCache.getDebugOutput());
        return debug;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        PlayerPlugin.writeOwnerToNBT(data, owner);

        NBTPlugin.writeUUID(data, "uuid", uuid);
        if (!customName.isEmpty())
            data.setString("customName", customName);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        owner = PlayerPlugin.readOwnerFromNBT(data);
        uuid = NBTPlugin.readUUID(data, "uuid");
        customName = data.getString("customName");
    }

    public final int getX() {
        return getPos().getX();
    }

    public final int getY() {
        return getPos().getY();
    }

    public final int getZ() {
        return getPos().getZ();
    }

    @Nullable
    @Override
    public final World theWorld() {
        return world;
    }

    @Deprecated //useless
    public short getId() {
        return -1;
    }

    @Override
    public boolean hasCustomName() {
        return !customName.isEmpty();
    }

    public void setCustomName(@Nullable String name) {
        customName = Strings.nullToEmpty(name);
    }

    @Override
    public String getName() {
        return hasCustomName() ? customName : getLocalizationTag();
    }

    @Override
    public ITextComponent getDisplayName() {
        return hasCustomName() ? new TextComponentString(customName) : new TextComponentTranslation(getLocalizationTag());
    }
}
