/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.statements.*;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.machine.single.TileEngine.EnergyStage;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Triggers implements ITriggerExternal {

    HAS_WORK("work", new TriggerHasWork()),
    HAS_CART("cart", new TriggerHasCart()),
    ENGINE_BLUE("engine.blue", new TriggerEngine(EnumSet.of(EnergyStage.BLUE))),
    ENGINE_GREEN("engine.green", new TriggerEngine(EnumSet.of(EnergyStage.GREEN))),
    ENGINE_YELLOW("engine.yellow", new TriggerEngine(EnumSet.of(EnergyStage.YELLOW))),
    ENGINE_ORANGE("engine.orange", new TriggerEngine(EnumSet.of(EnergyStage.ORANGE))),
    ENGINE_RED("engine.red", new TriggerEngine(EnumSet.of(EnergyStage.RED, EnergyStage.OVERHEAT))),
    LOW_FUEL("fuel", new TriggerLowFuel()),
    TEMP_COLD("temp.cold", new TriggerTemp(0, 100)),
    TEMP_WARM("temp.warm", new TriggerTemp(100, 300)),
    TEMP_HOT("temp.hot", new TriggerTemp(300, Integer.MAX_VALUE)),
    NEEDS_MAINT("maintenance", new TriggerMaintenance()),
    ASPECT_GREEN("aspect.green", new TriggerAspect(SignalAspect.GREEN)),
    ASPECT_BLINK_YELLOW("aspect.yellow.blink", new TriggerAspect(SignalAspect.BLINK_YELLOW)),
    ASPECT_YELLOW("aspect.yellow", new TriggerAspect(SignalAspect.YELLOW)),
    ASPECT_BLINK_RED("aspect.red.blink", new TriggerAspect(SignalAspect.BLINK_RED)),
    ASPECT_RED("aspect.red", new TriggerAspect(SignalAspect.RED)),
    ASPECT_OFF("aspect.off", new TriggerAspect(SignalAspect.OFF));
    public static final Triggers[] VALUES = values();
    private final Trigger trigger;
    private final String tag;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite sprite;

    Triggers(String tag, Trigger trigger) {
        this.tag = tag;
        this.trigger = trigger;
    }

    public static void init() {
        for (Triggers trigger : VALUES) {
            StatementManager.registerStatement(trigger);
            StatementManager.statements.put("railcraft." + trigger.tag, trigger);
        }
    }

    @Override
    public String getUniqueTag() {
        return "railcraft:" + tag;
    }

    @Override
    public String getDescription() {
        return LocalizationPlugin.translate("gates.trigger." + tag);
    }

    @Override
    public boolean isTriggerActive(TileEntity tile, EnumFacing side, IStatementContainer isc, IStatementParameter[] parameter) {
        return trigger.isTriggerActive(side, tile, parameter);
    }

    @Override
    public void registerIcons(TextureMap register) {
        sprite = register.registerSprite(new ResourceLocation("railcraft", "buildcraft.gate.trigger." + tag));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getGuiSprite() {
        return sprite;
    }

    @Override
    public int maxParameters() {
        return 0;
    }

    @Override
    public int minParameters() {
        return 0;
    }

    @Override
    public IStatementParameter createParameter(int i) {
        return null;
    }

    @Override
    public IStatement rotateLeft() {
        return this;
    }

}
