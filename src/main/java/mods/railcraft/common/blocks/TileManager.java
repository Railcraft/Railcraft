/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileManager<T extends TileEntity> {
    private final IBlockAccess world;
    private final BlockPos pos;
    private final Class<T> tileClass;
    private Optional<TileEntity> tile = Optional.empty();

    private TileManager(Function<IBlockState, Class<T>> classMapper, IBlockState state, IBlockAccess world, BlockPos pos) {
        this.tileClass = classMapper.apply(state);
        this.world = world;
        this.pos = pos;
    }

    public static <T extends TileEntity> TileManager<T> forTile(Function<IBlockState, Class<T>> classMapper, IBlockState state, IBlockAccess world, BlockPos pos) {
        return new TileManager<T>(classMapper, state, world, pos);
    }

    public static <T extends TileEntity, I> boolean isInstance(Function<IBlockState, Class<T>> classMapper, Class<I> interfaceClass, IBlockState state) {
        Class<T> clazz = classMapper.apply(state);
        return clazz != null && interfaceClass.isAssignableFrom(clazz);
    }

    public <I> TileManager action(Class<I> interfaceClass, Consumer<I> action) {
        if (interfaceClass.isAssignableFrom(tileClass)) {
            if (!tile.isPresent())
                tile = WorldPlugin.getTileEntity(world, pos);
            tile.filter(interfaceClass::isInstance).map(interfaceClass::cast).ifPresent(action);
        }
        return this;
    }

    public <I, U> Optional<U> retrieve(Class<I> interfaceClass, Function<I, U> function) {
        if (interfaceClass.isAssignableFrom(tileClass)) {
            if (!tile.isPresent())
                tile = WorldPlugin.getTileEntity(world, pos);
            return tile.filter(interfaceClass::isInstance).map(interfaceClass::cast).map(function);
        }
        return Optional.empty();
    }
}
