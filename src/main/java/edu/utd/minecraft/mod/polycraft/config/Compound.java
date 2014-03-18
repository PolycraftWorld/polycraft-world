package edu.utd.minecraft.mod.polycraft.config;

public class Compound extends Entity {

	public static final EntityRegistry<Compound> registry = new EntityRegistry<Compound>();

	public static final Compound bitumen = registry.register(new Compound("Bitumen", false));
	public static final Compound naturalGas = registry.register(new Compound("Natural Gas", true));
	public static final Compound ethane = registry.register(new Compound("Ethane", true));
	public static final Compound ethylene = registry.register(new Compound("Ethylene", true));
	public static final Compound ethyleneOxide = registry.register(new Compound("Ethylene Oxide", true));
	public static final Compound ethyleneGlycol = registry.register(new Compound("Ethylene Glycol", true));
	public static final Compound propane = registry.register(new Compound("Propane", true));
	public static final Compound propylene = registry.register(new Compound("Propylene", true));
	public static final Compound butane = registry.register(new Compound("Butane", true));
	public static final Compound butadiene = registry.register(new Compound("Butadiene", true));
	public static final Compound methane = registry.register(new Compound("Methane", true));
	public static final Compound methanol = registry.register(new Compound("Methanol", true));
	public static final Compound dimethylTerephthalate = registry.register(new Compound("Dimethyl Terephthalate", true));
	public static final Compound naphtha = registry.register(new Compound("Naphtha", true));
	public static final Compound gasOil = registry.register(new Compound("Gas Oil", true));
	public static final Compound btx = registry.register(new Compound("BTX", true));
	public static final Compound olefins = registry.register(new Compound("Olefins ", true));
	public static final Compound paraffin = registry.register(new Compound("Paraffin", true));
	public static final Compound diesel = registry.register(new Compound("Diesel", true));
	public static final Compound kerosene = registry.register(new Compound("Kerosene", true));
	public static final Compound terephthalicAcid = registry.register(new Compound("Terephthalic Acid", true));
	public static final Compound edc = registry.register(new Compound("EDC", true));
	public static final Compound vinylChloride = registry.register(new Compound("Vinyl Chloride ", true));
	public static final Compound hcl = registry.register(new Compound("HCL", true));
	public static final Compound acetylene = registry.register(new Compound("Acetylene", true));
	public static final Compound cl2 = registry.register(new Compound("Cl2", true));
	public static final Compound h2 = registry.register(new Compound("H2", true));
	public static final Compound h2o = registry.register(new Compound("H2O", true));
	public static final Compound sulfuricAcid = registry.register(new Compound("Sulfuric Acid ", true));
	public static final Compound benzene = registry.register(new Compound("Benzene", true));
	public static final Compound ethylbenzene = registry.register(new Compound("Ethylbenzene", true));
	public static final Compound styrene = registry.register(new Compound("Styrene", true));

	public final boolean fluid;

	public Compound(final String name, final boolean fluid) {
		super("compound_" + name.toLowerCase().replaceAll(" ", "_"), name);
		this.fluid = fluid;
	}
}