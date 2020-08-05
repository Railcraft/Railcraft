/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.ISmartTile;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.ChargeSourceLogic;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Created by CovertJaguar on 8/1/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockStructureCharge<T extends TileLogic & ISmartTile> extends BlockStructure<T> implements IChargeBlock {
    private final Map<Charge, ChargeSpec> chargeSpec;

    protected BlockStructureCharge(Material materialIn, Map<Charge, ChargeSpec> chargeSpec) {
        super(materialIn);
        this.chargeSpec = chargeSpec;
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public @Nullable Charge.IAccess getMeterAccess(Charge network, IBlockState state, World world, BlockPos pos) {
        Optional<TileLogic> tile = WorldPlugin.getTileEntity(world, pos, TileLogic.class);
        return tile.flatMap(t -> t.getLogic(ChargeSourceLogic.class)).map(ChargeSourceLogic::access).orElse(null);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        Charge.effects().throwSparks(state, worldIn, pos, rand, 50);
    }

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        return chargeSpec;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        deregisterNode(worldIn, pos);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        return Charge.distribution.network(worldIn).access(pos).getComparatorOutput();
    }
}
