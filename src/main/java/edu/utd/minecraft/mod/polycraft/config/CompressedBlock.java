package edu.utd.minecraft.mod.polycraft.config;

public class CompressedBlock extends SourcedEntity {

	public static final EntityRegistry<CompressedBlock> registry = new EntityRegistry<CompressedBlock>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, CompressedBlock.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new CompressedBlock(
					line[0],
					Entity.find(line[1], line[2]) //source
			));
	}

	public CompressedBlock(final String name, final Entity source) {
		super(name, source);
	}
}