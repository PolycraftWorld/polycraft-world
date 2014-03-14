package edu.utd.minecraft.mod.polycraft.config;

public class Compound extends Entity {

	public static final EntityRegistry<Compound> registry = new EntityRegistry<Compound>();

	static {
		registry.register(new Compound("Bitumen", false));
		registry.register(new Compound("Natural Gas", true));
		registry.register(new Compound("Ethane", true));
		registry.register(new Compound("Ethylene", true));
		registry.register(new Compound("Propane", true));
		registry.register(new Compound("Propylene", true));
		registry.register(new Compound("Butane", true));
		registry.register(new Compound("Butadiene", true));
		registry.register(new Compound("Methane", true));
		registry.register(new Compound("Naphtha", true));
		registry.register(new Compound("BTX", true));
		registry.register(new Compound("Ethylene", true));
		registry.register(new Compound("Olefins ", true));
		registry.register(new Compound("Paraffin", true));
		registry.register(new Compound("Diesel", true));
		registry.register(new Compound("Kerosene", true));
	}

	public final String name;
	public final boolean fluid;

	public Compound(final String name, final boolean fluid) {
		super("compound_" + name.toLowerCase().replaceAll(" ", "_"), name);
		this.name = name;
		this.fluid = fluid;
	}
}