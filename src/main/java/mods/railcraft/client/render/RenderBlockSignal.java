/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.signals.EnumSignal;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockSignal extends BlockRenderer {

    public RenderBlockSignal() {
        super(RailcraftBlocks.getBlockSignal());

        addCombinedRenderer(EnumSignal.DUAL_HEAD_BLOCK_SIGNAL.ordinal(), new RenderSignalDual());
        addCombinedRenderer(EnumSignal.DUAL_HEAD_DISTANT_SIGNAL.ordinal(), new RenderSignalDual());
        addCombinedRenderer(EnumSignal.BLOCK_SIGNAL.ordinal(), new RenderSignal(SignalAspect.GREEN));
        addCombinedRenderer(EnumSignal.DISTANT_SIGNAL.ordinal(), new RenderSignal(SignalAspect.RED));
        addCombinedRenderer(EnumSignal.SWITCH_MOTOR.ordinal(), new RenderSwitch(EnumSignal.SWITCH_MOTOR));
        addCombinedRenderer(EnumSignal.SWITCH_LEVER.ordinal(), new RenderSwitch(EnumSignal.SWITCH_LEVER));
        addCombinedRenderer(EnumSignal.SWITCH_ROUTING.ordinal(), new RenderSwitch(EnumSignal.SWITCH_ROUTING));
        addCombinedRenderer(EnumSignal.BOX_RECEIVER.ordinal(), RenderSignalBox.INSTANCE);
        addCombinedRenderer(EnumSignal.BOX_CONTROLLER.ordinal(), RenderSignalBox.INSTANCE);
        addCombinedRenderer(EnumSignal.BOX_ANALOG_CONTROLLER.ordinal(), RenderSignalBox.INSTANCE);
        addCombinedRenderer(EnumSignal.BOX_CAPACITOR.ordinal(), RenderSignalBox.INSTANCE);
        addCombinedRenderer(EnumSignal.BOX_BLOCK_RELAY.ordinal(), RenderSignalBox.INSTANCE);
        addCombinedRenderer(EnumSignal.BOX_SEQUENCER.ordinal(), RenderSignalBox.INSTANCE);
        addCombinedRenderer(EnumSignal.BOX_INTERLOCK.ordinal(), RenderSignalBox.INSTANCE);
    }
}
