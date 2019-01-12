/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.core.INetworkedObject;
import mods.railcraft.api.core.IWorldSupplier;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.carts.CartBaseLogic;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Optional;

/**
 * The basic logic class.
 */
public class Logic implements ITickable, INetworkedObject<RailcraftInputStream,
        RailcraftOutputStream>, IWorldNameable, IGuiReturnHandler {
    protected final Adapter adapter;
    private int clock = MiscTools.RANDOM.nextInt();

    Logic(Adapter adapter) {
        this.adapter = adapter;
    }

    public <L> Optional<L> getLogic(Class<L> logicClass) {
        if (logicClass.isInstance(this))
            return Optional.of(logicClass.cast(this));
        return Optional.empty();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void update() {
        clock++;
        if (Game.isHost(theWorldAsserted()))
            updateServer();
        else
            updateClient();
    }

    void updateClient() { }

    void updateServer() { }

    protected int clock() {
        return clock;
    }

    protected boolean clock(int interval) {
        return clock % interval == 0;
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

    @Override
    public void sendUpdateToClient() {
        adapter.sendUpdateToClient();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        return data;
    }

    public void readFromNBT(NBTTagCompound data) { }

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

        @Nullable TileRailcraft tile() {
            return null;
        }

        abstract boolean isUsableByPlayer(EntityPlayer player);

        private static class Tile extends Adapter {
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

            @Override
            @Nullable TileRailcraft tile() {
                return tile;
            }

            @Override
            boolean isUsableByPlayer(EntityPlayer player) {
                return TileRailcraft.isUsableByPlayerHelper(tile, player);
            }
        }

        public static Adapter of(TileRailcraft tile) {
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
