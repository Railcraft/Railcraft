/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import com.google.gson.JsonObject;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

/**
 * A condition for a recipe which a railcraft module needs to be present for a recipe to load.
 */
@SuppressWarnings("unused")
public final class OreTagCondition implements BooleanSupplier {

    private final String oreTag;

    OreTagCondition(String oreTag) {
        this.oreTag = oreTag;
    }

    @Override
    public boolean getAsBoolean() {
        return OreDictPlugin.oreExists(oreTag);
    }

    public static final class Factory implements IConditionFactory {

        @Override
        public BooleanSupplier parse(JsonContext context, JsonObject json) {
            return new OreTagCondition(JsonUtils.getString(json, "tag"));
        }
    }
}
