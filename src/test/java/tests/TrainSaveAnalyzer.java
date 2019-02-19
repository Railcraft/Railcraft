package tests;

import mods.railcraft.common.plugins.forge.NBTPlugin;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TrainSaveAnalyzer {

    private static final String FILE = "C:\\mc\\one" + "\\railcraft.trains.dat";
    private static final Logger LOGGER = LogManager.getLogger("train save analyzer");

    public static void main(String... args) throws IOException {
        File file = new File(FILE);
        Set<UUID> seen = new HashSet<>();
        LOGGER.info("Starting");
        NBTTagCompound root = CompressedStreamTools.readCompressed(new BufferedInputStream(new FileInputStream(file)));
        NBTTagList list = root.getCompoundTag("data").getTagList("trains", Constants.NBT.TAG_COMPOUND);
        for (NBTTagCompound tag : NBTPlugin.<NBTTagCompound>asList(list)) {
            List<UUID> carts = NBTPlugin.getNBTList(tag, "carts", NBTTagCompound.class).stream().map(NBTUtil::getUUIDFromTag).collect(Collectors.toList());
            for (UUID cart : carts) {
                if (seen.contains(cart)) {
                    LOGGER.error("Found duplicate minecart of uuid {}", cart);
                } else {
                    seen.add(cart);
                    LOGGER.trace("Added cart of uuid {}", cart);
                }
            }
        }
        LOGGER.info("Finished");
    }
}
