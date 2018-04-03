package thaumcraft.api.casters;

import java.util.HashMap;

import thaumcraft.api.casters.IFocusPart.EnumFocusPartType;

public class FocusHelper {
	
	// mediums
	public static IFocusPartMedium TOUCH;
	public static IFocusPartMedium BOLT;
	public static IFocusPartMedium PROJECTILE;
	
	// effects
	public static IFocusPartEffect FIRE;
	public static IFocusPartEffect FROST;
	public static IFocusPartEffect MAGIC;
	public static IFocusPartEffect CURSE;
	public static IFocusPartEffect BREAK;
	public static IFocusPartEffect RIFT;
	public static IFocusPartEffect EXCHANGE;
	
	// modifiers
	public static IFocusPart FRUGAL;
	public static IFocusPart POTENCY;
	public static IFocusPart LINGERING;
	public static IFocusPart SCATTER;
	public static IFocusPart CHAIN;
	public static IFocusPart SILKTOUCH;
	public static IFocusPart FORTUNE;
	public static IFocusPart CHARGE;
	
	public static HashMap<String,IFocusPart> focusParts = new HashMap<>();
	public static HashMap<String,IFocusPart[]> focusPartsConnections = new HashMap<>();
	
	
	/**
	 * Registers a focus part for use	
	 * @param part the part to register
	 * @param connections what other parts this part can connect to. By default all 'effects' & 'mediums' can connect so this does not need to be listed
	 * @return
	 */
	public static boolean registerFocusPart(IFocusPart part, IFocusPart ... connections) {
		if (focusParts.containsKey(part.getKey())) return false;
		focusParts.put(part.getKey(), part);
		if (connections!=null) focusPartsConnections.put(part.getKey(), connections);
		return true;
	}
	
	public static IFocusPart getFocusPart(String key) {
		return focusParts.get(key);
	}
	
	public static boolean canPartsConnect(IFocusPart part1,IFocusPart part2) {
		if (part1==null || part2==null) return false;
		if (part1.getType()==part2.getType()) return false;
		
		if (!part1.canConnectTo(part2) || !part2.canConnectTo(part1)) return false;
		
		if (part1.getType()==EnumFocusPartType.MEDIUM && part2.getType()==EnumFocusPartType.EFFECT ||
			part2.getType()==EnumFocusPartType.MEDIUM && part1.getType()==EnumFocusPartType.EFFECT) return true;
				
		IFocusPart[] conns = focusPartsConnections.get(part1.getKey());
		if (conns!=null)
			for (IFocusPart pc:conns) 
				if (pc==part2) return true; 
		conns = focusPartsConnections.get(part2.getKey());
		if (conns!=null)
			for (IFocusPart pc:conns) 
				if (pc==part1) return true;
		return false;
	}
}
