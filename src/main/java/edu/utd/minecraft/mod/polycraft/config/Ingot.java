package edu.utd.minecraft.mod.polycraft.config;

public class Ingot extends Entity {

	public static final EntityRegistry<Ingot> registry = new EntityRegistry<Ingot>();

	public static final Ingot carbon = registry.register(new Ingot(Element.carbon));
	public static final Ingot magnesium = registry.register(new Ingot(Element.magnesium));
	public static final Ingot aluminium = registry.register(new Ingot(Element.aluminium));
	public static final Ingot titanium = registry.register(new Ingot(Element.titanium));
	public static final Ingot manganese = registry.register(new Ingot(Element.manganese));
	public static final Ingot cobalt = registry.register(new Ingot(Element.cobalt));
	public static final Ingot copper = registry.register(new Ingot(Element.copper));
	public static final Ingot palladium = registry.register(new Ingot(Element.palladium));
	public static final Ingot antimony = registry.register(new Ingot(Element.antimony));
	public static final Ingot platinum = registry.register(new Ingot(Element.platinum));
	public static final Ingot steel = registry.register(new Ingot(Alloy.steel));

	public final Entity type;

	public Ingot(final Entity type) {
		super("ingot_" + type.gameName, type.name);
		this.type = type;
	}

	@Override
	public String export(final String delimiter) {
		return String.format("%2$s%1$s%3$s.%4$s",
				delimiter, super.export(delimiter), type.getClass().getSimpleName(), type.name.toLowerCase());
	}

	public static String generate(final String[] entity) {
		final String[] params = new String[] { entity[1] };
		return Entity.generate(Ingot.class.getSimpleName(), getVariableName(entity[0]), params);
	}
}
