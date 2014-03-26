package edu.utd.minecraft.mod.polycraft.config;

public class Polymer extends Compound {

	public enum Category {
		None,
		Polyolefin,
		Polyaldehyde,
		Cellulosic,
		Polyamide,
		Polyester,
		Polyaramid,
		Polycarbonate,
		Polyether,
		Polyphenol
	}

	public enum ResinCode {
		NONE(0, ""),
		PET(1, "Polyethylene terephthalate"),
		HDPE(2, "High-density polyethylene"),
		PVC(3, "Polyvinyl chloride"),
		LDPE(4, "Low-density polyethylene"),
		PP(5, "Polypropylene"),
		PS(6, "Polystyrene"),
		O(7, "Other");

		public final int recyclingNumber;
		public final String name;

		private ResinCode(final int recyclingNumber, final String name) {
			this.recyclingNumber = recyclingNumber;
			this.name = name;
		}
	}

	public static final EntityRegistry<Polymer> registry = new EntityRegistry<Polymer>();

	/*******************************************************************************************************************************************
	 * Recycling Plastics - Plastic #1 - Plastic #7
	 *********************************************************************************************************************************************/

	/*
	 * USES: (PET) automobile tire yarns, conveyor belts and drive belts, reinforcement for fire and garden hoses, seat belts (an application in which it has largely replaced nylon), nonwoven fabrics for stabilizing drainage ditches,
	 * culverts, and railroad beds, and nonwovens for use as diaper top sheets and disposable medical garments. recycled bottles for beverages
	 */
	public static final Polymer PET = registry.register(new Polymer("poly ethylene terephthalate (PET)", Category.Polyester, ResinCode.PET));

	/*
	 * USES: (HDPE) -
	 */
	public static final Polymer HDPE = registry.register(new Polymer("high density polyethylene (HDPE)", Category.Polyolefin, ResinCode.HDPE));

	/*
	 * USES: (PVC) -
	 */
	public static final Polymer PVC = registry.register(new Polymer("polyvinyl chloride (PVC)", Category.Polyolefin, ResinCode.PVC));

	/*
	 * USES: (LDPE) -
	 */
	public static final Polymer LDPE = registry.register(new Polymer("low density polyethylene (LDPE)", Category.Polyolefin, ResinCode.LDPE));

	/*
	 * USES: (PP) -
	 */
	public static final Polymer PP = registry.register(new Polymer("polypropylene (PP)", Category.Polyolefin, ResinCode.PP));

	/*
	 * USES: (PS) -
	 */
	public static final Polymer PS = registry.register(new Polymer("polystyrene (PS)", Category.Polyolefin, ResinCode.PP));

	/*
	 * USES: (PC) -
	 */
	public static final Polymer PC = registry.register(new Polymer("polycarbonate (PC)", Category.Polycarbonate, ResinCode.O));

	/*******************************************************************************************************************************************
	 * Polyolefins - Plastic #8
	 *********************************************************************************************************************************************/
	// ETHYLENE-PROPYLENE COPOLYMERS - USES: automobile parts, impact modifier for PP, flexible seals, wire and cable insulation, weather stripping, tire sidewalls, hoses, roofing film
	public static final Polymer EPM = registry.register(new Polymer("ethylene-propylene monomer (EPM)", Category.Polyolefin));
	public static final Polymer EPDM = registry.register(new Polymer("ethylene-propylene-diene monomer (EPM)", Category.Polyolefin));

	public static final Polymer PMMS = registry.register(new Polymer("poly m-methyl styrene (PMMS)", Category.Polyolefin));
	public static final Polymer PPMS = registry.register(new Polymer("poly p-methyl styrene (PPMS)", Category.Polyolefin));
	public static final Polymer PAN = registry.register(new Polymer("polyacrylonitrile (PAN)", Category.Polyolefin));

	// isobutylene rubbers - USES: rubber tires, bladders, innertubes, anything to hold hot steam
	public static final Polymer PIB = registry.register(new Polymer("poly isobutylene (PIB)", Category.Polyolefin));
	public static final Polymer IIR = registry.register(new Polymer("isobutylene-isoprene rubber (IIR)", Category.Polyolefin));
	public static final Polymer BIIR = registry.register(new Polymer("bromine isobutylene-isoprene rubber (BIIR)", Category.Polyolefin));
	public static final Polymer CIIR = registry.register(new Polymer("chlorine isobutylene-isoprene rubber (CIIR)", Category.Polyolefin));

