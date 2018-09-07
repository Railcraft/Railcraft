package mods.railcraft.api.carts;

/**
 *
 */
public final class CartsApiAccess {

    public static void setLinkageManager(ILinkageManager instance) {
        CartToolsAPI.linkageManager = instance;
    }

    private CartsApiAccess() {
    }
}
