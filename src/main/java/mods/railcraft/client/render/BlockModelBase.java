/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockModelBase extends PerspAwareModelBase implements ISmartBlockModel, ISmartItemModel, IItemBaker {

    private final Map<IBlockState, IBlockBaker> blockBakers = new HashMap<IBlockState, IBlockBaker>();
    private final Map<Integer, IItemBaker> itemBakers = new HashMap<Integer, IItemBaker>();
    private ICombinedBaker defaultBaker = new DefaultRenderer();
    private final Block block;

    public BlockModelBase(Block block) {
        this.block = block;
    }

    public void addCombinedRenderer(IBlockState state, ICombinedBaker renderer) {
        blockBakers.put(state, renderer);
        itemBakers.put(state.getBlock().getMetaFromState(state), renderer);
    }

    public void addBlockRenderer(IBlockState state, IBlockBaker renderer) {
        blockBakers.put(state, renderer);
    }

    public void addItemRenderer(int meta, IItemBaker renderer) {
        itemBakers.put(meta, renderer);
    }

    public void setDefaultRenderer(ICombinedBaker renderer) {
        defaultBaker = renderer;
    }
    
    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        if (!blockBakers.isEmpty()) {
            IBlockBaker baker = blockBakers.get(state);
            if (baker != null) {
                IBakedModel model = baker.bakeBlock(state);
                if (model != null) {
                    return model;
                }
            }
        }
        return defaultBaker.bakeBlock(state);
    }
    
    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return bakeItem(stack);
    }

    @Override
    public IBakedModel bakeItem(ItemStack item) {
        IItemBaker renderer = itemBakers.get(item.getItemDamage());
        if (renderer != null) {
            return renderer.bakeItem(item);
        }
        return defaultBaker.bakeItem(item);
    }

    public Block getBlock() {
        return block;
    }

    protected class EmptyBlockBaker implements IBlockBaker {
        @Override
        public IBakedModel bakeBlock(IBlockState state) {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        }
    }

    public class DefaultRenderer implements ICombinedBaker {
        @Override
        public IBakedModel bakeItem(ItemStack item) {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        }

        @Override
        public IBakedModel bakeBlock(IBlockState state) {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        }
    }
}