	/*******************************************************************************************************************************************
	 * Polyaldehydes - Plastic #9
	 *********************************************************************************************************************************************/
	public static final Polymer phenolicResin = registry.register(new Polymer("phenolic resin (phenol formaldehydes)", Category.Polyaldehyde));
	// USES: (phenol-formaldehyde) darker in color... appliance handles, wood adhesives, particle board adhesive, exterior wood
	public static final Polymer UFP = registry.register(new Polymer("urea-formaldehyde polymers (UFP)", Category.Polyaldehyde));
	// USES: (urea-formaldehyde) lighter in color... appliance handles, wood adhesives, particle board adhesive, interior wood
	public static final Polymer MFP = registry.register(new Polymer("melamine-formaldehyde polymers (MFP)", Category.Polyaldehyde));
	// USES: (melamine-formaldehyde) lighter in color... decorative dinner ware, countertops, coatings

	/*******************************************************************************************************************************************
	 * Cellulosic Polymers - Plastic #10
	 *********************************************************************************************************************************************/

	public static final Polymer cellulosics = registry.register(new Polymer("cellulosics", Category.Cellulosic));
	// USES: (cellulosics) cotton, flax, hemp, kapok, sisal, jute, ramie,
	public static final Polymer lignins = registry.register(new Polymer("lignins", Category.Cellulosic));

	public static final Polymer CDAP = registry.register(new Polymer("Cellulose diacetate (CDAP)", Category.Cellulosic));
	// USES: (CDAP) cigarette filters, protective goggles, tool handles, oil gauges, toothbrushes and eyeglass frames
	public static final Polymer CTAP = registry.register(new Polymer("Cellulose triacetate (CTAP)", Category.Cellulosic));
	// USES: (CTAP) clothes, ease of washing and drying, less wrinkling, graceful drape

	/*******************************************************************************************************************************************
	 * Polyamides - Plastic #11
	 *********************************************************************************************************************************************/

	public static final Polymer PHMA = registry.register(new Polymer("poly hexamethylene adipamide (Nylon 6,6) (PHMA)", Category.Polyamide));
	public static final Polymer PHMS = registry.register(new Polymer("poly hexamethylene sebacamide (Nylon 6,10) (PHMS)", Category.Polyamide));

	/*******************************************************************************************************************************************
	 * Polyesters - Plastic #12
	 *********************************************************************************************************************************************/

	// degradable polyesters
	public static final Polymer PLLA = registry.register(new Polymer("poly(l-lactic acid) (PLLA)", Category.Polyester));
	public static final Polymer PLGA = registry.register(new Polymer("poly(lactic-co-glycolic acid) (PLGA)", Category.Polyester));
	public static final Polymer PHB = registry.register(new Polymer(" poly-2-hydroxy butyrate (PHB)", Category.Polyester));
	public static final Polymer PCL = registry.register(new Polymer(" polycaprolactone (PCL)", Category.Polyester));
	public static final Polymer PHBV = registry.register(new Polymer("poly hydroxybutyrate-co-hydroxyvalerate (PHBV)", Category.Polyester));

	// alkyd resins - network polyesters - dicarboxylic acids or their anhydrides and polyfunctional alcohols such as glycerol
	public static final Polymer alkydResins = registry.register(new Polymer("Alkyd Resins", Category.Polyester));

	public static final Polymer PBT = registry.register(new Polymer("poly butylene terephthalate", Category.Polyester));

	/*******************************************************************************************************************************************
	 * Polyaramids - Plastic #13
	 *********************************************************************************************************************************************/

	public static final Polymer PEEK = registry.register(new Polymer("poly ether ether ketone (PEEK)", Category.Polyaramid));
	public static final Polymer PI = registry.register(new Polymer("polyimide (PI)", Category.Polyaramid));

	/*******************************************************************************************************************************************
	 * Polycarbonate - Plastic #14
	 *********************************************************************************************************************************************/

