/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forge;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.*;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import static net.minecraft.world.storage.loot.LootTableList.register;

/**
 * A utility class handling stuffs related to loot tables.
 *
 * Created by CovertJaguar on 4/10/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO fix loot broken when module is not avaiable. Maybe inject with events?
public final class LootPlugin {
    /**
     * The only instance of loot plugin.
     */
    public static final LootPlugin INSTANCE = new LootPlugin();
    /**
     * Name of the item exist condition.
     *
     * It is supposed to be {@code railcraft:item_exist} as a plain string.
     */
    public static final ResourceLocation ITEM_EXIST_CONDITION_NAME = RailcraftConstantsAPI.locationOf("item_exist");
    /**
     * The location of the railcraft workshop chest loot table.
     */
    public static final ResourceLocation CHESTS_VILLAGE_WORKSHOP = register(RailcraftConstantsAPI.locationOf( "chests/village_workshop"));
    private static final String[] poolNames = {"tools", "resources", "carts", "tracks", "general"};

    private LootPlugin() {
    }

    /**
     * Initializes the loot plugin.
     */
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
//        LootConditionManager.registerCondition(new ItemExistConditionSerializer());
    }

    @SubscribeEvent
    public void lootLoad(LootTableLoadEvent event) {
        if (!"minecraft".equals(event.getName().getNamespace())) {
            return;
        }

        ResourceLocation resourceLocation = RailcraftConstantsAPI.locationOf(event.getName().getPath());
        LootTable lootTable = LootTableLoader.loadBuiltinLootTable(resourceLocation, event.getLootTableManager());
        if (lootTable != null) {
            for (String poolName : poolNames) {
                LootPool pool = lootTable.getPool(RailcraftConstants.RESOURCE_DOMAIN + "_" + poolName);
                if (pool != null)
                    event.getTable().addPool(pool);
            }
        }
    }

    /**
     * Broken.
     */
    @Deprecated
    static final class ItemExistCondition implements LootCondition {
        final String module;
        final ResourceLocation name;

        ItemExistCondition(String module, ResourceLocation name) {
            this.module = module;
            this.name = name;
        }

        @Override
        public boolean testCondition(Random rand, LootContext context) {
            if (!RailcraftModuleManager.isModuleEnabled(module)) {
                Game.log().msg(Level.INFO, "disabled loot item {} for disabled module {}", name, module);
                return false;
            }
            if (ForgeRegistries.ITEMS.getValue(name) == null) {
                Game.log().msg(Level.INFO, "disabled loot item {} for missing in registry", name);
                return false;
            }
            return true;
        }
    }

    /**
     * A serializer for item exist condition.
     */
    @Deprecated
    static final class ItemExistConditionSerializer extends LootCondition.Serializer<ItemExistCondition> {
        ItemExistConditionSerializer() {
            super(ITEM_EXIST_CONDITION_NAME, ItemExistCondition.class);
        }

        @Override
        public void serialize(JsonObject json, ItemExistCondition value, JsonSerializationContext context) {
            json.addProperty("module", value.module);
            json.addProperty("name", value.name.toString());
        }

        @Override
        public ItemExistCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            String module = json.has("module") ? json.get("module").getAsString() : "railcraft:core";
            String name = json.get("name").getAsString();
            return new ItemExistCondition(module, new ResourceLocation(name));
        }
    }

    /**
     * Copy of {@link LootTableManager} that can load Railcraft's loot table additions.
     * This is a workaround so we can load loot table jsons that have pools to be added to vanilla's chests.
     * During {@link LootTableLoadEvent} the world's lootTable is not set yet.
     *
     * @author mezz
     */
    static final class LootTableLoader {
        private static final Gson GSON_INSTANCE = new GsonBuilder()
                .registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
                .registerTypeAdapter(LootPool.class, new LootPool.Serializer())
                .registerTypeAdapter(LootTable.class, new LootTable.Serializer())
                .registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer())
                .registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer())
                .registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer())
                .registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer())
                .create();

        @Nullable
        public static LootTable loadBuiltinLootTable(ResourceLocation resource, LootTableManager manager) {
            URL url = LootTableLoader.class.getResource("/assets/" + resource.getNamespace() + "/loot_tables/" + resource.getPath() + ".json");

            if (url != null) {
                String s;

                try {
                    s = Resources.toString(url, Charsets.UTF_8);
                } catch (IOException ioexception) {
                    Game.log().throwable("Couldn\'t load loot table {0} from {1}", ioexception, resource, url);
                    return LootTable.EMPTY_LOOT_TABLE;
                }

                try {
                    // custom is false so that other mods can listen to the loot load event of rc custom tables
                    return ForgeHooks.loadLootTable(GSON_INSTANCE, resource, s, false, manager);
                } catch (JsonParseException jsonparseexception) {
                    Game.log().throwable("Couldn\'t load loot table {0} from {1}", jsonparseexception, resource, url);
                    return LootTable.EMPTY_LOOT_TABLE;
                }
            } else {
                return null;
            }
        }

    }
}
