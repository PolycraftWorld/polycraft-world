package edu.utd.minecraft.mod.polycraft.config;

public class Alloy extends Entity {

	public static final EntityRegistry<Alloy> registry = new EntityRegistry<Alloy>();

	public static final Alloy steel = registry.register(new Alloy("Steel"));

	public Alloy(final String name) {
		super("alloy_" + getSafeName(name), name);
	}
}