	/*******************************************************************************************************************************************
	 * Polyether - Plastic #15
	 *********************************************************************************************************************************************/

	public static final Polymer PEG = registry.register(new Polymer("polyethylene glycol (PEG)", Category.Polyether));
	public static final Polymer PPG = registry.register(new Polymer("polypropylene glycol (PPG)", Category.Polyether));
	public static final Polymer POM = registry.register(new Polymer("Poly oxymethylene (POM)", Category.Polyether));
	public static final Polymer PEO = registry.register(new Polymer("polyethylene oxide (PEO)", Category.Polyether));
	public static final Polymer PTMG = registry.register(new Polymer("poly tetramethylene glycol (PTMG)", Category.Polyether));
	public static final Polymer PTMEG = registry.register(new Polymer("poly tetramethylene ether glycol (PTMEG)", Category.Polyether));

	/*******************************************************************************************************************************************
	 * Polyphenols - Plastic #16
	 *********************************************************************************************************************************************/

	public static final Polymer polyphenol = registry.register(new Polymer("polyphenol", Category.Polyphenol));
	public static final Polymer PPO = registry.register(new Polymer("poly phenylene oxide (PPO)", Category.Polyphenol));
	public static final Polymer PPS = registry.register(new Polymer("poly p-phenylene sulphide (PPS)", Category.Polyphenol));
	public static final Polymer PPTA = registry.register(new Polymer("poly p-phenylene terephthalamide (PPTA)", Category.Polyphenol));
	public static final Polymer PMIA = registry.register(new Polymer("poly m-phenylene isophthalamide (PMIA)", Category.Polyphenol));

	/*******************************************************************************************************************************************
	 * Polyacrylates - Plastic #17
	 *********************************************************************************************************************************************/

	public static final Polymer PMA = registry.register(new Polymer("poly(methyl acrylate) (PMA)", Category.Polyphenol));
	public static final Polymer PMMA = registry.register(new Polymer("poly(methyl methacrylate) (PMMA)", Category.Polyphenol));
	public static final Polymer PEA = registry.register(new Polymer("poly(ethyl acrylate) (PeA)", Category.Polyphenol));
	public static final Polymer PnBA = registry.register(new Polymer("poly(n-butyl acrylate) (PnBA)", Category.Polyphenol));
	public static final Polymer PtBA = registry.register(new Polymer("poly(tert-butyl acrylate) (PtBA)", Category.Polyphenol));
	public static final Polymer PiBA = registry.register(new Polymer("poly(iso-butyl acrylate) (PiBA)", Category.Polyphenol));
	public static final Polymer PIBoA = registry.register(new Polymer("poly(iso-borynl acrylate) (PIBoA)", Category.Polyphenol));
	public static final Polymer P2HEMA = registry.register(new Polymer("poly 2-hydroxyethyl methacrylate (PHEMA)", Category.Polyphenol));
	public static final Polymer PMCA = registry.register(new Polymer("poly methyl cyanoacrylate (PMCA)", Category.Polyphenol));
	public static final Polymer polyacrylateElastomers = registry.register(new Polymer("polyacrylate elastomers", Category.Polyphenol));

	/*******************************************************************************************************************************************
	 * Fluoropolymers - Plastic #18
	 *********************************************************************************************************************************************/

	public static final Polymer PTFE = registry.register(new Polymer("polytetrafluoroethylene", Category.Polyphenol));
	public static final Polymer PVDF = registry.register(new Polymer("poly vinylidene fluoride (PVDF)", Category.Polyphenol));
	public static final Polymer PVDF_TrFE = registry.register(new Polymer("poly vinylidene fluoride-trifluoroethylene (PVDF-TrFE)", Category.Polyphenol));
	public static final Polymer PCTFE = registry.register(new Polymer("poly chlorotrifluoroethylene (PCTFE)", Category.Polyphenol));
	public static final Polymer PVF = registry.register(new Polymer("poly vinyl fluoride (PVF)", Category.Polyphenol));

	/*******************************************************************************************************************************************
	 * Vinyl polymers - Plastic #19
	 *********************************************************************************************************************************************/

