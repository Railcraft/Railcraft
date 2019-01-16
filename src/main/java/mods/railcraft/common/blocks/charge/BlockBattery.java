/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collections;
import java.util.Map;

/**
 * Created by CovertJaguar on 7/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockBattery extends BlockCharge {
    public static final String RECHARGEABLE_BATTERY_ORE_TAG = "blockChargeBatteryRechargeable";
    public static final AxisAlignedBB COLLISION_BOX = AABBFactory.start().box().raiseCeiling(-0.0625D).build();

    protected BlockBattery() {
        super(Material.CIRCUITS);
        setResistance(10F);
        setHardness(5F);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void initializeDefinition() {
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 1, this);
        ForestryPlugin.addBackpackItem("forestry.builder", this);
    }

    /**
     * Called When an Entity Collided with the Block
     */
    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollision(world, pos, state, entity);
        if (Game.isHost(world))
            Charge.distribution.network(world).access(pos).zap(entity, Charge.DamageOrigin.BLOCK, 1F);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nullable AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_BOX;
    }

    protected abstract IChargeBlock.ChargeSpec getChargeSpec(IBlockState state);

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        return Collections.singletonMap(Charge.distribution, getChargeSpec(state));
    }
}
