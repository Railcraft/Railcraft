/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

public class BlockSignalRailcraft extends BlockSignalBase {

    public static final PropertyEnum<EnumSignal> TYPE = PropertyEnum.create("type", EnumSignal.class);

    public BlockSignalRailcraft() {
        setRegistryName("railcraft.signal");
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumSignal.BLOCK_SIGNAL));

        GameRegistry.registerTileEntity(TileBoxController.class, "RCTileStructureControllerBox");
        GameRegistry.registerTileEntity(TileBoxReceiver.class, "RCTileStructureReceiverBox");
        GameRegistry.registerTileEntity(TileBoxCapacitor.class, "RCTileStructureCapacitorBox");
        GameRegistry.registerTileEntity(TileBoxBlockRelay.class, "RCTileStructureSignalBox");
        GameRegistry.registerTileEntity(TileBoxSequencer.class, "RCTileStructureSequencerBox");
        GameRegistry.registerTileEntity(TileBoxInterlock.class, "RCTileStructureInterlockBox");
        GameRegistry.registerTileEntity(TileBoxAnalogController.class, "RCTileStructureAnalogBox");
        GameRegistry.registerTileEntity(TileSwitchMotor.class, "RCTileStructureSwitchMotor");
        GameRegistry.registerTileEntity(TileSwitchLever.class, "RCTileStructureSwitchLever");
        GameRegistry.registerTileEntity(TileSwitchRouting.class, "RCTileStructureSwitchRouting");
        GameRegistry.registerTileEntity(TileSignalDistantSignal.class, "RCTileStructureDistantSignal");
        GameRegistry.registerTileEntity(TileSignalDualHeadBlockSignal.class, "RCTileStructureDualHeadBlockSignal");
        GameRegistry.registerTileEntity(TileSignalBlockSignal.class, "RCTileStructureBlockSignal");
        GameRegistry.registerTileEntity(TileSignalDualHeadDistantSignal.class, "RCTileStructureDualHeadDistantSignal");
    }

    @Override
    public ISignalTileDefinition getSignalType(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumSignal type : EnumSignal.getCreativeList()) {
            if (type.isEnabled())
                list.add(type.getItem());
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return state.getValue(TYPE).getBlockEntity();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, EnumSignal.fromOrdinal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, TYPE);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }
}
