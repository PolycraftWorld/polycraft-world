package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;

public class Armor extends Config {

	public static final ConfigRegistry<Armor> registry = new ConfigRegistry<Armor>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Armor.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				registry.register(new Armor(
						PolycraftMod.getVersionNumeric(line[0]),
						new String[] { line[1], line[2], line[3], line[4] }, //component game ids
						line[5], //crafting item name
						line[6], //name
						new String[] { line[7], line[8], line[9], line[10] }, //component names
						Integer.parseInt(line[11]), //durability
						Integer.parseInt(line[12]), //enchantability
						new int[] { Integer.parseInt(line[13]), Integer.parseInt(line[14]), Integer.parseInt(line[15]), Integer.parseInt(line[16]) } //reduction ammounts
				));
			}
	}

	public final String[] componentGameIDs;
	public final String craftingItemName;
	public final String[] componentNames;
	public final int durability;
	public final int enchantability;
	public final int[] reductionAmounts;

	public Armor(
			final int[] version, final String[] componentGameIDs, final String craftingItemName,
			final String name, final String[] componentNames,
			final int durability, final int enchantability,
			final int[] reductionAmounts) {
		super(version, name);
		this.componentGameIDs = componentGameIDs;
		this.componentNames = componentNames;
		this.craftingItemName = craftingItemName;
		this.durability = durability;
		this.enchantability = enchantability;
		this.reductionAmounts = reductionAmounts;
	}
	
	public String getFullComponentName(final ArmorSlot slot) {
		return name + " " + componentNames[slot.getValue()];
	}

	public String getTexture() {
		return PolycraftMod.getAssetName("textures/models/armor/" + PolycraftMod.getFileSafeName(name) + ".png");
	}
}