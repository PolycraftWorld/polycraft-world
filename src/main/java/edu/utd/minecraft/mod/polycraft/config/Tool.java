package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Tool extends Config {

	public enum Type {
		HOE("Hoe", 0),
		SWORD("Sword", 1),
		SHOVEL("Shovel", 2),
		PICKAXE("Pickaxe", 3),
		AXE("Axe", 4);

		private final String name;
		private final int value;

		/**
		 * @return the friendly name of the type
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * @return the integer value of the tool slot.
		 */
		public int getValue() {
			return this.value;
		}

		private Type(final String name, final int value) {
			this.name = name;
			this.value = value;
		}
	}

	public static final ConfigRegistry<Tool> registry = new ConfigRegistry<Tool>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Tool.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				registry.register(new Tool(
						PolycraftMod.getVersionNumeric(line[0]),
						new String[] { line[1], line[2], line[3], line[4], line[5] }, //type game ids
						line[6], //crafting item shaft name
						line[7], //crafting item head name
						line[8], //name
						Integer.parseInt(line[9]), //harvestLevel
						Integer.parseInt(line[10]), //maxUses
						Integer.parseInt(line[11]), //efficiency
						Integer.parseInt(line[12]), //damage
						Integer.parseInt(line[13]) //enchantability

				));
			}
	}

	public final String[] typeGameIDs;
	public final String craftingHeadItemName;
	public final String craftingShaftItemName;
	public final int harvestLevel;
	public final int maxUses;
	public final int efficiency;
	public final int damage;
	public final int enchantability;

	public Tool(
			final int[] version, final String[] typeGameIDs,
			final String craftingShaftItemName, final String craftingHeadItemName,
			final String name,
			final int harvestLevel, final int maxUses, final int efficiency,
			final int damage, final int enchantability) {
		super(version, name);
		this.typeGameIDs = typeGameIDs;
		this.craftingHeadItemName = craftingHeadItemName;
		this.craftingShaftItemName = craftingShaftItemName;
		this.harvestLevel = harvestLevel;
		this.maxUses = maxUses;
		this.efficiency = efficiency;
		this.damage = damage;
		this.enchantability = enchantability;
	}

	public String getFullTypeName(final Type type) {
		return name + " " + type.getName();
	}

	public String getTexture(final Type type) {
		return PolycraftMod.getAssetNameString(PolycraftMod.getFileSafeName(getFullTypeName(type)));
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