/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forestry;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackInterface;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.crafting.FilterBeesGenomeRecipe;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ForestryPlugin {
    public static final String FORESTRY_ID = "forestry";
    public static final String FORESTRY_NAME = "Forestry";
    private static ForestryPlugin instance;

    public static ForestryPlugin instance() {
        if (instance == null) {
            if (Mod.FORESTRY.isLoaded())
                instance = new ForestryPluginInstalled();
            else
                instance = new ForestryPlugin();
        }
        return instance;
    }

    public static ItemStack getItem(String tag) {
        if (!Mod.FORESTRY.isLoaded())
            return InvTools.emptyStack();
        Item item = Item.REGISTRY.getObject(new ResourceLocation(Mod.FORESTRY.modId, tag));
        if (item == null)
            return InvTools.emptyStack();
        return new ItemStack(item, 1);
    }

    public static void addBackpackItem(String pack, @Nullable ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return;
        addBackpackItem(pack, stack.getItem(), stack.getItemDamage());
    }

    public static void addBackpackItem(String pack, Item item) {
        sendBackpackMessage(String.format("%s@%s:*", pack, ForgeRegistries.ITEMS.getKey(item)));
    }

    public static void addBackpackItem(String pack, Item item, int damage) {
        sendBackpackMessage(String.format("%s@%s:%d", pack, ForgeRegistries.ITEMS.getKey(item), damage));
    }

    public static void addBackpackItem(String pack, Block block) {
        sendBackpackMessage(String.format("%s@%s:*", pack, ForgeRegistries.BLOCKS.getKey(block)));
    }

    public static void addBackpackItem(String pack, Block block, int meta) {
        sendBackpackMessage(String.format("%s@%s:%d", pack, ForgeRegistries.BLOCKS.getKey(block), meta));
    }

    private static void sendBackpackMessage(String message) {
        if (message.contains("null"))
            throw new IllegalArgumentException("Attempting to register broken item with Forestry Backpack!");
//        Game.logDebug(Level.FINEST, "Sending IMC to Forestry add-backpack-items: {0}", message);
        FMLInterModComms.sendMessage(ForestryPlugin.FORESTRY_ID, "add-backpack-items", message);
    }

    public void defineBackpackRecipes() {
    }

    public void setupBackpackContents() {
    }

    public void addCarpenterRecipe(String recipeTag,
                                   int packagingTime,
                                   @Nullable FluidStack liquid,
                                   ItemStack box,
                                   ItemStack product,
                                   Object... materials) {
    }

    public @Nullable Item getBackpack(String backpackId, String type) {
        return null;
    }

    public boolean isBee(ItemStack stack) {
        return false;
    }

    public boolean isAnalyzedBee(ItemStack stack) {
        return false;
    }

    public void registerBeeFilterRecipe() {
    }

    @SuppressWarnings("deprecation")
    private static final class ForestryPluginInstalled extends ForestryPlugin {
        @Override
        @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
        public void defineBackpackRecipes() {
            MinecraftForge.EVENT_BUS.register(new BackpackEventHandler());

            ItemStack silk = ModItems.SILK.getStack();

            RailcraftItems trackmanT1 = RailcraftItems.BACKPACK_TRACKMAN_T1;
            if (trackmanT1.isLoaded()) {
                ItemStack backpack = trackmanT1.getStack();
                if (!InvTools.isEmpty(backpack)) {
                    addBackpackTooltip(backpack);
                    CraftingPlugin.addShapedRecipe(backpack,
                            "X#X",
                            "VYV",
                            "X#X",
                            '#', Blocks.WOOL,
                            'V', new ItemStack(Blocks.RAIL),
                            'X', Items.STRING,
                            'Y', "chestWood");
                }
            }

            RailcraftItems trackmanT2 = RailcraftItems.BACKPACK_TRACKMAN_T2;
            if (trackmanT1.isLoaded() && trackmanT2.isLoaded()) {
                ItemStack backpack = trackmanT2.getStack();
                if (!InvTools.isEmpty(backpack)) {
                    addBackpackTooltip(backpack);
                    if (!InvTools.isEmpty(silk)) {
                        forestry.api.recipes.RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.get(1000), ItemStack.EMPTY, backpack,
                                "WXW",
                                "WTW",
                                "WWW",
                                'X', "gemDiamond",
                                'W', silk,
                                'T', RailcraftItems.BACKPACK_TRACKMAN_T1.getWildcard());
                    }
                }
            }

            RailcraftItems signalmanT1 = RailcraftItems.BACKPACK_SIGNALMAN_T1;
            if (signalmanT1.isLoaded()) {
                ItemStack backpack = signalmanT1.getStack();
                if (!InvTools.isEmpty(backpack)) {
                    addBackpackTooltip(backpack);
                    CraftingPlugin.addShapedRecipe(backpack,
                            "X#X",
                            "VYV",
                            "X#X",
                            '#', Blocks.WOOL,
                            'V', RailcraftItems.SIGNAL_LAMP,
                            'X', Items.STRING,
                            'Y', "chestWood");
                }
            }

            RailcraftItems signalmanT2 = RailcraftItems.BACKPACK_SIGNALMAN_T2;
            if (signalmanT1.isLoaded() && signalmanT2.isLoaded()) {
                ItemStack backpack = signalmanT2.getStack();
                if (!InvTools.isEmpty(backpack)) {
                    addBackpackTooltip(backpack);
                    if (!InvTools.isEmpty(silk)) {
                        forestry.api.recipes.RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.get(1000), ItemStack.EMPTY, backpack,
                                "WXW",
                                "WTW",
                                "WWW",
                                'X', "gemDiamond",
                                'W', silk,
                                'T', signalmanT1.getWildcard());
                    }
                }
            }

            RailcraftItems icemanT1 = RailcraftItems.BACKPACK_ICEMAN_T1;
            if (icemanT1.isLoaded()) {
                ItemStack backpack = icemanT1.getStack();
                if (!InvTools.isEmpty(backpack)) {
                    addBackpackTooltip(backpack);
                    CraftingPlugin.addShapedRecipe(backpack,
                            "X#X",
                            "VYV",
                            "X#X",
                            '#', Blocks.WOOL,
                            'V', new ItemStack(Blocks.SNOW),
                            'X', Items.STRING,
                            'Y', "chestWood");
                }
            }

            RailcraftItems icemanT2 = RailcraftItems.BACKPACK_ICEMAN_T2;
            if (icemanT1.isLoaded() && icemanT2.isLoaded()) {
                ItemStack backpack = icemanT2.getStack();
                if (!InvTools.isEmpty(backpack)) {
                    addBackpackTooltip(backpack);
                    if (!InvTools.isEmpty(silk)) {
                        forestry.api.recipes.RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.get(1000), ItemStack.EMPTY, backpack,
                                "WXW",
                                "WTW",
                                "WWW",
                                'X', "gemDiamond",
                                'W', silk,
                                'T', icemanT1.getWildcard());
                    }
                }
            }


            RailcraftItems apothecaryT1 = RailcraftItems.BACKPACK_APOTHECARY_T1;
            if (apothecaryT1.isLoaded()) {
                ItemStack backpack = apothecaryT1.getStack();
                if (!InvTools.isEmpty(backpack)) {
                    addBackpackTooltip(backpack);
//                if (!ThaumcraftPlugin.isModInstalled()) {
                    CraftingPlugin.addShapedRecipe(backpack,
                            "X#X",
                            "VYV",
                            "X#X",
                            '#', Blocks.WOOL,
                            'V', Items.GLASS_BOTTLE,
                            'X', Items.STRING,
                            'Y', "chestWood");
//                } else
//                    ApothecariesBackpack.registerThaumcraftResearch();
                }
            }

            RailcraftItems apothecaryT2 = RailcraftItems.BACKPACK_APOTHECARY_T2;
            if (apothecaryT1.isLoaded() && apothecaryT2.isLoaded()) {
                ItemStack backpack = apothecaryT2.getStack();
                if (!InvTools.isEmpty(backpack)) {
                    addBackpackTooltip(backpack);
                    if (!InvTools.isEmpty(silk)) {
                        forestry.api.recipes.RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.get(1000), ItemStack.EMPTY, backpack,
                                "WXW",
                                "WTW",
                                "WWW",
                                'X', "gemDiamond",
                                'W', silk,
                                'T', apothecaryT1.getWildcard());
                    }
                }
            }
        }

        @Override
        @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
        public @Nullable Item getBackpack(String backpackId, String type) {
            IBackpackInterface backMan = forestry.api.storage.BackpackManager.backpackInterface;
            if (backMan == null)
                return null;
            BaseBackpack backpack;
            switch (backpackId) {
                case "trackman":
                    backpack = TrackmanBackpack.getInstance();
                    break;
                case "iceman":
                    backpack = IcemanBackpack.getInstance();
                    break;
                case "apothecary":
                    backpack = ApothecariesBackpack.getInstance();
                    break;
                case "signalman":
                    backpack = SignalmanBackpack.getInstance();
                    break;
                default:
                    throw new RuntimeException("Invalid backpackId");
            }
            backMan.registerBackpackDefinition(backpack.getId(), backpack);
            return new ItemBackpackWrapper(backMan.createBackpack(backpack.getId(), EnumBackpackType.valueOf(type)).setCreativeTab(CreativePlugin.RAILCRAFT_TAB));
        }

        @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
        private void addBackpackTooltip(ItemStack stack) {
            InvTools.addItemToolTip(stack, "\u00a77\u00a7o" + LocalizationPlugin.translate("item.railcraft.backpack.tips"));
        }

        @Override
        @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
        public void setupBackpackContents() {
            try {
                if (forestry.api.storage.BackpackManager.backpackInterface == null)
                    return;
                TrackmanBackpack.getInstance().setup();
                IcemanBackpack.getInstance().setup();
                ApothecariesBackpack.getInstance().setup();
                SignalmanBackpack.getInstance().setup();
            } catch (Throwable error) {
                Game.log().api(ForestryPlugin.FORESTRY_NAME, error, forestry.api.storage.BackpackManager.class);
            }
        }

        @Override
        @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
        public void addCarpenterRecipe(String name, int packagingTime, @Nullable FluidStack liquid, ItemStack box, @Nullable ItemStack product, Object... materials) {
            if (product == null) {
                Game.log().trace(Level.WARN, "Tried to define invalid Carpenter recipe {0}, the result was null or zero. Skipping", name);
                return;
            }
            ResourceLocation resource = new ResourceLocation(name);
//            try {
//                materials = CraftingPlugin.cleanRecipeArray(CraftingPlugin.RecipeType.SHAPED, product, materials);
//            } catch (InvalidRecipeException ex) {
//                Game.logTrace(Level.WARN, "Tried to define invalid Carpenter recipe \"{0}\", some ingredients were missing. Skipping", recipeTag);
//                Game.logTrace(Level.WARN, ex.getRawMessage());
//                return;
//            }
            try {
                if (forestry.api.recipes.RecipeManagers.carpenterManager != null && RailcraftConfig.getRecipeConfig("forestry.carpenter." + resource.getPath()))
                    forestry.api.recipes.RecipeManagers.carpenterManager.addRecipe(packagingTime, liquid, box, product, materials);
            } catch (Throwable error) {
                Game.log().api(ForestryPlugin.FORESTRY_NAME, error, forestry.api.recipes.RecipeManagers.class);
            }
        }

        @Override
        public boolean isBee(ItemStack stack) {
            IBeeRoot root = BeeManager.beeRoot;
            if (root == null) {
                return false;
            }
            IBee bee = root.getMember(stack);
            return bee != null;
        }

        @Override
        public boolean isAnalyzedBee(ItemStack stack) {
            IBeeRoot root = BeeManager.beeRoot;
            if (root == null) {
                return false;
            }
            IBee bee = root.getMember(stack);
            return bee != null && bee.isAnalyzed();
        }

        @Override
        public void registerBeeFilterRecipe() {
            CraftingPlugin.addRecipe(new FilterBeesGenomeRecipe());
//            RecipeSorter.register("railcraft:filter_bees_species", FilterBeesGenomeRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        }
    }
}
