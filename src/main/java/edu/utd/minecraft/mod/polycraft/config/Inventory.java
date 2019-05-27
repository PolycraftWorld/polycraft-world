package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.BlockFace;

public class Inventory extends GameIdentifiedConfig {

	public static final ConfigRegistry<Inventory> registry = new ConfigRegistry<Inventory>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Inventory.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final Inventory inventory = registry.register(new Inventory(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						line[2], //tileEntityGameID
						line[3], //name
						Integer.parseInt(line[4]), //guiID
						Integer.parseInt(line[5]), //renderID
						Boolean.parseBoolean(line[6]), //3D rendering?
						Integer.parseInt(line[7]), //length
						Integer.parseInt(line[8]), //width
						Integer.parseInt(line[9]), //height
						line.length > 10 ? line[10].split(",") : null, //fuel inputs
						line.length > 11 ? line[11].split(";") : null, //input areas
						line.length > 12 ? line[12].split(",") : null, //output areas
						line.length > 13 ? PolycraftMod.getFileSafeName(line[3] + "_" + line[13]) : null, //inventoryAsset
						line.length > 22 ? line[22].split(",") : null, //paramNames
						line, 23 //params
						));
				for (int i = 14; i <= 21; i++) {
					if (line.length > i) {
						if (!line[i].isEmpty())
							inventory.blockFaceAssets.put(BlockFace.values()[i - 14], PolycraftMod.getFileSafeName(inventory.name + "_" + line[i]));
					}
					else
						break;
				}
			}
	}

	public final String tileEntityGameID;
	public final int guiID;
	public int renderID;
	public final int length, width, height;
	public final boolean render3D;
	public final String inventoryAsset;
	public final Map<BlockFace, String> blockFaceAssets = Maps.newHashMap();
	public PolycraftContainerType containerType;
	public List<int[]> inputBlockOffset; //offset in length, width and height from inventory block
	public int[] outputBlockOffset; //offset in length, width and height from inventory block
	public int[] fuelBlockOffset; //offset in length, width and height from inventory block

	public Inventory(final int[] version, final String gameID, final String tileEntityGameID, final String name, final int guiID, final int renderID,
			final boolean render3D, final int length, final int width, final int height, final String[] fuelCoords, final String[] inputCoords, final String[] outputCoords, final String inventoryAsset,
			final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, gameID, name, paramNames, paramValues, paramsOffset);
		this.tileEntityGameID = tileEntityGameID;
		this.guiID = guiID;
		this.renderID = renderID;
		this.render3D = render3D;
		this.length = length;
		this.width = width;
		this.height = height;
		this.inventoryAsset = inventoryAsset;
		this.inputBlockOffset = Lists.newArrayList();
		this.outputBlockOffset = new int[3];
		this.fuelBlockOffset = new int[3];

		if (inputCoords[0].isEmpty())
			inputBlockOffset = null;
		else
		{
			for (String inputCoord : inputCoords)
			{
				String[] singleInput = inputCoord.replaceAll("\"", "").split(",");
				int[] singleInputInt = new int[singleInput.length];
				for (int x = 0; x < singleInput.length; x++)
				{
					singleInputInt[x] = Integer.parseInt(singleInput[x].replaceAll("\"", ""));

				}
				inputBlockOffset.add(singleInputInt);
			}
		}
		if (outputCoords[0].isEmpty())
			outputBlockOffset = null;
		else
		{

			for (int x = 0; x < outputBlockOffset.length; x++) {
				this.outputBlockOffset[x] = Integer.parseInt(outputCoords[x].replaceAll("\"", ""));
			}
		}

		if (fuelCoords[0].isEmpty())
			fuelBlockOffset = null;
		else
		{

			for (int x = 0; x < fuelBlockOffset.length; x++) {
				this.fuelBlockOffset[x] = Integer.parseInt(fuelCoords[x].replaceAll("\"", ""));
			}
		}

	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size);
	}
}