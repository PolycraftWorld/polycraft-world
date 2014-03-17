package edu.utd.minecraft.mod.polycraft.config;

public class Plastic extends Entity {

	public static final String[] colors = new String[] { "white", "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange" };

	public static final String getDefaultColor() {
		return colors[0];
	}

	public static final EntityRegistry<Plastic> registry = new EntityRegistry<Plastic>();

	static {
		for (int i = 0; i < colors.length; i++) {
			registry.register(new Plastic(1, "Polyethylene terephthalate", "PET", colors[i], .1, 64));
			registry.register(new Plastic(2, "High-density polyethylene", "PE-HD", colors[i], 1, 32));
			registry.register(new Plastic(3, "Polyvinyl chloride", "PVC", colors[i], .2, 16));
			registry.register(new Plastic(4, "Low-density polyethylene", "PELD", colors[i], .05, 8));
			registry.register(new Plastic(5, "Polypropylene", "PP", colors[i], .4, 4));
			registry.register(new Plastic(6, "Polystyrene", "PS", colors[i], .3, 2));
			registry.register(new Plastic(7, "Other polycarbonate", "O", colors[i], 2, 1));
		}
	}

	private static String getGameName(final int type, final String color) {
		return "plastic_" + type + "_" + color;
	}

	public static Plastic getDefaultForType(final int type) {
		return registry.get(getGameName(type, getDefaultColor()));
	}

	public final String itemNamePellet;
	public final String itemNameFiber;
	public final String itemNameGrip;
	public final int type;
	public final String abbreviation;
	public final String color;
	public final double itemDurabilityBonus;
	public final int craftingPelletsPerBlock;

	public Plastic(final int type, final String name, final String abbrevation, final String color, final double itemDurabilityBonus, final int craftingPelletsPerBlock) {
		super(getGameName(type, color), name);
		this.itemNamePellet = gameName + "_pellet"; // this makes pellets of different colors
		this.itemNameFiber = gameName + "_fiber"; // this makes fibers of different colors
		this.itemNameGrip = "plastic_" + type + "_grip"; // this makes only one color of grip
		this.type = type;
		this.abbreviation = abbrevation;
		this.color = color;
		this.itemDurabilityBonus = itemDurabilityBonus;
		this.craftingPelletsPerBlock = craftingPelletsPerBlock;
	}

	public boolean isDefaultColor() {
		return color == getDefaultColor();
	}
}
