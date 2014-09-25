package thaumcraft.api.visnet;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.world.World;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import cpw.mods.fml.common.FMLLog;

public class VisNetHandler {

	// / NODE DRAINING
	/**
	 * This method drains vis from a relay or source near the passed in
	 * location. The amount received can be less than the amount requested so
	 * take that into account.
	 * 
	 * @param world
	 * @param x the x position of the draining block or entity
	 * @param y the y position of the draining block or entity
	 * @param z the z position of the draining block or entity
	 * @param aspect what aspect to drain
	 * @param vis how much to drain
	 * @return how much was actually drained
	 */
	public static int drainVis(World world, int x, int y, int z, Aspect aspect, int amount) {

		int drainedAmount = 0;

		WorldCoordinates drainer = new WorldCoordinates(x, y, z,
				world.provider.dimensionId);
		if (!nearbyNodes.containsKey(drainer)) {
			calculateNearbyNodes(world, x, y, z);
		}

		ArrayList<WeakReference<TileVisNode>> nodes = nearbyNodes.get(drainer);
		if (nodes!=null && nodes.size()>0)
		for (WeakReference<TileVisNode> noderef : nodes) {
			
			TileVisNode node = noderef.get();
			
			if (node == null) continue;

			int a = node.consumeVis(aspect, amount);
			drainedAmount += a;
			amount -= a;
			if (a>0) {
				int color = Aspect.getPrimalAspects().indexOf(aspect);
				generateVisEffect(world.provider.dimensionId, x, y, z, node.xCoord, node.yCoord, node.zCoord, color);				
			}
			if (amount <= 0) {
				break;
			}
		}
		
		return drainedAmount;
	}
	
	static Method generateVisEffect;
	public static void generateVisEffect(int dim, int x, int y, int z, int x2, int y2, int z2, int color) {
		try {
	        if(generateVisEffect == null) {
	            Class fake = Class.forName("thaumcraft.common.lib.Utils");
	            generateVisEffect = fake.getMethod("generateVisEffect", int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class);
	        }
	        generateVisEffect.invoke(null, dim, x,y,z,x2,y2,z2,color);
	    } catch(Exception ex) { 
	    	FMLLog.warning("[Thaumcraft API] Could not invoke thaumcraft.common.lib.Utils method generateVisEffect");
	    }
	}

	public static HashMap<Integer, HashMap<WorldCoordinates, WeakReference<TileVisNode>>> sources = new HashMap<Integer, HashMap<WorldCoordinates, WeakReference<TileVisNode>>>();

	public static void addSource(World world, TileVisNode vs) {
		HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist = sources
				.get(world.provider.dimensionId);
		if (sourcelist == null) {
			sourcelist = new HashMap<WorldCoordinates, WeakReference<TileVisNode>>();
		}
		sourcelist.put(vs.getLocation(), new WeakReference(vs));
		sources.put(world.provider.dimensionId, sourcelist);
		nearbyNodes.clear();
	}

	public static boolean isNodeValid(WeakReference<TileVisNode> node) {
		if (node == null || node.get() == null || node.get().isInvalid())
			return false;
		return true;
	}

	public static WeakReference<TileVisNode> addNode(World world, TileVisNode vn) {
		WeakReference ref = new WeakReference(vn);

		HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist = sources
				.get(world.provider.dimensionId);
		if (sourcelist == null) {
			sourcelist = new HashMap<WorldCoordinates, WeakReference<TileVisNode>>();
			return null;
		}

		ArrayList<Object[]> nearby = new ArrayList<Object[]>();

		for (WeakReference<TileVisNode> root : sourcelist.values()) {
			if (!isNodeValid(root))
				continue;

			TileVisNode source = root.get();

			float r = inRange(world, vn.getLocation(), source.getLocation(),
					vn.getRange());
			if (r > 0) {
				nearby.add(new Object[] { source, r - vn.getRange() * 2 });
			}
			nearby = findClosestNodes(vn, source, nearby);
		}

		float dist = Float.MAX_VALUE;
		TileVisNode closest = null;
		if (nearby.size() > 0) {
			for (Object[] o : nearby) {
				if ((Float) o[1] < dist) {// && canNodeBeSeen(vn,(TileVisNode)
											// o[0])) {
					dist = (Float) o[1];
					closest = (TileVisNode) o[0];
				}
			}
		}
		if (closest != null) {
			closest.getChildren().add(ref);
			nearbyNodes.clear();
			return new WeakReference(closest);
		}

		return null;
	}

