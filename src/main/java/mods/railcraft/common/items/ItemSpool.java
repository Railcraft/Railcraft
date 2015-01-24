package mods.railcraft.common.items;

import java.util.ArrayList;

import mods.railcraft.common.blocks.aesthetics.post.BlockPostBase;
import mods.railcraft.common.blocks.machine.delta.BlockCatenary;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSpool extends ItemRailcraft {

	private static final String ITEM_TAG = "railcraft.tool.spool";
	private static final int MAX_WIRES = 256;
	private static final int MAX_RANGE = 16;
	private static Item item;
	
	public static void register() {
		if(item == null) {
			item = new ItemSpool();
			item.setUnlocalizedName(ITEM_TAG);
			item.setMaxStackSize(1);
			RailcraftRegistry.register(item);
			
			CraftingPlugin.addShapedRecipe(new ItemStack(item),
					"III",
					" G ",
					"III",
					'I', Items.iron_ingot,
					'G', RailcraftItem.gear.getRecipeObject(ItemGear.EnumGear.IRON));
		}
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack itemStack) {
		return 1.0 - itemStack.stackTagCompound.getInteger("wires") / MAX_WIRES;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack itemStack) {
		return true;
	}
	
	@Override
	public boolean hasEffect(ItemStack itemStack, int idunnolol) {
		if(itemStack.stackTagCompound == null)
			onCreated(itemStack, null, null);
		return itemStack.stackTagCompound.getBoolean("started");
	}
	
	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		itemStack.stackTagCompound = new NBTTagCompound();
		itemStack.stackTagCompound.setInteger("wires", 0);
		itemStack.stackTagCompound.setBoolean("started", false);
		itemStack.stackTagCompound.setIntArray("startCoords", new int[3]);
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(Game.isNotHost(world))
			return false;
		
		Block b = world.getBlock(x, y, z);
		
		//We can only place wires if it's under some sort of a post
		if(side == ForgeDirection.DOWN.ordinal() && (b instanceof BlockPostBase ||
				(b.isAssociatedBlock(Blocks.dirt) && world.getBlock(x, ++y, z) instanceof BlockPostBase))) {	//TODO: temp
			y--;			
			
			//If we don't have a starting location, set it now
			if(!stack.stackTagCompound.getBoolean("started")) {
				stack.stackTagCompound.setBoolean("started", true);
				stack.stackTagCompound.setIntArray("startCoords", new int[] {x, y, z});
				return true;
			}
			
			//If the current location is the same as the starting location, or the starting location doesn't have a post above it, clear it
			int[] placeFrom = stack.stackTagCompound.getIntArray("startCoords");
			if((placeFrom[0] == x && placeFrom[1] == y && placeFrom[2] == z) ||
					!(world.getBlock(placeFrom[0], placeFrom[1]+1, placeFrom[2]) instanceof BlockPostBase)) {
				stack.stackTagCompound.setBoolean("started", false);
				return true;
			}
			
			/*
			 * Make sure that a max of 2 axis are different, none of the axis goes over by a specified amount of blocks (16 for now),
			 * and that we have enough wires
			 */
			int nx = x - placeFrom[0], ny = y - placeFrom[1], nz = z - placeFrom[2];
			int anx = Math.abs(nx), any = Math.abs(ny), anz = Math.abs(nz);
			if((nx != 0 && ny != 0 && nz != 0) || anx > MAX_RANGE || any > MAX_RANGE || anz > MAX_RANGE ||
					anx + any + anz > stack.stackTagCompound.getInteger("wires")) {
				stack.stackTagCompound.setBoolean("started", false);
				return true;
			}
			
			//Determine the placement strategy (i.e. how the catenary wires are going to be placed from the starting position)
			if((nx == 0 && ny == 0) || (ny == 0 && nz == 0) || (nx == 0 && any == anz) || (ny == 0 && anx == anz) || (nz == 0 && any == anz)) { }
			else {	//The strategy is invalid
				stack.stackTagCompound.setBoolean("started", false);
				return true;
			}
			
			//Compose an array of coordinates where the blocked are placed in the order x, z, y
			//If the block is not empty, reject the entire array
			int iterations = Math.max(Math.max(anx, any), anz);
			ArrayList<int[]> blox = new ArrayList<int[]>();
			
			if(!world.isAirBlock(placeFrom[0], placeFrom[1], placeFrom[2])) {
				stack.stackTagCompound.setBoolean("started", false);
				return true;
			}
			blox.add(placeFrom.clone());
			
			for(int i = 0; i < iterations; i++) {
				if(nx != 0) {
					placeFrom[0] += (nx > 0) ? 1 : -1;
					b = world.getBlock(placeFrom[0], placeFrom[1], placeFrom[2]);
					if(b.isAssociatedBlock(Blocks.air) || !(b.isAssociatedBlock(Blocks.dirt))) {
						stack.stackTagCompound.setBoolean("started", false);
						return true;
					}
					blox.add(placeFrom.clone());
				}
				if(nz != 0) {
					placeFrom[2] += (nz > 0) ? 1 : -1;
					b = world.getBlock(placeFrom[0], placeFrom[1], placeFrom[2]);
					if(b.isAssociatedBlock(Blocks.air) || !(b.isAssociatedBlock(Blocks.dirt))) {
						stack.stackTagCompound.setBoolean("started", false);
						return true;
					}
					blox.add(placeFrom.clone());
				}
				if(ny != 0) {
					placeFrom[1] += (ny > 0) ? 1 : -1;
					b = world.getBlock(placeFrom[0], placeFrom[1], placeFrom[2]);
					if(b.isAssociatedBlock(Blocks.air) || !(b.isAssociatedBlock(Blocks.dirt))) {
						stack.stackTagCompound.setBoolean("started", false);
						return true;
					}
					blox.add(placeFrom.clone());
				}
			}
			
			//Finally, actually place the blocks in the coordinates given
			for(int[] i : blox) {
				world.setBlock(i[0], i[1], i[2], Blocks.dirt);	//TODO: temp
			}
			
			stack.stackTagCompound.setBoolean("started", false);
			return true;
		}
		
		
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if(Game.isNotHost(world) || !player.isSneaking())
			return itemStack;
		
		//If the player is crouching, put any wires they have into the spool
		InventoryPlayer inv = player.inventory;
		if(itemStack.stackTagCompound == null)
			onCreated(itemStack, world, player);
		int totalWires = itemStack.stackTagCompound.getInteger("wires");
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack is = inv.getStackInSlot(i);
			if(is != null && is.getItem().equals(Item.getItemFromBlock(BlockCatenary.getBlock()))) {
				int n = Math.min(is.stackSize, MAX_WIRES - totalWires);
				totalWires += n;
				if(is.stackSize == n)
					inv.setInventorySlotContents(i, null);
				else
					is.stackSize -= n;
			}
		}

		ItemStack newIS = itemStack.copy();
		newIS.stackTagCompound.setInteger("wires", totalWires);
		return newIS;
	}
	
	
	
}
