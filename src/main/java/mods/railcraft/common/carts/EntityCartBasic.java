/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("UnnecessaryThis")
public class EntityCartBasic extends EntityMinecartEmpty implements IRailcraftCart {

    public EntityCartBasic(World world) {
        super(world);
    }

    public EntityCartBasic(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        cartInit();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        saveToNBT(compound);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        loadFromNBT(compound);
    }

    @Override
    public EntityMinecart.Type getType() {
        return Type.RIDEABLE;
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.BASIC;
    }

    @Override
    public ItemStack getCartItem() {
        return createCartItem(this);
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        killAndDrop(this);
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return CartTools.isInRangeToRenderDist(this, distance);
    }

    @Override
    protected void moveAlongTrack(BlockPos pos, IBlockState state) {
        this.fallDistance = 0.0F;
        Vec3d vec3d = this.getPos(this.posX, this.posY, this.posZ);
        this.posY = (double) pos.getY();
        boolean boosted = false;
        boolean unpowered = false;
        BlockRailBase blockrailbase = (BlockRailBase) state.getBlock();

        if (blockrailbase == Blocks.GOLDEN_RAIL) {
            boosted = state.getValue(BlockRailPowered.POWERED);
            unpowered = !boosted;
        }

        double slopeAdjustment = getSlopeAdjustment();
        BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = blockrailbase.getRailDirection(world, pos, state, this);

        switch (blockrailbase$enumraildirection) {
            case ASCENDING_EAST:
                this.motionX -= slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_WEST:
                this.motionX += slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_NORTH:
                this.motionZ += slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_SOUTH:
                this.motionZ -= slopeAdjustment;
                ++this.posY;
        }

        int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
        double d1 = (double) (aint[1][0] - aint[0][0]);
        double d2 = (double) (aint[1][2] - aint[0][2]);
        double d3 = Math.sqrt(d1 * d1 + d2 * d2);
        double d4 = this.motionX * d1 + this.motionZ * d2;

        if (d4 < 0.0D) {
            d1 = -d1;
            d2 = -d2;
        }

        double d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if (d5 > 2.0D) {
            d5 = 2.0D;
        }

        this.motionX = d5 * d1 / d3;
        this.motionZ = d5 * d2 / d3;
        Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);

        if (entity instanceof EntityLivingBase) {
            double d6 = (double) ((EntityLivingBase) entity).moveForward;

            if (d6 > 0.0D) {
                double d7 = -Math.sin((double) (entity.rotationYaw * 0.017453292F));
                double d8 = Math.cos((double) (entity.rotationYaw * 0.017453292F));
                double d9 = this.motionX * this.motionX + this.motionZ * this.motionZ;

                if (d9 < 0.01D) {
                    this.motionX += d7 * 0.02D; // Railcraft: decrease entity pulling power
                    this.motionZ += d8 * 0.02D; // Railcraft #316
                    unpowered = false;
                }
            }
        }

