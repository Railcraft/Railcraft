/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.gates.*;
import java.util.EnumSet;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.machine.beta.TileEngine.EnergyStage;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Triggers implements ITileTrigger {

    HAS_WORK(400, "work", new TriggerHasWork()),
    HAS_CART(401, "cart", new TriggerHasCart()),
    ENGINE_BLUE(402, "engine.blue", new TriggerEngine(EnumSet.of(EnergyStage.BLUE))),
    ENGINE_GREEN(403, "engine.green", new TriggerEngine(EnumSet.of(EnergyStage.GREEN))),
    ENGINE_YELLOW(404, "engine.yellow", new TriggerEngine(EnumSet.of(EnergyStage.YELLOW))),
    ENGINE_ORANGE(405, "engine.orange", new TriggerEngine(EnumSet.of(EnergyStage.ORANGE))),
    ENGINE_RED(406, "engine.red", new TriggerEngine(EnumSet.of(EnergyStage.RED, EnergyStage.OVERHEAT))),
    LOW_FUEL(407, "fuel", new TriggerLowFuel()),
    TEMP_COLD(408, "temp.cold", new TriggerTemp(0, 100)),
    TEMP_WARM(409, "temp.warm", new TriggerTemp(100, 300)),
    TEMP_HOT(410, "temp.hot", new TriggerTemp(300, Integer.MAX_VALUE)),
    NEEDS_MAINT(411, "maintenance", new TriggerMaintenance()),
    ASPECT_GREEN(412, "aspect.green", new TriggerAspect(SignalAspect.GREEN)),
    ASPECT_BLINK_YELLOW(413, "aspect.yellow.blink", new TriggerAspect(SignalAspect.BLINK_YELLOW)),
    ASPECT_YELLOW(414, "aspect.yellow", new TriggerAspect(SignalAspect.YELLOW)),
    ASPECT_BLINK_RED(415, "aspect.red.blink", new TriggerAspect(SignalAspect.BLINK_RED)),
    ASPECT_RED(416, "aspect.red", new TriggerAspect(SignalAspect.RED)),
    ASPECT_OFF(417, "aspect.off", new TriggerAspect(SignalAspect.OFF));
    public static final Triggers[] VALUES = values();
    private final Trigger trigger;
    private final int id;
    private final String tag;
    private IIcon icon;

    private Triggers(int id, String tag, Trigger trigger) {
        this.id = id;
        this.tag = tag;
        this.trigger = trigger;
    }

    public static void init() {
        for (Triggers trigger : VALUES) {
            ActionManager.registerTrigger(trigger);
        }
    }

    @Override
    public String getUniqueTag() {
        return "railcraft." + tag;
    }

    @Override
    public IIcon getIcon() {
        return icon;
    }

    @Override
    public boolean hasParameter() {
        return false;
    }

    @Override
    public boolean requiresParameter() {
        return false;
    }

    @Override
    public String getDescription() {
        return LocalizationPlugin.translate("gates.trigger." + tag);
    }

    @Override
    public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter) {
        return trigger.isTriggerActive(side, tile, parameter);
    }

    @Override
    public ITriggerParameter createParameter() {
        return new TriggerParameter();
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon("railcraft:buildcraft.gate.trigger." + tag);
    }

    @Override
    public ITrigger rotateLeft() {
        return this;
    }

}
