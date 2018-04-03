/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mods.railcraft.common.plugins.buildcraft.actions;

import buildcraft.api.core.render.ISprite;
import buildcraft.api.statements.*;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Actions implements IActionExternal {

    PAUSE("pause"),
    SEND_CART("sendcart");
    public static final Actions[] VALUES = values();
    private final String tag;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite sprite;

    Actions(String tag) {
        this.tag = tag;
    }

    public static void init() {
        for (Actions action : VALUES) {
            StatementManager.registerStatement(action);
            StatementManager.statements.put("railcraft." + action.tag, action);
        }
    }

    @Override
    public String getUniqueTag() {
        return "railcraft:" + tag;
    }

    @Override
    public String getDescription() {
        return LocalizationPlugin.translate("gates.action." + tag);
    }

    @Nullable
    @Override
    public ISprite getSprite() {
        //TODO
        return null;
    }

    @Override
    public IActionExternal rotateLeft() {
        return this;
    }

    @Override
    public IStatement[] getPossible() {
        return new IStatement[] {this};
    }

    @Override
    public void actionActivate(TileEntity tile, EnumFacing side, IStatementContainer isc, IStatementParameter[] isps) {
        if (tile instanceof IActionReceptor)
            ((IActionReceptor) tile).actionActivated(this);
    }

    @Override
    public int maxParameters() {
        return 0;
    }

    @Override
    public int minParameters() {
        return 0;
    }

    @Override
    public IStatementParameter createParameter(int i) {
        return null;
    }

}
