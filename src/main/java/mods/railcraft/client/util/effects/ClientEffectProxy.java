/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.util.effects;

import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.client.core.AuraKeyHandler;
import mods.railcraft.client.particles.*;
import mods.railcraft.client.render.tesr.TESRSignals;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.items.ItemGoggles.GoggleAura;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import mods.railcraft.common.util.effects.CommonEffectProxy;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.effects.EffectManager.IEffectSource;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.PacketEffect.Effect;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.sounds.RailcraftSoundEvents;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Set;

import static net.minecraft.util.EnumParticleTypes.PORTAL;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
// TODO: Where you stupid when you decided to use a Proxy? Just no, no...
@SideOnly(Side.CLIENT)
public class ClientEffectProxy extends CommonEffectProxy {
    public static final short TELEPORT_PARTICLES = 64;
    public static final short TRACKING_DISTANCE = 32 * 32;

    @SuppressWarnings("unused")
    public ClientEffectProxy() {
        SignalTools.effectManager = this;
    }

    private void doTeleport(RailcraftInputStream data) throws IOException {
        World world = Game.getWorld();
        if (world == null)
            return;

        Vec3d start = data.readVec3d();
        Vec3d destination = data.readVec3d();
//        for(int i = 0; i < TELEPORT_PARTICLES / 4; i++) {
//            float vX = (RANDOM.nextFloat() - 0.5F) * 0.2F;
//            float vY = (RANDOM.nextFloat() - 0.5F) * 0.2F;
//            float vZ = (RANDOM.nextFloat() - 0.5F) * 0.2F;
//            Game.getWorld().spawnParticle("portal", startX, startY, startZ, vX, vY, vZ);
//        }
        for (int i = 0; i < TELEPORT_PARTICLES; i++) {
            double travel = (double) i / ((double) TELEPORT_PARTICLES - 1.0D);
            float vX = (rand.nextFloat() - 0.5F) * 0.2F;
            float vY = (rand.nextFloat() - 0.5F) * 0.2F;
            float vZ = (rand.nextFloat() - 0.5F) * 0.2F;
            double pX = start.xCoord + (destination.xCoord - start.xCoord) * travel + (rand.nextDouble() - 0.5D) * 2.0D;
            double pY = start.yCoord + (destination.yCoord - start.yCoord) * travel + (rand.nextDouble() - 0.5D) * 2.0D;
            double pZ = start.zCoord + (destination.zCoord - start.zCoord) * travel + (rand.nextDouble() - 0.5D) * 2.0D;
            world.spawnParticle(PORTAL, pX, pY, pZ, vX, vY, vZ);
        }
    }

    public void doForceSpawn(RailcraftInputStream data) throws IOException {
        if (thinParticles(true))
            return;

        World world = Game.getWorld();
        if (world == null)
            return;

        BlockPos pos = data.readBlockPos();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
//        double vx = RANDOM.nextGaussian() * 0.1;
//        double vy = RANDOM.nextDouble() * 0.01;
//        double vz = RANDOM.nextGaussian() * 0.1;
        Vec3d vel = new Vec3d(0, 0, 0);
        spawnParticle(new ParticleForceSpawn(world, new Vec3d(x + 0.1, y, z + 0.1), vel));
        spawnParticle(new ParticleForceSpawn(world, new Vec3d(x + 0.9, y, z + 0.1), vel));
        spawnParticle(new ParticleForceSpawn(world, new Vec3d(x + 0.1, y, z + 0.9), vel));
        spawnParticle(new ParticleForceSpawn(world, new Vec3d(x + 0.9, y, z + 0.9), vel));
    }

    @Override
    public boolean isTuningAuraActive() {
        return isGoggleAuraActive(GoggleAura.TUNING) || isGoggleAuraActive(GoggleAura.SIGNALLING);
    }

    @Override
    public boolean isGoggleAuraActive(GoggleAura aura) {
        if (RailcraftItems.GOGGLES.item() != null) {
            ItemStack goggles = ItemGoggles.getGoggles(Minecraft.getMinecraft().thePlayer);
            return ItemGoggles.getCurrentAura(goggles) == aura;
        }
        return AuraKeyHandler.isAuraEnabled(aura);
    }

    private double getRandomParticleOffset() {
        return 0.5 + rand.nextGaussian() * 0.1;
    }

