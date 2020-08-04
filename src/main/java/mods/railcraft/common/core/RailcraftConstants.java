/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.core;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class RailcraftConstants {

    public static final String RESOURCE_DOMAIN = Railcraft.MOD_ID;
    public static final String SEPERATOR = "_";
    public static final String SOUND_FOLDER = RESOURCE_DOMAIN + ":";
    public static final String TEXTURE_FOLDER = RESOURCE_DOMAIN + ":textures/";
    public static final String ENTITY_TEXTURE_FOLDER = TEXTURE_FOLDER + "entities/";
    public static final String TESR_TEXTURE_FOLDER = TEXTURE_FOLDER + "tesr/";
    public static final String CART_TEXTURE_FOLDER = ENTITY_TEXTURE_FOLDER + "carts/";
    public static final String LOCOMOTIVE_TEXTURE_FOLDER = ENTITY_TEXTURE_FOLDER + "locomotives/";
    public static final String GUI_TEXTURE_FOLDER = TEXTURE_FOLDER + "gui/";
    public static final String ARMOR_TEXTURE_FOLDER = ENTITY_TEXTURE_FOLDER + "armor/";
    public static final String TRIGGER_TEXTURE_FILE = TEXTURE_FOLDER + "triggers.png";
    public static final String CONFIG_FILE_NAME = "railcraft.cfg";
    public static final String RAILCRAFT_PLAYER_NBT_TAG = "railcraftData";
    public static final String EMBLEM_URL = "https://dl.dropboxusercontent.com/u/38558957/Minecraft/Railcraft/Emblems/";
    public static final long TICKS_PER_HOUR = 72000;
    public static final long TICKS_PER_MIN = 1200;
    public static final long TICKS_PER_SECOND = 20;

    public static final int BOOK_LINE_LENGTH = 37;
    public static final int BOOK_LINES_PER_PAGE = 13;
    public static final int BOOK_MAX_PAGES = 50;

    /**
     * 4 RF = 1 EU
     */
    public static final double FE_EU_RATIO = 0.25;

    private RailcraftConstants() {
    }
}
