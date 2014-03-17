package edu.utd.minecraft.mod.polycraft.config;

public class Ingot extends Entity {

	public static final EntityRegistry<Ingot> registry = new EntityRegistry<Ingot>();

	static {
		for (final Element element : Element.registry.values())
			if (!element.fluid)
				registry.register(new Ingot(element));
		for (final Alloy alloy : Alloy.registry.values())
			registry.register(new Ingot(alloy));
	}

	public final Entity type;

	public Ingot(final Entity type) {
		super("ingot_" + type.gameName, type.name);
		this.type = type;
	}
}
