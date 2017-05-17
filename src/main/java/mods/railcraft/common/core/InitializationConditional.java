/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.misc.Game;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.SimpleMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 9/1/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InitializationConditional {
    private List<Condition> conditions = new ArrayList<>();
    private Supplier<String> failureReason = () -> "";

    public boolean test(IRailcraftObjectContainer<?> objectContainer) {
        for (Condition condition : conditions) {
            if (!condition.predicate.test(objectContainer)) {
                failureReason = condition.failureReason;
                return false;
            }
        }
        return true;
    }

    public void printFailureReason(IRailcraftObjectContainer<?> objectContainer) {
        Game.log(Level.INFO, new SimpleMessage(objectContainer + " cannot be defined because " + failureReason.get()));
    }

    public void add(IRailcraftObjectContainer<?> objectContainer) {
        add(o -> RailcraftModuleManager.isObjectDefined(objectContainer) && objectContainer.isEnabled(), () -> objectContainer + " is disabled");
    }

    public void add(IRailcraftObjectContainer<?> objectContainer, IVariantEnum variant) {
        add(o -> RailcraftModuleManager.isObjectDefined(objectContainer) && objectContainer.isEnabled() && variant.isEnabled(), () -> objectContainer + "#" + variant + " is disabled");
    }

    public void add(Class<? extends IRailcraftModule> moduleClass) {
        add(o -> RailcraftModuleManager.isModuleEnabled(moduleClass), () -> "Module " + RailcraftModuleManager.getModuleName(moduleClass) + " is disabled");
    }

    public void add(Mod mod) {
        add(o -> mod.isLoaded(), () -> "Mod " + mod.modId + " is not ");
    }

    public void add(IVariantEnum variant) {
        add(o -> variant.isEnabled(), () -> "Object Variant " + variant.getClass().getSimpleName() + "." + variant.getName() + " is disabled");
    }

    public void add(BooleanSupplier condition, Supplier<String> failureReason) {
        add(o -> condition.getAsBoolean(), failureReason);
    }

    public void add(Predicate<IRailcraftObjectContainer<?>> condition, Supplier<String> failureReason) {
        conditions.add(new Condition(condition, failureReason));
    }

    private class Condition {
        private final Predicate<IRailcraftObjectContainer<?>> predicate;
        private final Supplier<String> failureReason;

        public Condition(Predicate<IRailcraftObjectContainer<?>> predicate, Supplier<String> failureReason) {
            this.predicate = predicate;
            this.failureReason = failureReason;
        }
    }
}
