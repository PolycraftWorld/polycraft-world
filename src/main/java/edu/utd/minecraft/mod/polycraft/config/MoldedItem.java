package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class MoldedItem extends SourcedConfig<Mold> {

	public static final ConfigRegistry<MoldedItem> registry = new ConfigRegistry<MoldedItem>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MoldedItem.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new MoldedItem(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						line[2], //name
						Mold.registry.get(line[3]), //source
						PolymerPellets.registry.get(line[4]), //polymerPellets
						Integer.parseInt(line[6]), //craftingPellets
						Float.parseFloat(line[7]), //craftingDurationSeconds
						line[8], //maxStackSize
						Boolean.parseBoolean(line[9]), //customTextureBool
						line.length <= 10 || line[10].isEmpty() ? null : line[10].split(","), //paramNames
						line, 11 //paramValues
				));
	}

	public final PolymerPellets polymerPellets;
	public final int craftingPellets;
	public final float craftingDurationSeconds;
	public final int maxStackSize;
	public final boolean loadCustomTexture;

	public MoldedItem(final int[] version, final String gameID, final String name, final Mold source, final PolymerPellets polymerPellets,
			final int craftingPellets, final float craftingDurationSeconds, final String maxStackSize, final boolean loadCustomTexture, final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, gameID, name, source, paramNames, paramValues, paramsOffset);
		this.polymerPellets = polymerPellets;
		this.craftingPellets = craftingPellets;
		this.craftingDurationSeconds = craftingDurationSeconds;
		this.maxStackSize = StringUtils.isEmpty(maxStackSize) ? 0 : Integer.parseInt(maxStackSize);
		this.loadCustomTexture = loadCustomTexture;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Polymer Pellets", "Crafting Pellets", "Crafting Duration (sec)", "Max Stack Size");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				polymerPellets.name,
				PolycraftMod.numFormat.format(craftingPellets),
				PolycraftMod.numFormat.format(craftingDurationSeconds),
				PolycraftMod.numFormat.format(maxStackSize));
	}
	
	public static void checkItemJSONs(MoldedItem config, String path){
		String name = PolycraftMod.getFileSafeName(config.name);
		String texture = PolycraftMod.getFileSafeName(config.source.polymerObject.name);
		if(name.contains("scuba_fins"))
			texture = "scuba_fins";
		else if(name.contains("mask"))
			if(name.contains("light"))
				texture = "scuba_mask_light";
			else
				texture = "scuba_mask";
		File json = new File(path + "models\\item\\" + name + ".json");
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

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + name + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}