    @Override
    public void tuningEffect(TileEntity start, TileEntity dest) {
        if (thinParticles(false))
            return;
        if (rand.nextInt(2) == 0) {
            BlockPos pos = start.getPos();
            double px = pos.getX() + getRandomParticleOffset();
            double py = pos.getY() + getRandomParticleOffset();
            double pz = pos.getZ() + getRandomParticleOffset();

            TESRSignals.ColorProfile colorProfile = TESRSignals.ColorProfile.RAINBOW;
            if (isGoggleAuraActive(GoggleAura.SIGNALLING))
                colorProfile = TESRSignals.ColorProfile.ASPECT;

            int color = colorProfile.getColor(start, start.getPos(), dest.getPos());

            Particle particle = new ParticleTuningAura(start.getWorld(), new Vec3d(px, py, pz), EffectManager.getEffectSource(start), EffectManager.getEffectSource(dest), color);
            spawnParticle(particle);
        }
    }

    @Override
    public void trailEffect(BlockPos start, TileEntity dest, long colorSeed) {
        if (thinParticles(false))
            return;
        if (Minecraft.getMinecraft().thePlayer.getDistanceSq(start) > TRACKING_DISTANCE)
            return;
        if (rand.nextInt(3) == 0) {
            double px = start.getX() + 0.5 + rand.nextGaussian() * 0.1;
            double py = start.getY() + 0.5 + rand.nextGaussian() * 0.1;
            double pz = start.getZ() + 0.5 + rand.nextGaussian() * 0.1;
            Particle particle = new ParticleHeatTrail(dest.getWorld(), new Vec3d(px, py, pz), colorSeed, EffectManager.getEffectSource(dest));
            spawnParticle(particle);
        }
    }

    @Override
    public void fireSparkEffect(World world, Vec3d start, Vec3d end) {
        if (thinParticles(false))
            return;
        IEffectSource es = EffectManager.getEffectSource(start);
        Particle particle = new ParticleFireSpark(world, start, end);
        spawnParticle(particle);
        SoundHelper.playSoundClient(world, es.getPos(), SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, .2F + rand.nextFloat() * .2F, .9F + rand.nextFloat() * .15F);
    }

    private void doFireSpark(RailcraftInputStream data) throws IOException {
        Vec3d start = data.readVec3d();
        Vec3d destination = data.readVec3d();
        fireSparkEffect(Minecraft.getMinecraft().theWorld, start, destination);
    }

    @Override
    public void handleEffectPacket(RailcraftInputStream data) throws IOException {

        byte effectId = data.readByte();
        if (effectId < 0)
            return;

        Effect effect = Effect.VALUES[effectId];
        switch (effect) {
            case TELEPORT:
                doTeleport(data);
                break;
            case FIRESPARK:
                doFireSpark(data);
                break;
            case FORCE_SPAWN:
                doForceSpawn(data);
                break;
            case ZAP_DEATH:
                doZapDeath(data);
                break;
        }
    }

    @Override
    public void chunkLoaderEffect(World world, Object source, Set<ChunkPos> chunks) {
        if (!isGoggleAuraActive(GoggleAura.WORLDSPIKE))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);

        Vec3d sourcePos = es.getPosF();
        if (FMLClientHandler.instance().getClient().thePlayer.getDistanceSq(sourcePos.xCoord, sourcePos.yCoord, sourcePos.zCoord) > 25600)
            return;

