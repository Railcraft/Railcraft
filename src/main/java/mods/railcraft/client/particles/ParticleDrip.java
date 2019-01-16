/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.particles;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleDrip extends ParticleBase {

    /**
     * The height of the current bob
     */
    private int bobTimer;
    private boolean glows;

    public ParticleDrip(World world, Vec3d start, float particleRed, float particleGreen, float particleBlue) {
        this(world, start, particleRed, particleGreen, particleBlue, false);
    }

    public ParticleDrip(World world, Vec3d start, float particleRed, float particleGreen, float particleBlue, boolean glows) {
        super(world, start, new Vec3d(0, 0, 0));
        this.motionX = this.motionY = this.motionZ = 0.0D;

        this.particleRed = particleRed;
        this.particleGreen = particleGreen;
        this.particleBlue = particleBlue;

        setParticleTextureIndex(113);
        setSize(0.01F, 0.01F);
        this.particleGravity = 0.06F;
        this.bobTimer = 40;
        this.particleMaxAge = (int) (64.0D / (Math.random() * 0.8D + 0.2D));
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.glows = glows;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return glows ? 257 : super.getBrightnessForRender(partialTicks);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;

        this.motionY -= (double) particleGravity;

        if (bobTimer > 0) {
            this.motionX *= 0.02D;
            this.motionY *= 0.02D;
            this.motionZ *= 0.02D;
            setParticleTextureIndex(113);
        } else
            setParticleTextureIndex(112);
        this.bobTimer--;

        move(motionX, motionY, motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (particleMaxAge <= 0)
            setExpired();
        this.particleMaxAge--;

        if (onGround) {
            setParticleTextureIndex(114);

            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }

        BlockPos pos = new BlockPos(posX, posY, posZ);
        IBlockState blockState = world.getBlockState(pos);
        Material material = blockState.getMaterial();

        if (material.isLiquid() || material.isSolid()) {
            double filledPercent = 0.0D;

            if (blockState.getBlock() instanceof BlockLiquid) {
                filledPercent = (double) BlockLiquid.getLiquidHeightPercent(blockState.getValue(BlockLiquid.LEVEL));
            }

            double surfaceY = (double) (MathHelper.floor(posY) + 1) - filledPercent;

            if (posY < surfaceY) {
                setExpired();
            }
        }
    }

}
