/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.client.render.tools;

import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import org.apache.logging.log4j.Level;

import java.util.Arrays;

/**
 * Created by CovertJaguar on 7/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelManager {
    public static void registerItemModel(Item item, int meta) {
        ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName(), "inventory");
        registerItemModel(item, meta, location);
    }

    public static void registerItemModel(Item item, int meta, String json) {
        ModelResourceLocation location = new ModelResourceLocation(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, json), "inventory");
        registerItemModel(item, meta, location);
    }

    public static void registerItemModel(Item item, int meta, ModelResourceLocation location) {
        if (Game.IS_DEBUG)
            Game.log(Level.INFO, "Registering item model: {0} meta:{1} location:{2}", item.getRegistryName(), meta, location);
        ModelLoader.setCustomModelResourceLocation(item, meta, location);
    }

    public static void registerBlockItemModel(ItemBlock item, IBlockState state) {
        if (Game.IS_DEBUG)
            Game.log(Level.INFO, "Registering block item model: {0} state:{1}", item.getRegistryName(), state);
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(state.getBlock().getRegistryName(), new DefaultStateMapper().getPropertyString(state.getProperties()));
        ModelLoader.setCustomModelResourceLocation(item, state.getBlock().getMetaFromState(state), modelResourceLocation);
    }

    public static void registerComplexItemModel(Item item, ItemMeshDefinition meshDefinition, ModelResourceLocation... locations) {
        if (Game.IS_DEBUG)
            Game.log(Level.INFO, "Registering complex item model: {0} locations:{1}", item.getRegistryName(), Arrays.toString(locations));
        ModelLoader.setCustomMeshDefinition(item, meshDefinition);
        for (ModelResourceLocation location : locations) {
            ModelBakery.registerItemVariants(item, location);
        }
    }
}
