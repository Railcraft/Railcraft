package mods.railcraft.common.carts;

import mods.railcraft.client.core.SleepKeyHandler;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 */
public class ItemCartBed extends ItemCart {
    public ItemCartBed(IRailcraftCartContainer cart) {
        super(cart);
    }

    @Override
    public void initializeDefinition() {
        super.initializeDefinition();
        MinecraftForge.EVENT_BUS.register(BedCartEventListener.INSTANCE);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(getStack(),
                "B",
                "M",
                'B', Items.BED,
                'M', Items.MINECART);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initializeClient() {
        super.initializeClient();
        SleepKeyHandler.INSTANCE.init();
    }
}
