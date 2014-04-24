package edu.utd.minecraft.mod.polycraft.config;

import java.util.Map;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.BlockFace;

public class Inventory extends GameIdentifiedConfig {

	public static final ConfigRegistry<Inventory> registry = new ConfigRegistry<Inventory>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Inventory.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final Inventory inventory = registry.register(new Inventory(
						line[0], //gameID
						line[1], //tileEntityGameID
						line[2], //name
						Integer.parseInt(line[3]), //guiID
						Integer.parseInt(line[4]), //renderID
						line.length > 5 ? PolycraftMod.getFileSafeName(line[2] + "_" + line[5]) : null //inventoryAsset
						));
				for (int i = 6; i <= 13; i++) {
					if (line.length > i) {
						if (!line[i].isEmpty())
							inventory.blockFaceAssets.put(BlockFace.values()[i - 6], PolycraftMod.getFileSafeName(inventory.name + "_" + line[i]));
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

	public Inventory(final String gameID, final String tileEntityGameID, final String name, final int guiID, final int renderID, final String inventoryAsset) {
		super(gameID, name);
		this.tileEntityGameID = tileEntityGameID;
		this.guiID = guiID;
		this.renderID = renderID;
		this.inventoryAsset = inventoryAsset;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getBlock(this), size);
	}
}