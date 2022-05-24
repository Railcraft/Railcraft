/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * An addition to the API provided IWorldSupplier. Adds a bunch of helper functions.
 *
 * Created by CovertJaguar on 9/8/2021 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IWorldSupplier extends mods.railcraft.api.core.IWorldSupplier {

    default Optional<World> ifWorld() {return Optional.ofNullable(theWorld());}

    default void ifWorld(Consumer<World> action) {ifWorld().ifPresent(action);}

    default boolean isHost() {return ifWorld().map(Game::isHost).orElse(false);}

    default boolean isClient() {
        return ifWorld().map(Game::isClient).orElse(false);
    }

    default void ifHost(Consumer<World> action) {ifWorld().filter(Game::isHost).ifPresent(action);}

    default void ifHost(Runnable action) {ifWorld().filter(Game::isHost).ifPresent(world -> action.run());}

    default void ifClient(Consumer<World> action) {ifWorld().filter(Game::isClient).ifPresent(action);}

    default void ifClient(Runnable action) {ifWorld().filter(Game::isClient).ifPresent(world -> action.run());}

    default WorldOperator fromWorld() {return new WorldOperator(theWorld());}

    class WorldOperator {
        private final @Nullable World world;

        public WorldOperator(@Nullable World world) {
            this.world = world;
        }

        public Optional<TileEntity> getTile(@Nullable BlockPos pos) {
            return WorldPlugin.getTileEntity(world, pos, TileEntity.class, true);
        }

        public <T> Optional<T> getTile(@Nullable BlockPos pos, Class<T> tileClass) {
            return WorldPlugin.getTileEntity(world, pos, tileClass, true);
        }

        public Optional<Block> getBlock(BlockPos pos) {
            if (world == null) return Optional.empty();
            return Optional.of(WorldPlugin.getBlock(world, pos));
        }

        public Optional<IBlockState> getBlockState(BlockPos pos) {
            if (world == null) return Optional.empty();
            return Optional.of(WorldPlugin.getBlockState(world, pos));
        }
    }
}