	public static ArrayList<Object[]> findClosestNodes(TileVisNode target,
			TileVisNode root, ArrayList<Object[]> in) {
		TileVisNode closestChild = null;

		for (WeakReference<TileVisNode> child : root.getChildren()) {
			TileVisNode n = child.get();

			if (n != null
					&& !n.equals(target)
					&& !n.equals(root)
					&& (target.getAttunement() == -1 || n.getAttunement() == -1 || n
							.getAttunement() == target.getAttunement())) {

				float r2 = inRange(n.getWorldObj(), n.getLocation(),
						target.getLocation(), target.getRange());
				if (r2 > 0) {
					in.add(new Object[] { n, r2 });
				}

				in = findClosestNodes(target, n, in);
			}
		}
		return in;
	}

	private static float inRange(World world, WorldCoordinates cc1,
			WorldCoordinates cc2, int range) {
		float distance = cc1.getDistanceSquaredToWorldCoordinates(cc2);
		return distance > range * range ? -1 : distance;
	}

	private static HashMap<WorldCoordinates, ArrayList<WeakReference<TileVisNode>>> nearbyNodes = new HashMap<WorldCoordinates, ArrayList<WeakReference<TileVisNode>>>();

	private static void calculateNearbyNodes(World world, int x, int y, int z) {

		HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist = sources
				.get(world.provider.dimensionId);
		if (sourcelist == null) {
			sourcelist = new HashMap<WorldCoordinates, WeakReference<TileVisNode>>();
			return;
		}

		ArrayList<WeakReference<TileVisNode>> cn = new ArrayList<WeakReference<TileVisNode>>();
		WorldCoordinates drainer = new WorldCoordinates(x, y, z,
				world.provider.dimensionId);

		ArrayList<Object[]> nearby = new ArrayList<Object[]>();

		for (WeakReference<TileVisNode> root : sourcelist.values()) {
			
			if (!isNodeValid(root))
				continue;

			TileVisNode source = root.get();
			
			TileVisNode closest = null;
			float range = Float.MAX_VALUE;

			float r = inRange(world, drainer, source.getLocation(),
					source.getRange());
			if (r > 0) {
				range = r;
				closest = source;
			}
			
			ArrayList<WeakReference<TileVisNode>> children = new ArrayList<WeakReference<TileVisNode>>();
			children = getAllChildren(source,children);
			
			for (WeakReference<TileVisNode> child : children) {
				TileVisNode n = child.get();
				if (n != null && !n.equals(root)) {
					
					float r2 = inRange(n.getWorldObj(), n.getLocation(),
							drainer, n.getRange());
					if (r2 > 0 && r2 < range) {
						range = r2;
						closest = n;
					}
				}
			}

			if (closest != null) {
				
				cn.add(new WeakReference(closest));
			}
		}

		nearbyNodes.put(drainer, cn);
	}
	
	private static ArrayList<WeakReference<TileVisNode>> getAllChildren(TileVisNode source, ArrayList<WeakReference<TileVisNode>> list) {
		for (WeakReference<TileVisNode> child : source.getChildren()) {
			TileVisNode n = child.get();
			if (n != null) {
				list.add(child);
				list = getAllChildren(n,list);
			}
		}
		return list;
	}

	// public static boolean canNodeBeSeen(TileVisNode source,TileVisNode
	// target)
	// {
	// double d = Math.sqrt(source.getDistanceFrom(target.xCoord, target.yCoord,
	// target.zCoord));
	// double xd = (source.xCoord-target.xCoord) / d;
	// double yd = (source.yCoord-target.yCoord) / d;
	// double zd = (source.zCoord-target.zCoord) / d;
	// return source.getWorldObj().rayTraceBlocks(
	// Vec3.createVectorHelper(source.xCoord-xd+.5+.5, source.yCoord-yd,
	// source.zCoord-zd),
	// Vec3.createVectorHelper(target.xCoord+.5, target.yCoord+.5,
	// target.zCoord+.5)) == null;
	// }

	// public static HashMap<WorldCoordinates,WeakReference<TileVisNode>>
	// noderef = new HashMap<WorldCoordinates,WeakReference<TileVisNode>>();
	//
	// public static TileVisNode getClosestNodeWithinRadius(World world, int x,
	// int y, int z, int radius) {
	// TileVisNode out = null;
	// WorldCoordinates wc = null;
	// float cd = Float.MAX_VALUE;
	// for (int sx = x - radius; sx <= x + radius; sx++) {
	// for (int sy = y - radius; sy <= y + radius; sy++) {
	// for (int sz = z - radius; sz <= z + radius; sz++) {
	// wc = new WorldCoordinates(sx,sy,sz,world.provider.dimensionId);
	// if (noderef.containsKey(wc)) {
	// float d = wc.getDistanceSquared(x, y, z);
	// if (d<radius*radius && noderef.get(wc).get()!=null &&
	// !noderef.get(wc).get().isReceiver() &&
	// isNodeValid(noderef.get(wc).get().getParent())
	// ) {
	// out = noderef.get(wc).get();
	// cd = d;
	// }
	// }
	// }
	// }
	// }
	// return out;
	// }

}
