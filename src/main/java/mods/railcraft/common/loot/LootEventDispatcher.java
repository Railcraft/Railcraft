package mods.railcraft.common.loot;

import mods.railcraft.common.core.Railcraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class LootEventDispatcher {

    public static LootEventDispatcher INSTANCE = new LootEventDispatcher();

    private static Map<ResourceLocation, LootTable> lootTableMap = new HashMap<>();

    public static void registerHandler() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public static LootTable getLootTable(ResourceLocation resourceLocation) {
        LootTable lootTable = lootTableMap.get(resourceLocation);
        if (lootTable == null) {
            throw new NullPointerException("No loot table loaded for resource location: " + resourceLocation.toString());
        }
        return lootTable;
    }

    public static void addItem(ResourceLocation location, WeightedRandomChestContent loot) {
        ResourceLocation railcraftLocation = new ResourceLocation(Railcraft.MOD_ID, location.getResourcePath());
        String railcraftLocationString = railcraftLocation.toString();

        LootTable lootTable = LootEventDispatcher.getLootTable(location);

        LootPool lootPool = lootTable.getPool(railcraftLocationString);
        if (lootPool == null) {
            lootPool = emptyPool(railcraftLocationString);
            lootTable.addPool(lootPool);
        }

        LootEntryItem lootEntryItem = new LootEntryItem(
                loot.getStack().getItem(),
                loot.getLootChance(),
                1,
                new LootFunction[]{new SetCount(new LootCondition[0], new RandomValueRange(loot.getMinStack(), loot.getMaxStack()))},
                new LootCondition[0],
                Railcraft.MOD_ID
        );
        lootPool.addEntry(lootEntryItem);
    }

    private static LootPool emptyPool(String name) {
        return new LootPool(
                new LootEntry[0],
                new LootCondition[0],
                new RandomValueRange(1),
                new RandomValueRange(1),
                name);
    }

    @SubscribeEvent
    public void onLootTablesLoaded(LootTableLoadEvent event) {
        ResourceLocation resourceLocation = event.getName();
        LootTable lootTable = event.getTable();
        lootTableMap.put(resourceLocation, lootTable);
    }
}
