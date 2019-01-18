/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.util.charge.BatteryBlock;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChargeFeederAdmin extends TileCharge {

    @Override
    public void update() {
        super.update();
        if (Game.isHost(world) && clock % 8 == 0)
            updateBatteryState(getBlockState());
    }

    @Override
    public IEnumMachine<?> getMachineType() {
        return FeederVariant.ADMIN;
    }

    @Override
    public List<ItemStack> getDrops(int fortune) {
        return Collections.emptyList();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        if (Game.isHost(world))
            updateBatteryState(state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        super.neighborChanged(state, worldIn, pos, blockIn);
        updateBatteryState(state);
    }

    private void updateBatteryState(IBlockState state) {
        getBattery().setState(state.getValue(BlockChargeFeeder.REDSTONE) ? BatteryBlock.State.INFINITE : BatteryBlock.State.DISABLED);
    }
}
