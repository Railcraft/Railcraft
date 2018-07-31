package thaumcraft.api.golems.seals;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISealEntity {

	void tickSealEntity(World world);

	ISeal getSeal();

	SealPos getSealPos();

	byte getPriority();

	void setPriority(byte priority);

	void readNBT(NBTTagCompound nbt);

	NBTTagCompound writeNBT();

	void syncToClient(World world);

	BlockPos getArea();

	void setArea(BlockPos v);

	boolean isLocked();

	void setLocked(boolean locked);
	
	boolean isRedstoneSensitive();

	void setRedstoneSensitive(boolean redstone);

	String getOwner();

	void setOwner(String owner);
	
	byte getColor();

	void setColor(byte color);

	boolean isStoppedByRedstone(World world);

}