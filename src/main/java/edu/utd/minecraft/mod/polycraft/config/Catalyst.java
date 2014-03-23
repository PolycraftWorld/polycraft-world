package edu.utd.minecraft.mod.polycraft.config;

public class Catalyst extends Entity {

	public static final EntityRegistry<Catalyst> registry = new EntityRegistry<Catalyst>();

	public static final Catalyst platinum = registry.register(new Catalyst(Element.Platinum.name));
	public static final Catalyst titanium = registry.register(new Catalyst(Element.Titanium.name));
	public static final Catalyst palladium = registry.register(new Catalyst(Element.Palladium.name));
	public static final Catalyst cobalt = registry.register(new Catalyst(Element.Cobalt.name));
	public static final Catalyst manganese = registry.register(new Catalyst(Element.Manganese.name));
	public static final Catalyst magnesiumOxide = registry.register(new Catalyst(Element.Magnesium.name + " Oxide"));
	public static final Catalyst antimonyTrioxide = registry.register(new Catalyst(Element.Antimony.name + " Trioxide"));
	public static final Catalyst copperIIChloride = registry.register(new Catalyst(Element.Copper.name + " II Chloride"));
	public static final Catalyst ironIIIChloride = registry.register(new Catalyst("Iron III Chloride"));
	public static final Catalyst ironIIIOxide = registry.register(new Catalyst("Iron III Oxide"));
	public static final Catalyst zieglerNatta = registry.register(new Catalyst("Ziegler-Natta"));
	public static final Catalyst cobaltManganeseBromide = registry.register(new Catalyst(Element.Cobalt.name + "-" + Element.Manganese.name + "-Bromide"));
	public static final Catalyst rhodium = registry.register(new Catalyst(Element.Rhodium.name));
	public static final Catalyst potassiumHydroxide = registry.register(new Catalyst(Element.Potassium.name + "Hydroxide"));

	public Catalyst(final String name) {
		super("catalyst_" + name.toLowerCase().replaceAll(" ", "_").replaceAll("-", "_"), name);
	}
}