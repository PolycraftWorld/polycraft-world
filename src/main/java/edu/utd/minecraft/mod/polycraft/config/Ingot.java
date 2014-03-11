package edu.utd.minecraft.mod.polycraft.config;

import java.util.HashMap;
import java.util.Map;

public class Ingot extends Entity {

	public static final Map<String, Ingot> ingots = new HashMap<String, Ingot>();

	private static final Ingot registerIngot(final Ingot ingot) {
		ingots.put(ingot.gameName, ingot);
		return ingot;
	}

	static {
		for (Element element : Element.elements.values())
			registerIngot(new Ingot(element));
	}

	public Entity type;

	public Ingot(Entity type) {
		super("ingot_" + type.gameName, type.name);
		this.type = type;
	}
}
