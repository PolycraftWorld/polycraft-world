package edu.utd.minecraft.mod.polycraft.config;

public class Compound extends Entity {

	public static final EntityRegistry<Compound> registry = new EntityRegistry<Compound>();

	public final boolean fluid;
	public final String formula;
	public final String uses;
	public final String sources;

	public Compound(final String name, final boolean fluid) {
		this(name, fluid, "", "", "");
	}

	public Compound(final String name, final boolean fluid, final String formula, final String uses, final String sources) {
		super(name);
		this.fluid = fluid;
		this.formula = formula;
		this.uses = uses;
		this.sources = sources;
	}
}