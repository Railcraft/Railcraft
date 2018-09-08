package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.api.tracks.IItemTrack;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.BlockTrackStateless;
import mods.railcraft.common.blocks.tracks.ItemTrack;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemTrackStateless extends ItemTrack implements IItemTrack {

    private final BlockTrackStateless block;

    public ItemTrackStateless(Block block) {
        super(block);
        this.block = (BlockTrackStateless) block;
    }

    @Override
    public TrackType getTrackType(ItemStack stack) {
        return block.getTrackType();
    }
}