	public static final Polymer PVDC = registry.register(new Polymer("poly vinylidene dichloride (PVDC)", Category.Polyphenol));
	public static final Polymer PVAC = registry.register(new Polymer("poly vinyl acetate (PVAC)", Category.Polyphenol));
	public static final Polymer PVA = registry.register(new Polymer("poly vinyl alcohol (PVA)", Category.Polyphenol));
	public static final Polymer PVB = registry.register(new Polymer("poly vinyl butyral (PVB)", Category.Polyphenol));
	public static final Polymer PVFo = registry.register(new Polymer("poly vinyl formal (PVFo)", Category.Polyphenol));
	public static final Polymer PVME = registry.register(new Polymer("poly vinyl methyl ether (PVME)", Category.Polyphenol));

	/*******************************************************************************************************************************************
	 * Diene polymers - Plastic #20
	 *********************************************************************************************************************************************/

	// diene polymers
	public static final Polymer PChl = registry.register(new Polymer("polychloroprene (PChl)", Category.Polyphenol));
	public static final Polymer polyisoprene = registry.register(new Polymer("natural rubber (polyisoprene)", Category.Polyphenol));
	public static final Polymer polybutadiene = registry.register(new Polymer("polybutadiene", Category.Polyphenol));
	public static final Polymer PBR = registry.register(new Polymer("polybutadiene rubber (PBR)", Category.Polyphenol));
	public static final Polymer ABS = registry.register(new Polymer("acrylonitrile-butadiene-styrene (ABS)", Category.Polyphenol));
	public static final Polymer SBR = registry.register(new Polymer("styrene-butadiene rubber (SBR)", Category.Polyphenol));
	// USES: (SBR) all purpose - most important synthetic rubber in world, carpet backing, flooring, belting, wire insulation, footwear, latex
	public static final Polymer NBR = registry.register(new Polymer("nitrile-butadiene rubber (NBR)", Category.Polyphenol));
	public static final Polymer HNBR = registry.register(new Polymer("hydrogenated nitrile-butadiene rubber (HNBR)", Category.Polyphenol));

	/*******************************************************************************************************************************************
	 * Styrenics - Plastic #21
	 *********************************************************************************************************************************************/

	// styrene block copolymers - USES: hot melt adhesives, shoes and to improve bitumen
	// public static final Polymer SBR = registry.register(new Polymer("styrene-butadiene rubber (SBR)", Category.Polyphenol));
	// public static final Polymer ABS = registry.register(new Polymer("acrylonitrile-butadiene-styrene (ABS)", Category.Polyphenol));
	public static final Polymer SBS = registry.register(new Polymer("styrene-butadiene-styrene (SBS)", Category.Polyphenol));
	public static final Polymer SIS = registry.register(new Polymer("styrene-isoprene-styrene (SIS)", Category.Polyphenol));
	public static final Polymer SAN = registry.register(new Polymer("styrene-acrylonitrile (SAN)", Category.Polyphenol));
	// USES: (SAN) battery cases, automotive parts, kitchen ware, appliances, furniture, medical supplies
	public static final Polymer SMAC = registry.register(new Polymer("styrene-maleic anhydride copolymer (SMAC)", Category.Polyphenol));
	// USES: (SMAC) food service trays

	/*******************************************************************************************************************************************
	 * As of yet unlabeled plastics - Complex polymers
	 *********************************************************************************************************************************************/

	public static final Polymer polyurethane = registry.register(new Polymer("polyurethane"));
	public static final Polymer PB = registry.register(new Polymer("poly 1-butene"));

