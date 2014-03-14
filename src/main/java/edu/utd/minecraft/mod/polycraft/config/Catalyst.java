package edu.utd.minecraft.mod.polycraft.config;

public class Catalyst extends Entity {

	public static final EntityRegistry<Catalyst> registry = new EntityRegistry<Catalyst>();

	static {
		registry.register(new Catalyst(Element.registry.get("element_platinum"), 16));
	}

	public final int craftingAmount;

	public Catalyst(final Entity type, final int craftingAmount) {
		super("catalyst_" + type.gameName, type.name);
		this.craftingAmount = craftingAmount;
	}
}
