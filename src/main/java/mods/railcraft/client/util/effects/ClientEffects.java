/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.util.effects;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.signals.IPairEffectRenderer;
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.client.core.AuraKeyHandler;
import mods.railcraft.client.particles.*;
import mods.railcraft.client.render.tesr.TESRSignals;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.items.ItemGoggles.GoggleAura;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.effects.EffectManager.IEffectSource;
import mods.railcraft.common.util.misc.Code;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.sounds.RailcraftSoundEvents;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.Random;
import java.util.Set;

import static net.minecraft.util.EnumParticleTypes.PORTAL;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class ClientEffects implements IPairEffectRenderer, Charge.IZapEffectRenderer {
    public static final short TELEPORT_PARTICLES = 64;
    public static final short TRACKING_DISTANCE = 32 * 32;
    public static final ClientEffects INSTANCE = new ClientEffects();

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random rand = new Random();

    public static void init() {} //classloading

    private ClientEffects() {
        SignalTools.effectManager = this;
        Code.setValue(Charge.class, null, this, "effects");
    }

    public void readTeleport(RailcraftInputStream data) throws IOException {
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
            double pX = start.x + (destination.x - start.x) * travel + (rand.nextDouble() - 0.5D) * 2.0D;
            double pY = start.y + (destination.y - start.y) * travel + (rand.nextDouble() - 0.5D) * 2.0D;
            double pZ = start.z + (destination.z - start.z) * travel + (rand.nextDouble() - 0.5D) * 2.0D;
            world.spawnParticle(PORTAL, pX, pY, pZ, vX, vY, vZ);
        }
    }

    public void readForceSpawn(RailcraftInputStream data) throws IOException {
        if (thinParticles(true))
            return;

        World world = Game.getWorld();
        if (world == null)
            return;

        BlockPos pos = data.readBlockPos();
        int color = data.readInt();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
//        double vx = RANDOM.nextGaussian() * 0.1;
//        double vy = RANDOM.nextDouble() * 0.01;
//        double vz = RANDOM.nextGaussian() * 0.1;
        Vec3d vel = new Vec3d(0, 0, 0);
        spawnParticle(new ParticleForceSpawn(world, new Vec3d(x + 0.1, y, z + 0.1), vel, color));
        spawnParticle(new ParticleForceSpawn(world, new Vec3d(x + 0.9, y, z + 0.1), vel, color));
        spawnParticle(new ParticleForceSpawn(world, new Vec3d(x + 0.1, y, z + 0.9), vel, color));
        spawnParticle(new ParticleForceSpawn(world, new Vec3d(x + 0.9, y, z + 0.9), vel, color));
    }

    @Override
    public boolean isTuningAuraActive() {
        return isGoggleAuraActive(GoggleAura.TUNING) || isGoggleAuraActive(GoggleAura.SIGNALLING);
    }

    public boolean isGoggleAuraActive(GoggleAura aura) {
        if (RailcraftItems.GOGGLES.isLoaded()) {
            ItemStack goggles = ItemGoggles.getGoggles(mc.player);
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

            TESRSignals.ColorProfile colorProfile = TESRSignals.ColorProfile.COORD_RAINBOW;
            if (isGoggleAuraActive(GoggleAura.SIGNALLING))
                colorProfile = TESRSignals.ColorProfile.CONTROLLER_ASPECT;

            int color = colorProfile.getColor(start, start.getPos(), dest.getPos());

            Particle particle = new ParticleTuningAura(start.getWorld(), new Vec3d(px, py, pz), EffectManager.getEffectSource(start), EffectManager.getEffectSource(dest), color);
            spawnParticle(particle);
        }
    }

    public void trailEffect(BlockPos start, TileEntity dest, long colorSeed) {
        if (thinParticles(false))
            return;
        if (mc.player.getDistanceSq(start) > TRACKING_DISTANCE)
            return;
        if (rand.nextInt(3) == 0) {
            double px = start.getX() + 0.5 + rand.nextGaussian() * 0.1;
            double py = start.getY() + 0.5 + rand.nextGaussian() * 0.1;
            double pz = start.getZ() + 0.5 + rand.nextGaussian() * 0.1;
            Particle particle = new ParticleHeatTrail(dest.getWorld(), new Vec3d(px, py, pz), colorSeed, EffectManager.getEffectSource(dest));
            spawnParticle(particle);
        }
    }

    public void fireSparkEffect(World world, Vec3d start, Vec3d end) {
        if (thinParticles(false))
            return;
        IEffectSource es = EffectManager.getEffectSource(start);
        Particle particle = new ParticleFireSpark(world, start, end);
        spawnParticle(particle);
        SoundHelper.playSoundClient(world, es.getPos(), SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, .2F + rand.nextFloat() * .2F, .9F + rand.nextFloat() * .15F);
    }

    public void readFireSpark(RailcraftInputStream data) throws IOException {
        Vec3d start = data.readVec3d();
        Vec3d destination = data.readVec3d();
        fireSparkEffect(mc.world, start, destination);
    }

    public void chunkLoaderEffect(World world, Object source, Set<ChunkPos> chunks) {
        if (!isGoggleAuraActive(GoggleAura.WORLDSPIKE))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);

        Vec3d sourcePos = es.getPosF();
        if (FMLClientHandler.instance().getClient().player.getDistanceSq(sourcePos.x, sourcePos.y, sourcePos.z) > 25600)
            return;

        for (ChunkPos chunk : chunks) {
            int xCorner = chunk.x * 16;
            int zCorner = chunk.z * 16;
            double yCorner = sourcePos.y - 8;

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

    public void snowEffect(World world, Object source, double yOffset) {
        if (thinParticles(true))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);
        double vx = rand.nextGaussian() * 0.1;
        double vy = rand.nextDouble() * 0.01;
        double vz = rand.nextGaussian() * 0.1;
        Vec3d start = es.getPosF().add(0.0, yOffset, 0.0);
        world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, start.x, start.y, start.z, vx, vy, vz);
    }

    public void steamEffect(World world, Object source, double yOffset) {
        if (thinParticles(true))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);
        double vx = rand.nextGaussian() * 0.1;
        double vy = rand.nextDouble() * 0.01;
        double vz = rand.nextGaussian() * 0.1;
        spawnParticle(new ParticleSteam(world, es.getPosF().add(0.0, yOffset, 0.0), new Vec3d(vx, vy, vz)));
    }

    public void steamJetEffect(World world, Object source, Vec3d vel) {
        if (thinParticles(true))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);
        vel = vel.add(rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02);
        ParticleSteam fx = new ParticleSteam(world, es.getPosF(), vel, 1.5F);
        fx.setParticleGravity(0F);
        spawnParticle(fx);
    }

    public void chimneyEffect(World world, double x, double y, double z, EnumColor color) {
        if (thinParticles(false))
            return;
        spawnParticle(new ParticleChimney(world, new Vec3d(x, y, z), color));
    }

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
        if (mc.getRenderViewEntity().getDistanceSq(es.getPos()) > 400)
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
            // oh naw
            return;
        }
        if (thinParticles(false))
            return;
        IEffectSource es = EffectManager.getEffectSource(source);
        if (mc.getRenderViewEntity().getDistanceSq(es.getPos()) > 400)
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

    public void readZapDeath(RailcraftInputStream data) throws IOException {
        Vec3d pos = data.readVec3d();
        zapEffectDeath(mc.world, pos);
    }

    @Override
    public void zapEffectSurface(IBlockState stateIn, World worldIn, BlockPos pos) {
        if (thinParticles(false))
            return;
        if (mc.getRenderViewEntity().getDistanceSq(pos) > 400)
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
            // TODO This should probably use the bounding box or something. Its got to be wrong for tracks atm.
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

    public void blockParticle(World world, Object source, Vec3d pos, Vec3d velocity, IBlockState state, boolean blockDust, String location) {
        if (Game.isHost(world)) {
            // oh naw
            return;
        }
        ParticleBlockCrack particle = new ParticleBlockCrack(world, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z, state);

        particle.setBlockPos(EffectManager.getEffectSource(source).getPos());
        if (!location.isEmpty()) {
            TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(location);
            if (sprite == mc.getTextureMapBlocks().getMissingSprite())
                Game.log().msg(Level.WARN, "Cannot find sprite at {0} for block state {1}", location, state);
            particle.setParticleTexture(sprite);
        }
        if (blockDust) {
            particle.setVelocity(velocity);
        }

        spawnParticle(particle);
    }

    public void readBlockParticle(RailcraftInputStream data) throws IOException {
        BlockPos block = data.readBlockPos();
        Vec3d pos = data.readVec3d();
        Vec3d velocity = data.readVec3d();
        IBlockState state = Block.getStateById(data.readInt());
        boolean blockDust = data.readBoolean();
        String location = data.readUTF();
        blockParticle(mc.world, block, pos, velocity, state, blockDust, location);
    }

    private boolean thinParticles(boolean canDisable) {
        int particleSetting = mc.gameSettings.particleSetting;
        if (!canDisable && particleSetting > 1)
            particleSetting = 1;
        if (particleSetting == 1 && MiscTools.RANDOM.nextInt(3) == 0)
            particleSetting = 2;
        return particleSetting > 1;
    }

    protected void spawnParticle(Particle particle) {
        mc.effectRenderer.addEffect(particle);
    }
}
