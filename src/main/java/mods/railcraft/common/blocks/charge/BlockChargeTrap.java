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
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

/**
 * Created by CovertJaguar on 7/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockChargeTrap extends BlockCharge {
    public static final AxisAlignedBB COLLISION_BOX = AABBFactory.start().box().grow(-0.0625D).build();
    public static final PropertyBool REDSTONE = PropertyBool.create("redstone");
    private static final Map<Charge, ChargeSpec> CHARGE_SPECS = ChargeSpec.make(Charge.distribution, ConnectType.BLOCK, 0.025);

    public BlockChargeTrap() {
        super(Material.IRON);
        IBlockState defaultState = blockState.getBaseState().withProperty(REDSTONE, false);
        setDefaultState(defaultState);
        setResistance(10F);
        setHardness(5F);
    }

    @Override
    public void initializeDefinition() {
//                HarvestPlugin.setStateHarvestLevel(instance, "crowbar", 0);
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 1, this);

        ForestryPlugin.addBackpackItem("forestry.builder", this);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(1),
                "PGP",
                "GBG",
                "PGP",
                'P', RailcraftItems.PLATE, Metal.TIN,
                'B', RailcraftItems.CHARGE, ItemCharge.EnumCharge.TERMINAL,
                'G', Blocks.IRON_BARS);
    }

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHARGE_SPECS;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        state = state.withProperty(REDSTONE, (meta & 0x8) > 0);
        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (state.getValue(REDSTONE))
            meta |= 0x8;
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, REDSTONE);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(REDSTONE))
            Charge.effects().throwSparks(stateIn, worldIn, pos, rand, 10);
    }

    /**
     * Called When an Entity Collided with the Block
     */
    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollision(world, pos, state, entity);
        if (Game.isHost(world))
            Charge.distribution.network(world).access(pos).zap(entity, Charge.DamageOrigin.BLOCK, 10F);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nullable AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_BOX;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos posIn) {
        super.neighborChanged(state, worldIn, pos, blockIn, posIn);
        IBlockState newState = detectRedstoneState(state, worldIn, pos);
        if (state != newState)
            WorldPlugin.setBlockState(worldIn, pos, newState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        return detectRedstoneState(state, worldIn, pos);
    }

    private IBlockState detectRedstoneState(IBlockState state, World worldIn, BlockPos pos) {
        if (Game.isClient(worldIn))
            return state;
        return state.withProperty(REDSTONE, PowerPlugin.isBlockBeingPowered(worldIn, pos));
    }
}
