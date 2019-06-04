package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.item.ItemMask;

public class Mask extends GameIdentifiedConfig {

	public enum Type {
		Mask
	}

	public static final ConfigRegistry<Mask> registry = new ConfigRegistry<Mask>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Mask.class.getSimpleName().toLowerCase()))
			if (line.length > 0)

				registry.register(new Mask(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID mask
						line[2], //name
						Type.valueOf(line[3].replaceAll(" ", "")), //type
						line[4], //electronic object
						line[5]
						));

	}

	public final Type maskType;

	//public final PolymerObject polymerObject;
	//public final int craftingMaxDamage;

	public Mask(final int[] version, final String gameID, final String name, final Type maskType, final String electronicObj,
			final String layer)
	{
		super(version, gameID, name);
		this.maskType = maskType;
		//this.polymerObject = polymerObject;
		//this.craftingMaxDamage = craftingMaxDamage;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public ItemStack getItemStack(final Nugget nugget) {
		return ItemMask.setDamagePerUse(new ItemStack(PolycraftRegistry.getItem(this)), nugget.maskDamagePerUse);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of(
			"Mask Type");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				this.maskType.name());
	}
	
	public static void checkItemJSONs(Mask config, String path){
		String texture = PolycraftMod.getFileSafeName(config.name);
		File json = new File(path + "models\\item\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{
				//Item model file
				String fileContent = String.format("{\n" + 
						"    \"parent\": \"builtin/generated\",\n" + 
						"    \"textures\": {\n" + 
						"        \"layer0\": \"polycraft:items/itemmask\"\n" + 
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