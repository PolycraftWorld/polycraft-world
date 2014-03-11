package edu.utd.minecraft.mod.polycraft.config;

import java.util.HashMap;
import java.util.Map;

public class Plastic extends Entity {

	public static final Map<String, Plastic> plastics = new HashMap<String, Plastic>();

	private static final Plastic registerPlastic(final Plastic plastic) {
		plastics.put(plastic.gameName, plastic);
		return plastic;
	}

	static {
		registerPlastic(new Plastic(1, "Polyethylene terephthalate", "PET", .1, 64));
		registerPlastic(new Plastic(2, "High-density polyethylene", "PE-HD", 1, 32));
		registerPlastic(new Plastic(3, "Polyvinyl chloride", "PVC", .2, 16));
		registerPlastic(new Plastic(4, "Low-density polyethylene", "PELD", .05, 8));
		registerPlastic(new Plastic(5, "Polypropylene", "PP", .4, 4));
		registerPlastic(new Plastic(6, "Polystyrene", "PS", .3, 2));
		registerPlastic(new Plastic(7, "Other polycarbonate", "O", 2, 1));
	}

	public final String itemNamePellet;
	public final String itemNameGrip;
	public final int type;
	public final String abbreviation;
	public final double itemDurabilityBonus;
	public final int craftingPelletsPerBlock;

	public Plastic(final int type, final String name, final String abbrevation, final double itemDurabilityBonus, final int craftingPelletsPerBlock) {
		super("plastic" + type, name);
		this.itemNamePellet = gameName + "_pellet";
		this.itemNameGrip = gameName + "_grip";
		this.type = type;
		this.abbreviation = abbrevation;
		this.itemDurabilityBonus = itemDurabilityBonus;
		this.craftingPelletsPerBlock = craftingPelletsPerBlock;
	}
}
