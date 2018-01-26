package thaumcraft.api.casters;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.casters.IFocusPartMedium.EnumFocusCastMethod;

public class FocusCore {

	public IFocusPartMedium medium;
	public IFocusPart[] mediumModifiers;
	public FocusEffect[] effects;	
	public float cost;	
	public LinkedHashMap<String,IFocusPart> partsRaw;
	
	public FocusCore(IFocusPartMedium medium, IFocusPart[] mediumModifiers, FocusEffect[] effects) {
		this.medium = medium;
		this.mediumModifiers = mediumModifiers;
		this.effects = effects;
	}
	
	public FocusCore() {
		medium = FocusHelper.TOUCH;
		FocusEffect fe = new FocusEffect();
		fe.effect = FocusHelper.FIRE;
		fe.costMultipler = FocusHelper.TOUCH.getCostMultiplier();
		fe.effectMultipler = FocusHelper.TOUCH.getEffectMultiplier();
		effects = new FocusEffect[] {fe};
		generate();
	}
	
	public EnumFocusCastMethod getFinalCastMethod() {
		if (mediumModifiers!=null)
			for (IFocusPart part:mediumModifiers) {
				if (part==FocusHelper.CHARGE) return EnumFocusCastMethod.CHARGE;
			}
		return this.medium.getCastMethod();
	}
	
	public int getFinalChargeTime() {
		if (mediumModifiers!=null)
			for (IFocusPart part:mediumModifiers) {
				if (part==FocusHelper.CHARGE) {
					return this.medium.getChargeTime() * 10;
				}
			}
		return this.medium.getChargeTime();
	}
	
	public void generate() {
		this.partsRaw = new LinkedHashMap<>();
		if (medium==null) return;
		partsRaw.put(medium.getKey(), medium);
		cost=0;
		if (mediumModifiers!=null)
		for (IFocusPart p:mediumModifiers) {
			partsRaw.put(p.getKey(), p);
		}		
		for (FocusEffect fe:effects) {
			partsRaw.put(fe.effect.getKey(), fe.effect);
			float cost2 = fe.effect.getBaseCost();	
			cost2 *= fe.costMultipler;			
			if (fe.modifiers!=null)
			for (IFocusPart p:fe.modifiers) {
				partsRaw.put(p.getKey(), p);
			}			
			cost += cost2;
		}			
	}

	public NBTTagCompound serialize() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("medium", medium.getKey());
		
		if (mediumModifiers!=null && mediumModifiers.length>0) {
			String s="";
			for (IFocusPart p:mediumModifiers) {
				s+="~"+p.getKey();
			}
			s=s.replaceFirst("~", "");
			nbt.setString("mediumMods", s);
		}				
		
		NBTTagList efflist = new NBTTagList();
		for (FocusEffect fe:effects) {
			NBTTagCompound gt = new NBTTagCompound();
			gt.setString("effect", fe.effect.getKey());
			gt.setFloat("costMod", fe.costMultipler);
			gt.setFloat("effMod", fe.effectMultipler);
			if (fe.modifiers!=null && fe.modifiers.length>0) {
				String s="";
				for (IFocusPart p:fe.modifiers) {
					s+="~"+p.getKey();
				}
				s=s.replaceFirst("~", "");
				gt.setString("mods", s);
			}
			efflist.appendTag(gt);
		}				
		nbt.setTag("effects", efflist);		
		return nbt;
	}
	
	public void deserialize(NBTTagCompound nbt) {	
		
		IFocusPart mp = FocusHelper.getFocusPart(nbt.getString("medium"));
		if (mp==null) return;
		
		this.medium = (IFocusPartMedium) mp;
		String s = nbt.getString("mediumMods");
		String[] ss = s.split("~");
		if (ss.length>0) {			
			ArrayList<IFocusPart> li = new ArrayList<>();
			for (int a=0;a<ss.length;a++) {
				IFocusPart p=FocusHelper.getFocusPart(ss[a]);
				if (p!=null) {
					li.add(p);
				}
			}			
			this.mediumModifiers = li.toArray(new IFocusPart[li.size()]);
		}
		
		NBTTagList efflist = nbt.getTagList("effects", (byte)10);	
		ArrayList<FocusEffect> fes = new ArrayList<>();
		for (int x=0;x<efflist.tagCount();x++) {
			NBTTagCompound nbtdata = (NBTTagCompound) efflist.getCompoundTagAt(x);
			FocusEffect fe = new FocusEffect();
			fe.effect=(IFocusPartEffect) FocusHelper.getFocusPart(nbtdata.getString("effect"));
			fe.costMultipler =  nbtdata.getFloat("costMod");
			fe.effectMultipler = nbtdata.getFloat("effMod"); 
			String mods = nbtdata.getString("mods");
			if (!mods.isEmpty()) {
				String[] modlist=mods.split("~");
				ArrayList<IFocusPart> li = new ArrayList<>();
				for (int a=0;a<modlist.length;a++) {
					IFocusPart p=FocusHelper.getFocusPart(modlist[a]);
					if (p!=null) {
						li.add((IFocusPart) p);
					}
				}			
				fe.modifiers = li.toArray(new IFocusPart[li.size()]);
			}
			fes.add(fe);
		}
		this.effects = fes.toArray(new FocusEffect[fes.size()]);
		this.generate();
	}
		
	public String getSortingHelper() {
		String s = medium.getKey();
		for (FocusEffect ef:effects) s += ef.effect.getKey();
		return s;
	}
	
	public static class FocusEffect {
		public IFocusPartEffect effect;
		public IFocusPart[] modifiers;
		public float effectMultipler;
		public float costMultipler;
		public FocusEffect() {}
		
	}
}
