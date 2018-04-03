package mods.railcraft.client.render.tesr;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 *
 */
public class RailcraftCustomItemRenderer extends TileEntityItemStackRenderer {

    public static final RailcraftCustomItemRenderer INSTANCE = new RailcraftCustomItemRenderer();
    private final TileEntityItemStackRenderer parent;
    private final Collection<Predicate<ItemStack>> handles = new ArrayList<>();

    private RailcraftCustomItemRenderer() {
        parent = TileEntityItemStackRenderer.instance;
        TileEntityItemStackRenderer.instance = this;
    }

    public void registerItemStackHandler(Predicate<ItemStack> handle) {
        handles.add(handle);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        for (Predicate<ItemStack> handle : handles) {
            if (handle.test(itemStackIn))
                return;
        }
        parent.renderByItem(itemStackIn);
    }
}
