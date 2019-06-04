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

public class WaferItem extends SourcedConfig<Mask> {

	public static final ConfigRegistry<WaferItem> registry = new ConfigRegistry<WaferItem>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, WaferItem.class.getSimpleName().toLowerCase()))
			if (line.length > 0)

				registry.register(new WaferItem(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID wafer
						Mask.registry.get(line[2]), //source Mask
						line[3], //electronic object
						line[4], //layer
						Integer.parseInt(line[6]), //layer number
						line[7], //name
						WaferItem.registry.get(line[8]), //source Wafer
						(PolymerPellets.registry.get(line[9]) != null) ? PolymerPellets.registry.get(line[9]) : Nugget.registry.get(line[9]),
						Boolean.parseBoolean(line[10]), //customTextureBool
						Boolean.parseBoolean(line[11]), //needs to be in clean environment
						line.length <= 12 || line[12].isEmpty() ? null : line[12].split(","), //paramNames
						line, 13 //paramValues
				));
	}

	public final String electronicObject;
	public final String layerName;
	public final boolean loadCustomTexture;
	public final boolean mustBeInCleanroom;
	public final WaferItem sourceWafer;
	public final PolymerPellets photoresist;
	public final Nugget nugget;

	public WaferItem(final int[] version, final String gameID, final Mask source, final String electronicObject, final String layerName,
			final int layerNumber, final String name, final WaferItem sourceWafer, final Object thinFilmMaterial, final boolean customTextureBool,
			final boolean mustBeInCleanroom, final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, gameID, name, source, paramNames, paramValues, paramsOffset);
		this.electronicObject = electronicObject;
		this.layerName = layerName;
		this.loadCustomTexture = customTextureBool;
		this.mustBeInCleanroom = mustBeInCleanroom;
		this.sourceWafer = sourceWafer;
		if (thinFilmMaterial instanceof PolymerPellets)
		{
			this.photoresist = (PolymerPellets) thinFilmMaterial;
			this.nugget = null;
		}
		else
		{
			this.nugget = (Nugget) thinFilmMaterial;
			this.photoresist = null;
		}

	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of(
			"Electronic Object",
			"Layer Name",
			"Must Keep Clean",
			"Source Wafer",
			"Photoresist",
			"Nugget");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				this.electronicObject,
				this.layerName,
				String.valueOf(this.mustBeInCleanroom),
				this.sourceWafer != null ? this.sourceWafer.name : "null",
				this.photoresist != null ? this.photoresist.name : "null",
				this.nugget != null ? this.nugget.name : "null");

		//return null;
	}
	
	public static void checkItemJSONs(WaferItem config, String path){
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
						"        \"layer0\": \"polycraft:items/waferitem\"\n" + 
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