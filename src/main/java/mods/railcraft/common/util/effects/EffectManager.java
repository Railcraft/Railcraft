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
import net.minecraftforge.fml.common.SidedProxy;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EffectManager {

    @SidedProxy(clientSide = "mods.railcraft.client.util.effects.ClientEffectProxy", serverSide = "mods.railcraft.common.util.effects.CommonEffectProxy")
    public static IEffectManager instance;

    public interface IEffectSource {

        double getX();

        double getY();

        double getZ();

        boolean isDead();
    }

    public static class EffectSourceTile implements IEffectSource {

        private final TileEntity source;

        private EffectSourceTile(TileEntity source) {
            this.source = source;
        }

        @Override
        public double getX() {
            return source.getPos().getX() + 0.5;
        }

        @Override
        public double getY() {
            return source.getPos().getY() + 0.5;
        }

        @Override
        public double getZ() {
            return source.getPos().getZ() + 0.5;
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
        public double getX() {
            return source.posX;
        }

        @Override
        public double getY() {
            return source.posY + source.getYOffset();
        }

        @Override
        public double getZ() {
            return source.posZ;
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
        return null;
    }
}
