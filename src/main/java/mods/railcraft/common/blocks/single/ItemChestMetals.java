package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemChestMetals extends ItemBlockRailcraft {
    public ItemChestMetals(Block block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
            private final TileChestMetals template = new TileChestMetals();
            private final TileEntitySpecialRenderer<TileChestMetals> renderer = TileEntityRendererDispatcher.instance.getRenderer(TileChestMetals.class);

            @Override
            @SideOnly(Side.CLIENT)
            public void renderByItem(ItemStack p_192838_1_, float partialTicks) {
                renderer.render(template, 0, 0, 0, partialTicks, -1, 0.0F);
            }
        });
    }

    @Override
    public void defineRecipes() {
        //TODO recipe
    }
}
