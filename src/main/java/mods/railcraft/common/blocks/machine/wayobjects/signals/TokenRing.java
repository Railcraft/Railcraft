/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.api.signals.ITokenRing;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.plugins.forge.EntitySearcher;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by CovertJaguar on 4/23/2015 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TokenRing implements ITokenRing {
    private final UUID uuid;
    private final Set<BlockPos> signals = new HashSet<>();
    private final Set<UUID> trackedCarts = new HashSet<>();
    private BlockPos centroid = BlockPos.ORIGIN;
    private final TokenManager.TokenWorldManager manager;
    private boolean isPairing;

    public TokenRing(TokenManager.TokenWorldManager manager, UUID uuid) {
        this.manager = manager;
        this.uuid = uuid;
    }

    public TokenRing(TokenManager.TokenWorldManager manager, UUID uuid, BlockPos origin) {
        this(manager, uuid);
        addSignal(origin);
    }

    @Override
    public void startPairing() {
        isPairing = true;
    }

    @Override
    public void endPairing() {
        isPairing = false;
    }

    @Override
    public boolean createPair(TileEntity other) {
        if (other instanceof TileSignalToken) {
            TileSignalToken tokenTile = (TileSignalToken) other;
            TokenRing otherRing = ((TileSignalToken) other).getTokenRing();
            otherRing.removeSignal(other.getPos());
            otherRing.endPairing();
//            TokenRing tokenRing = otherRing.signals.size() > signals.size() ? otherRing : this;
            TokenRing tokenRing = this;
            tokenTile.setTokenRingUUID(tokenRing.uuid);
            tokenRing.addSignal(tokenTile.getPos());
            return true;
        }
        return false;
    }

    public void tick(World world) {
        if (!signals.isEmpty()) {
            BlockPos origin = signals.stream().findAny().orElse(BlockPos.ORIGIN);
            AABBFactory aabbFactory = AABBFactory.start().createBoxForTileAt(origin);
            for (BlockPos pos : signals) {
                aabbFactory.expandToCoordinate(pos);
            }
            aabbFactory.grow(16);
            List<EntityMinecart> carts = EntitySearcher.findMinecarts().inArea(aabbFactory.build()).at(world);
            trackedCarts.retainAll(carts.stream().map(Entity::getUniqueID).collect(Collectors.toSet()));
        }
    }

    public boolean isOrphaned(World world) {
        return signals.stream().noneMatch(blockPos -> !WorldPlugin.isBlockLoaded(world, blockPos) || isTokenSignal(world, blockPos));
    }

    private boolean isTokenSignal(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return SignalVariant.TOKEN.isState(state) || SignalDualVariant.TOKEN.isState(state);
    }

    void loadSignals(Collection<BlockPos> signals) {
        this.signals.addAll(signals);
        centroid = calculateCentroid();
    }

    void loadCarts(Collection<UUID> carts) {
        trackedCarts.addAll(carts);
    }

    public boolean addSignal(BlockPos pos) {
        boolean changed = signals.add(pos);
        if (changed)
            signalsChanged();
        return changed;
    }

    public boolean removeSignal(BlockPos pos) {
        boolean changed = signals.remove(pos);
        if (changed)
            signalsChanged();
        return changed;
    }

    private void signalsChanged() {
        manager.markDirty();
        centroid = calculateCentroid();
    }

    public void markCart(EntityMinecart cart) {
        UUID cartID = cart.getUniqueID();
        if (trackedCarts.remove(cartID)) {
            manager.markDirty();
            return;
        }
        if (trackedCarts.add(cartID))
            manager.markDirty();
    }

    public Set<BlockPos> getSignals() {
        return Collections.unmodifiableSet(signals);
    }

    public Set<UUID> getTrackedCarts() {
        return Collections.unmodifiableSet(trackedCarts);
    }

    public SignalAspect getAspect() {
        if (isPairing)
            return SignalAspect.BLINK_YELLOW;
        if (signals.size() <= 1)
            return SignalAspect.BLINK_RED;
        return trackedCarts.isEmpty() ? SignalAspect.GREEN : SignalAspect.RED;
    }

    public UUID getUUID() {
        return uuid;
    }

    public BlockPos centroid() {
        return centroid;
    }

    @Nullable
    private BlockPos calculateCentroid() {
        double x = 0;
        double y = 0;
        double z = 0;
        for (BlockPos pos : signals) {
            x += pos.getX();
            y += pos.getY();
            z += pos.getZ();
        }
        int size = signals.size();
        x /= size;
        y /= size;
        z /= size;
        return new BlockPos(x, y, z);
    }
}
