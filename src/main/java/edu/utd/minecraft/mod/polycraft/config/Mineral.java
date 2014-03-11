package edu.utd.minecraft.mod.polycraft.config;

import java.util.HashMap;
import java.util.Map;

public class Mineral extends Entity {

	public static final Map<String, Mineral> minerals = new HashMap<String, Mineral>();

	private static final Mineral registerMineral(final Mineral mineral) {
		minerals.put(mineral.gameName, mineral);
		return mineral;
	}

	static {
		registerMineral(new Mineral("Bauxite"));
	}

	public Mineral(final String name) {
		super("mineral_" + name.toLowerCase(), name);
	}
}