	public static final Polymer PPE = registry.register(new Polymer("poly 2,6-dimethyl-1,4-phenylene ether"));
	public static final Polymer AF = registry.register(new Polymer("acrylic-formaldehude"));
	public static final Polymer silicone = registry.register(new Polymer("silicone"));
	public static final Polymer polysaccharide = registry.register(new Polymer("polysaccharide"));
	public static final Polymer PTA = registry.register(new Polymer("poly thiazyl (PTA)"));
	public static final Polymer PPHD = registry.register(new Polymer("poly pentamethylene hexamethylene dicarbamate (PPHD)"));
	public static final Polymer PPOX = registry.register(new Polymer("poly propylene oxide (PPOX)"));
	public static final Polymer PEI = registry.register(new Polymer("poly etherimide (PEI)"));
	public static final Polymer PEHD = registry.register(new Polymer("Poly ethylene hexamethylene dicarbamate (PEHD)"));
	public static final Polymer PES = registry.register(new Polymer("poly ethylene sulphide (PES)"));
	public static final Polymer polyol = registry.register(new Polymer("polyol"));
	public static final Polymer epoxyResin = registry.register(new Polymer("epoxy resin"));
	public static final Polymer PDMS = registry.register(new Polymer("polydimethylsiloxane (PDMS)"));
	public static final Polymer polyphosphazene = registry.register(new Polymer("polyphosphazene"));

	/*******************************************************************************************************************************************
	 * Trade names for polymers of all types
	 *********************************************************************************************************************************************/

