/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.ItemMachine;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by CovertJaguar on 3/15/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemMachineActuator extends ItemMachine {
    public ItemMachineActuator(BlockMachine<?> block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, IBlockState state) {
        BlockMachineActuator block = (BlockMachineActuator) RailcraftBlocks.ACTUATOR.block();
        if (block != null) {
            ActuatorVariant variant = block.getVariant(state);
            return new ModelResourceLocation(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, variant.getName()), "inventory");
        }
        return super.getModelLocation(stack, state);
    }
}
