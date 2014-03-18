package edu.utd.minecraft.mod.polycraft.config;

public class Element extends Entity {

	public static final EntityRegistry<Element> registry = new EntityRegistry<Element>();

	public static final Element carbon = registry.register(new Element("Carbon", "C", 6));
	public static final Element magnesium = registry.register(new Element("Magnesium", "Mg", 12));
	public static final Element aluminum = registry.register(new Element("Aluminum", "Al", 13));
	public static final Element chlorine = registry.register(new Element("Chlorine", "Cl", 17, true));
	public static final Element titanium = registry.register(new Element("Titanium", "Ta", 22));
	public static final Element manganese = registry.register(new Element("Manganese", "Mn", 25));
	public static final Element cobalt = registry.register(new Element("Cobalt", "Co", 27));
	public static final Element copper = registry.register(new Element("Copper", "Cu", 29));
	public static final Element bromine = registry.register(new Element("Bromine", "Br", 35, true));
	public static final Element palladium = registry.register(new Element("Palladium", "Pd", 46));
	public static final Element antimony = registry.register(new Element("Antimony", "Sb", 51));
	public static final Element platinum = registry.register(new Element("Platinum", "Pt", 78));

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