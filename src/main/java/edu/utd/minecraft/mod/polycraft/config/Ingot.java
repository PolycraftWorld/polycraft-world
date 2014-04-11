package edu.utd.minecraft.mod.polycraft.config;

public class Ingot extends Entity {
	public static final EntityRegistry<Ingot> registry = new EntityRegistry<Ingot>();

	public final Entity type;

	public Ingot(final Entity type) {
		super(type.name);
		this.type = type;
	}
}
