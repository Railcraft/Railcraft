package mods.railcraft.common.blocks.machine.delta;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.blocks.frame.BlockFrame;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockCatenary extends Block implements IPostConnection {
	
	private static BlockCatenary instance;
	private TileCatenary tile = new TileCatenary();

	public static BlockCatenary getBlock() {
		return instance;
	}
	
	public static void registerBlock() {
		if(instance == null) {
			instance = new BlockCatenary(Railcraft.proxy.getRenderId());
            //RailcraftRegistry.register(instance, ItemBlock.class);

            HarvestPlugin.setHarvestLevel(instance, "crowbar", 0);
            HarvestPlugin.setHarvestLevel(instance, "pickaxe", 1);
            
            RailcraftCraftingManager.rollingMachine.getRecipeList().add(new ShapedOreRecipe(getItem(12),
            		"WWW",
            		'W', EnumMachineDelta.WIRE.getBlock()));
		}
	}
	
	public static ItemStack getItem() {
        return getItem(1);
    }

    public static ItemStack getItem(int qty) {
        if (instance == null) return null;
        return new ItemStack(instance, qty, 0);
    }
	
    private final int renderId;
    
	protected BlockCatenary(int renderId) {
		super(Material.iron);
		this.renderId = renderId;
		setResistance(10);
        setHardness(5);
        setStepSound(Block.soundTypeMetal);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setBlockName("railcraft.machine.delta.catenary");
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		//We don't want this block to be placed by a player
		if(!(entity instanceof EntityPlayer))
			super.onBlockPlacedBy(world, x, y, z, entity, stack);
	}

	@Override
	public ConnectStyle connectsToPost(IBlockAccess world, int x, int y, int z,
			ForgeDirection side) {
		return (side == ForgeDirection.DOWN) ? ConnectStyle.SINGLE_THICK : ConnectStyle.NONE;
	}

}
