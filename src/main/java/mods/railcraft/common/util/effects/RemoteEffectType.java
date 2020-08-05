/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.effects;

import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.util.network.RailcraftInputStream;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

/**
 *
 */
public enum RemoteEffectType {

    TELEPORT() {
        @Override
        @SideOnly(Side.CLIENT)
        public void handle(ClientEffects effects, RailcraftInputStream input) throws IOException {
            effects.readTeleport(input);
        }
    },
    FIRE_SPARK() {
        @Override
        @SideOnly(Side.CLIENT)
        public void handle(ClientEffects effects, RailcraftInputStream input) throws IOException {
            effects.readFireSpark(input);
        }
    },
    FORCE_SPAWN() {
        @Override
        @SideOnly(Side.CLIENT)
        public void handle(ClientEffects effects, RailcraftInputStream input) throws IOException {
            effects.readForceSpawn(input);
        }
    },
    ZAP_DEATH() {
        @Override
        @SideOnly(Side.CLIENT)
        public void handle(ClientEffects effects, RailcraftInputStream input) throws IOException {
            effects.readZapDeath(input);
        }
    },
    BLOCK_PARTICLE() {
        @Override
        @SideOnly(Side.CLIENT)
        public void handle(ClientEffects effects, RailcraftInputStream input) throws IOException {
            effects.readBlockParticle(input);
        }
    },
    ;

    public static final RemoteEffectType[] VALUES = values();

    @SideOnly(Side.CLIENT)
    public abstract void handle(ClientEffects effects, RailcraftInputStream input) throws IOException;
}
