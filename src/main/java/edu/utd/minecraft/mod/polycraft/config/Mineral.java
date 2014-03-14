package edu.utd.minecraft.mod.polycraft.config;

public class Mineral extends Entity {

	public static final EntityRegistry<Mineral> registry = new EntityRegistry<Mineral>();

	static {
		registry.register(new Mineral("Bauxite"));
		registry.register(new Mineral("Tar Sand"));
		registry.register(new Mineral("Shale"));
	}

	public Mineral(final String name) {
		super("mineral_" + name.toLowerCase().replaceAll(" ", "_"), name);
	}
}
