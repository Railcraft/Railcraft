/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.core.IWorldSupplier;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.carts.CartBaseLogic;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The basic logic class.
 */
public class Logic implements ITickable, IWorldNameable, ILogicContainer {
    protected final Adapter adapter;
    private int clock = MiscTools.RANDOM.nextInt();
    private List<Logic> subLogics = new ArrayList<>();
    private Optional<Logic> parentLogic = Optional.empty();

    /**
     * Helper function, for our ugly GUI factories mostly.
     *
     * Use with caution, will throw exceptions.
     *
     * @throws IllegalArgumentException if the logic doesn't implement the class or if the structure isn't valid.
     */
    public static <L extends Logic> L get(Class<L> logicClass, Object obj) {
        return ((ILogicContainer) obj).getLogic(logicClass)
                .orElseThrow(IllegalArgumentException::new);
    }

    Logic(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public <L> Optional<L> getLogic(Class<L> logicClass) {
        if (parentLogic.isPresent())
            return parentLogic.get().getLogic(logicClass);
        if (logicClass.isInstance(this))
            return Optional.of(logicClass.cast(this));
        if (!subLogics.isEmpty())
            return subLogics().stream().flatMap(Streams.toType(logicClass)).findFirst();
        return Optional.empty();
    }

    private Collection<Logic> subLogics() {
        List<Logic> logics = new ArrayList<>();
        for (Logic sub : subLogics) {
            logics.add(sub);
            logics.addAll(sub.subLogics());
        }
        return logics;
    }

    public Logic addSubLogic(Logic logic) {
        subLogics.add(logic);
        logic.parentLogic = Optional.of(this);
        return this;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void update() {
        clock++;
        if (Game.isHost(theWorldAsserted()))
            updateServer();
        else
            updateClient();
        subLogics.forEach(Logic::update);
    }

    protected void updateClient() { }

    protected void updateServer() { }

    protected int clock() {
        return clock;
    }

    protected boolean clock(int interval) {
        return clock % interval == 0;
    }

    @OverridingMethodsMustInvokeSuper
    public void placed(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        subLogics.forEach(l -> l.placed(state, placer, stack));
    }

    /**
     * @return true if the interaction resulted in something changing.
     */
    @OverridingMethodsMustInvokeSuper
    public boolean interact(EntityPlayer player, EnumHand hand) {
        return subLogics.stream().map(l -> l.interact(player, hand)).filter(b -> b).findFirst().orElse(false);
    }

    @OverridingMethodsMustInvokeSuper
    public void onStructureChanged(boolean isComplete, boolean isMaster, Object[] data) {
        subLogics.forEach(subLogic -> subLogic.onStructureChanged(isComplete, isMaster, data));
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
        if (Game.isHost(theWorldAsserted()))
            adapter.sendUpdateToClient();
        else
            adapter.updateModels();
    }

    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        subLogics.forEach(l -> l.writeToNBT(data));
    }

    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        subLogics.forEach(l -> l.readFromNBT(data));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        for (Logic l : subLogics) {
            l.writePacketData(data);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        for (Logic l : subLogics) {
            l.readPacketData(data);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        for (Logic l : subLogics) {
            l.writeGuiData(data);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        for (Logic l : subLogics) {
            l.readGuiData(data, sender);
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

        Optional<TileRailcraft> tile() {
            return Optional.empty();
        }

        abstract boolean isUsableByPlayer(EntityPlayer player);

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
            boolean isUsableByPlayer(EntityPlayer player) {
                return TileRailcraft.isUsableByPlayerHelper(tile, player);
            }
        }

        public static Adapter.Tile of(TileRailcraft tile) {
            return new Tile(tile);
        }

        public static Adapter of(CartBaseLogic cart) {
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
                void sendUpdateToClient() {
                    cart.sendUpdateToClient();
                }

                @Override
                void updateModels() { }

                @Override
                public @Nullable World theWorld() {
                    return cart.world;
                }

                @Override
                boolean isUsableByPlayer(EntityPlayer player) {
                    return !cart.isDead && player.getDistanceSq(cart) <= 64.0D;
                }
            };
        }
    }
}
