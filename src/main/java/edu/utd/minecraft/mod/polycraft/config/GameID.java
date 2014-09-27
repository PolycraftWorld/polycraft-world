package edu.utd.minecraft.mod.polycraft.config;

public enum GameID {
	MoldedRunningShoes("E"),
	MoldScubaFins("G"),
	MoldScubaMask("H"),
	MoldLifePreserver("J"),
	InventoryTreeTap("3h"),
	InventoryMachiningMill("3i"),
	InventoryExtruder("3j"),
	InventoryInjectionMolder("3k"),
	InventoryDistillationColumn("3E"),
	InventorySteamCracker("3D"),
	InventoryMeroxTreatmentUnit("5N"),
	InventoryChemicalProcessor("3J"),
	InventoryFueledLamp("3V"),
	InventorySpotlight("41"),
	InventoryOilDerrick("3K"),
	CustomBucketOil("3m"),
	CustomFlameThrower("3n"),
	CustomFlashlight("3o"),
	CustomJetPack("3p"),
	CustomParachute("5a"),
	CustomPhaseShifter("3r"),
	CustomScubaTank("3x"),
	CustomKevlarVest("5b"),
	InternalOil("1D"),
	InventoryIndustrialOven("3F"),
	InventoryPlasticChest("3L"),
	InventoryCondenser("20"),
	InventoryFlowRegulator("5j"), 
	InventoryPump("4i"),
	CustomHeatedKnifeDiamondPolyIsoPrene("114"),
	CustomHeatedKnifeDiamondPolyPropylene("115"),
	CustomHeatedKnifeDiamondPEEK("116"),
	CustomHeatedKnifeStainlessPolyIsoPrene("117"),
	CustomHeatedKnifeStainlessPolyPropylene("118"),
	CustomHeatedKnifeStainlessPEEK("119");
	

	private final String id;

	private GameID(final String id) {
		this.id = id;
	}

	public boolean matches(final GameIdentifiedConfig config) {
		return config.gameID.equals(id);
	}
}