	public static final Polymer bakelite = registry.register(new Polymer("Bakelite (phenolic resin)"));
	public static final Polymer neoprene = registry.register(new Polymer("Neoprene (PChl)"));
	public static final Polymer kevlar = registry.register(new Polymer("Kevlar (PPTA)"));
	/*
	 * USES: (Kevlar and Nomex) Aramids are not produced in as high a volume as the commodity fibres such as nylon and polyester, but because of their high unit price they represent a large business. End uses for aramids in the home are few
	 * (Nomex-type fibres have been made into ironing-board covers), but industrial uses are increasing (especially for aramids of the Kevlar class) as designers of products learn how to exploit the properties offered by these unusual
	 * materials.
	 * 
	 * Aside from the above-mentioned bulletproof vests, Kevlar and its competitors are employed in belts for radial tires, cables, reinforced composites for aircraft panels and boat hulls, flame-resistant garments (especially in blends
	 * with Nomex), sports equipment such as golf club shafts and lightweight bicycles, and as asbestos replacements in clutches and brakes. Nomex-type fibres are made into filter bags for hot stack gases; clothes for presses that apply
	 * permanent-press finishes to fabrics; dryer belts for papermakers; insulation paper and braid for electric motors; flame-resistant protective clothing for fire fighters, military pilots, and race-car drivers; and V belts and hoses.
	 */
	public static final Polymer dacron = registry.register(new Polymer("Dacron (PET)"));
	public static final Polymer terylene = registry.register(new Polymer("Terylene (PET)"));
	public static final Polymer mylar = registry.register(new Polymer("Mylar (PET)"));
	public static final Polymer melinex = registry.register(new Polymer("Melinex (PET)"));
	public static final Polymer twaron = registry.register(new Polymer("Twaron (PPTA)"));
	public static final Polymer nomex = registry.register(new Polymer("Nomex (PMIA)"));
	public static final Polymer orlon = registry.register(new Polymer("Orlon (PAN)"));
	public static final Polymer rislan = registry.register(new Polymer("Rislan"));
	public static final Polymer technora = registry.register(new Polymer("Technora"));
	public static final Polymer ultem = registry.register(new Polymer("Ultem (PEI)"));
	public static final Polymer vectran = registry.register(new Polymer("Vectran"));
	public static final Polymer viton = registry.register(new Polymer("Viton"));
	public static final Polymer zylon = registry.register(new Polymer("Zylon"));
	public static final Polymer acrilan = registry.register(new Polymer("Acrilan (PAN)"));
	public static final Polymer seetec = registry.register(new Polymer("SEETEC BR (PBR)"));
	public static final Polymer duraflex = registry.register(new Polymer("Duraflex (PB)"));
	public static final Polymer artlex = registry.register(new Polymer("Artlex (PPE)"));
	public static final Polymer dianium = registry.register(new Polymer("Dianium (PPE)"));
	public static final Polymer norpex = registry.register(new Polymer("Norpex (PPE)"));
	public static final Polymer prevex = registry.register(new Polymer("Prevex (PPE)"));
	public static final Polymer victrex = registry.register(new Polymer("Tictrex (PEEK)"));
	public static final Polymer zyex = registry.register(new Polymer("Zyex (PEEK)"));
	public static final Polymer himod = registry.register(new Polymer("Himod (PEEK)"));
	public static final Polymer gatone = registry.register(new Polymer("Gatone (PEEK)"));
	public static final Polymer ketron = registry.register(new Polymer("Ketron (PEEK)"));
	public static final Polymer pebax = registry.register(new Polymer("Pebax (PEI)"));
	public static final Polymer tecapei = registry.register(new Polymer("Tecapei (PEI)"));
	public static final Polymer microthene = registry.register(new Polymer("Microthene (PE)", Category.Polyolefin, ResinCode.LDPE));
	public static final Polymer retain = registry.register(new Polymer("Retain (PE)", Category.Polyolefin, ResinCode.LDPE));
	public static final Polymer flexirene = registry.register(new Polymer("Flexirene (LDPE)", Category.Polyolefin, ResinCode.LDPE));
	public static final Polymer ipethene = registry.register(new Polymer("Ipethene (LDPE)", Category.Polyolefin, ResinCode.LDPE));
	public static final Polymer lacqtene = registry.register(new Polymer("Lacqtene (LDPE)", Category.Polyolefin, ResinCode.LDPE));
	public static final Polymer miralon = registry.register(new Polymer("Miralon (LDPE)", Category.Polyolefin, ResinCode.LDPE));
	public static final Polymer neoZex = registry.register(new Polymer("Neo-zex (MDPE)", Category.Polyolefin, ResinCode.LDPE));
	public static final Polymer eltex = registry.register(new Polymer("Eltex (HDPE)", Category.Polyolefin, ResinCode.HDPE));
	public static final Polymer Finathane = registry.register(new Polymer("Finathane (HDPE)", Category.Polyolefin, ResinCode.HDPE));
	public static final Polymer Fortiflex = registry.register(new Polymer("Fortiflex (HDPE)", Category.Polyolefin, ResinCode.HDPE));
	public static final Polymer g_Lex = registry.register(new Polymer("G_Lex (HDPE)", Category.Polyolefin, ResinCode.HDPE));
	public static final Polymer hival = registry.register(new Polymer("Hival (HDPE)", Category.Polyolefin, ResinCode.HDPE));
	public static final Polymer chirulen = registry.register(new Polymer("Chirulen (UHMWPE)", Category.Polyolefin, ResinCode.HDPE));
	public static final Polymer hostalen = registry.register(new Polymer("Hostalen  (UHMWPE)", Category.Polyolefin, ResinCode.HDPE));
	public static final Polymer GUR = registry.register(new Polymer("GUR (UHMWPE)", Category.Polyolefin, ResinCode.HDPE));
	public static final Polymer stamylanUH = registry.register(new Polymer("Stamylan UH (UHMWPE)", Category.Polyolefin, ResinCode.HDPE));
	public static final Polymer polyox = registry.register(new Polymer("Polyox (PEO)"));
	public static final Polymer sumikaexcel = registry.register(new Polymer("Sumikaexcel (PES)"));
	public static final Polymer talpa = registry.register(new Polymer("Talpa (PES)"));
	public static final Polymer ultrason = registry.register(new Polymer("Ultrason (PES)"));
	public static final Polymer zytel = registry.register(new Polymer("Zytel (Nylon 6,6) (PHMA)"));
	public static final Polymer maranyl = registry.register(new Polymer("Maranyl (Nylon 6,6) (PHMA)"));
	public static final Polymer ultramidA = registry.register(new Polymer("Ultramid A (Nylon 6,6) (PHMA)"));
	public static final Polymer edgetek = registry.register(new Polymer("Edgetek (Nylon 6,10) (PHMS)"));
	public static final Polymer kapton = registry.register(new Polymer("Kapton (PI)"));
	public static final Polymer aurum = registry.register(new Polymer("Aurum (PI)"));
	public static final Polymer upilex = registry.register(new Polymer("Upilex (PI)"));
	public static final Polymer upimol = registry.register(new Polymer("Upimol (PI)"));
	public static final Polymer vespel = registry.register(new Polymer("Vespel (PI)"));
	public static final Polymer vistanex = registry.register(new Polymer("Vistanex (PI)"));
	public static final Polymer diakon = registry.register(new Polymer("Diakon (PMMA)"));
	public static final Polymer lucite = registry.register(new Polymer("Lucite (PMMA)"));
	public static final Polymer oroglas = registry.register(new Polymer("Oroglas (PMMA)"));
	public static final Polymer degalas = registry.register(new Polymer("Degalas (PMMA)"));
	public static final Polymer delpet = registry.register(new Polymer("Delpet (PMMA)"));
	public static final Polymer astalon = registry.register(new Polymer("Astalon (PC)", Category.Polycarbonate, ResinCode.O));
	public static final Polymer anjalon = registry.register(new Polymer("Anjalon (PC)", Category.Polycarbonate, ResinCode.O));
	public static final Polymer calibre = registry.register(new Polymer("Calibre (PC)", Category.Polycarbonate, ResinCode.O));
	public static final Polymer lexan = registry.register(new Polymer("Lexan (PC)", Category.Polycarbonate, ResinCode.O));
	public static final Polymer makrolon = registry.register(new Polymer("Makrolon (PC)", Category.Polycarbonate, ResinCode.O));
	public static final Polymer nylon6 = registry.register(new Polymer("Nylon 6 (polycaprolactam)"));
	public static final Polymer nylon6_6 = registry.register(new Polymer("Nylon 6,6 (PHMA)"));
	// USES" (nylon) stockings, apparel, home furnishings, automobiles parts, gear wheels, oil seals, bearings, and temperature-resistant packaging film
	// automobile and truck tires, ropes, seat belts, parachutes, substrates for coated fabrics such as artificial leather, fire and garden hoses,
	// nonwoven fabrics for carpet underlayments, and disposable garments for the health-care industry
	// bearings, pulleys, gears, zippers, and automobile fan blades
	public static final Polymer nylon4_6 = registry.register(new Polymer("Nylon 4,6"));
	public static final Polymer nylon6_10 = registry.register(new Polymer("Nylon 6,10"));
	public static final Polymer nylon6_12 = registry.register(new Polymer("Nylon 6,12"));
	public static final Polymer nylon12_12 = registry.register(new Polymer("Nylon 12,12"));
	public static final Polymer nylon11 = registry.register(new Polymer("Nylon 12,12"));
	public static final Polymer nylon12 = registry.register(new Polymer("Nylon 12,12"));
	public static final Polymer capron = registry.register(new Polymer("Capron (polycaprolactam)"));
	public static final Polymer ultramid = registry.register(new Polymer("Ultramid (polycaprolactam)"));
	public static final Polymer duranex = registry.register(new Polymer("Duranex (PBT)"));
	public static final Polymer durlex = registry.register(new Polymer("Durlex (PBT)"));
	public static final Polymer deniter = registry.register(new Polymer("Deniter (PBT)"));
	public static final Polymer bergaform = registry.register(new Polymer("Bergaform (POM)"));
	public static final Polymer celcon = registry.register(new Polymer("Celcon (POM)"));
	public static final Polymer dafnelan = registry.register(new Polymer("Dafnelan (POM)"));
	public static final Polymer serpol = registry.register(new Polymer("Serpol (PPO)"));
	public static final Polymer ceramer = registry.register(new Polymer("Ceramer (PPS)"));
	public static final Polymer fordon = registry.register(new Polymer("Fordon (PPS)"));
	public static final Polymer bearec = registry.register(new Polymer("Bearec (PPS)"));
	public static final Polymer adflex = registry.register(new Polymer("Adflex (PP)", Category.Polyolefin, ResinCode.PP));
	public static final Polymer addilene = registry.register(new Polymer("Addilene (PP)", Category.Polyolefin, ResinCode.PP));
	public static final Polymer adpro = registry.register(new Polymer("Adpro (PP)"));
	public static final Polymer avantra = registry.register(new Polymer("Avantra (PS)", Category.Polyolefin, ResinCode.PP));
	public static final Polymer cosden = registry.register(new Polymer("Cosden (PS)", Category.Polyolefin, ResinCode.PP));
	public static final Polymer daicelStyrol = registry.register(new Polymer("Daicel styrol (PS)", Category.Polyolefin, ResinCode.PP));
	public static final Polymer teflon = registry.register(new Polymer("Teflon (PTFE)"));
	public static final Polymer fluon = registry.register(new Polymer("Fluon (PTFE)"));
	public static final Polymer duraflon = registry.register(new Polymer("Duraflon (PTFE)"));
	public static final Polymer dyneon = registry.register(new Polymer("Dyneon (PTFE)"));
	public static final Polymer fulton = registry.register(new Polymer("Fulton (PTFE)"));
	public static final Polymer apex = registry.register(new Polymer("Apex (PVC)", Category.Polyolefin, ResinCode.PVC));
	public static final Polymer apiflex = registry.register(new Polymer("Apiflex (PVC)", Category.Polyolefin, ResinCode.PVC));
	public static final Polymer boltaron = registry.register(new Polymer("Boltaron (PVC)", Category.Polyolefin, ResinCode.PVC));
	public static final Polymer dyflor = registry.register(new Polymer("Dyflor (PVDF)"));
	public static final Polymer kynar = registry.register(new Polymer("Kynar (PVDF)"));
	public static final Polymer solef = registry.register(new Polymer("Solef (PVDF)"));
	public static final Polymer foraflon = registry.register(new Polymer("foraflon (PVDF)"));
	public static final Polymer hylar = registry.register(new Polymer("Hylar (PVDF)"));
	public static final Polymer lutonalM = registry.register(new Polymer("Lutonal M (PVME)"));
	public static final Polymer plexigum = registry.register(new Polymer("Plexigum (PMA)"));
	public static final Polymer perspex = registry.register(new Polymer("Perspex (PMMA)"));
	public static final Polymer plexiglas = registry.register(new Polymer("plexiglas (PMMA)"));
	public static final Polymer bunaS = registry.register(new Polymer("Buna S (SBR)"));
	public static final Polymer bunaN = registry.register(new Polymer("Buna N (NBR)"));
	public static final Polymer santoprene = registry.register(new Polymer("Santoprene (EPDM)"));
	public static final Polymer formica = registry.register(new Polymer("Formica (MFP)"));
	public static final Polymer celanese = registry.register(new Polymer("Celanese (CDAP)"));
	public static final Polymer tricel = registry.register(new Polymer("Tricel (CTAP)"));
	public static final Polymer arnel = registry.register(new Polymer("Arnel (CTAP)"));

