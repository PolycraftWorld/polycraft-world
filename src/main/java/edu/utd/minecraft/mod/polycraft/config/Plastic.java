package edu.utd.minecraft.mod.polycraft.config;

public class Plastic extends Entity {

	public static final EntityRegistry<Plastic> registry = new EntityRegistry<Plastic>();
	public static final String[] color_names = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
	

	static {
		for (int i = 0; i < color_names.length; i++)
	     {		
			registry.register(new Plastic(1, "Polyethylene terephthalate", "PET", color_names[i], .1, 64));
			registry.register(new Plastic(2, "High-density polyethylene", "PE-HD", color_names[i], 1, 32));
			registry.register(new Plastic(3, "Polyvinyl chloride", "PVC", color_names[i], .2, 16));
			registry.register(new Plastic(4, "Low-density polyethylene", "PELD", color_names[i], .05, 8));
			registry.register(new Plastic(5, "Polypropylene", "PP", color_names[i], .4, 4));
			registry.register(new Plastic(6, "Polystyrene", "PS", color_names[i], .3, 2));
			registry.register(new Plastic(7, "Other polycarbonate", "O", color_names[i],  2, 1));
	     }
	}

	public final String itemNamePellet;
	public final String itemNameFiber;
	public final String itemNameGrip;
	public final int type;
	public final String abbreviation;
	public final double itemDurabilityBonus;
	public final int craftingPelletsPerBlock;
	public final String color;

	public Plastic(final int type, final String name,  final String abbrevation, final String color, final double itemDurabilityBonus, final int craftingPelletsPerBlock) {
		super("plastic_" + type +"_"+ color, name);
		this.itemNamePellet = gameName + "_pellet"; 		//this makes pellets of different colors
		this.itemNameFiber = gameName + "_fiber"; 			//this makes fibers of different colors
		this.itemNameGrip = "plastic_" + type + "_grip"; 	//this makes only one color of grip
		this.type = type;
		this.color = color;
		this.abbreviation = abbrevation;
		this.itemDurabilityBonus = itemDurabilityBonus;
		this.craftingPelletsPerBlock = craftingPelletsPerBlock;
	}
}
