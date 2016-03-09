/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import cpw.mods.fml.client.registry.ClientRegistry;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.signals.EnumSignal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockSignal extends BlockRenderer {

    public RenderBlockSignal() {
        super(RailcraftBlocks.getBlockSignal());

        addCombinedRenderer(EnumSignal.DUAL_HEAD_BLOCK_SIGNAL, new RenderSignalDual());
        addCombinedRenderer(EnumSignal.DUAL_HEAD_DISTANT_SIGNAL, new RenderSignalDual());
        addCombinedRenderer(EnumSignal.BLOCK_SIGNAL, new RenderSignal(SignalAspect.GREEN));
        addCombinedRenderer(EnumSignal.DISTANT_SIGNAL, new RenderSignal(SignalAspect.RED));
        addCombinedRenderer(EnumSignal.SWITCH_MOTOR, new RenderSwitch(EnumSignal.SWITCH_MOTOR));
        addCombinedRenderer(EnumSignal.SWITCH_LEVER, new RenderSwitch(EnumSignal.SWITCH_LEVER));
        addCombinedRenderer(EnumSignal.SWITCH_ROUTING, new RenderSwitch(EnumSignal.SWITCH_ROUTING));
        addCombinedRenderer(EnumSignal.BOX_RECEIVER, new RenderSignalBox(EnumSignal.BOX_RECEIVER));
        addCombinedRenderer(EnumSignal.BOX_CONTROLLER, new RenderSignalBox(EnumSignal.BOX_CONTROLLER));
        addCombinedRenderer(EnumSignal.BOX_CAPACITOR, new RenderSignalBox(EnumSignal.BOX_CAPACITOR));
        addCombinedRenderer(EnumSignal.BOX_BLOCK_RELAY, new RenderSignalBox(EnumSignal.BOX_BLOCK_RELAY));
        addCombinedRenderer(EnumSignal.BOX_SEQUENCER, new RenderSignalBox(EnumSignal.BOX_SEQUENCER));
        addCombinedRenderer(EnumSignal.BOX_INTERLOCK, new RenderSignalBox(EnumSignal.BOX_INTERLOCK));
        addCombinedRenderer(EnumSignal.BOX_ANALOG_CONTROLLER, new RenderSignalBox(EnumSignal.BOX_ANALOG_CONTROLLER));
    }

    public void addCombinedRenderer(EnumSignal type, ICombinedRenderer renderer) {
        addCombinedRenderer(type.ordinal(), renderer);
        if(renderer instanceof TileEntitySpecialRenderer){
            ClientRegistry.bindTileEntitySpecialRenderer(type.getTileClass(), (TileEntitySpecialRenderer) renderer);
        }
    }
}
