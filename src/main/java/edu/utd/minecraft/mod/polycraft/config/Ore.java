package edu.utd.minecraft.mod.polycraft.config;

import java.util.HashMap;
import java.util.Map;

public class Ore extends Entity {

	public static final Map<String, Ore> ores = new HashMap<String, Ore>();

	private static final Ore registerOre(final Ore ore) {
		ores.put(ore.gameName, ore);
		return ore;
	}

	static {
		// element ores
		registerOre(new Ore(Element.elements.get("element_magnesium"), 3, 5, 2, 5, 0, 30, 10, 10, Ingot.ingots.get("ingot_element_magnesium"), 1, .1f));
		registerOre(new Ore(Element.elements.get("element_titanium"), 3, 5, 1, 4, 0, 30, 10, 10, Ingot.ingots.get("ingot_element_titanium"), 1, .1f));
		registerOre(new Ore(Element.elements.get("element_manganese"), 3, 5, 2, 5, 0, 30, 10, 10, Ingot.ingots.get("ingot_element_manganese"), 1, .1f));
		registerOre(new Ore(Element.elements.get("element_cobalt"), 3, 5, 2, 5, 0, 30, 10, 10, Ingot.ingots.get("ingot_element_cobalt"), 1, .1f));
		registerOre(new Ore(Element.elements.get("element_copper"), 3, 5, 3, 7, 0, 30, 10, 10, Ingot.ingots.get("ingot_element_copper"), 1, .1f));
		registerOre(new Ore(Element.elements.get("element_palladium"), 3, 5, 1, 4, 0, 30, 10, 10, Ingot.ingots.get("ingot_element_palladium"), 1, .1f));
		registerOre(new Ore(Element.elements.get("element_antimony"), 3, 5, 2, 5, 0, 30, 10, 10, Ingot.ingots.get("ingot_element_antimony"), 1, .1f));
		registerOre(new Ore(Element.elements.get("element_platinum"), 3, 5, 1, 4, 0, 30, 10, 10, Ingot.ingots.get("ingot_element_platinum"), 1, .1f));

		// mineral ores
		registerOre(new Ore(Mineral.minerals.get("mineral_bauxite"), 3, 5, 2, 5, 10, 30, 10, 10, Ingot.ingots.get("ingot_element_aluminum"), 1, .1f));
	}

	public final Entity type;
	public final float hardness;
	public final float resistance;
	public final int dropExperienceMin;
	public final int dropExperienceMax;
	public final int generationStartYMin;
	public final int generationStartYMax;
	public final int generationVeinsPerChunk;
	public final int generationBlocksPerVein;
	public final Ingot smeltingIngot;
	public final int smeltingIngotsPerBlock;
	public final float smeltingExperience;

	public Ore(final Entity type, final float hardness, final float resistance, final int dropExperienceMin, final int dropExperienceMax, final int generationStartYMin, final int generationStartYMax, final int generationVeinsPerChunk,
			final int generationBlocksPerVein, final Ingot smeltingIngot, final int smeltingIngotsPerBlock, final float smeltingExperience) {
		super("ore_" + type.gameName, type.name);
		this.type = type;
		this.hardness = hardness;
		this.resistance = resistance;
		this.dropExperienceMin = dropExperienceMin;
		this.dropExperienceMax = dropExperienceMax;
		this.generationStartYMin = generationStartYMin;
		this.generationStartYMax = generationStartYMax;
		this.generationVeinsPerChunk = generationVeinsPerChunk;
		this.generationBlocksPerVein = generationBlocksPerVein;
		this.smeltingIngot = smeltingIngot;
		this.smeltingIngotsPerBlock = smeltingIngotsPerBlock;
		this.smeltingExperience = smeltingExperience;
	}
}
