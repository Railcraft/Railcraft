/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forge;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;

import static net.minecraft.world.storage.loot.LootTableList.register;

/**
 * Created by CovertJaguar on 4/10/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LootPlugin {
    public static final LootPlugin INSTANCE = new LootPlugin();
    public static final ResourceLocation CHESTS_VILLAGE_WORKSHOP = register(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, "chests/village_workshop"));
    private static final String[] poolNames = {"tools", "resources", "carts", "tracks", "general"};

    @SubscribeEvent
    public void lootLoad(LootTableLoadEvent event) {
        if (!"minecraft".equals(event.getName().getResourceDomain())) {
            return;
        }

        ResourceLocation resourceLocation = new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, event.getName().getResourcePath());
        LootTable lootTable = LootTableLoader.loadBuiltinLootTable(resourceLocation);
        if (lootTable != null) {
            for (String poolName : poolNames) {
                LootPool pool = lootTable.getPool(RailcraftConstants.RESOURCE_DOMAIN + "_" + poolName);
                if (pool != null)
                    event.getTable().addPool(pool);
            }
        }
    }

    /**
     * Copy of {@link LootTableManager} that can load Railcraft's loot table additions.
     * This is a workaround so we can load loot table jsons that have pools to be added to vanilla's chests.
     * During {@link LootTableLoadEvent} the world's lootTable is not set yet.
     *
     * @author mezz
     */
    public static class LootTableLoader {
        private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();

        @Nullable
        public static LootTable loadBuiltinLootTable(ResourceLocation resource) {
            URL url = LootTableLoader.class.getResource("/assets/" + resource.getResourceDomain() + "/loot_tables/" + resource.getResourcePath() + ".json");

            if (url != null) {
                String s;

                try {
                    s = Resources.toString(url, Charsets.UTF_8);
                } catch (IOException ioexception) {
                    Game.logThrowable("Couldn\'t load loot table {0} from {1}", ioexception, resource, url);
                    return LootTable.EMPTY_LOOT_TABLE;
                }

                try {
                    return net.minecraftforge.common.ForgeHooks.loadLootTable(GSON_INSTANCE, resource, s, false);
                } catch (JsonParseException jsonparseexception) {
                    Game.logThrowable("Couldn\'t load loot table {0} from {1}", jsonparseexception, resource, url);
                    return LootTable.EMPTY_LOOT_TABLE;
                }
            } else {
                return null;
            }
        }

    }
}