        for (ChunkPos chunk : chunks) {
            int xCorner = chunk.chunkXPos * 16;
            int zCorner = chunk.chunkZPos * 16;
            double yCorner = sourcePos.yCoord - 8;

//            System.out.println(xCorner + ", " + zCorner);
            if (rand.nextInt(3) == 0) {
                if (thinParticles(false))
                    continue;
                double xParticle = xCorner + rand.nextFloat() * 16;
                double yParticle = yCorner + rand.nextFloat() * 16;
                double zParticle = zCorner + rand.nextFloat() * 16;

                Particle particle = new ParticleChunkLoader(world, new Vec3d(xParticle, yParticle, zParticle), es);
                spawnParticle(particle);
            }
        }
    }

    @Override
    public void steamEffect(World world, Object source, double yOffset) {
        if (thinParticles(true))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);
        double vx = rand.nextGaussian() * 0.1;
        double vy = rand.nextDouble() * 0.01;
        double vz = rand.nextGaussian() * 0.1;
        spawnParticle(new ParticleSteam(world, es.getPosF().addVector(0.0, yOffset, 0.0), new Vec3d(vx, vy, vz)));
    }

    @Override
    public void steamJetEffect(World world, Object source, Vec3d vel) {
        if (thinParticles(true))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);
        vel = vel.addVector(rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02);
        ParticleSteam fx = new ParticleSteam(world, es.getPosF(), vel, 1.5F);
        fx.setParticleGravity(0F);
        spawnParticle(fx);
    }

    @Override
    public void chimneyEffect(World world, double x, double y, double z, EnumColor color) {
        if (thinParticles(false))
            return;
        spawnParticle(new ParticleChimney(world, new Vec3d(x, y, z), color));
    }

    @Override
    public void locomotiveEffect(World world, double x, double y, double z) {
        if (thinParticles(false))
            return;
        if (SeasonPlugin.HALLOWEEN && rand.nextInt(4) == 0) {
            spawnParticle(new ParticlePumpkin(world, new Vec3d(x, y, z)));
        } else
            spawnParticle(new ParticleLocomotive(world, new Vec3d(x, y, z)));
    }

    @Override
    public void zapEffectPoint(World world, Object source) {
        if (thinParticles(false))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);
        if (Minecraft.getMinecraft().getRenderViewEntity().getDistanceSq(es.getPos()) > 400)
            return;
        Vec3d vel = new Vec3d(
                rand.nextDouble() - 0.5D,
                rand.nextDouble() - 0.5D,
                rand.nextDouble() - 0.5D);
        spawnParticle(new ParticleSpark(world, es.getPosF(), vel));
        SoundHelper.playSoundClient(world, es.getPos(), RailcraftSoundEvents.MECHANICAL_ZAP, SoundCategory.BLOCKS, .2F, 1F);
    }

    @Override
    public void zapEffectDeath(World world, Object source) {
        if (Game.isHost(world)) {
            super.zapEffectDeath(world, source);
            return;
        }
        if (thinParticles(false))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);
        if (Minecraft.getMinecraft().getRenderViewEntity().getDistanceSq(es.getPos()) > 400)
            return;
        SoundHelper.playSoundClient(world, es.getPos(), RailcraftSoundEvents.MECHANICAL_ZAP, SoundCategory.BLOCKS, 3F, 0.75F);
        for (int i = 0; i < 20; i++) {
            Vec3d vel = new Vec3d(
                    rand.nextDouble() - 0.5D,
                    rand.nextDouble() - 0.5D,
                    rand.nextDouble() - 0.5D);
            spawnParticle(new ParticleSpark(world, es.getPosF(), vel));
        }
    }

    private void doZapDeath(RailcraftInputStream data) throws IOException {
        Vec3d pos = data.readVec3d();
        zapEffectDeath(Minecraft.getMinecraft().theWorld, pos);
    }

    @Override
    public void zapEffectSurface(IBlockState stateIn, World worldIn, BlockPos pos) {
        if (thinParticles(false))
            return;
        if (Minecraft.getMinecraft().getRenderViewEntity().getDistanceSq(pos) > 400)
            return;
        SoundHelper.playSoundClient(worldIn, pos, RailcraftSoundEvents.MECHANICAL_ZAP, SoundCategory.BLOCKS, .1F + rand.nextFloat() * .2F, .9F + rand.nextFloat() * .15F);
        for (EnumFacing side : EnumFacing.VALUES) {
            if (!stateIn.shouldSideBeRendered(worldIn, pos, side))
                continue;
            Vec3d normal = new Vec3d(side.getDirectionVec());
            Vec3d variance = new Vec3d(
                    (rand.nextGaussian() - 0.5) * 0.2,
                    (rand.nextGaussian() - 0.5) * 0.2,
                    (rand.nextGaussian() - 0.5) * 0.2);
            Vec3d vel = normal.add(variance);
            Vec3d start = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).add(normal.scale(0.5));
            switch (side.getAxis()) {
                case X:
                    start = start.add(new Vec3d(0.0, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5));
                    break;
                case Y:
                    start = start.add(new Vec3d(rand.nextDouble() - 0.5, 0.0, rand.nextDouble() - 0.5));
                    break;
                case Z:
                    start = start.add(new Vec3d(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, 0.0));
                    break;
            }
            spawnParticle(new ParticleSpark(worldIn, start, vel));
        }
    }

    private boolean thinParticles(boolean canDisable) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        int particleSetting = mc.gameSettings.particleSetting;
        if (!canDisable && particleSetting > 1)
            particleSetting = 1;
        if (particleSetting == 1 && MiscTools.RANDOM.nextInt(3) == 0)
            particleSetting = 2;
        return particleSetting > 1;
    }

    @Override
    protected void spawnParticle(Particle particle) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        mc.effectRenderer.addEffect(particle);
    }
}
