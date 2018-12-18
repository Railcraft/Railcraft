/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.logic;

import mods.railcraft.api.core.IWorldSupplier;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * A base logic with a world defined.
 */
public abstract class AbstractLogic implements ILogic {
    protected final LogicAdapter adapter;
    private int clock = MiscTools.RANDOM.nextInt();

    AbstractLogic(LogicAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public final void update() {
        clock++;
        if (Game.isHost(theWorldAsserted()))
            updateServer();
        else
            updateClient();
    }

    @SuppressWarnings("EmptyMethod")
    void updateClient() {

    }

    void updateServer() {

    }

    public boolean clock(int interval) {
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

    public abstract static class LogicAdapter implements IWorldSupplier, IWorldNameable {

        abstract double getX();

        abstract double getY();

        abstract double getZ();

        abstract BlockPos getPos();

        abstract void sendUpdateToClient();

        public static LogicAdapter of(TileRailcraft tile) {
            return new LogicAdapter() {
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
            };
        }

        public static LogicAdapter of(EntityMinecart entity) {
            return new LogicAdapter() {
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
            };
        }
    }
}
