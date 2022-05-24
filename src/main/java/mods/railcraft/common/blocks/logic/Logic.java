/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import com.mojang.authlib.GameProfile;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.core.IWorldSupplier;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.misc.Clock;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The basic logic class.
 */
public class Logic implements ITickable, ILogicContainer {

    protected final Adapter adapter;
    private final Clock clock = new Clock();
    private final List<Logic> children = new ArrayList<>();
    private Optional<Logic> parent = Optional.empty();

    /**
     * Helper function, for our ugly GUI factories mostly.
     *
     * Use with caution, will throw exceptions.
     *
     * @throws IllegalArgumentException if the logic doesn't implement the class or if the structure isn't valid.
     */
    public static <L extends Logic> L get(Class<L> logicClass, Object obj) {
        return ((ILogicContainer) obj).getLogic(logicClass)
                .orElseThrow(() -> new IllegalArgumentException("Logic Container does not contain specified Logic"));
    }

    public Logic(Adapter adapter) {
        this.adapter = adapter;
    }

    public final Logic root() {
        return parent.map(Logic::root).orElse(this);
    }

    @Override
    public final <L> Optional<L> getLogic(Class<L> logicClass) {
        return root().logics().flatMap(Streams.toType(logicClass)).findFirst();
    }

    public Stream<Logic> logics() {
        return Stream.concat(Stream.of(this), children.stream().flatMap(Logic::logics));
    }

