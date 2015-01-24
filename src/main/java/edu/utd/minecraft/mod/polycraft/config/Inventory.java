package edu.utd.minecraft.mod.polycraft.config;

import java.util.Map;

import net.minecraft.item.ItemStack;

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
						line.length > 6 ? PolycraftMod.getFileSafeName(line[3] + "_" + line[6]) : null, //inventoryAsset
						line.length > 15 ? line[15].split(",") : null, //paramNames
						line, 16 //params
						));
				for (int i = 7; i <= 14; i++) {
					if (line.length > i) {
						if (!line[i].isEmpty())
							inventory.blockFaceAssets.put(BlockFace.values()[i - 7], PolycraftMod.getFileSafeName(inventory.name + "_" + line[i]));
					}
					else
						break;
				}
			}
	}

	public final String tileEntityGameID;
	public final int guiID;
	public final int renderID;
	public final String inventoryAsset;
	public final Map<BlockFace, String> blockFaceAssets = Maps.newHashMap();
	public PolycraftContainerType containerType;

	public Inventory(final int[] version, final String gameID, final String tileEntityGameID, final String name, final int guiID, final int renderID, final String inventoryAsset,
			final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, gameID, name, paramNames, paramValues, paramsOffset);
		this.tileEntityGameID = tileEntityGameID;
		this.guiID = guiID;
		this.renderID = renderID;
		this.inventoryAsset = inventoryAsset;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size);
	}
}