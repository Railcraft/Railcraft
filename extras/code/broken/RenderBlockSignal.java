/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.client.render.broken;

import cpw.mods.fml.client.registry.ClientRegistry;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.client.render.tesr.TESRSignalLamp;
import mods.railcraft.client.render.tesr.TESRSignalBox;
import mods.railcraft.client.render.tesr.TESRSignalLampDual;
import mods.railcraft.common.blocks.wayobjects.EnumSignal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockSignal extends BlockRenderer {

    public RenderBlockSignal() {
        super(RailcraftBlocksOld.getBlockSignal());

        addCombinedRenderer(EnumSignal.DUAL_HEAD_BLOCK_SIGNAL, new TESRSignalLampDual());
        addCombinedRenderer(EnumSignal.DUAL_HEAD_DISTANT_SIGNAL, new TESRSignalLampDual());
        addCombinedRenderer(EnumSignal.BLOCK_SIGNAL, new TESRSignalLamp(SignalAspect.GREEN));
        addCombinedRenderer(EnumSignal.DISTANT_SIGNAL, new TESRSignalLamp(SignalAspect.RED));
        addCombinedRenderer(EnumSignal.SWITCH_MOTOR, new RenderSwitch(EnumSignal.SWITCH_MOTOR));
        addCombinedRenderer(EnumSignal.SWITCH_LEVER, new RenderSwitch(EnumSignal.SWITCH_LEVER));
        addCombinedRenderer(EnumSignal.SWITCH_ROUTING, new RenderSwitch(EnumSignal.SWITCH_ROUTING));
        addCombinedRenderer(EnumSignal.BOX_RECEIVER, new TESRSignalBox(EnumSignal.BOX_RECEIVER));
        addCombinedRenderer(EnumSignal.BOX_CONTROLLER, new TESRSignalBox(EnumSignal.BOX_CONTROLLER));
        addCombinedRenderer(EnumSignal.BOX_CAPACITOR, new TESRSignalBox(EnumSignal.BOX_CAPACITOR));
        addCombinedRenderer(EnumSignal.BOX_BLOCK_RELAY, new TESRSignalBox(EnumSignal.BOX_BLOCK_RELAY));
        addCombinedRenderer(EnumSignal.BOX_SEQUENCER, new TESRSignalBox(EnumSignal.BOX_SEQUENCER));
        addCombinedRenderer(EnumSignal.BOX_INTERLOCK, new TESRSignalBox(EnumSignal.BOX_INTERLOCK));
        addCombinedRenderer(EnumSignal.BOX_ANALOG_CONTROLLER, new TESRSignalBox(EnumSignal.BOX_ANALOG_CONTROLLER));
    }

    public void addCombinedRenderer(EnumSignal type, ICombinedRenderer renderer) {
        addCombinedRenderer(type.ordinal(), renderer);
        if(renderer instanceof TileEntitySpecialRenderer){
            ClientRegistry.bindTileEntitySpecialRenderer(type.getTileClass(), (TileEntitySpecialRenderer) renderer);
        }
    }
}
