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
import edu.utd.minecraft.mod.polycraft.item.ItemMold;

public class Mold extends GameIdentifiedConfig {

	public enum Type {
		Mold, MetalDie
	}

	public static final ConfigRegistry<Mold> registry = new ConfigRegistry<Mold>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Mold.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Mold(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						line[2], //name
						Type.valueOf(line[3].replaceAll(" ", "")), //type
						PolymerObject.registry.get(line[4]), //polymerObject
						Integer.parseInt(line[5]) //craftingMaxDamage
				));
	}

	public final Type moldType;
	public final PolymerObject polymerObject;
	public final int craftingMaxDamage;

	public Mold(final int[] version, final String gameID, final String name, final Type moldType, final PolymerObject polymerObject, final int craftingMaxDamage) {
		super(version, gameID, name);
		this.moldType = moldType;
		this.polymerObject = polymerObject;
		this.craftingMaxDamage = craftingMaxDamage;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public ItemStack getItemStack(final Ingot ingot) {
		return ItemMold.setDamagePerUse(new ItemStack(PolycraftRegistry.getItem(this)), ingot.moldDamagePerUse);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Crafting Max Damage");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(PolycraftMod.numFormat.format(craftingMaxDamage));
	}
	
	public static void checkItemJSONs(Mold config, String path){
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
						"}", "mold");

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
