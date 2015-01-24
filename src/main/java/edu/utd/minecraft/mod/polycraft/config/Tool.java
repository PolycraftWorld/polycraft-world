package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Tool extends Config {

	public enum Type {
		HOE("Hoe"),
		SWORD("Sword"),
		SHOVEL("Shovel"),
		PICKAXE("Pickaxe"),
		AXE("Axe");
		
		private final String name;
		
		/**
		 * @return the friendly name of the type
		 */
		public String getName() {
			return this.name;
		}
		
		private Type(final String name) {
			this.name = name;
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
						Integer.parseInt(line[13]), //enchantability
						Integer.parseInt(line[14]), //underwaterBuff
						Integer.parseInt(line[15]) //ladderBuff
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
	//TODO actually use these (Walter)
	public final int underwaterBuff;
	public final int ladderBuff;

	public Tool(
			final int[] version, final String[] typeGameIDs,
			final String craftingHeadItemName, final String craftingShaftItemName,
			final String name,
			final int harvestLevel, final int maxUses, final int efficiency,
			final int damage, final int enchantability, final int underwaterBuff, final int ladderBuff) {
		super(version, name);
		this.typeGameIDs = typeGameIDs;
		this.craftingHeadItemName = craftingHeadItemName;
		this.craftingShaftItemName = craftingShaftItemName;
		this.harvestLevel = harvestLevel;
		this.maxUses = maxUses;
		this.efficiency = efficiency;
		this.damage = damage;
		this.enchantability = enchantability;
		this.underwaterBuff = underwaterBuff;
		this.ladderBuff = ladderBuff;
	}
	
	public String getFullTypeName(final Type type) {
		return name + " " + type.getName();
	}

	public String getTexture(final Type type) {
		return PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(getFullTypeName(type)));
	}
}