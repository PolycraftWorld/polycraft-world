package edu.utd.minecraft.mod.polycraft.config;

public class Element extends Entity {

	public static final EntityRegistry<Element> registry = new EntityRegistry<Element>();

	static {
		registry.register(new Element("Carbon", "C", 6));
		registry.register(new Element("Magnesium", "Mg", 12));
		registry.register(new Element("Aluminum", "Al", 13));
		registry.register(new Element("Chlorine", "Cl", 17, true));
		registry.register(new Element("Titanium", "Ta", 22));
		registry.register(new Element("Manganese", "Mn", 25));
		registry.register(new Element("Cobalt", "Co", 27));
		registry.register(new Element("Copper", "Cu", 29));
		registry.register(new Element("Bromine", "Br", 35, true));
		registry.register(new Element("Palladium", "Pd", 46));
		registry.register(new Element("Antimony", "Sb", 51));
		registry.register(new Element("Platinum", "Pt", 78));
	}

	public final String symbol;
	public final int atomicNumber;
	public final boolean fluid;

	public Element(final String name, final String symbol, final int atomicNumber) {
		this(name, symbol, atomicNumber, false);
	}

	public Element(final String name, final String symbol, final int atomicNumber, boolean fluid) {
		super("element_" + name.toLowerCase().replaceAll(" ", "_"), name);
		this.symbol = symbol;
		this.atomicNumber = atomicNumber;
		this.fluid = fluid;
	}
}