/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.simplemachine;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.machine.BlockMachine;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockMachineSimple extends BlockMachine<SimpleMachineVariant> {
    public static final PropertyEnum<SimpleMachineVariant> VARIANT = PropertyEnum.create("variant", SimpleMachineVariant.class);

    public BlockMachineSimple() {
        super(SimpleMachineVariant.PROXY, true);
        setDefaultState(getDefaultState());
    }

    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return SimpleMachineVariant.class;
    }

    @Override
    public IVariantEnum[] getVariants() {
        return SimpleMachineVariant.VALUES;
    }

    @Override
    public IProperty<SimpleMachineVariant> getVariantProperty() {
        return VARIANT;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }
}
