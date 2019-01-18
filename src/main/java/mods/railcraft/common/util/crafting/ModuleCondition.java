/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import com.google.gson.JsonObject;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

/**
 * A condition for a recipe which a railcraft module needs to be present for a recipe to load.
 */
@SuppressWarnings("unused")
public final class ModuleCondition implements BooleanSupplier {

    private final String moduleName;

    ModuleCondition(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public boolean getAsBoolean() {
        return RailcraftModuleManager.isModuleEnabled(moduleName);
    }

    public static final class Factory implements IConditionFactory {

        @Override
        public BooleanSupplier parse(JsonContext context, JsonObject json) {
            boolean primitive = JsonUtils.getBoolean(json, "primitive", false);
            String moduleName = JsonUtils.getString(json, "module");
            return new ModuleCondition(primitive ? moduleName : context.appendModId(moduleName));
        }
    }
}
