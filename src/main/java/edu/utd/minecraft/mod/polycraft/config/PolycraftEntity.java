package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import net.minecraft.item.ItemStack;

public class PolycraftEntity extends GameIdentifiedConfig {

	//Behaviors
	public static final String HOSTILE = "Hostile";
	public static final String PASSIVE = "Passive";
	public static final String CUSTOM = "Custom";

	//RenderTypes
	public static final String HUMANOID = "Humanoid";
	public static final String MODEL = "Model";

	public static final ConfigRegistry<PolycraftEntity> registry = new ConfigRegistry<PolycraftEntity>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolycraftEntity.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final PolycraftEntity polycraftEntity = registry.register(new PolycraftEntity(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						Integer.parseInt(line[2]), //entityID
						line[3], //name
						Integer.parseInt(line[4]), //renderID
						Integer.parseInt(line[5]), //length
						Integer.parseInt(line[6]), //width
						Integer.parseInt(line[7]), //height
						line[8], //Entity Type
						line[9], //Render Type
						line.length > 10 ? line[10].split(",") : null, //paramNames
						line, 11 //params
				));
			}
	}

	public final int entityID;
	public final int length, width, height;
	public final int renderID;
	public final String entityType;
	public final String renderType;

	public PolycraftEntity(final int[] version, final String gameID, final int entityID, final String name, final int renderID, final int length, final int width, final int height,
			final String entityType, final String renderType,
			final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, gameID, name, paramNames, paramValues, paramsOffset);
		this.entityID = entityID;
		this.renderID = renderID;
		this.length = length;
		this.width = width;
		this.height = height;
		this.entityType = entityType;
		this.renderType = renderType;

	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size);
	}
}