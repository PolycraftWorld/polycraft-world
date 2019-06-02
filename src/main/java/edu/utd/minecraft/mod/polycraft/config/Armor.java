package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
						new int[] { Integer.parseInt(line[13]), Integer.parseInt(line[14]), Integer.parseInt(line[15]), Integer.parseInt(line[16]) }, //reduction ammounts
						Integer.parseInt(line[17]) //aquaAffinityLevel
				));
			}
	}

	public final String[] componentGameIDs;
	public final String craftingItemName;
	public final String[] componentNames;
	public final int durability;
	public final int enchantability;
	public final int aquaAffinityLevel;
	public final int[] reductionAmounts;

	public Armor(
			final int[] version, final String[] componentGameIDs, final String craftingItemName,
			final String name, final String[] componentNames,
			final int durability, final int enchantability,
			final int[] reductionAmounts,
			final int aquaAffinityLevel) {
		super(version, name);
		this.componentGameIDs = componentGameIDs;
		this.componentNames = componentNames;
		this.craftingItemName = craftingItemName;
		this.durability = durability;
		this.enchantability = enchantability;
		this.reductionAmounts = reductionAmounts;
		this.aquaAffinityLevel = aquaAffinityLevel;
	}

	public String getFullComponentName(final ArmorSlot slot) {
		return name + " " + componentNames[slot.getValue()];

	}

	public String getTexture() {
		return PolycraftMod.getAssetNameString("textures/models/armor/" + PolycraftMod.getFileSafeName(name) + ".png");
	}
	
	public static void checkItemJSONs(Config config, String path, String texture){
		//String texture = PolycraftMod.getFileSafeName(config.name);
		File json = new File(path + "models\\item\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{
				//Item model file
				String fileContent = String.format("{\n" + 
						"    \"parent\": \"builtin/generated\",\n" + 
						"    \"textures\": {\n" + 
						"        \"layer0\": \"polycraft:items/%s\"\n" + 
						"    },\n" + 
						"    \"display\": {\n" + 
						"        \"thirdperson\": {\n" + 
						"            \"rotation\": [ -90, 0, 0 ],\n" + 
						"            \"translation\": [ 0, 1, -3 ],\n" + 
						"            \"scale\": [ 0.55, 0.55, 0.55 ]\n" + 
						"        },\n" + 
						"        \"firstperson\": {\n" + 
						"            \"rotation\": [ 0, -135, 25 ],\n" + 
						"            \"translation\": [ 0, 4, 2 ],\n" + 
						"            \"scale\": [ 1.7, 1.7, 1.7 ]\n" + 
						"        }\n" + 
						"    }\n" + 
						"}", texture);

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}