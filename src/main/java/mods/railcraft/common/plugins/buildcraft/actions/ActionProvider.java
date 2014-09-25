package mods.railcraft.common.plugins.buildcraft.actions;

import buildcraft.api.gates.IAction;
import buildcraft.api.gates.IActionProvider;
import java.util.LinkedList;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import mods.railcraft.common.plugins.buildcraft.triggers.*;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ActionProvider implements IActionProvider {

    public ActionProvider() {
        Actions.init();
    }

    @Override
    public LinkedList<IAction> getNeighborActions(Block block, TileEntity tile) {
        LinkedList<IAction> actions = new LinkedList<IAction>();
        if (tile instanceof IHasWork) {
            actions.add(Actions.PAUSE);
        }
        if (tile instanceof IHasCart) {
            actions.add(Actions.SEND_CART);
        }
        return actions;
    }
}
