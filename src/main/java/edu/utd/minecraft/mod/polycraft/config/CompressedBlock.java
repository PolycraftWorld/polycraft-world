package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.block.material.MapColor;

public class CompressedBlock extends Entity {

	public static final EntityRegistry<CompressedBlock> registry = new EntityRegistry<CompressedBlock>();

	public static final CompressedBlock carbon = registry.register(new CompressedBlock(Ingot.carbon, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock magnesium = registry.register(new CompressedBlock(Ingot.magnesium, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock aluminum = registry.register(new CompressedBlock(Ingot.aluminium, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock titanium = registry.register(new CompressedBlock(Ingot.titanium, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock manganese = registry.register(new CompressedBlock(Ingot.manganese, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock cobalt = registry.register(new CompressedBlock(Ingot.cobalt, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock copper = registry.register(new CompressedBlock(Ingot.copper, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock palladium = registry.register(new CompressedBlock(Ingot.palladium, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock antimony = registry.register(new CompressedBlock(Ingot.antimony, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock platinum = registry.register(new CompressedBlock(Ingot.platinum, MapColor.ironColor, 3, 7, 9));
	public static final CompressedBlock steel = registry.register(new CompressedBlock(Ingot.steel, MapColor.ironColor, 3, 7, 9));

	public final Entity type;
	public final MapColor color;
	public final float hardness;
	public final float resistance;
	public final int itemsPerBlock;

	public CompressedBlock(final Entity type, final MapColor color, final float hardness, final float resistance, final int itemsPerBlock) {
		super("compressed_" + type.gameName, type.name);
		this.type = type;
		this.color = color;
		this.hardness = hardness;
		this.resistance = resistance;
		this.itemsPerBlock = itemsPerBlock;
	}

	@Override
	public String export(final String delimiter) {
		return String.format("%2$s%1$s%3$s.%4$s%1$s%5$s%1$s%6$s%1$s%7$d",
				delimiter, super.export(delimiter), type.getClass().getSimpleName(), type.name.toLowerCase(), hardness, resistance, itemsPerBlock);
	}

	public static String generate(final String[] entity) {
		final String[] params = new String[] { entity[1], "MapColor.ironColor", entity[2] + "f", entity[3] + "f", entity[4] };
		return Entity.generate(CompressedBlock.class.getSimpleName(), getVariableName(entity[0]), params);
	}
}