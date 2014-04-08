package edu.utd.minecraft.mod.polycraft.config;

public class Mineral extends Entity {

	public static final EntityRegistry<Mineral> registry = new EntityRegistry<Mineral>();

	public static final Mineral graphite = registry.register(new Mineral("Graphite"));
	public static final Mineral bauxite = registry.register(new Mineral("Bauxite"));
	public static final Mineral tarSand = registry.register(new Mineral("Tar Sand"));
	public static final Mineral shale = registry.register(new Mineral("Shale"));

	public Mineral(final String name) {
		super(name);
	}

	public static String generate(final String[] entity) {
		final String[] params = new String[] { "\"" + entity[0] + "\"" };
		return Entity.generate(Mineral.class.getSimpleName(), getVariableName(entity[0]), params);
	}
}
