package edu.utd.minecraft.mod.polycraft.config;

public class Compound extends Entity {

	public static final EntityRegistry<Compound> registry = new EntityRegistry<Compound>();

	// from oil and gas
	// public static final Compound acetylene = registry.register(new Compound("Acetylene", true));
	public static final Compound alkydResin = registry.register(new Compound("Alkyd Resin", true));
	public static final Compound benzene = registry.register(new Compound("Benzene", true));
	public static final Compound benzoicAcid = registry.register(new Compound("Benzoic Acid", true));
	public static final Compound bitumen = registry.register(new Compound("Bitumen", false));
	public static final Compound btx = registry.register(new Compound("BTX", true));
	public static final Compound butadiene = registry.register(new Compound("Butadiene", true));
	// public static final Compound butane = registry.register(new Compound("Butane", true));
	public static final Compound caprolactum = registry.register(new Compound("Caprolactum", true));
	public static final Compound cl2 = registry.register(new Compound("Cl2", true));
	public static final Compound diesel = registry.register(new Compound("Diesel", true));
	public static final Compound dimethylTerephthalate = registry.register(new Compound("Dimethyl Terephthalate", true));
	public static final Compound edc = registry.register(new Compound("EDC", true));
	public static final Compound ethane = registry.register(new Compound("Ethane", true));
	public static final Compound ethylbenzene = registry.register(new Compound("Ethylbenzene", true));
	public static final Compound ethylene = registry.register(new Compound("Ethylene", true));
	public static final Compound ethyleneGlycol = registry.register(new Compound("Ethylene Glycol", true));
	public static final Compound ethyleneOxide = registry.register(new Compound("Ethylene Oxide", true));
	public static final Compound gasOil = registry.register(new Compound("Gas Oil", true));
	public static final Compound h2 = registry.register(new Compound("H2", true));
	public static final Compound h2o = registry.register(new Compound("H2O", true));
	public static final Compound hcl = registry.register(new Compound("HCL", true));
	public static final Compound isophthalicAcid = registry.register(new Compound("Isophthalic Acid", true));
	// public static final Compound kerosene = registry.register(new Compound("Kerosene", true));
	public static final Compound metaXylene = registry.register(new Compound("Meta-Xylene", true));
	// public static final Compound methane = registry.register(new Compound("Methane", true));
	// public static final Compound methanol = registry.register(new Compound("Methanol", true));
	public static final Compound naphtha = registry.register(new Compound("Naphtha", true));
	public static final Compound naturalGas = registry.register(new Compound("Natural Gas", true));
	public static final Compound olefins = registry.register(new Compound("Olefins ", true));
	public static final Compound orthoXylene = registry.register(new Compound("Ortho-Xylene", true));
	// public static final Compound paraffin = registry.register(new Compound("Paraffin", true));
	public static final Compound paraXylene = registry.register(new Compound("Para-Xylene", true));

	// public static final Compound propane = registry.register(new Compound("Propane", true));
	public static final Compound propylene = registry.register(new Compound("Propylene", true));
	public static final Compound styrene = registry.register(new Compound("Styrene", true));
	public static final Compound sulfuricAcid = registry.register(new Compound("Sulfuric Acid ", true));
	public static final Compound terephthalicAcid = registry.register(new Compound("Terephthalic Acid", true));
	// public static final Compound toluene = registry.register(new Compound("Toluene", true));
	public static final Compound tolueneDiisocyanate = registry.register(new Compound("Toluene Diisocyanate", true));
	public static final Compound vinylChloride = registry.register(new Compound("Vinyl Chloride ", true));
	// public static final Compound xylene = registry.register(new Compound("Xylene", true));

	// necessary for polymers - must find ways to craft
	// public static final Compound phenol = registry.register(new Compound("Phenol", true));
	// public static final Compound urea = registry.register(new Compound("Urea", true));
	public static final Compound melamine = registry.register(new Compound("Melamine", true));
	public static final Compound formaldehyde = registry.register(new Compound("Formaldehyde", true));
	public static final Compound phenolFormaldehyde = registry.register(new Compound("Phenol formaldehyde", true));
	public static final Compound ureaFormaldehyde = registry.register(new Compound("Urea formaldehyde", true));
	public static final Compound melamineFormaldehyde = registry.register(new Compound("Melamine formaldehyde", true));

	public static final Compound celluloseNitrate = registry.register(new Compound("cellulose nitrate", true));
	public static final Compound celluloseAcetate = registry.register(new Compound("cellulose acetate", true));
	public static final Compound adipicAcid = registry.register(new Compound("adipic acid", true));
	public static final Compound _1_6_hexamethylenediamine = registry.register(new Compound("1,6-hexamethylenediamine", true));
	public static final Compound diamine = registry.register(new Compound("diamine", true));
	public static final Compound dicarboxyllicAcid = registry.register(new Compound("Dicarboxyllic acid", true));
	public static final Compound bisphenolA = registry.register(new Compound("Bisphenol A", true));
	public static final Compound phosgene = registry.register(new Compound("Phosgene", true));

