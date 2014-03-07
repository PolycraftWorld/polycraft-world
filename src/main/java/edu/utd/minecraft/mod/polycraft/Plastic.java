package edu.utd.minecraft.mod.polycraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Plastic
{
	public static final Collection<Plastic> plastics = new ArrayList<Plastic>();
	static
	{
		plastics.add(new Plastic(1, "PET", "Polyethylene terephthalate", .1, 64));
		plastics.add(new Plastic(2, "PE-HD", "High-density polyethylene", 1, 32));
		plastics.add(new Plastic(3, "PVC", "Polyvinyl chloride", .2, 16));
		plastics.add(new Plastic(4, "PELD", "Low-density polyethylene", .05, 8));
		plastics.add(new Plastic(5, "PP", "Polypropylene", .4, 4));
		plastics.add(new Plastic(6, "PS", "Polystyrene", .3, 2));
		plastics.add(new Plastic(7, "O", "Other polycarbonate", 2, 1));
	}

	public final String gameName;
	public final int type;
	public final String abbrevation;
	public final String name;
	public final double itemDurabilityBonus;
	public final int pelletsPerBlock;
	
	public Plastic(final int type, final String abbrevation, final String name, final double itemDurabilityBonus, final int pelletsPerBlock)
	{
		this.gameName = "plastic" + type;
		this.type = type;
		this.abbrevation = abbrevation;
		this.name = name;
		this.itemDurabilityBonus = itemDurabilityBonus;
		this.pelletsPerBlock = pelletsPerBlock;
	}
}
