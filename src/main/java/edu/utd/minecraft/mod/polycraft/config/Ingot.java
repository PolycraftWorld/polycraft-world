package edu.utd.minecraft.mod.polycraft.config;

public class Ingot extends Entity {

	public static final EntityRegistry<Ingot> registry = new EntityRegistry<Ingot>();

	public static final Ingot carbon = registry.register(new Ingot(Element.Carbon));
	public static final Ingot magnesium = registry.register(new Ingot(Element.Magnesium));
	public static final Ingot aluminum = registry.register(new Ingot(Element.Aluminium));
	public static final Ingot titanium = registry.register(new Ingot(Element.Titanium));
	public static final Ingot manganese = registry.register(new Ingot(Element.Manganese));
	public static final Ingot cobalt = registry.register(new Ingot(Element.Cobalt));
	public static final Ingot copper = registry.register(new Ingot(Element.Copper));
	public static final Ingot palladium = registry.register(new Ingot(Element.Palladium));
	public static final Ingot antimony = registry.register(new Ingot(Element.Antimony));
	public static final Ingot platinum = registry.register(new Ingot(Element.Platinum));
	public static final Ingot steel = registry.register(new Ingot(Alloy.steel));

	public final Entity type;

	public Ingot(final Entity type) {
		super("ingot_" + type.gameName, type.name);
		this.type = type;
	}
}
