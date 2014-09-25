/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mods.railcraft.common.plugins.buildcraft.actions;

import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.IAction;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Actions implements IAction {

    PAUSE(400, "pause"),
    SEND_CART(401, "sendcart");
    public static final Actions[] VALUES = values();
    private final int id;
    private final String tag;
    private IIcon icon;

    private Actions(int id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public static void init() {
        for (Actions action : VALUES) {
            ActionManager.registerAction(action);
        }
    }

    @Override
    public String getUniqueTag() {
        return "railcraft." + tag;
    }

    @Override
    public final IIcon getIcon() {
        return icon;
    }

    @Override
    public boolean hasParameter() {
        return false;
    }

    @Override
    public String getDescription() {
        return LocalizationPlugin.translate("gates.action." + tag);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon("railcraft:buildcraft.gate.action." + tag);
    }

    @Override
    public IAction rotateLeft() {
        return this;
    }

}
