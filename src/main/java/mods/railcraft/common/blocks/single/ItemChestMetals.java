package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.ItemBlockEntityDelegate;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemChestMetals extends ItemBlockEntityDelegate {
    public ItemChestMetals(Block block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
            private final TileChestMetals template = new TileChestMetals();

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
                "GPG",
                "PAP",
                "GPG",
                'A', new ItemStack(Blocks.ANVIL),
                'P', new ItemStack(Blocks.PISTON),
                'G', "gearSteel");
    }
}
