package mods.railcraft.common.plugins.buildcraft.actions;

import buildcraft.api.statements.IActionExternal;
import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.IActionProvider;
import buildcraft.api.statements.IStatementContainer;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasCart;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import net.minecraft.util.EnumFacing;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ActionProvider implements IActionProvider {

    public ActionProvider() {
        Actions.init();
    }

    @Override
    public Collection<IActionInternal> getInternalActions(IStatementContainer isc) {
        return null;
    }

    @Override
    public Collection<IActionExternal> getExternalActions(EnumFacing side, net.minecraft.tileentity.TileEntity tile) {
        Collection<IActionExternal> actions = new LinkedList<IActionExternal>();
        if (tile instanceof IHasWork)
            actions.add(Actions.PAUSE);
        if (tile instanceof IHasCart)
            actions.add(Actions.SEND_CART);
        return actions;
    }

}