    public Logic addLogic(Logic logic) {
        children.add(logic);
        logic.parent = Optional.of(this);
        return this;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void update() {
        clock.tick();
        ifHost(this::updateServer);
        ifClient(this::updateClient);
        children.forEach(Logic::update);
    }

    protected void updateClient() {}

    protected void updateServer() {}

    protected Clock clock() {
        return clock;
    }

    @OverridingMethodsMustInvokeSuper
    public void placed(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        children.forEach(l -> l.placed(state, placer, stack));
    }

    /**
     * @return true if the interaction resulted in something changing.
     */
    @OverridingMethodsMustInvokeSuper
    public boolean interact(EntityPlayer player, EnumHand hand) {
        return children.stream().map(l -> l.interact(player, hand)).filter(b -> b).findFirst().orElse(false);
    }

    @OverridingMethodsMustInvokeSuper
    public void onStructureChanged(boolean isComplete, boolean isMaster, Object[] data) {
        children.forEach(subLogic -> subLogic.onStructureChanged(isComplete, isMaster, data));
    }

    @Override
    public final @Nullable World theWorld() {
        return adapter.theWorld();
    }

    public final double getX() {
        return adapter.getX();
    }

    public final double getY() {
        return adapter.getY();
    }

    public final double getZ() {
        return adapter.getZ();
    }

    @SuppressWarnings("unused")
    public final BlockPos getPos() {
        return adapter.getPos();
    }

    @Override
    public final String getName() {
        return adapter.getName();
    }

    @Override
    public final boolean hasCustomName() {
        return adapter.hasCustomName();
    }

    @Override
    public final ITextComponent getDisplayName() {
        return adapter.getDisplayName();
    }

    public @Nullable EnumGui getGUI() {
        return null;
    }

    @Override
    public void sendUpdateToClient() {
        adapter.sendUpdateToClient();
    }

    public void sendUpdateOrUpdateModels() {
        ifHost(adapter::sendUpdateToClient);
        ifClient(adapter::updateModels);
    }

    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        children.forEach(l -> l.writeToNBT(data));
    }

    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        children.forEach(l -> l.readFromNBT(data));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        for (Logic l : children) {
            l.writePacketData(data);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        for (Logic l : children) {
            l.readPacketData(data);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        for (Logic l : children) {
            l.writeGuiData(data);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        for (Logic l : children) {
            l.readGuiData(data, sender);
        }
    }

    @Override
    public WorldOperatorLogic fromWorld() {
        return new WorldOperatorLogic(theWorld());
    }

    public static class WorldOperatorLogic extends WorldOperator {

        public WorldOperatorLogic(@Nullable World world) {
            super(world);
        }

        public <L> Optional<L> getLogic(@Nullable BlockPos pos, Class<L> logicClass) {
            return getTile(pos, TileLogic.class).flatMap(tile -> tile.getLogic(logicClass));
        }
    }

    public boolean isBlock() {
        return adapter instanceof Adapter.Tile;
    }

    public abstract static class Adapter implements IWorldSupplier, IWorldNameable {

        abstract double getX();

        abstract double getY();

        abstract double getZ();

        abstract BlockPos getPos();

        abstract void sendUpdateToClient();

        abstract void updateModels();

        abstract Object getContainer();

        abstract GameProfile getOwner();

        Optional<TileRailcraft> tile() {
            return Optional.empty();
        }

        public abstract boolean isUsableByPlayer(EntityPlayer player);

        public static class Tile extends Adapter {
            private final TileRailcraft tile;

            public Tile(TileRailcraft tile) {
                this.tile = tile;
            }

            @Override
            Object getContainer() {
                return tile;
            }

            @Override
            public @Nullable World theWorld() {
                return tile.theWorld();
            }

            @Override
            double getX() {
                return tile.getX() + 0.5;
            }

            @Override
            double getY() {
                return tile.getY() + 0.5;
            }

            @Override
            double getZ() {
                return tile.getZ() + 0.5;
            }

            @Override
            BlockPos getPos() {
                return tile.getPos();
            }

            @Override
            public String getName() {
                return tile.getName();
            }

            @Override
            public ITextComponent getDisplayName() {
                return tile.getDisplayName();
            }

            @Override
            public boolean hasCustomName() {
                return tile.hasCustomName();
            }

            @Override
            GameProfile getOwner() {
                return tile.getOwner();
            }

            @Override
            void sendUpdateToClient() {
                tile.sendUpdateToClient();
            }

            @Override
            void updateModels() {
                tile.markBlockForUpdate();
            }

            void notifyNeighbors() {
                tile.notifyBlocksOfNeighborChange();
            }

            @Override
            Optional<TileRailcraft> tile() {
                return Optional.of(tile);
            }

            @Override
            public boolean isUsableByPlayer(EntityPlayer player) {
                return TileRailcraft.isUsableByPlayerHelper(tile, player);
            }
        }

        public static Adapter.Tile of(TileRailcraft tile) {
            return new Tile(tile);
        }

        public static Adapter from(ILogicContainer logicContainer) {
            if (logicContainer instanceof EntityMinecart)
                return of((EntityMinecart) logicContainer);
            if (logicContainer instanceof TileLogic)
                return of((TileLogic) logicContainer);
            if (logicContainer instanceof Logic)
                return ((Logic) logicContainer).adapter;
            throw new IllegalArgumentException("Invalid Logic Container: " + logicContainer.getClass());
        }

        public static Adapter of(EntityMinecart cart) {
            return new Adapter() {
                @Override
                Object getContainer() {
                    return cart;
                }

                @Override
                double getX() {
                    return cart.posX;
                }

                @Override
                double getY() {
                    return cart.posY;
                }

                @Override
                double getZ() {
                    return cart.posZ;
                }

                @Override
                BlockPos getPos() {
                    return cart.getPosition();
                }

                @Override
                public String getName() {
                    return cart.getName();
                }

                @Override
                public ITextComponent getDisplayName() {
                    return cart.getDisplayName();
                }

                @Override
                public boolean hasCustomName() {
                    return cart.hasCustomName();
                }

                @Override
                GameProfile getOwner() {
                    return CartToolsAPI.getCartOwner(cart);
                }

                @Override
                void sendUpdateToClient() {
                    if (cart.isAddedToWorld() && cart.isEntityAlive())
                        PacketBuilder.instance().sendEntitySync(cart);
                }

                @Override
                void updateModels() {}

                @Override
                public @Nullable World theWorld() {
                    return cart.world;
                }

                @Override
                public boolean isUsableByPlayer(EntityPlayer player) {
                    return !cart.isDead && player.getDistanceSq(cart) <= 64.0D;
                }
            };
        }
    }
}
