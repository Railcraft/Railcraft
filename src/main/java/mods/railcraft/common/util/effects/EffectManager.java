/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.effects;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.SidedProxy;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EffectManager {

    @SidedProxy(clientSide = "mods.railcraft.client.util.effects.ClientEffectProxy", serverSide = "mods.railcraft.common.util.effects.CommonEffectProxy")
    public static IEffectManager instance;

    public interface IEffectSource {

        Vec3d getPos();

        boolean isDead();
    }

    public static class EffectSourceTile implements IEffectSource {

        private final TileEntity source;

        private EffectSourceTile(TileEntity source) {
            this.source = source;
        }

        @Override
        public Vec3d getPos() {
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
        public Vec3d getPos() {
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
        }
        throw new RuntimeException("Invalid Effect Source");
    }
}
