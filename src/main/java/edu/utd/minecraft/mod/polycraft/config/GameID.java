package edu.utd.minecraft.mod.polycraft.config;

public enum GameID {
	MoldRunningShoes("E"),
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
	InventoryContactPrinter("1ee"),
	InventoryFloodlight("3V"),
	InventorySpotlight("41"),
	InventoryGaslamp("1bW"),
	InventoryOilDerrick("3K"),
	CustomBucketOil("3m"),
	CustomFlameThrower("3n"),
	CustomFlameTosser("134"),
	CustomFlameChucker("135"),
	CustomFlameHurler("136"),
	CustomFreezeRayBeginner("1bO"),
	CustomFreezeRayIntermediate("1bP"),
	CustomFreezeRayAdvanced("1bQ"),
	CustomFreezeRayPro("1bR"),
	CustomWaterCannonBeginner("1bS"),
	CustomWaterCannonIntermediate("1bT"),
	CustomWaterCannonAdvanced("1bU"),
	CustomWaterCannonPro("1bV"),
	CustomFlashlight("3o"),
	CustomSiliconBoule("1et"),
	CustomJetPackBeginner("3p"),
	CustomJetPackIntermediate("19P"),
	CustomJetPackAdvanced("19Q"),
	CustomJetPackPro("19R"),
	CustomParachute("5a"),
	CustomPhaseShifter("3r"),
	CustomScubaTankBeginner("3x"),
	CustomScubaTankIntermediate("13a"),
	CustomScubaTankAdvanced("13b"),
	CustomScubaTankPro("13c"),
	InternalOil("1D"),
	InternalBlockPipe("11f"),
	InternalItemPipe("11g"),
	InternalBlockCollision("1bA"),
	InternalItemCollision("1bB"),
	InventoryIndustrialOven("3F"),
	InventoryPlasticChest("3L"),
	InventoryPortalChest("1ed"),
	InventoryCondenser("20"),
	InventorySolarArray("1el"),
	InventoryFlowRegulator("5j"),
	InventoryPump("4i"),
	InventoryMaskWriter("1eu"),
	InventoryTradingHouse("1fo"),
	InventoryTerritoryFlag("1fq"),
	InventoryPrintingPress("1fr"),
	InventoryCHEM2323("1gm"),
	InventoryComputer("1hb"),
	InventoryHospital("1hd"),
	InventoryFluorescentLamp("1xl"),
	CustomHeatedKnifeDiamondPolyIsoPrene("114"),
	CustomHeatedKnifeDiamondPolyPropylene("115"),
	CustomHeatedKnifeDiamondPEEK("116"),
	CustomHeatedKnifeStainlessPolyIsoPrene("117"),
	CustomHeatedKnifeStainlessPolyPropylene("118"),
	CustomHeatedKnifeStainlessPEEK("119"),
	CustomRunningShoesSprinter("11q"),
	CustomScubaMaskLightBeginner("13e"),
	CustomScubaMaskLightIntermediate("13f"),
	CustomScubaMaskLightAdvanced("13g"),
	CustomScubaMaskLightPro("13h"),
	MoldedItemScubaMaskBeginner("3w"),
	MoldedItemScubaMaskIntermediate("2V"),
	MoldedItemScubaMaskAdvanced("12T"),
	MoldedItemScubaMaskPro("2W"),
	CustomCarbonFiberWeave("1bz"),
	CustomCoinCopper("1eN"),
	CustomBarCopper("1eO"),
	CustomStackCopper("1eP"),
	CustomTroveCopper("1bY"),
	Custom254nmUVLight("1eF"),
	Custom365nmUVLight("1eG"),
	CustomVoiceCone("1eQ"),
	CustomMegaphone("1eR"),
	CustomHAMRadio("1eS"),
	CustomWalkyTalky("1eT"),
	CustomCellPhone("1eU"),
	CustomSmartPhone("1eq"),
	CustomFlagEmpty("1bZ"),
	CustomBillStackEmpty("1fx"),
	CustomAirQualityDetecctor("1fv"),
	CustomRipstopNylonSheet("1fw"),
	FluorescentBulbs("1xn"),
	EntityTerritoryFlag("1hg"),
	EntityResearchAssistant("1hf"), 
	EntityOilSlime("1hi"),
	EntityDummy("1hj"),
	CustomOilSlimeBall("1hl"),
	EntityOilSlimeBall("1hm"),
	PasswordDoor("1hY");
	
	public final String id;

	private GameID(final String id) {
		this.id = id;
	}

	public boolean matches(final GameIdentifiedConfig config) {
		return config.gameID.equals(id);
	}

	public boolean matches(final int inputId) {
		return String.valueOf(inputId).equals(id);
	}

}
