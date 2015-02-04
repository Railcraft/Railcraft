/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class BlockSignalRailcraft extends BlockSignalBase {

    public static IIcon[] texturesBox;
    public static IIcon[] texturesLampTop;
    public static IIcon[] texturesLampBottom;
    public static IIcon[] texturesLampBox;
    public static IIcon[] texturesSignalSingle;
    public static IIcon[] texturesSignalDual;
    public static IIcon[] texturesSwitch;
    public static IIcon[] texturesSwitchTarget;
    public static IIcon texturesSwitchLever;

    public BlockSignalRailcraft(int renderType) {
        super(renderType);
        setBlockName("railcraft.signal");

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
    public ISignalTileDefinition getSignalType(int meta) {
        return EnumSignal.fromId(meta);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumSignal type : EnumSignal.getCreativeList()) {
            if (type.isEnabled())
                list.add(type.getItem());
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        texturesBox = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.box", 6);
        texturesLampBox = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.lamp.box", 4);

        // TODO: insert color blind mode here!
        texturesLampTop = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.lamp.top", 4);
        texturesLampBottom = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.lamp.bottom", 4);
        texturesSignalSingle = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.single", 5);
        texturesSignalDual = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.dual", 5);
        texturesSwitch = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.switch", 2);
        texturesSwitchTarget = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.switch.target", 2);
        texturesSwitchLever = iconRegister.registerIcon("railcraft:signal.switch.throw");

        EnumSignal.BOX_BLOCK_RELAY.setIcon(iconRegister.registerIcon("railcraft:signal.box.block.relay"));
        EnumSignal.BOX_CAPACITOR.setIcon(iconRegister.registerIcon("railcraft:signal.box.capacitor"));
        EnumSignal.BOX_CONTROLLER.setIcon(iconRegister.registerIcon("railcraft:signal.box.controller"));
        EnumSignal.BOX_RECEIVER.setIcon(iconRegister.registerIcon("railcraft:signal.box.receiver"));
        EnumSignal.BOX_SEQUENCER.setIcon(iconRegister.registerIcon("railcraft:signal.box.sequencer"));
        EnumSignal.BOX_INTERLOCK.setIcon(iconRegister.registerIcon("railcraft:signal.box.interlock"));
        EnumSignal.BOX_ANALOG_CONTROLLER.setIcon(iconRegister.registerIcon("railcraft:signal.box.analog"));

        EnumSignal.SWITCH_MOTOR.setIcon(iconRegister.registerIcon("railcraft:signal.switch.motor"));
        EnumSignal.SWITCH_LEVER.setIcon(iconRegister.registerIcon("railcraft:signal.switch.lever"));
        EnumSignal.SWITCH_ROUTING.setIcon(iconRegister.registerIcon("railcraft:signal.switch.routing"));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        EnumSignal type = EnumSignal.fromId(meta);
        switch (type) {
            case DUAL_HEAD_BLOCK_SIGNAL:
            case DUAL_HEAD_DISTANT_SIGNAL:
                return texturesSignalDual[0];
            case DISTANT_SIGNAL:
            case BLOCK_SIGNAL:
                return texturesSignalSingle[0];
            default:
                return type.getIcon();
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return EnumSignal.fromId(metadata).getBlockEntity();
    }

}
