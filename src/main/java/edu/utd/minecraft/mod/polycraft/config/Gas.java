package edu.utd.minecraft.mod.polycraft.config;

public class Gas extends Entity {
	public static final EntityRegistry<Element> registry = new EntityRegistry<Element>();
	
	// 21% oxygen; 79% nitrogen.
	public static final Gas Air = new Gas("Air");
	
	// Scuba diving air mix of 32% oxygen and 68% nitrogen.
	// Not good for deep diving, but great for reducing nitrogen buildup.
	// Increases oxygen toxicity at depth.
	public static final Gas NitroxEan32 = new Gas("Nitrox EAN32");

	// Scuba diving mixture of Oxygen, Helium, and Nitrogen.  Reduced problems with
	// nitrogen narcosis and oxygen toxicity.
	public static final Gas Trimix = new Gas("Trimix");	

	// Scuba diving mixture of Helium and Oxygen, with no Nitrogen.
	// Usable for very deep diving without causing nitrogen narcosis or oxygen toxicity.  Best
	// used in a rebreather where the helium can be recycled.
	public static final Gas Heliox = new Gas("Heliox");
	
	public Gas(final String name) {
		super("gas_" + getSafeName(name), name);
	}
}
