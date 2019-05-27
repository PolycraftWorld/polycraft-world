package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class InternalObject extends GameIdentifiedConfig {

	public static final ConfigRegistry<InternalObject> registry = new ConfigRegistry<InternalObject>();

	public final String display;
	public final String tileEntityGameID;
	public int renderID;
	public final String itemID;
	public final String itemName;

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, InternalObject.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new InternalObject(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						line[2], //name
						line[3], //display
						line[4], //tileEntityID
						Integer.parseInt(line[5]), //renderID
						line[6] //itemID
				));
	}

	public InternalObject(final int[] version, final String gameID, final String name, final String display, final String tileEntityID, final int renderID, final String itemID) {
		super(version, gameID, name);
		this.display = display;
		if (tileEntityID != null)
			this.tileEntityGameID = tileEntityID;
		else
			this.tileEntityGameID = "NA";
		this.renderID = renderID; //0 means standard block

		if (itemID != null)
		{
			this.itemID = itemID;
			this.itemName = name + " Item";
		}
		else
		{
			this.itemID = "NA";
			this.itemName = "NA";
		}
	}

	@Override
	public ItemStack getItemStack(int size) {
		throw new Error("Not implemented");
	}
}