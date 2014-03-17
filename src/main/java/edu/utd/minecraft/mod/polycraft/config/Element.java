package edu.utd.minecraft.mod.polycraft.config;

public class Element extends Entity {

	public static final EntityRegistry<Element> registry = new EntityRegistry<Element>();

	static {
		registry.register(new Element("Carbon", "C", 6));
		registry.register(new Element("Magnesium", "Mg", 12));
		registry.register(new Element("Aluminum", "Al", 13));
		registry.register(new Element("Titanium", "Ta", 22));
		registry.register(new Element("Manganese", "Mn", 25));
		registry.register(new Element("Cobalt", "Co", 27));
		registry.register(new Element("Copper", "Cu", 29));
		registry.register(new Element("Palladium", "Pd", 46));
		registry.register(new Element("Antimony", "Sb", 51));
		registry.register(new Element("Platinum", "Pt", 78));
	}

	public final String symbol;
	public final int atomicNumber;

	public Element(final String name, final String symbol, final int atomicNumber) {
		super("element_" + name.toLowerCase().replaceAll(" ", "_"), name);
		this.symbol = symbol;
		this.atomicNumber = atomicNumber;
	}
}