	// Compounds added as most useful compounds around
	public static final Compound _1_1_1_2tetrafluoroethane = registry.register(new Compound("1,1,1,2tetrafluoroethane", true, "CF3CH2F", "—",
			"Most canned air products contain this gas under pressure. Read the labels as some contain other solvents."));
	public static final Compound _1_1_1trichloroethane = registry.register(new Compound("1,1,1trichloroethane", true, "CCl3CH3", "cleaning fluid, degreaser", "hardware store"));
	public static final Compound _2_propanol = registry.register(new Compound("2-propanol", true, "CH3CHOHCH3", "rubbing alcohol",
			"Rubbing alcohol (isopropyl alcohol) is a concentrated solution of 2-propanol and may be found in most drug stores. Supermarkets owned by Safeway carry a 99% pure version."));
	public static final Compound Acetic_acid = registry.register(new Compound("Acetic acid", true, "CH3COOH + H2O", "5% Solution: White vinegar",
			"Grocery store; white vinegar—5% Photography supply store; Indicator Stop Bath—89% (with Bromocresol Purple dye) or Glacial Acetic Acid 99.5–100%"));
	public static final Compound Acetone = registry.register(new Compound("Acetone", true, "CH3COCH3", "Acetone", "General; nail polish remover"));
	public static final Compound Acetylene = registry.register(new Compound("Acetylenegas", true, "C2H2", "Ethyne", "Hardware store or Welding supply; acetylene"));
	public static final Compound Acetylsalicylic_acid = registry.register(new Compound("Acetylsalicylic acid", true, "C9H8O4", "Aspirin", "Pharmacy; Aspirin"));
	public static final Compound Aluminium = registry.register(new Compound("Aluminium", true, "Al", "Aluminium", "General; aluminium foil"));
	public static final Compound Aluminium_ammonium_sulfate = registry.register(new Compound("Aluminium ammonium sulfate", true, "AlNH4(SO4)2", "ammonium alum", "General; styptic pencils for shaving"));
	public static final Compound Aluminium_hydroxide = registry.register(new Compound("Aluminium hydroxide", true, "Al(OH)3", "alumina hydrate", "General; antacid tablets—blended with magnesium hydroxide"));
	public static final Compound Aluminium_oxide = registry.register(new Compound("Aluminium oxide", true, "Al2O3", "alumina", "Welding/Industrial supply; sandblasting sand—graded sizes of crystals"));
	public static final Compound Aluminium_potassium_sulfate = registry.register(new Compound("Aluminium potassium sulfate", true, "KAl(SO4)212H2O", "Alum, potassium aluminium sulphate", "General; astringent to shrink mucus membranes"));
	public static final Compound Aluminium_sulfate = registry.register(new Compound("Aluminium sulfate", true, "Al2(SO4)3", "Flocculating Powder", "Pool supply; by name"));
	public static final Compound Ammonia = registry.register(new Compound("Ammonia", true, "NH3(aq); NH4OH", "Ammonia,Ammonium Hydroxide", "Grocery store; household ammonia—ammonium hydroxide—10% solution."));
	public static final Compound Ammonium_bicarbonate = registry.register(new Compound("Ammonium bicarbonate", true, "(NH4)HCO3", "Salt of Hartshorn,Baker's Ammonia", "Scandinavian baking ingredient"));
	public static final Compound Ammonium_bifluoride = registry.register(new Compound("Ammonium bifluoride", true, "NH4HF2", "Ammonium hydrogen fluoride", "Hardware store; toilet bowl cleaner, rust stain remover"));
	public static final Compound Ammonium_bromide = registry.register(new Compound("Ammonium bromide", true, "NH4Br", "—", "Photography store/Darkroom; bleach bath for photograph development"));
	public static final Compound Ammonium_carbonate = registry.register(new Compound("Ammonium carbonate", true, "(NH4)2CO3", "Smelling Salts,Salt of Hartshorn,Baker's Ammonia", "Grocery/Drug store; smelling salts (first-aid section)"));
	public static final Compound Ammonium_chloride = registry.register(new Compound("Ammonium chloride", true, "NH4Cl", "Soldering Flux", "Hardware store (plumbing); Salt of Ammon, Sal Ammoniac (soldering flux/soldering iron cleaner)"));
	public static final Compound Ammonium_nitrate = registry.register(new Compound("Ammonium nitrate", true, "NH4NO3", "Ammonium Nitrate", "Garden supply; Nitrate of Ammonia (fertilizer)"));
	public static final Compound Ammonium_persulfate = registry.register(new Compound("Ammonium persulfate", true, "(NH4)2S2O8", "Etching Solution,Ammonium Peroxodisulphate", "Electronics supply; circuit board etching solution"));
	public static final Compound Ammonium_phosphate = registry.register(new Compound("Ammonium phosphate", true, "(NH4)3PO4", "Fertilizer", "Garden/Agricultural supply; by name"));
	public static final Compound Ammonium_sulfate = registry.register(new Compound("Ammonium sulfate", true, "(NH4)2SO4", "—", "Garden/Agricultural supply; fertilizer or pH adjuster for soil"));
	public static final Compound Amylose = registry.register(new Compound("Amylose", true, "(C6H9O5)n", "Cornstarch", "Grocery store; corn starch"));
	public static final Compound Anthocyanin = registry.register(new Compound("Anthocyanin", true, "—", "—", "Extraction from Red Cabbage: Cut, boil, then filter"));
	public static final Compound Argon_gas = registry.register(new Compound("Argon gas", true, "Ar", "—", "Welding supply; by name"));
	public static final Compound Ascorbic_acid = registry.register(new Compound("Ascorbic acid", true, "C6H8O6", "Vitamin C", "General; Vitamin C tablets"));
	public static final Compound barium_carbonate = registry.register(new Compound("barium carbonate", true, "BaCO3", "—", "Difficult to find. Sometimes available from pottery and ceramic supply stores."));
	public static final Compound barium_sulfate = registry
			.register(new Compound(
					"barium sulfate",
					true,
					"BaSO4",
					"Lithopone",
					"Available from fine art supply houses in a compound with zinc oxide. This compound is known as Lithipone pigment. Also available in very pure form from radiology labs and medical supply as a radio contrast agent for x-rays and CAT scans—here it may be scented or even flavoured. May be available from a well-stocked pottery and ceramics supply store."));
	public static final Compound benzoyl_peroxide = registry
			.register(new Compound(
					"benzoyl peroxide",
					true,
					"C14H10O4",
					"benzoic acid peroxide",
					"Concentrations as high as 10% are available in the US at drug stores in cream form for treatment of acne. A paste containing 50% benzoyl peroxide, is available from some hardware stores under the brand name Minwax High Performance Wood Filler."));
	public static final Compound bismuth = registry
			.register(new Compound(
					"bismuth",
					true,
					"Bi",
					"Lead-free shot/sinkers",
					"Available as a non-toxic lead replacement for bullets, shot, and fishing sinkers. Unfortunately, not as inexpensive as lead. Also, when sold as a lead replacement it is generally alloyed with tin. Note that to make bismuth crystals you will need very pure bismuth metal."));
	public static final Compound bismuth_subsalicylate = registry.register(new Compound("bismuth subsalicylate", true, "C7H5BiO4", "Pepto-Bismol", "The active ingredient in Pepto-bismol and Kaopectate."));
	public static final Compound boric_acid = registry.register(new Compound("boric acid", true, "H3BO3", "Ant/Roach Killer", "Grocery, drug store, hardware stores; pest control section."));
	public static final Compound bromthymol_blue = registry.register(new Compound("bromthymol blue", true, "C27H28Br2O5S", "pH Test", "Aquarium pH test kits often employ bromthymol blue because it changes color in the 6.0–7.6 range."));
	public static final Compound butane = registry
			.register(new Compound("butane", true, "C4H10", "Butane",
					"The lighter fluid in hand-held fire starters or cigarette lighters is usually liquid butane. Also available in sporting goods & camping stores as a stove fuel. Many similar fuels use propane, so be sure to read the labeling."));
	public static final Compound cadmium_sulfide = registry.register(new Compound("cadmium sulfide", true, "CdS", "Cadmium Yellow",
			"Fine art supply house. Available as a yellow powder. Sometimes pure, sometimes blended with zinc sulfide to create alternative hues."));
	public static final Compound caffeine = registry.register(new Compound("caffeine", true, "C8H10N4O2", "No-Doz", "Grocery or drug store. Often in formulation with corn starch as a binder."));
	public static final Compound calcium_carbide = registry.register(new Compound("calcium carbide", true, "CaC2", "—",
			"Still used by cavers and spelunkers to generate acetylene gas for miner's lamps. May be found through a local caver organization (grotto) or at some non-chain camping/outdoor supply stores."));
	public static final Compound calcium_carbonate = registry.register(new Compound("calcium carbonate", true, "CaCO3", "Limestone, Carbonate of Lime", "Chunks: Marble, limestone. Powder: Precipitated chalk."));
	public static final Compound calcium_chloride = registry.register(new Compound("calcium chloride", true, "CaCl2", "Laundry Aid/Road Salt/De-Icer",
			"Supermarket, Hardware store; much road salt (de-icer) used to de-ice roads in cold climates."));
	public static final Compound calcium_hydroxide = registry.register(new Compound("calcium hydroxide", true, "Ca(OH)2", "Slaked Lime,garden lime", "Gardening department; used to reduce acidity in soil."));
	public static final Compound calcium_hypochlorite = registry
			.register(new Compound("calcium hypochlorite", true, "Ca(ClO)2", "—",
					"Bleaching powder and some swimming pool disinfectants contain calcium hypochlorite. Available from cleaning or swimming pool supply companies. Keep in mind that some formulations contain cyanuric acid as a stabilizer against UV light."));
	public static final Compound calcium_nitrate = registry.register(new Compound("calcium nitrate", true, "Ca(NO3)2·4H2O", "Norwegian saltpeter", "Available from garden supply and agricultural supply stores as a fertilizer."));
	public static final Compound calcium_oxide = registry.register(new Compound("calcium oxide", true, "CaO", "Lime, Quicklime", "Builder's supply; used to make plaster."));
	public static final Compound calcium_phosphate__monobasic = registry.register(new Compound("calcium phosphate, monobasic", true, "Ca(H2PO4)2", "superphosphate",
			"Available as a fertilizer from garden supply stores. Also sometimes available as a nutritional supplement at health food stores."));
	public static final Compound calcium_sulfate_anhydrous = registry.register(new Compound("calcium sulfate anhydrous", true, "CaSO4", "—",
			"Can be made from commonly available plaster of paris by additional heating to above 180C. Also available at fine art supply stores as Terra Alba."));
	public static final Compound calcium_sulfate_dihydrate = registry.register(new Compound("calcium sulfate dihydrate", true, "CaSO4·2H20", "Gypsum", "Can be made from the reaction of Plaster of Paris with Water."));
	public static final Compound calcium_sulfate_hemihydrate = registry.register(new Compound("calcium sulfate,hemihydrate", true, "CaSO4·1/2H20", "Plaster of Paris", "Hardware stores, arts and craft stores, and hobby shops."));
	public static final Compound camphor = registry.register(new Compound("camphor", true, "C10H16O", "—",
			"Can be found as a flavor additive at some Chinese grocery stores. Sometimes found in the drugstore as a cream or oral remedy. It feels cool like menthol on the skin."));
	public static final Compound carbon = registry
			.register(new Compound(
					"carbon",
					true,
					"C",
					"Soot, Graphite,Graphene,Carbon nanotubes,Fullerenes,Diamond,Charcoal",
					"Charcoal briquettes used in cooking are available at the grocery store but contain a variety of additives.Activated charcoal used in fish tank filters and graphiteused in pencil leads are good sources of carbon.Lamp Black artists pigment is pure carbon in a fine powder and is available from fine art supply stores. The anodes in carbon-zinc (heavy-duty) batteries are pure carbon. Coal is not a good source for carbon due to a wide number of impurities and varying grades. Brazing or Gouging rods are available from welding supply stores and are composed of graphite. Graphite is also available as a common lubricant from hardware stores. Other forms of carbon are occasionally available through online retailers selling to element collectors, eBay, engineering suppliers and industrial suppliers. Pure or near-pure carbon can be produced at home through the reaction of Sucrosewith Sulfuric Acid or the thermal decomposition ofsugars. Diamonds, although prohibitively expensive (except bort or artificial diamonds), can also act as a source of Carbon, such as reducing oxides."));
	public static final Compound carbon_dioxide = registry
			.register(new Compound(
					"carbon dioxide",
					true,
					"CO2",
					"—",
					"Dry ice is available from party stores, supermarkets, refrigeration supply companies and ice cream companies. Nationally distributed by Airgas through Penguin Brand Dry Ice. Gaseous form available in small canisters at sporting goods stores. Used in air guns and in paintball."));
	public static final Compound carbonic_acid = registry.register(new Compound("carbonic acid", true, "H2CO3", "—",
			"Soda water is simply carbonated water, a dilute solution of carbonic acid. A Soda Club home soda maker is a good way to make your own carbonic acid in varying strengths."));
	public static final Compound carrageenan = registry.register(new Compound("carrageenan", true, "—", "—",
			"Available in some health food stores as a food additive. Used in many commercial food preparations as a thickener and gelling agent. Available at fine art supply stores for use in molding and marbling."));
	public static final Compound cellulose = registry.register(new Compound("cellulose", true, "(C6H10O5)n", "—",
			"Processed cotton fiber is almost pure cellulose making many undyed clothing articles an excellent source of cellulose. 100% cotton paper is also a good source of this material."));
	public static final Compound chromium_oxide = registry
			.register(new Compound("chromium oxide", true, "Cr2O3", "Chrome Green", "Used as a green pigment for fine art paints. It is available as a powder from most fine art supply stores."));
	public static final Compound citric_acid = registry.register(new Compound("citric acid", true, "C6H8O7", "Sour Salt", "Available at the grocery store as Sour Salt. Also used for home soap making and also in photographic development."));
	public static final Compound copper = registry
			.register(new Compound(
					"copper",
					true,
					"Cu",
					"—",
					"Electrical wire, copper pipe, and copper sheeting are available at hardware stores and are almost 100% pure copper. American pennies (minted 1944–1946, 1962–1982) are 95% copper and 5% zinc. Pennies minted after 1982 are only copper clad."));
	public static final Compound copper_naphthenate = registry
			.register(new Compound(
					"copper naphthenate",
					true,
					"Cu(C11H10O2)2",
					"cupric naphthenate",
					"Used as a wood preservative to protect lumber from termites, ants, and other burrowing insects. Brands including Wolman and Jasco are up to 25% copper naphthenate with the remainder composed of linseed oil and petroleum distillate-based solvents."));
	public static final Compound copper_sulfate_pentahydrate = registry.register(new Compound("copper sulfate pentahydrate", true, "CuSO25H2O", "cupric sulfate, blue vitriol, bluestone",
			"Hardware store; rootkiller (Zep Root Kill, Rooto Root Kill)"));
	public static final Compound copper_sulfate_anhydrous = registry.register(new Compound("copper sulfate(anhydrous)", true, "CuSO4", "cupric sulfate(anhydrous)",
			"Available from garden supply or pond supply stores as an algaecide and rootkiller."));
	public static final Compound cyanuric_acid = registry.register(new Compound("cyanuric acid", true, "C3H3N3O3", "isocyanuric acid",
			"Available from swimming pool and lawn/yard supply stores as a chlorine stabilizer. A pure tablet form sells under the brand name Kem Tek."));
	public static final Compound dextrose = registry.register(new Compound("dextrose", true, "C6H12O6", "glucose, corn syrup", "Grocery; corn syrup, some throat lozenges are pure dextrose."));
	public static final Compound dichloromethane = registry
			.register(new Compound(
					"dichloromethane",
					true,
					"CH2Cl2",
					"methylene chloride",
					"Used as a solvent, degreaser and adhesive remover, it is available from hardware, automotive, and sometimes craft stores. Purity of commercial products can approach 90% with the remainder being composed of alcohols and petroleum distillates. Common brands include Champion Paint Off, many choke and valve cleaners, and Klean Strip Graffiti Remover."));
	public static final Compound dimethyl_sulfoxide = registry
			.register(new Compound(
					"dimethyl sulfoxide",
					true,
					"(CH3)2SO",
					"DMSO",
					"Available from health food and naturopathic supply stores for treating injuries. Warning: DMSO will allow anything it has dissolved to pass directly through your skin into your bloodstream. Use gloves and skin protection when working with this chemical."));
	public static final Compound disodium_phosphate = registry.register(new Compound("disodium phosphate", true, "Na2HPO4", "pH Down", "Found at pet stores and used to lower the pH of aquarium water."));
	public static final Compound ethanol_ethyl_alcohol = registry.register(new Compound("ethanol/ethyl alcohol", true, "C2H5OH", "Everclear",
			"Available as 95% pure ethanol and 5% water from liquor stores in most states. Also available in denaturedform from hardware stores. Grocery and drug stores may carry it as ethyl rubbing alcohol."));
	public static final Compound ether = registry.register(new Compound("ether", true, "(C2H5)2O", "diethyl ether,ethoxyethane",
			"Sold at auto supply stores in aerosol cans as Starter Fluid or Cold Start Spray. Used to help start damp or inefficient old engines."));
	public static final Compound ethylene_glycol = registry.register(new Compound("ethylene glycol", true, "CH2OHCH2OH", "1,2-ethanediol",
			"Some engine antifreezes are primarily ethylene glycol and can be obtained at an automotive supply store."));
	public static final Compound eugenol = registry
			.register(new Compound(
					"eugenol",
					true,
					"CH2CH2CH2C6H3(OCH3)OH",
					"oil of clove, 2-methoxy-4-(2-propenyl)phenol",
					"Available at most health food and natural healing stores as clove oil. Note that organically derived oil of clove will have numerous impurities from the distillation process. Synthetic oil of clove is generally pure but more difficult to find."));
	public static final Compound ferric_chloride = registry.register(new Compound("ferric chloride", true, "FeCl3", "Etching Solution",
			"Available in solution from Radio Shack and other electronics suppliers as an etching solution for circuit boards. Also available in solution from fine art supply stores."));
	public static final Compound ferric_oxide_Iron_III_Oxide = registry.register(new Compound("ferric oxide(Iron III Oxide)", true, "Fe2O3", "Rust",
			"Common rust. Reddish in color. Can be scraped off of rusted iron objects or purchased in a purified form as the artists pigment Red Iron Oxide from fine art supply stores."));
	public static final Compound ferrous_ferric_oxide_Iron_II_III_Oxide = registry.register(new Compound("ferrous ferric oxide (Iron II III Oxide)", true, "Fe3O4", "Magnetite",
			"Used as the artists pigment Mars Black and can be purchased from fine art supply stores."));
	public static final Compound formic_acid = registry.register(new Compound("formic acid", true, "CH2O2", "methanoic acid",
			"Available in some stain removers at the hardware store. A fairly pure 5% concentration can be obtained from the blue chamber of Clorox Dual Action Toilet Bowl Cleanser."));
	public static final Compound fructose = registry.register(new Compound("fructose", true, "C6H12O6", "Fruit Sugar", "Easily available from most grocery stores."));
	public static final Compound glucose = registry.register(new Compound("glucose", true, "C6H12O6", "Dextrose, corn syrup", "Grocery; corn syrup. Some throat lozenges are pure dextrose."));
	public static final Compound glycerin_glycerol = registry.register(new Compound("glycerin, glycerol", true, "C3H8O3", "1,2,3-propanetriol",
			"Glycerin is an emollient used to soften skin by delaying the evaporation of water. It is available at most drug stores."));
	public static final Compound gold = registry
			.register(new Compound(
					"gold",
					true,
					"Au",
					"—",
					"Gold jewelry is generally not pure gold. Gold is generally alloyed with other metals to increase strength. Gold coins are often .999 fine gold and many may be obtained at bullion value from local coin shops. Pure gold dust and gold foils are available from fine art supply stores."));
	public static final Compound gum_Arabic = registry.register(new Compound("gum Arabic", true, "CH4O3S", "gum acacia",
			"Available at arts & crafts stores and at fine art supply stores as a binder for turning pigments into paints. In solution with water."));
	public static final Compound helium = registry
			.register(new Compound(
					"helium",
					true,
					"He",
					"—",
					"Helium can be obtained from party stores or wherever helium balloons are available. Small disposable containers are available for long-term storage. Containers made by Worthington Industries are 99% pure helium. Note that balloon gas is a mixture of helium and normal air."));
	public static final Compound hexamine = registry.register(new Compound("hexamine", true, "C6H12N4", "Hexamethylenetetramine", "Sold as solid fuel tablets in camping and outdoor stores, in combination with 1,3,5-trioxane."));
	public static final Compound hydrochloric_acid = registry.register(new Compound("hydrochloric acid", true, "HCl", "Masonry Cleaner,Muriatic acid",
			"Hardware store; used in swimming pool maintenance, as masonry cleaner and in silverware cleaner."));
	public static final Compound hydrofluoric_acid = registry.register(new Compound("hydrofluoric acid", true, "HF", "Glass Etching Solution", "Hardware store; Aluminum brightener & tire cleaner"));
	public static final Compound hydrogen_gas = registry
			.register(new Compound(
					"hydrogen gas",
					true,
					"H2",
					"Hydrogen",
					"Can be generated and captured from electrolysis of water and a wide variety of inorganic chemical reactions. Adding aluminium foil to a solution of Sodium Hydroxide will generate hydrogen gas. Highly flammable and explosive under pressure or in quantity."));
	public static final Compound hydrogen_peroxide = registry
			.register(new Compound(
					"hydrogen peroxide",
					true,
					"H2O2",
					"Peroxide",
					"Hydrogen peroxide antiseptic (3%)or even (6%) is available from the drug store. Clairoxide hair bleach by Clairol is much more concentrated (12%) and is available from beauty supply stores. Concentrations as high as 35% may be available at health food stores and natural medicine stores as a source of natural oxygen. Swimming pool and spa suppliers often carry concentrations as high as 27% but these usually contain preservatives/stabilizers. Note: Do not heat. Do not store in metal containers."));
	public static final Compound hydroquinone = registry.register(new Compound("hydroquinone", true, "C6H6O2", "—", "Photography store catering to the at-home darkroom crowd."));
	public static final Compound hypochlorous_acid = registry.register(new Compound("hypochlorous acid", true, "HClO", "Laundry Bleach", "Grocery store."));
	public static final Compound iodine = registry
			.register(new Compound(
					"iodine",
					true,
					"I2",
					"Iodide",
					"Tincture of iodine, a topical antiseptic used for treating wounds, is a solution of iodine dissolved in ethyl alcohol. It is available at most drug stores. Available from camping stores as crystals for sterilizing water. Crystal Iodine can also be synthesized from Potassium Iodide as radiation pills, utilizing laboratory methods. Certain laboratory methods can be considerably hazardous. Iodine is illicitly used in the production of methamphetamine, making iodine somewhat difficult to obtain. In the United States, it is illegal to sell, purchase, transport, or possess iodine with the knowledge or intention of its use for producing methamphetamine. Small quantities, such as those used in chemistry experiments such as the Briggs–Rauscher reaction, are legal to obtain and require no permits nor record-keeping.[3]"));
	public static final Compound iron = registry.register(new Compound("iron", true, "Fe", "—", "Steel wool, iron nails, iron bolts, nuts, screws are good sources of iron."));
	public static final Compound iron_sulfate = registry.register(new Compound("iron sulfate", true, "FeSO4", "Ferrous sulfate, Sulfate of iron, Copperas",
			"Available from garden supply stores as a nutrient. Also available as a mordant for pigments from stores that carry dyeing and textile supplies."));
	public static final Compound kerosene = registry.register(new Compound("kerosene", true, "CnH2n+1(n=12–16)", "Aviation Fuel",
			"Lamp oil or kerosene is sold in the paint departments of most hardware stores. Also available in larger quantities from gas stations in many parts of the country."));
	public static final Compound lactic_acid = registry.register(new Compound("lactic acid", true, "C3H6O3", "Milk Acid", "Available from grocery stores and health food stores."));
	public static final Compound latex = registry.register(new Compound("latex", true, "C3H6O3", "Liquid Latex Body Paint",
			"Available from adult toy stores as a novelty item and from select hobby shops. Compositions vary in the amount of ammonia and fillers they contain. Also costume/magic shops, as liquid skin/liquid latex."));
	public static final Compound lead = registry
			.register(new Compound(
					"lead",
					true,
					"Pb",
					"—",
					"Lead shot and lead sinkers are used by fishermen and are available at sporting goods stores. They may be alloyed with tin andantimony. Also lead is used for wheel balancing weights at auto garages. Lead sheeting may be obtained from stores that carry roofing supplies."));
	public static final Compound linseed_oil = registry
			.register(new Compound("linseed oil", true, "—", "—",
					"Used as a binder in paints and as a finish for wood. Available at hardware stores and fine art supply stores. Boiled linseed oil contains Lead Oxide (boiled with PbO), which speeds hardening (oxidizing) on contact with oxygen."));
	public static final Compound Lithium = registry.register(new Compound("Lithium", true, "Li", "—", "Lithium can be obtained from Lithium batteries.[4]"));
	public static final Compound magnesium = registry.register(new Compound("magnesium", true, "Mg", "Fire starter",
			"Sold in solid blocks as a fire starter in many camping and outdoor stores. Also used in the construction of metal bodied pencil sharpeners made by the company KUM of Germany."));
	public static final Compound magnesium_carbonate = registry
			.register(new Compound(
					"magnesium carbonate",
					true,
					"MgCO3",
					"Athlete's chalk",
					"Often sold as a powder in sporting goods stores for gymnastics, weightlifting, and climbing to help keep hands dry. Sometimes it is combined with calcium carbonate in this use. Available in tablet form as an antacid. Some antacids are pure while others are blended with calcium carbonate."));
	public static final Compound magnesium_chloride = registry.register(new Compound("magnesium chloride", true, "MgCl2", "Nigari or Lushui",
			"Asian markets; Nigari, as it's called in Japan, or Lushui, as it's called in China. Used to make tofu from soy milk."));
	public static final Compound magnesium_hydroxide = registry.register(new Compound("magnesium hydroxide", true, "Mg(OH)2", "Milk of Magnesia",
			"Milk of Magnesia is an antacid used to settle sour (acidic) stomachs. Some antacid tablets also contain magnesium hydroxide."));
	public static final Compound magnesium_silicate = registry.register(new Compound("magnesium silicate", true, "Mg3Si4O10(OH)2", "Talc",
			"Talcum powder comes from talc, the softest of all minerals, and is used as a dusting powder for babies. It is available in the body care section of the drug store."));
	public static final Compound magnesium_sulfate = registry.register(new Compound("magnesium sulfate", true, "MgSO47H2O", "Epsom Salt",
			"Drug store; epsom salt is sold as a laxative or as an anti-inflammatory soak. Also available as a chemical for photographic development."));
	public static final Compound manganese_dioxide = registry.register(new Compound("manganese dioxide", true, "MnO2", "Pyrolusite",
			"Hardware store; available as the black powder in unused alkaline or carbon-zinc batteries. By extensively washing this powder in distilled water you can remove the embedded Zinc Chloride solution."));
	public static final Compound mercury = registry
			.register(new Compound(
					"mercury",
					true,
					"Hg",
					"quicksilver",
					"Becoming difficult to find due to fears of toxicity and environmental contamination. Old mercury switches can be found in many thermostats. Old-style thermometers contain mercury. Fluorescent light tubes contain a small amount of mercury though it can be difficult to extract. Specialty occult stores may carry it as quicksilver. Ben-Wha balls at sex toy shops."));
	public static final Compound methane = registry.register(new Compound("methane", true, "CH4", "Natural Gas", "Gas line in many homes."));
	public static final Compound methanol = registry.register(new Compound("methanol", true, "CH3OH", "Methyl Alcohol",
			"Methanol is sold as a solvent in paint supply stores under the names wood alcohol or methyl alcohol. Available from automotive supply as a gasoline line anti-freeze."));
	public static final Compound methyl_ethyl_ketone = registry.register(new Compound("methyl ethyl ketone", true, "CH3COC2H5", "MEK", "Available from most hardware stores as a solvent."));
	public static final Compound methyl_ethyl_ketone_peroxide = registry.register(new Compound("methyl ethyl ketone peroxide", true, "C8H16O4", "MEKP",
			"Available in dilute solution from hardware and boating supply stores as a catalyst for the polymerization of polyester resins."));
	public static final Compound methyl_isobutyl_ketone = registry.register(new Compound("methyl isobutyl ketone", true, "CH3COCH2CH(CH3)2", "MIBK", "Available from many hardware stores as a solvent."));
	public static final Compound methyl_salicylate = registry.register(new Compound("methyl salicylate", true, "C6H4(OH)COOCH3", "Oil of Wintergreen", "Drug store"));
	public static final Compound methylene_blue = registry.register(new Compound("methylene blue", true, "C16H18ClN3S", "Methidote",
			"Methylene blue (Methidote antiseptic) is used to treat small injured fish and is available at pet stores."));
	public static final Compound methylene_chloride = registry
			.register(new Compound(
					"methylene chloride",
					true,
					"CH2Cl2",
					"dichloromethane",
					"Used as a solvent, degreaser and adhesive remover, it is available from hardware, automotive, and sometimes craft stores. Purity of commercial products can approach 90% with the remainder being composed of alcohols and petroleum distillates. Common brands include Champion Paint Off, many choke and valve cleaners, and Klean Strip Graffiti Remover."));
	public static final Compound mineral_oil = registry.register(new Compound("mineral oil", true, "complex mixture of hydrocarbons", "—",
			"Mineral oil is sold in drug stores as an emollient. Some baby oils are essentially mineral oil and fragrance."));
	public static final Compound monosodium_glutamate = registry.register(new Compound("monosodium glutamate", true, "C5H8NNaO4", "MSG",
			"Widely used as a condiment in many parts of Asia. Can be purchased from Chinese or Japanese grocery stores. Also easily acquired in many supermarkets as a flavor enhancer."));
	public static final Compound naphthalene = registry.register(new Compound("naphthalene", true, "C10H8", "moth balls", "Hardware store, grocery store."));
	public static final Compound nickel = registry
			.register(new Compound(
					"nickel",
					true,
					"Ni",
					"—",
					"Pre-1985 Canadian nickels are made of nickel. Note: American nickels are composed of 25% nickel and 75% copper. Nickel is often used as a welding filler material, and 1/16–1/8 Ni wire is often available at welding supply stores. Silver center of Euro coins is pure Nickel."));
	public static final Compound nitrogen = registry.register(new Compound("nitrogen", true, "N2", "liquid nitrogen",
			"Hospital, laboratory, university, welding supply house, and industrial gas supplier can all provide liquid or gaseous nitrogen."));
	public static final Compound nitrous_oxide = registry
			.register(new Compound(
					"nitrous oxide",
					true,
					"N2O",
					"whipping gas,laughing gas",
					"Whipped cream canisters from the grocery store. Or in larger canisters from commercial restaurant supply houses. When packaged for automotive use it is usually contaminated with hydrogen sulfide to discourage inhalation. Industrial (welding supply) Nitrous Oxide contains NO and NO2 (can be removed with moist steel wool)which makes Nitric acid in your lungs so use with good ventilation. (Heating Ammonium Nitrate generates an industrial grade of Nitrous Oxide at the risk of explosion.)"));
	public static final Compound oxalic_acid = registry.register(new Compound("oxalic acid", true, "C2H2O4", "rust remover, non-chlorine bleach powder cleanser", "Grocery, drug, hardware stores. Bar Keeper's Friend."));
	public static final Compound oxygen = registry.register(new Compound("oxygen", true, "O2", "—",
			"Portable welding oxygen tanks are available at welding shops and some hardware stores. Can be generated in small quantities from the electrolysisof water."));
	public static final Compound para_dichlorobenzene = registry.register(new Compound("para-dichlorobenzene", true, "C6H4Cl2", "moth flakes, moth crystals", "Hardware or grocery store."));
	public static final Compound paraffin = registry
			.register(new Compound(
					"paraffin",
					true,
					"CnH2n+2 (n>19)",
					"—",
					"Unless otherwise labeled, candle wax is made of paraffin. Some grocery stores sell paraffin as a sealant for home canning. Many arts and crafts stores sell paraffin for at-home candle makers. Vaseline brand is also paraffin, albeit with molecules that are slightly shorter."));
	public static final Compound phenol = registry.register(new Compound("phenol", true, "C6H5OH", "—", "Phenol can be found in small amounts (~1–2%) in analgesic sore throat spray."));
	public static final Compound phenol_red = registry.register(new Compound("phenol red", true, "C19H19SO5", "phenolsulfonephthalein", "Used as a pH tester for swimming pools. Available at swimming pool supply stores."));
	public static final Compound phenolphthalein = registry.register(new Compound("phenolphthalein", true, "C20H14O4", "—",
			"Magic shops might carry it as a pinkish disappearing ink. Blue disappearing ink is not phenolphthalein but thymolphthalein. Difficult to find outside of chemical supply."));
	public static final Compound phosphoric_acid = registry
			.register(new Compound(
					"phosphoric acid",
					true,
					"H3PO4",
					"pH Down",
					"Some pH reducers (available at pet stores) used in fish tanks are simply dilute solutions (up to 30%) of phosphoric acid. Also available in gel form at hardware stores as a rust remover. Naval Jelly in a spray bottle at hardware stores. Also used as asolder flux."));
	public static final Compound polystyrene = registry.register(new Compound("polystyrene", true, "—", "casting resin", "Hobby shop"));
	public static final Compound polyurethane_foam = registry.register(new Compound("polyurethane foam", true, "—", "Mountains in Minutes", "Hobby shop, hardware, or building supply store."));
	public static final Compound potassium_aluminium_sulfate = registry
			.register(new Compound("potassium aluminium sulfate", true, "KAl(SO4)2·12H2O", "Alum, aluminium potassium sulfate",
					"It is used as an astringent to shrinkmucous membranes. Some styptic pencils use it in their formulation. Often sold as a deodorant stone at natural health stores. Also available as a mordant for clothing dyes (RIT brand and others)."));
	public static final Compound potassium_bitartrate = registry.register(new Compound("potassium bitartrate", true, "KHC4H4O6", "Cream of Tartar",
			"Cream of Tartar is available at the grocery store and is used to stabilize delicate foods like meringue toppings and other baked egg-white products."));
	public static final Compound potassium_bromide = registry.register(new Compound("potassium bromide", true, "KBr", "—", "Photography store catering to the at-home darkroom crowd."));
	public static final Compound potassium_carbonate = registry.register(new Compound("potassium carbonate", true, "K2CO3", "Potash", "Available at some garden supply and agricultural stores."));
	public static final Compound potassium_chloride = registry.register(new Compound("potassium chloride", true, "KCl", "Salt substitute",
			"Available as a salt substitute by people who must limit their sodium intake, and is available at most supermarkets. Also available in larger, though slightly less pure, quantities as a road salt de-icer."));
	public static final Compound potassium_chromium_sulfate = registry.register(new Compound("potassium chromium sulfate", true, "KCr(SO4)2·12H20", "potassium chrome alum", "Photography store catering to the at-home darkroom crowd."));
	public static final Compound potassium_dichromate = registry.register(new Compound("potassium dichromate", true, "K2Cr2O7", "—",
			"Photography store catering to the at-home darkroom crowd. Also used as a mordant (Chrome Mordant) for textiles and may be available at a dyeing and fabric store."));
	public static final Compound potassium_hydroxide = registry
			.register(new Compound(
					"potassium hydroxide",
					true,
					"KOH",
					"Caustic potash",
					"Available at some ceramic supply houses and from arts and crafts stores for soap making. Difficult to find. May be the active ingredient in some drain cleaning solutions. Kodak SII Activator is 10% Potassium Hydroxide with various sulfites and bromides in the mixture. Veterinary supply houses may carry it for dissolving hair and horns in animals."));
	public static final Compound potassium_iodide = registry.register(new Compound("potassium iodide", true, "KI", "Radiation Pills",
			"Available sometimes as a photographic development chemical. Also from outdoor/survivalist stores in pill form to avoid the uptake of radioactive iodine during a nuclear accident."));
	public static final Compound potassium_iron_hexacyanoferrate = registry.register(new Compound("potassium iron (ii) hexacyanoferrate(iii)", true, "KFe[Fe(CN)6]", "potassium iron ferricyanide, potassium ferroferricyanide",
			"Mrs. Stewart's liquid laundry bluing is used to whiten clothes and may be found in the detergents section of the supermarket."));
	public static final Compound potassium_metabisulfite = registry.register(new Compound("potassium metabisulfite", true, "K2S3O5", "Campden tablets",
			"Used as a sterilizing agent in winemaking and brewing processes. Sold as tablets which may be pure, blended with sodium metabisulfite, or contain only sodium metabisulfite."));
	public static final Compound potassium_nitrate = registry.register(new Compound("potassium nitrate", true, "KNO3", "Saltpeter, saltpetre",
			"Supermarket; saltpeter or quick salt is used to cure home-made sausagesand corned beef. May be available at independent butcher shops."));
	public static final Compound potassium_permanganate = registry
			.register(new Compound(
					"potassium permanganate",
					true,
					"KMnO4",
					"Clearwater",
					"Clearwater is a solution of approximately 50% potassium permanganate and is used to remove odors and cloudiness from water to be used in aquariums and garden ponds. Also used to treat diseases in fish. Used as ~98% pure powder for treatment of hard well water in water softeners. Potassium Permanganate in a dilute solution form can also be found in certain fish water additive sometimes labelled as Gill, Fungus, Parasite's Special, One sure way to test is to put some onto your skin and leave it there for a while, then see if there's a brownish coloration/stain. The color of the solution is colored as normal permanganate in solution."));
	public static final Compound potassium_sodium_tartrate = registry.register(new Compound("potassium sodium tartrate", true, "NaKC4H4O6 * 4H2O", "Rochelle salt", "Drug store"));
	public static final Compound propane = registry.register(new Compound("propane", true, "C3H8", "—", "Gas barbecue fuel is generally made of propane and is available at many gasoline stations or picnic supply stores."));
	public static final Compound silicon_carbide = registry.register(new Compound("silicon carbide", true, "SiC", "Carborundum",
			"This hard, abrasive powder is often found sorted by particle size for use in sandblasting and industrial supply houses."));
	public static final Compound silicon_dioxide = registry
			.register(new Compound(
					"silicon dioxide",
					true,
					"SiO2",
					"Sand, silica",
					"Quartz sand is relatively pure silicon dioxide and is available at many building supply stores and also aquarium shops. Often found sorted by particle size for use in sandblasting. Micrometre sized particles are used as a filler in rubbers, plastics, and mold making materials and may be available in hobby shops as Min-u-Sil brand."));
	public static final Compound silver = registry.register(new Compound("silver", true, "Ag", "—",
			"Older non-clad American dimes, quarters, half dollars and silver dollars are 90% silver and 10% copper. Pure silver coins and bullion can be obtained from local coin shops, often at bullion value."));
	public static final Compound sodium_acetate = registry.register(new Compound("sodium acetate", true, "NaC2H3O2", "Hand Warmer",
			"Main ingredient in hand-warmers that are available at sporting goods stores. Can also be made through the reaction of baking soda and vinegar."));
	public static final Compound sodium_bicarbonate = registry.register(new Compound("sodium bicarbonate", true, "NaHCO3", "Baking Soda, Bicarbonate of Soda", "Grocery baking section; single action baking soda"));
	public static final Compound sodium_bisulfate = registry.register(new Compound("sodium bisulfate", true, "NaHSO4", "pH Down", "Pool supply store. Crystal Vanish drain cleaner."));
	public static final Compound sodium_borate = registry.register(new Compound("sodium borate", true, "Na2B4O710H2O", "—",
			"The water can be driven off of grocery store Borax by heating to 200C to create pure sodium borate (although it will begin absorbing atmospheric moisture again when cooled and left in the open)."));
	public static final Compound sodium_bromide = registry.register(new Compound("sodium bromide", true, "NaBr", "Hot Tub Salt", "Pool supply store"));
	public static final Compound sodium_carbonate = registry.register(new Compound("sodium carbonate", true, "Na2CO3", "Washing soda,soda ash", "Grocery; Arm & Hammer"));
	public static final Compound sodium_chlorate = registry
			.register(new Compound(
					"sodium chlorate",
					true,
					"NaClO3",
					"—",
					"Available from gardening and agricultural stores. This herbicide is toxic to all green plants and, as such, is becoming less available as more selective herbicides some onto the market. Some brand names are Atlacide, Defol, Drop-Leaf, and Tumbleaf. Read product labeling to deduce the particular purity of any of these brands."));
	public static final Compound sodium_chloride = registry.register(new Compound("sodium chloride", true, "NaCl", "Salt",
			"Grocery; Table salt used in cooking is sodium chloride, usually with anti-caking agents such as magnesium carbonate. Iodized salt contains traces of sodium iodide."));
	public static final Compound sodium_fluoride = registry
			.register(new Compound("sodium fluoride", true, "NaF", "—",
					"Used in toothpastes and mouthwashes to prevent cavities. Difficult to obtain in pure form. Most dental solutions contain a laundry list of inert ingredients to provide coloring, flavor, foaming, and other aesthetic properties."));
	public static final Compound sodium_hexametaphosphate = registry
			.register(new Compound(
					"sodium hexametaphosphate",
					true,
					"(NaPO3)6",
					"Sodium polymetaphosphate,Calgon water softener",
					"This water softener, surfactant and detergent is the primary ingredient in Calgon water softener. It is available at the grocery and hardware store. It is also known by other names including: glassy sodium, Graham's Salt, and hexasodium salt."));
	public static final Compound sodium_hydroxide = registry.register(new Compound("sodium hydroxide", true, "NaOH", "Lye, Caustic Soda",
			"Grocery, hardware; Known also as caustic soda and lye, sodium hydroxide is used in many commercial drain cleaners/openers."));
	public static final Compound sodium_hypochlorite = registry.register(new Compound("sodium hypochlorite", true, "NaClO", "Bleach", "Household bleach is generally a 5% solution of sodium hypochlorite."));
	public static final Compound sodium_metabisulfite = registry.register(new Compound("sodium metabisulfite", true, "Na2S2O5", "Campden tablets",
			"Used as a sterilizing agent in winemaking and brewing processes. Sold as tablets which may be pure, blended withpotassium metabisulfite, or contain onlypotassium metabisulfite."));
	public static final Compound sodium_nitrate = registry.register(new Compound("sodium nitrate", true, "NaNO3", "Chile saltpeter", "Garden supply store"));
	public static final Compound sodium_percarbonate = registry
			.register(new Compound(
					"sodium percarbonate",
					true,
					"2Na2CO3·3H2O2",
					"sodium carbonate peroxyhydrate",
					"A combination of sodium carbonate and hydrogen peroxide in solution, this is often sold as a wood bleach for use on decks and outdoor wood furniture. Available at many hardware and home improvement stores. Sometimes referred to as oxygenated bleach."));
	public static final Compound sodium_phosphate = registry.register(new Compound("sodium phosphate", true, "Na3PO4", "Tri Sodium Phosphate",
			"Tri-sodium phosphate, commonly known as TSP, is available at hardware stores and is used to clean walls prior to painting."));
	public static final Compound sodium_silicate = registry.register(new Compound("sodium silicate", true, "Na2SiO3, Na2Si3O7", "water glass, egg preserver, magic rocks",
			"Can be found at some hardware stores and drug stores. Available from ceramic suppliers as a flocculent in clay slip casting. Non-phosphate TSP substitute. Liquid sold with muffler bandage at auto stores."));
	public static final Compound sodium_sulfate = registry.register(new Compound("sodium sulfate", true, "Na2SO4", "—", "Photography store catering to the at-home darkroom crowd."));
	public static final Compound sodium_sulfite = registry.register(new Compound("sodium sulfite", true, "Na2SO3", "—", "Photography store catering to the at-home darkroom crowd."));
	public static final Compound sodium_tetraborate_decahydrate = registry.register(new Compound("sodium tetraborate decahydrate", true, "Na2B4O710H2O", "Borax", "Market, drug store; Borax, such asTwenty-Mule-Team Borax Laundry Booster."));
	public static final Compound sodium_thiosulfate = registry.register(new Compound("sodium thiosulfate", true, "Na2S2O3", "Hypo",
			"Photographer's hypo is used in photograph development and is available at photography supply stores. Also available from pet shops as a de-chlorination additive to fish water."));
	public static final Compound stearic_acid = registry.register(new Compound("stearic acid", true, "C17H35CO2H", "candle hardener", "Some arts and crafts stores will carry this item. It is used by do-it-yourself candle makers."));
	public static final Compound Steel = registry
			.register(new Compound(
					"Steel",
					true,
					"Fe alloyed with C and depending on type, possibly also Ni, B, V, Cr among others",
					"Galvanised wire, nail (fasteners),stainless steel, ball bearings, steel wool",
					"Steel, although commonly used in industry and construction, is often unavailable in pure form (other than a few types such of mild steel, galvanised steel, stainless steel and carbon steel that are used for amateur metalworking) toamateur scientists. Otherwise, they will have to make do with galvanised or stainless steel materials such as nails, screws, tools, cutlery, steel wool, ball bearings, steel girders and steel wire. Due to the variety of steel types, carboncontent will widely vary and can contain many other elements such as Nickel,Vanadium, Chromium, Silicon, Boron,Zinc, Aluminium, Titanium, Manganese,Tungsten; these alloys all have different names and are sporadically available in different areas and retailers. Steel of various types can also be plated with Zincor Tin to prevent corrosion or Gold to make it look precious"));
	public static final Compound sucrose = registry.register(new Compound("sucrose", true, "C12H22O11", "Sugar", "Grocery; Table sugar is available in large or small bags and in a variety of particle sizes."));
	public static final Compound sulfur = registry.register(new Compound("sulfur (sulphur)", true, "S", "—",
			"Flowers of sulfur are sold at some garden stores to treat certain plant diseases. Found at most Home Depot stores as Organic Garden sulfur. Also available at many drug stores."));
	public static final Compound sulfuric_acid = registry.register(new Compound("sulfuric acid", true, "H2SO4", "vitriol, oil of vitriol", "Hardware store; battery acid. May also be obtained at some auto supply stores."));
	public static final Compound tannic_acid = registry.register(new Compound("tannic acid", true, "C76H52O46", "tannin", "Arts and crafts store in the dyes and fabric section. Some photography stores and drug stores may carry it."));
	public static final Compound tartaric_acid = registry.register(new Compound("tartaric acid", true, "C4H6O6", "wine preservative", "Sold in wine-making supply stores and sometimes in the grocery store."));
	public static final Compound tetrachloroethylene = registry
			.register(new Compound(
					"tetrachloroethylene",
					true,
					"C2Cl4",
					"tetrachloroethene",
					"The most common solvent for dry cleaning, it is available in aerosol form from hardware, auto supply, and home improvement stores. Brands like Lectra Motive Auto Care and Gummout Professional Brake Parts Cleaner contain as much as 99% with methylene chloride as the only major contaminant."));
	public static final Compound thiourea = registry.register(new Compound("thiourea", true, "CS(NH2)2", "Thiocarbamide",
			"Many silver cleaning products including Hagerty and Tarn-X contain thiourea as the only active ingredient. Other brands may use other active compounds in combination."));
	public static final Compound thymolphthalein = registry.register(new Compound("thymolphthalein", true, "C28H30O4", "disappearing ink", "It is the blue disappearing ink available at magic supply stores."));
	public static final Compound titanium_dioxide = registry.register(new Compound("titanium dioxide", true, "TiO2", "Titanium White", "Available as a fine powder for use as a pure white pigment from fine art supply stores."));
	public static final Compound toluene = registry.register(new Compound("toluene", true, "C6H5CH3", "Methylbenzene,cellulose thinner", "Available from most hardware stores as a solvent."));
	public static final Compound town_gas = registry.register(new Compound("town gas", true, "A mixture mainly consists ofhydrogen,carbon monoxideandmethane", "Nil.",
			"Available from households in urban area. Toxic and explosive. The carbon monoxide can be removed from the mixture by passing the gas through large quantity of citrated chicken blood."));
	public static final Compound trichloroethylene = registry.register(new Compound("trichloroethylene", true, "CHClCCl2", "trichloroethene",
			"Available as a fairly pure aerosol from hardware and auto supply stores. Brand names include Lectra Clean and Trouble Free Rust Buster."));
	public static final Compound tungsten = registry
			.register(new Compound(
					"tungsten",
					true,
					"W",
					"—",
					"The filament in incandescent light bulbs is made of tungsten. Also, small tungsten rods are available at welding supply stores. They are sold as TIG electrodes, and they commonly come in 1/16, 3/32, and 1/8 diameters. Request pure tungsten, as they also come alloyed with 2% Thorium,Lanthanum and other elements. Lead free fishing sinkers are sometimes tungsten."));
	public static final Compound turpentine = registry.register(new Compound("turpentine", true, "—", "—", "Available at hardware stores and fine art supply stores."));
	public static final Compound urea = registry.register(new Compound("urea", true, "H2NCONH2", "ice melter,fertilizer", "Available at some hardware stores and at garden supply stores. Bonide Azalea fertilizer is 99% urea."));
	public static final Compound water = registry
			.register(new Compound(
					"water",
					true,
					"H2O",
					"—",
					"Distilled (pure) water, is available in most grocery stores. This and deionized water are sufficient for most chemical reactions. Spring water or tap water have numerous minerals in solution. For certain, extremely sensitive experiments, contamination from plasticizers in the bottle may affect your reactions."));
	public static final Compound xylene = registry.register(new Compound("xylene", true, "C8H10", "dimethylbenzene", "Available at hardware stores, paint stores, and fine art supply stores."));
	public static final Compound zinc = registry
			.register(new Compound(
					"zinc",
					true,
					"Zn",
					"zinc",
					"Recent American pennies are 95% zinc with a 5% copper coating Cent (United States coin). Galvanized coating on steel. Inside of carbon-zinc batteries. Found in some hardware stores as moss strip for rooftop. The case of carbon-zinc batteries is zinc. Galvanizing paint contains powdered Zinc in paint, separate with paint thinner."));
	public static final Compound zinc_chloride = registry.register(new Compound("zinc chloride", true, "ZnClf + H2O", "Tinner's Fluid, killed acid", "Hardware store; used for cleaning copper."));
	public static final Compound zinc_oxide = registry.register(new Compound("zinc oxide", true, "ZnO", "pompholyx", "Found in opaque sunscreens for its UV opacity. Chinese White fine arts pigment is very pure."));
	public static final Compound zinc_sulfate = registry.register(new Compound("zinc sulfate", true, "ZnSO4", "zinc sulfate", "Moss Killer for use on roofs and concrete. (Moss killer for grass lawns is Ferrous Ammonium Sulfate.)"));

	public static final Compound coke = registry.register(new Compound("coke", false));
	public static final Compound carbon_monoxide = registry.register(new Compound("Carbon Monoxide", true));

	public final boolean fluid;
	public final Plastic plastic;

	public Compound(final String name, final Plastic _plastic) {
		super("compound_" + name.toLowerCase().replaceAll(" ", "_"), name);
		this.plastic = _plastic;
		this.fluid = false;
	}

	public Compound(final String name, final boolean fluid) {
		super("compound_" + name.toLowerCase().replaceAll(" ", "_"), name);
		this.fluid = fluid;
		this.plastic = null;
	}

	public Compound(final String name, final boolean fluid, final String formula, final String uses, final String sources) {
		super("compound_" + name.toLowerCase().replaceAll(" ", "_"), name);
		this.fluid = fluid;
		this.plastic = null;
	}
}