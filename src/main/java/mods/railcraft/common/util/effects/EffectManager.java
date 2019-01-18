/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.effects;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EffectManager {

    public interface IEffectSource {

        BlockPos getPos();

        Vec3d getPosF();

        default boolean isDead() {
            return false;
        }
    }

    public static class EffectSourceBlockPos implements IEffectSource {

        private final BlockPos pos;
        private final Vec3d posF;

        private EffectSourceBlockPos(BlockPos pos) {
            this.pos = pos;
            this.posF = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public Vec3d getPosF() {
            return posF;
        }
    }

    public static class EffectSourceVec3d implements IEffectSource {

        private final BlockPos pos;
        private final Vec3d posF;

        private EffectSourceVec3d(Vec3d pos) {
            this.pos = new BlockPos(pos);
            this.posF = pos;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public Vec3d getPosF() {
            return posF;
        }
    }

    public static class EffectSourceTile implements IEffectSource {

        private final TileEntity source;

        private EffectSourceTile(TileEntity source) {
            this.source = source;
        }

        @Override
        public BlockPos getPos() {
            return source.getPos();
        }

        @Override
        public Vec3d getPosF() {
            BlockPos pos = source.getPos();
            return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }

        @Override
        public boolean isDead() {
            return source.isInvalid();
        }
    }

    public static class EffectSourceEntity implements IEffectSource {

        private final Entity source;

        private EffectSourceEntity(Entity source) {
            this.source = source;
        }

        @Override
        public BlockPos getPos() {
            return source.getPosition();
        }

        @Override
        public Vec3d getPosF() {
            return new Vec3d(source.posX, source.posY + source.getYOffset(), source.posZ);
        }

        @Override
        public boolean isDead() {
            return source.isDead;
        }
    }

    public static IEffectSource getEffectSource(Object source) {
        if (source instanceof TileEntity) {
            return new EffectSourceTile((TileEntity) source);
        } else if (source instanceof Entity) {
            return new EffectSourceEntity((Entity) source);
        } else if (source instanceof BlockPos) {
            return new EffectSourceBlockPos((BlockPos) source);
        } else if (source instanceof Vec3d) {
            return new EffectSourceVec3d((Vec3d) source);
        }
        throw new IllegalArgumentException("Invalid Effect Source");
    }
}
