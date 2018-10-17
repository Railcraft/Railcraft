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
        return RailcraftModuleManager.isModuleEnabled(this.moduleName);
    }

    public static final class Factory implements IConditionFactory {

        /**
         * Invoked by forge via reflection.
         */
        public Factory() {
        }

        @Override
        public BooleanSupplier parse(JsonContext context, JsonObject json) {
            boolean primitive = JsonUtils.getBoolean(json, "primitive", false);
            String moduleName = JsonUtils.getString(json, "module");
            return new ModuleCondition(primitive ? moduleName : context.appendModId(moduleName));
        }
    }
}