	// Cellulose based trade name polymers
	public static final Polymer rayon = registry.register(new Polymer("Rayon (cellulose, 'artifical silk')"));
	public static final Polymer cellophane = registry.register(new Polymer("cellophane (rayon)"));
	public static final Polymer chadonnetSilk = registry.register(new Polymer("Chadonnet silk (nitrocellulose rayon)"));
	public static final Polymer bembergSilk = registry.register(new Polymer("Bemberg silk (cuprammonium rayon)"));
	public static final Polymer viscoseRayon = registry.register(new Polymer("British silk (viscose rayon)"));
	public static final Polymer parkesine = registry.register(new Polymer("Parkesine (nitrocellulose + wood pulp)"));
	public static final Polymer xylonite = registry.register(new Polymer("Xylonite (nitrocellulose + camphor + castor oil)"));
	public static final Polymer celluloid = registry.register(new Polymer("Celluloid (nitrocellulose + camphor)"));
	// USES: (celluloid) ping pong balls, nail polish, clear coatings

	public final Category category;
	public final ResinCode resinCode;
	public final int craftingPelletsPerBlock;
	public final String itemNamePellet;
	public final String itemNameFiber;

	public Polymer(final String name) {
		this(name, Category.None);
	}

	public Polymer(final String name, final Category category) {
		this(name, category, ResinCode.NONE);
	}

	public Polymer(final String name, final Category category, final ResinCode resinCode) {
		this(name, category, resinCode, 8);
	}

	public Polymer(final String name, final Category category, final ResinCode resinCode, final int craftingPelletsPerBlock) {
		super(name, false);
		this.category = category;
		this.resinCode = resinCode;
		this.craftingPelletsPerBlock = craftingPelletsPerBlock;
		this.itemNamePellet = gameName + "_pellet";
		this.itemNameFiber = gameName + "_fiber";
	}
}