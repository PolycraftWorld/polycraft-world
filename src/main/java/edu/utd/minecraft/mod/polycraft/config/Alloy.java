package edu.utd.minecraft.mod.polycraft.config;

public class Alloy extends Entity {

	public static final EntityRegistry<Alloy> registry = new EntityRegistry<Alloy>();

	public static final Alloy steel = registry.register(new Alloy("Steel"));

	public Alloy(final String name) {
		super(name);
	}

	public static String generate(final String[] entity) {
		final String[] params = new String[] { "\"" + entity[0] + "\"" };
		return Entity.generate(Alloy.class.getSimpleName(), getVariableName(entity[0]), params);
	}
}