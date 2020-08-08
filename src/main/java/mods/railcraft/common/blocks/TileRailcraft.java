/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
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
import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.common.blocks.interfaces.ITile;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class TileRailcraft extends TileEntity implements INetworkedObject<RailcraftInputStream, RailcraftOutputStream>, ITile {

    protected final AdjacentTileCache tileCache = new AdjacentTileCache(this);

    private GameProfile owner = RailcraftFakePlayer.UNKNOWN_USER_PROFILE;
    private @Nullable UUID uuid;

    private String customName = "";

    public static boolean isUsableByPlayerHelper(TileEntity tile, EntityPlayer player) {
        return !tile.isInvalid() && tile.getWorld().getTileEntity(tile.getPos()) == tile && player.getDistanceSq(tile.getPos()) <= 64;
    }

    public UUID getUUID() {
        if (uuid == null)
            uuid = UUID.randomUUID();
        return uuid;
    }

    public IBlockState getBlockState() {
        if (isInvalid()) {
            if (Game.DEVELOPMENT_VERSION) {
                Game.log().msg(Level.ERROR, "Tried to access invalid blockstate on " + getClass() + " at " + getPos() + " hashcode " + System.identityHashCode(this));
                throw new RuntimeException();
            }
            return Blocks.AIR.getDefaultState();
        }
        return WorldPlugin.getBlockState(getWorld(), getPos());
    }

    public AdjacentTileCache getTileCache() {
        return tileCache;
    }

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
            byte[] syncData = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(syncData);
            nbt.setByteArray("sync", syncData);
        } catch (IOException e) {
            Game.log().throwable("Error constructing tile packet: {0}", e, getClass());
            if (Game.DEVELOPMENT_VERSION)
                throw new RuntimeException(e);
        } finally {
            byteBuf.release();
        }
        return nbt;
    }

    @Override
    public final void handleUpdateTag(NBTTagCompound nbt) {
        byte[] bytes = nbt.getByteArray("sync");
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             RailcraftInputStream data = new RailcraftInputStream(in)) {
            readPacketData(data);
        } catch (IOException e) {
            Game.log().throwable("Error decoding tile packet: {0}", e, getClass());
            if (Game.DEVELOPMENT_VERSION)
                throw new RuntimeException(e);
        }
    }

    @Override
    public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void markBlockForUpdate() {
//        System.out.println("updating");
        if (hasWorld()) {
            IBlockState state = getBlockState();
            if (state.getBlock().hasTileEntity(state))
                world.notifyBlockUpdate(getPos(), state, state, 8);
        }
    }

    @Override
    public void sendUpdateToClient() {
        PacketBuilder.instance().sendTileEntityPacket(this);
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        if (placer instanceof EntityPlayer)
            owner = ((EntityPlayer) placer).getGameProfile();
        notifyBlocksOfNeighborChange();
    }

    @OverridingMethodsMustInvokeSuper
    @Override
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
        setOwner(RailcraftFakePlayer.UNKNOWN_USER_PROFILE);
    }

    protected final void setOwner(GameProfile profile) {
        owner = profile;
//        sendUpdateToClient();  Sending this when a te is initialized will cause client net handler errors because the tile is not yet on client
    }

    @Override
    public final GameProfile getOwner() {
        return owner;
    }

    public final boolean isOwner(GameProfile player) {
        return PlayerPlugin.isSamePlayer(owner, player);
    }

    public String getLocalizationTag() {
        return getBlockType().getTranslationKey() + ".name";
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
    @OverridingMethodsMustInvokeSuper
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        PlayerPlugin.writeOwnerToNBT(data, owner);

        NBTPlugin.writeUUID(data, "uuid", uuid);
        if (!customName.isEmpty())
            data.setString("customName", customName);
        return data;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
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

    @Override
    public final @Nullable World theWorld() {
        return world;
    }

    @Override
    public boolean hasCustomName() {
        return !customName.isEmpty();
    }

    @SuppressWarnings("unused")
    public void setCustomName(@Nullable String name) {
        customName = Strings.nullToEmpty(name);
    }

    @Override
    public String getName() {
        return hasCustomName() ? customName : getLocalizationTag();
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return hasCustomName() ? new TextComponentString(customName) : new TextComponentTranslation(getLocalizationTag());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
