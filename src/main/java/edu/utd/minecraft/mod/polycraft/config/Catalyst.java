package edu.utd.minecraft.mod.polycraft.config;

public class Catalyst extends Compound {

	public static final EntityRegistry<Catalyst> registry = new EntityRegistry<Catalyst>();

	public static final Catalyst platinum = registry.register(new Catalyst(Element.platinum.name));
	public static final Catalyst titanium = registry.register(new Catalyst(Element.titanium.name));
	public static final Catalyst palladium = registry.register(new Catalyst(Element.palladium.name));
	public static final Catalyst cobalt = registry.register(new Catalyst(Element.cobalt.name));
	public static final Catalyst manganese = registry.register(new Catalyst(Element.manganese.name));
	public static final Catalyst magnesiumOxide = registry.register(new Catalyst(Element.magnesium.name + " Oxide"));
	public static final Catalyst antimonyTrioxide = registry.register(new Catalyst(Element.antimony.name + " Trioxide"));
	public static final Catalyst copperIIChloride = registry.register(new Catalyst(Element.copper.name + " II Chloride"));
	public static final Catalyst ironIIIChloride = registry.register(new Catalyst("Iron III Chloride"));
	public static final Catalyst ironIIIOxide = registry.register(new Catalyst("Iron III Oxide"));
	public static final Catalyst zieglerNatta = registry.register(new Catalyst("Ziegler-Natta"));
	public static final Catalyst cobaltManganeseBromide = registry.register(new Catalyst(Element.cobalt.name + "-" + Element.manganese.name + "-Bromide"));
	public static final Catalyst rhodium = registry.register(new Catalyst(Element.rhodium.name));
	public static final Catalyst potassiumHydroxide = registry.register(new Catalyst(Element.potassium.name + " Hydroxide"));

	public Catalyst(final String name) {
		super(name, false);
	}

	public static String generate(final String[] entity) {
		final String[] params = new String[] { "\"" + entity[0] + "\"" };
		return Entity.generate(Catalyst.class.getSimpleName(), getVariableName(entity[0]), params);
	}
}