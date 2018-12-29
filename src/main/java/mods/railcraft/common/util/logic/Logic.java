/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.logic;

import mods.railcraft.api.core.INetworkedObject;
import mods.railcraft.api.core.IWorldSupplier;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
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
        return Optional.of(logicClass.cast(this));
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

        public static Adapter of(EntityMinecart entity) {
            return new Adapter() {
                @Override
                Object getContainer() {
                    return entity;
                }

                @Override
                double getX() {
                    return entity.posX;
                }

                @Override
                double getY() {
                    return entity.posY;
                }

                @Override
                double getZ() {
                    return entity.posZ;
                }

                @Override
                BlockPos getPos() {
                    return entity.getPosition();
                }

                @Override
                public String getName() {
                    return entity.getName();
                }

                @Override
                public ITextComponent getDisplayName() {
                    return entity.getDisplayName();
                }

                @Override
                public boolean hasCustomName() {
                    return entity.hasCustomName();
                }

                @Override
                void sendUpdateToClient() {
                    // TODO
                }

                @Override
                public @Nullable World theWorld() {
                    return entity.world;
                }

                @Override
                boolean isUsableByPlayer(EntityPlayer player) {
                    return !entity.isDead && player.getDistanceSq(entity) <= 64.0D;
                }
            };
        }
    }
}
