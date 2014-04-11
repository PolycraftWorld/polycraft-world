package edu.utd.minecraft.mod.polycraft.config;

public class Catalyst extends Compound {

	public static final EntityRegistry<Catalyst> registry = new EntityRegistry<Catalyst>();

	public Catalyst(final String name, final String formula) {
		super(name, false, formula, "", "");
	}
}