        if (unpowered && shouldDoRailFunctions()) {
            double d17 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d17 < 0.03D) {
                this.motionX *= 0.0D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.0D;
            } else {
                this.motionX *= 0.5D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.5D;
            }
        }

        double d18 = (double) pos.getX() + 0.5D + (double) aint[0][0] * 0.5D;
        double d19 = (double) pos.getZ() + 0.5D + (double) aint[0][2] * 0.5D;
        double d20 = (double) pos.getX() + 0.5D + (double) aint[1][0] * 0.5D;
        double d21 = (double) pos.getZ() + 0.5D + (double) aint[1][2] * 0.5D;
        d1 = d20 - d18;
        d2 = d21 - d19;
        double d10;

        if (d1 == 0.0D) {
            this.posX = (double) pos.getX() + 0.5D;
            d10 = this.posZ - (double) pos.getZ();
        } else if (d2 == 0.0D) {
            this.posZ = (double) pos.getZ() + 0.5D;
            d10 = this.posX - (double) pos.getX();
        } else {
            double d11 = this.posX - d18;
            double d12 = this.posZ - d19;
            d10 = (d11 * d1 + d12 * d2) * 2.0D;
        }

        this.posX = d18 + d1 * d10;
        this.posZ = d19 + d2 * d10;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.moveMinecartOnRail(pos);

        if (aint[0][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[0][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[0][2]) {
            this.setPosition(this.posX, this.posY + (double) aint[0][1], this.posZ);
        } else if (aint[1][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[1][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[1][2]) {
            this.setPosition(this.posX, this.posY + (double) aint[1][1], this.posZ);
        }

        this.applyDrag();
        Vec3d vec3d1 = this.getPos(this.posX, this.posY, this.posZ);

        if (vec3d1 != null && vec3d != null) {
            double d14 = (vec3d.y - vec3d1.y) * 0.05D;
            d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d5 > 0.0D) {
                this.motionX = this.motionX / d5 * (d5 + d14);
                this.motionZ = this.motionZ / d5 * (d5 + d14);
            }

            this.setPosition(this.posX, vec3d1.y, this.posZ);
        }

        int j = MathHelper.floor(this.posX);
        int i = MathHelper.floor(this.posZ);

        if (j != pos.getX() || i != pos.getZ()) {
            d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = d5 * (double) (j - pos.getX());
            this.motionZ = d5 * (double) (i - pos.getZ());
        }


        if (shouldDoRailFunctions()) {
            ((BlockRailBase) state.getBlock()).onMinecartPass(world, this, pos);
        }

        if (boosted && shouldDoRailFunctions()) {
            double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d15 > 0.01D) {
                this.motionX += this.motionX / d15 * 0.06D;
                this.motionZ += this.motionZ / d15 * 0.06D;
            } else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.EAST_WEST) {
                if (this.world.getBlockState(pos.west()).isNormalCube()) {
                    this.motionX = 0.02D;
                } else if (this.world.getBlockState(pos.east()).isNormalCube()) {
                    this.motionX = -0.02D;
                }
            } else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
                if (this.world.getBlockState(pos.north()).isNormalCube()) {
                    this.motionZ = 0.02D;
                } else if (this.world.getBlockState(pos.south()).isNormalCube()) {
                    this.motionZ = -0.02D;
                }
            }
        }
    }

    //    @Override
//    protected double getDrag() {
//        if (RailcraftConfig.adjustBasicCartDrag()) {
//            return CartConstants.STANDARD_DRAG;
//        }
//        return super.getDrag();
//    }
//    @Override
//    public void onUpdate() {
//        if (Game.isHost(world) && world instanceof WorldServer) {
//            int blockId = world.getBlockId((int) posX, (int) posY, (int) posZ);
//
//            if (blockId == Block.portal.blockID) {
//                setInPortal();
//            }
//
//            if (inPortal) {
//                MinecraftServer mc = ((WorldServer) world).getMinecraftServer();
//                if (mc.getAllowNether()) {
//                    int maxPortalTime = getMaxInPortalTime();
//                    if (ridingEntity == null && field_82153_h++ >= maxPortalTime) {
//                        field_82153_h = maxPortalTime;
//                        timeUntilPortal = getPortalCooldown();
//                        byte dim;
//
//                        if (world.provider.getDimensionId() == -1) {
//                            dim = 0;
//                        } else {
//                            dim = -1;
//                        }
//
//                        Entity rider = riddenByEntity;
//                        if (rider != null) {
//                            rider.setInPortal();
//                            rider.timeUntilPortal = rider.getPortalCooldown();
//                            rider.travelToDimension(dim);
//                        }
//                        travelToDimension(dim);
//                    }
//
//                    inPortal = false;
//                }
//            }
//        }
//
//        super.onUpdate();
//    }

    @Override
    public void moveMinecartOnRail(BlockPos pos) {
        double mX = motionX;
        double mZ = motionZ;

//        if (this.riddenByEntity != null)
//        {
//            mX *= 0.75D;
//            mZ *= 0.75D;
//        }

        double max = getMaxSpeed();
        mX = MathHelper.clamp(mX, -max, max);
        mZ = MathHelper.clamp(mZ, -max, max);
        move(MoverType.SELF, mX, 0.0D, mZ);
    }

    /**
     * Called every tick the minecart is on an activator rail.
     *
     * We change this to disable the passenger removal. Use Disembarking Kits instead.
     */
    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        if (receivingPower && getRollingAmplitude() == 0) {
            setRollingDirection(-getRollingDirection());
            setRollingAmplitude(10);
            setDamage(50.0F);
            markVelocityChanged();
        }
    }
}
