package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.ItemBlockEntityDelegate;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemChestVoid extends ItemBlockEntityDelegate {
    public ItemChestVoid(Block block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
            private final TileChestVoid template = new TileChestVoid();

            @Override
            @SideOnly(Side.CLIENT)
            public void renderByItem(ItemStack p_192838_1_, float partialTicks) {
                TileEntityRendererDispatcher.instance.render(template, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
            }
        });
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "OOO",
                "OPO",
                "OOO",
                'O', new ItemStack(Blocks.OBSIDIAN),
                'P', new ItemStack(Items.ENDER_PEARL));
    }
}
