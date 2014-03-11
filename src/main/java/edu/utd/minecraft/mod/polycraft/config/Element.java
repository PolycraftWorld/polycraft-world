package edu.utd.minecraft.mod.polycraft.config;

import java.util.HashMap;
import java.util.Map;

public class Element extends Entity {

	public static final Map<String, Element> elements = new HashMap<String, Element>();

	private static final Element registerElement(final Element element) {
		elements.put(element.gameName, element);
		return element;
	}

	static {
		registerElement(new Element("Magnesium", "Mg", 12));
		registerElement(new Element("Aluminum", "Al", 13));
		registerElement(new Element("Titanium", "Ta", 22));
		registerElement(new Element("Manganese", "Mn", 25));
		registerElement(new Element("Cobalt", "Co", 27));
		registerElement(new Element("Copper", "Cu", 29));
		registerElement(new Element("Palladium", "Pd", 46));
		registerElement(new Element("Antimony", "Sb", 51));
		registerElement(new Element("Platinum", "Pt", 78));
	}

	public final String symbol;
	public final int atomicNumber;

	public Element(final String name, final String symbol, final int atomicNumber) {
		super("element_" + name.toLowerCase(), name);
		this.symbol = symbol;
		this.atomicNumber = atomicNumber;
	}
}