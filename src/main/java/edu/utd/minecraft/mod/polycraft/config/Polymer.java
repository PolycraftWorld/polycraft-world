package edu.utd.minecraft.mod.polycraft.config;

public class Polymer extends Compound {

	public static final EntityRegistry<Compound> registry = new EntityRegistry<Compound>();

	/*******************************************************************************************************************************************
	 * Recycling Plastics - Plastic #1 - Plastic #7
	 *********************************************************************************************************************************************/

	/*
	 * USES: (PET) automobile tire yarns, conveyor belts and drive belts, reinforcement for fire and garden hoses, seat belts (an application in which it has largely replaced nylon), nonwoven fabrics for stabilizing drainage ditches,
	 * culverts, and railroad beds, and nonwovens for use as diaper top sheets and disposable medical garments. recycled bottles for beverages
	 */
	public static final Polymer PET = (Polymer) registry.register(new Compound("poly ethylene terephthalate (PET)", Plastic.getDefaultForType(1)));

	/*
	 * USES: (HDPE) -
	 */
	public static final Polymer HDPE = (Polymer) registry.register(new Compound("high density polyethylene (HDPE)", Plastic.getDefaultForType(2)));

	/*
	 * USES: (PVC) -
	 */
	public static final Polymer PVC = (Polymer) registry.register(new Compound("polyvinyl chloride (PVC)", Plastic.getDefaultForType(3)));

	/*
	 * USES: (LDPE) -
	 */
	public static final Polymer LDPE = (Polymer) registry.register(new Compound("low density polyethylene (LDPE)", Plastic.getDefaultForType(4)));

	/*
	 * USES: (PP) -
	 */
	public static final Polymer PP = (Polymer) registry.register(new Compound("polypropylene (PP)", Plastic.getDefaultForType(5)));

	/*
	 * USES: (PS) -
	 */
	public static final Polymer PS = (Polymer) registry.register(new Compound("polystyrene (PS)", Plastic.getDefaultForType(6)));

	/*
	 * USES: (PC) -
	 */
	public static final Polymer PC = (Polymer) registry.register(new Compound("polycarbonate (PC)", Plastic.getDefaultForType(7)));

	/*******************************************************************************************************************************************
	 * Polyolefins - Plastic #8
	 *********************************************************************************************************************************************/
	// ETHYLENE-PROPYLENE COPOLYMERS - USES: automobile parts, impact modifier for PP, flexible seals, wire and cable insulation, weather stripping, tire sidewalls, hoses, roofing film
	public static final Polymer EPM = (Polymer) registry.register(new Compound("ethylene-propylene monomer (EPM)", Plastic.getDefaultForType(8)));
	public static final Polymer EPDM = (Polymer) registry.register(new Compound("ethylene-propylene-diene monomer (EPM)", Plastic.getDefaultForType(8)));

	public static final Polymer PMMS = (Polymer) registry.register(new Compound("poly m-methyl styrene (PMMS)", Plastic.getDefaultForType(8)));
	public static final Polymer PPMS = (Polymer) registry.register(new Compound("poly p-methyl styrene (PPMS)", Plastic.getDefaultForType(8)));
	public static final Polymer PAN = (Polymer) registry.register(new Compound("polyacrylonitrile (PAN)", Plastic.getDefaultForType(8)));

	// isobutylene rubbers - USES: rubber tires, bladders, innertubes, anything to hold hot steam
	public static final Polymer PIB = (Polymer) registry.register(new Compound("poly isobutylene (PIB)", Plastic.getDefaultForType(8)));
	public static final Polymer IIR = (Polymer) registry.register(new Compound("isobutylene-isoprene rubber (IIR)", Plastic.getDefaultForType(8)));
	public static final Polymer BIIR = (Polymer) registry.register(new Compound("bromine isobutylene-isoprene rubber (BIIR)", Plastic.getDefaultForType(8)));
	public static final Polymer CIIR = (Polymer) registry.register(new Compound("chlorine isobutylene-isoprene rubber (CIIR)", Plastic.getDefaultForType(8)));

	/*******************************************************************************************************************************************
	 * Polyaldehydes - Plastic #9
	 *********************************************************************************************************************************************/
	public static final Polymer phenolicResin = (Polymer) registry.register(new Compound("phenolic resin (phenol formaldehydes)", Plastic.getDefaultForType(9)));
	// USES: (phenol-formaldehyde) darker in color... appliance handles, wood adhesives, particle board adhesive, exterior wood
	public static final Polymer UFP = (Polymer) registry.register(new Compound("urea-formaldehyde polymers (UFP)", Plastic.getDefaultForType(9)));
	// USES: (urea-formaldehyde) lighter in color... appliance handles, wood adhesives, particle board adhesive, interior wood
	public static final Polymer MFP = (Polymer) registry.register(new Compound("melamine-formaldehyde polymers (MFP)", Plastic.getDefaultForType(9)));
	// USES: (melamine-formaldehyde) lighter in color... decorative dinner ware, countertops, coatings

	/*******************************************************************************************************************************************
	 * Cellulosic Polymers - Plastic #10
	 *********************************************************************************************************************************************/

	public static final Polymer cellulosics = (Polymer) registry.register(new Compound("cellulosics", Plastic.getDefaultForType(10)));
	// USES: (cellulosics) cotton, flax, hemp, kapok, sisal, jute, ramie,
	public static final Polymer lignins = (Polymer) registry.register(new Compound("lignins", Plastic.getDefaultForType(10)));

	public static final Polymer CDAP = (Polymer) registry.register(new Compound("Cellulose diacetate (CDAP)", Plastic.getDefaultForType(10)));
	// USES: (CDAP) cigarette filters, protective goggles, tool handles, oil gauges, toothbrushes and eyeglass frames
	public static final Polymer CTAP = (Polymer) registry.register(new Compound("Cellulose triacetate (CTAP)", Plastic.getDefaultForType(10)));
	// USES: (CTAP) clothes, ease of washing and drying, less wrinkling, graceful drape

	/*******************************************************************************************************************************************
	 * Polyamides - Plastic #11
	 *********************************************************************************************************************************************/

	public static final Polymer PHMA = (Polymer) registry.register(new Compound("poly hexamethylene adipamide (Nylon 6,6) (PHMA)", Plastic.getDefaultForType(11)));
	public static final Polymer PHMS = (Polymer) registry.register(new Compound("poly hexamethylene sebacamide (Nylon 6,10) (PHMS)", Plastic.getDefaultForType(11)));

	/*******************************************************************************************************************************************
	 * Polyesters - Plastic #12
	 *********************************************************************************************************************************************/

	// degradable polyesters
	public static final Polymer PLLA = (Polymer) registry.register(new Compound("poly(l-lactic acid) (PLLA)", Plastic.getDefaultForType(12)));
	public static final Polymer PLGA = (Polymer) registry.register(new Compound("poly(lactic-co-glycolic acid) (PLGA)", Plastic.getDefaultForType(12)));
	public static final Polymer PHB = (Polymer) registry.register(new Compound(" poly-2-hydroxy butyrate (PHB)", Plastic.getDefaultForType(12)));
	public static final Polymer PCL = (Polymer) registry.register(new Compound(" polycaprolactone (PCL)", Plastic.getDefaultForType(12)));
	public static final Polymer PHBV = (Polymer) registry.register(new Compound("poly hydroxybutyrate-co-hydroxyvalerate (PHBV)", Plastic.getDefaultForType(12)));

	// alkyd resins - network polyesters - dicarboxylic acids or their anhydrides and polyfunctional alcohols such as glycerol
	public static final Polymer alkydResins = (Polymer) registry.register(new Compound("Alkyd Resins", Plastic.getDefaultForType(12)));

	public static final Polymer PBT = (Polymer) registry.register(new Compound("poly butylene terephthalate", Plastic.getDefaultForType(12)));

	/*******************************************************************************************************************************************
	 * Polyaramids - Plastic #13
	 *********************************************************************************************************************************************/

	public static final Polymer PEEK = (Polymer) registry.register(new Compound("poly ether ether ketone (PEEK)", Plastic.getDefaultForType(13)));
	public static final Polymer PI = (Polymer) registry.register(new Compound("polyimide (PI)", Plastic.getDefaultForType(13)));

	/*******************************************************************************************************************************************
	 * Polycarbonate - Plastic #14
	 *********************************************************************************************************************************************/

	/*******************************************************************************************************************************************
	 * Polyether - Plastic #15
	 *********************************************************************************************************************************************/

	public static final Polymer PEG = (Polymer) registry.register(new Compound("polyethylene glycol (PEG)", Plastic.getDefaultForType(15)));
	public static final Polymer PPG = (Polymer) registry.register(new Compound("polypropylene glycol (PPG)", Plastic.getDefaultForType(15)));
	public static final Polymer POM = (Polymer) registry.register(new Compound("Poly oxymethylene (POM)", Plastic.getDefaultForType(15)));
	public static final Polymer PEO = (Polymer) registry.register(new Compound("polyethylene oxide (PEO)", Plastic.getDefaultForType(15)));
	public static final Polymer PTMG = (Polymer) registry.register(new Compound("poly tetramethylene glycol (PTMG)", Plastic.getDefaultForType(15)));
	public static final Polymer PTMEG = (Polymer) registry.register(new Compound("poly tetramethylene ether glycol (PTMEG)", Plastic.getDefaultForType(15)));

	/*******************************************************************************************************************************************
	 * Polyphenols - Plastic #16
	 *********************************************************************************************************************************************/

	public static final Polymer polyphenol = (Polymer) registry.register(new Compound("polyphenol", Plastic.getDefaultForType(16)));
	public static final Polymer PPO = (Polymer) registry.register(new Compound("poly phenylene oxide (PPO)", Plastic.getDefaultForType(16)));
	public static final Polymer PPS = (Polymer) registry.register(new Compound("poly p-phenylene sulphide (PPS)", Plastic.getDefaultForType(16)));
	public static final Polymer PPTA = (Polymer) registry.register(new Compound("poly p-phenylene terephthalamide (PPTA)", Plastic.getDefaultForType(16)));
	public static final Polymer PMIA = (Polymer) registry.register(new Compound("poly m-phenylene isophthalamide (PMIA)", Plastic.getDefaultForType(16)));

	/*******************************************************************************************************************************************
	 * Polyacrylates - Plastic #17
	 *********************************************************************************************************************************************/

	public static final Polymer PMA = (Polymer) registry.register(new Compound("poly(methyl acrylate) (PMA)", Plastic.getDefaultForType((17))));
	public static final Polymer PMMA = (Polymer) registry.register(new Compound("poly(methyl methacrylate) (PMMA)", Plastic.getDefaultForType((17))));
	public static final Polymer PEA = (Polymer) registry.register(new Compound("poly(ethyl acrylate) (PeA)", Plastic.getDefaultForType((17))));
	public static final Polymer PnBA = (Polymer) registry.register(new Compound("poly(n-butyl acrylate) (PnBA)", Plastic.getDefaultForType((17))));
	public static final Polymer PtBA = (Polymer) registry.register(new Compound("poly(tert-butyl acrylate) (PtBA)", Plastic.getDefaultForType((17))));
	public static final Polymer PiBA = (Polymer) registry.register(new Compound("poly(iso-butyl acrylate) (PiBA)", Plastic.getDefaultForType((17))));
	public static final Polymer PIBoA = (Polymer) registry.register(new Compound("poly(iso-borynl acrylate) (PIBoA)", Plastic.getDefaultForType((17))));
	public static final Polymer P2HEMA = (Polymer) registry.register(new Compound("poly 2-hydroxyethyl methacrylate (PHEMA)", Plastic.getDefaultForType((17))));
	public static final Polymer PMCA = (Polymer) registry.register(new Compound("poly methyl cyanoacrylate (PMCA)", Plastic.getDefaultForType((17))));
	public static final Polymer polyacrylateElastomers = (Polymer) registry.register(new Compound("polyacrylate elastomers", Plastic.getDefaultForType((17))));

	/*******************************************************************************************************************************************
	 * Fluoropolymers - Plastic #18
	 *********************************************************************************************************************************************/

	public static final Polymer PTFE = (Polymer) registry.register(new Compound("polytetrafluoroethylene", Plastic.getDefaultForType((18))));
	public static final Polymer PVDF = (Polymer) registry.register(new Compound("poly vinylidene fluoride (PVDF)", Plastic.getDefaultForType((18))));
	public static final Polymer PVDF_TrFE = (Polymer) registry.register(new Compound("poly vinylidene fluoride-trifluoroethylene (PVDF-TrFE)", Plastic.getDefaultForType((18))));
	public static final Polymer PCTFE = (Polymer) registry.register(new Compound("poly chlorotrifluoroethylene (PCTFE)", Plastic.getDefaultForType((18))));
	public static final Polymer PVF = (Polymer) registry.register(new Compound("poly vinyl fluoride (PVF)", Plastic.getDefaultForType((18))));

	/*******************************************************************************************************************************************
	 * Vinyl polymers - Plastic #19
	 *********************************************************************************************************************************************/

	public static final Polymer PVDC = (Polymer) registry.register(new Compound("poly vinylidene dichloride (PVDC)", Plastic.getDefaultForType((19))));
	public static final Polymer PVAC = (Polymer) registry.register(new Compound("poly vinyl acetate (PVAC)", Plastic.getDefaultForType((19))));
	public static final Polymer PVA = (Polymer) registry.register(new Compound("poly vinyl alcohol (PVA)", Plastic.getDefaultForType((19))));
	public static final Polymer PVB = (Polymer) registry.register(new Compound("poly vinyl butyral (PVB)", Plastic.getDefaultForType((19))));
	public static final Polymer PVFo = (Polymer) registry.register(new Compound("poly vinyl formal (PVFo)", Plastic.getDefaultForType((19))));
	public static final Polymer PVME = (Polymer) registry.register(new Compound("poly vinyl methyl ether (PVME)", Plastic.getDefaultForType((19))));

	/*******************************************************************************************************************************************
	 * Diene polymers - Plastic #20
	 *********************************************************************************************************************************************/

	// diene polymers
	public static final Polymer PChl = (Polymer) registry.register(new Compound("polychloroprene (PChl)", Plastic.getDefaultForType((20))));
	public static final Polymer polyisoprene = (Polymer) registry.register(new Compound("natural rubber (polyisoprene)", Plastic.getDefaultForType((20))));
	public static final Polymer polybutadiene = (Polymer) registry.register(new Compound("polybutadiene", Plastic.getDefaultForType((20))));
	public static final Polymer PBR = (Polymer) registry.register(new Compound("polybutadiene rubber (PBR)", Plastic.getDefaultForType((20))));
	public static final Polymer ABS = (Polymer) registry.register(new Compound("acrylonitrile-butadiene-styrene (ABS)", Plastic.getDefaultForType((20))));
	public static final Polymer SBR = (Polymer) registry.register(new Compound("styrene-butadiene rubber (SBR)", Plastic.getDefaultForType((20))));
	// USES: (SBR) all purpose - most important synthetic rubber in world, carpet backing, flooring, belting, wire insulation, footwear, latex
	public static final Polymer NBR = (Polymer) registry.register(new Compound("nitrile-butadiene rubber (NBR)", Plastic.getDefaultForType((20))));
	public static final Polymer HNBR = (Polymer) registry.register(new Compound("hydrogenated nitrile-butadiene rubber (HNBR)", Plastic.getDefaultForType((20))));

	/*******************************************************************************************************************************************
	 * Styrenics - Plastic #21
	 *********************************************************************************************************************************************/

	// styrene block copolymers - USES: hot melt adhesives, shoes and to improve bitumen
	// public static final Polymer SBR = (Polymer) registry.register(new Compound("styrene-butadiene rubber (SBR)", Plastic.getDefaultForType((21))));
	// public static final Polymer ABS = (Polymer) registry.register(new Compound("acrylonitrile-butadiene-styrene (ABS)", Plastic.getDefaultForType((21))));
	public static final Polymer SBS = (Polymer) registry.register(new Compound("styrene-butadiene-styrene (SBS)", Plastic.getDefaultForType((21))));
	public static final Polymer SIS = (Polymer) registry.register(new Compound("styrene-isoprene-styrene (SIS)", Plastic.getDefaultForType((21))));
	public static final Polymer SAN = (Polymer) registry.register(new Compound("styrene-acrylonitrile (SAN)", Plastic.getDefaultForType((21))));
	// USES: (SAN) battery cases, automotive parts, kitchen ware, appliances, furniture, medical supplies
	public static final Polymer SMAC = (Polymer) registry.register(new Compound("styrene-maleic anhydride copolymer (SMAC)", Plastic.getDefaultForType((21))));
	// USES: (SMAC) food service trays

	/*******************************************************************************************************************************************
	 * As of yet unlabeled plastics - Complex polymers
	 *********************************************************************************************************************************************/

	public static final Polymer polyurethane = (Polymer) registry.register(new Compound("polyurethane", Plastic.getDefaultForType(0)));
	public static final Polymer PB = (Polymer) registry.register(new Compound("poly 1-butene", Plastic.getDefaultForType(0)));

	public static final Polymer PPE = (Polymer) registry.register(new Compound("poly 2,6-dimethyl-1,4-phenylene ether", Plastic.getDefaultForType(0)));
	public static final Polymer AF = (Polymer) registry.register(new Compound("acrylic-formaldehude", Plastic.getDefaultForType(0)));
	public static final Polymer silicone = (Polymer) registry.register(new Compound("silicone", Plastic.getDefaultForType(0)));
	public static final Polymer polysaccharide = (Polymer) registry.register(new Compound("polysaccharide", Plastic.getDefaultForType(0)));
	public static final Polymer PTA = (Polymer) registry.register(new Compound("poly thiazyl (PTA)", Plastic.getDefaultForType(0)));
	public static final Polymer PPHD = (Polymer) registry.register(new Compound("poly pentamethylene hexamethylene dicarbamate (PPHD)", Plastic.getDefaultForType(0)));
	public static final Polymer PPOX = (Polymer) registry.register(new Compound("poly propylene oxide (PPOX)", Plastic.getDefaultForType(0)));
	public static final Polymer PEI = (Polymer) registry.register(new Compound("poly etherimide (PEI)", Plastic.getDefaultForType(0)));
	public static final Polymer PEHD = (Polymer) registry.register(new Compound("Poly ethylene hexamethylene dicarbamate (PEHD)", Plastic.getDefaultForType(0)));
	public static final Polymer PES = (Polymer) registry.register(new Compound("poly ethylene sulphide (PES)", Plastic.getDefaultForType(0)));
	public static final Polymer polyol = (Polymer) registry.register(new Compound("polyol", Plastic.getDefaultForType(0)));
	public static final Polymer epoxyResin = (Polymer) registry.register(new Compound("epoxy resin", Plastic.getDefaultForType(0)));
	public static final Polymer PDMS = (Polymer) registry.register(new Compound("polydimethylsiloxane (PDMS)", Plastic.getDefaultForType(0)));
	public static final Polymer polyphosphazene = (Polymer) registry.register(new Compound("polyphosphazene", Plastic.getDefaultForType(0)));

	/*******************************************************************************************************************************************
	 * Trade names for polymers of all types
	 *********************************************************************************************************************************************/

	public static final Polymer bakelite = (Polymer) registry.register(new Compound("Bakelite (phenolic resin)", Plastic.getDefaultForType(0)));
	public static final Polymer neoprene = (Polymer) registry.register(new Compound("Neoprene (PChl)", Plastic.getDefaultForType(0)));
	public static final Polymer kevlar = (Polymer) registry.register(new Compound("Kevlar (PPTA)", Plastic.getDefaultForType(0)));
	/*
	 * USES: (Kevlar and Nomex) Aramids are not produced in as high a volume as the commodity fibres such as nylon and polyester, but because of their high unit price they represent a large business. End uses for aramids in the home are few
	 * (Nomex-type fibres have been made into ironing-board covers), but industrial uses are increasing (especially for aramids of the Kevlar class) as designers of products learn how to exploit the properties offered by these unusual
	 * materials.
	 * 
	 * Aside from the above-mentioned bulletproof vests, Kevlar and its competitors are employed in belts for radial tires, cables, reinforced composites for aircraft panels and boat hulls, flame-resistant garments (especially in blends
	 * with Nomex), sports equipment such as golf club shafts and lightweight bicycles, and as asbestos replacements in clutches and brakes. Nomex-type fibres are made into filter bags for hot stack gases; clothes for presses that apply
	 * permanent-press finishes to fabrics; dryer belts for papermakers; insulation paper and braid for electric motors; flame-resistant protective clothing for fire fighters, military pilots, and race-car drivers; and V belts and hoses.
	 */
	public static final Polymer dacron = (Polymer) registry.register(new Compound("Dacron (PET)", Plastic.getDefaultForType(0)));
	public static final Polymer terylene = (Polymer) registry.register(new Compound("Terylene (PET)", Plastic.getDefaultForType(0)));
	public static final Polymer mylar = (Polymer) registry.register(new Compound("Mylar (PET)", Plastic.getDefaultForType(0)));
	public static final Polymer melinex = (Polymer) registry.register(new Compound("Melinex (PET)", Plastic.getDefaultForType(0)));
	public static final Polymer twaron = (Polymer) registry.register(new Compound("Twaron (PPTA)", Plastic.getDefaultForType(0)));
	public static final Polymer nomex = (Polymer) registry.register(new Compound("Nomex (PMIA)", Plastic.getDefaultForType(0)));
	public static final Polymer orlon = (Polymer) registry.register(new Compound("Orlon (PAN)", Plastic.getDefaultForType(0)));
	public static final Polymer rislan = (Polymer) registry.register(new Compound("Rislan", Plastic.getDefaultForType(0)));
	public static final Polymer technora = (Polymer) registry.register(new Compound("Technora", Plastic.getDefaultForType(0)));
	public static final Polymer ultem = (Polymer) registry.register(new Compound("Ultem (PEI)", Plastic.getDefaultForType(0)));
	public static final Polymer vectran = (Polymer) registry.register(new Compound("Vectran", Plastic.getDefaultForType(0)));
	public static final Polymer viton = (Polymer) registry.register(new Compound("Viton", Plastic.getDefaultForType(0)));
	public static final Polymer zylon = (Polymer) registry.register(new Compound("Zylon", Plastic.getDefaultForType(0)));
	public static final Polymer acrilan = (Polymer) registry.register(new Compound("Acrilan (PAN)", Plastic.getDefaultForType(0)));
	public static final Polymer seetec = (Polymer) registry.register(new Compound("SEETEC BR (PBR)", Plastic.getDefaultForType(0)));
	public static final Polymer duraflex = (Polymer) registry.register(new Compound("Duraflex (PB)", Plastic.getDefaultForType(0)));
	public static final Polymer artlex = (Polymer) registry.register(new Compound("Artlex (PPE)", Plastic.getDefaultForType(0)));
	public static final Polymer dianium = (Polymer) registry.register(new Compound("Dianium (PPE)", Plastic.getDefaultForType(0)));
	public static final Polymer norpex = (Polymer) registry.register(new Compound("Norpex (PPE)", Plastic.getDefaultForType(0)));
	public static final Polymer prevex = (Polymer) registry.register(new Compound("Prevex (PPE)", Plastic.getDefaultForType(0)));
	public static final Polymer victrex = (Polymer) registry.register(new Compound("Tictrex (PEEK)", Plastic.getDefaultForType(0)));
	public static final Polymer zyex = (Polymer) registry.register(new Compound("Zyex (PEEK)", Plastic.getDefaultForType(0)));
	public static final Polymer himod = (Polymer) registry.register(new Compound("Himod (PEEK)", Plastic.getDefaultForType(0)));
	public static final Polymer gatone = (Polymer) registry.register(new Compound("Gatone (PEEK)", Plastic.getDefaultForType(0)));
	public static final Polymer ketron = (Polymer) registry.register(new Compound("Ketron (PEEK)", Plastic.getDefaultForType(0)));
	public static final Polymer pebax = (Polymer) registry.register(new Compound("Pebax (PEI)", Plastic.getDefaultForType(0)));
	public static final Polymer tecapei = (Polymer) registry.register(new Compound("Tecapei (PEI)", Plastic.getDefaultForType(0)));
	public static final Polymer microthene = (Polymer) registry.register(new Compound("Microthene (PE)", Plastic.getDefaultForType(4)));
	public static final Polymer retain = (Polymer) registry.register(new Compound("Retain (PE)", Plastic.getDefaultForType(4)));
	public static final Polymer flexirene = (Polymer) registry.register(new Compound("Flexirene (LDPE)", Plastic.getDefaultForType(4)));
	public static final Polymer ipethene = (Polymer) registry.register(new Compound("Ipethene (LDPE)", Plastic.getDefaultForType(4)));
	public static final Polymer lacqtene = (Polymer) registry.register(new Compound("Lacqtene (LDPE)", Plastic.getDefaultForType(4)));
	public static final Polymer miralon = (Polymer) registry.register(new Compound("Miralon (LDPE)", Plastic.getDefaultForType(4)));
	public static final Polymer neoZex = (Polymer) registry.register(new Compound("Neo-zex (MDPE)", Plastic.getDefaultForType(4)));
	public static final Polymer eltex = (Polymer) registry.register(new Compound("Eltex (HDPE)", Plastic.getDefaultForType(2)));
	public static final Polymer Finathane = (Polymer) registry.register(new Compound("Finathane (HDPE)", Plastic.getDefaultForType(2)));
	public static final Polymer Fortiflex = (Polymer) registry.register(new Compound("Fortiflex (HDPE)", Plastic.getDefaultForType(2)));
	public static final Polymer g_Lex = (Polymer) registry.register(new Compound("G_Lex (HDPE)", Plastic.getDefaultForType(2)));
	public static final Polymer hival = (Polymer) registry.register(new Compound("Hival (HDPE)", Plastic.getDefaultForType(2)));
	public static final Polymer chirulen = (Polymer) registry.register(new Compound("Chirulen (UHMWPE)", Plastic.getDefaultForType(2)));
	public static final Polymer hostalen = (Polymer) registry.register(new Compound("Hostalen  (UHMWPE)", Plastic.getDefaultForType(2)));
	public static final Polymer GUR = (Polymer) registry.register(new Compound("GUR (UHMWPE)", Plastic.getDefaultForType(2)));
	public static final Polymer stamylanUH = (Polymer) registry.register(new Compound("Stamylan UH (UHMWPE)", Plastic.getDefaultForType(2)));
	public static final Polymer polyox = (Polymer) registry.register(new Compound("Polyox (PEO)", Plastic.getDefaultForType(0)));
	public static final Polymer sumikaexcel = (Polymer) registry.register(new Compound("Sumikaexcel (PES)", Plastic.getDefaultForType(0)));
	public static final Polymer talpa = (Polymer) registry.register(new Compound("Talpa (PES)", Plastic.getDefaultForType(0)));
	public static final Polymer ultrason = (Polymer) registry.register(new Compound("Ultrason (PES)", Plastic.getDefaultForType(0)));
	public static final Polymer zytel = (Polymer) registry.register(new Compound("Zytel (Nylon 6,6) (PHMA)", Plastic.getDefaultForType(0)));
	public static final Polymer maranyl = (Polymer) registry.register(new Compound("Maranyl (Nylon 6,6) (PHMA)", Plastic.getDefaultForType(0)));
	public static final Polymer ultramidA = (Polymer) registry.register(new Compound("Ultramid A (Nylon 6,6) (PHMA)", Plastic.getDefaultForType(0)));
	public static final Polymer edgetek = (Polymer) registry.register(new Compound("Edgetek (Nylon 6,10) (PHMS)", Plastic.getDefaultForType(0)));
	public static final Polymer kapton = (Polymer) registry.register(new Compound("Kapton (PI)", Plastic.getDefaultForType(0)));
	public static final Polymer aurum = (Polymer) registry.register(new Compound("Aurum (PI)", Plastic.getDefaultForType(0)));
	public static final Polymer upilex = (Polymer) registry.register(new Compound("Upilex (PI)", Plastic.getDefaultForType(0)));
	public static final Polymer upimol = (Polymer) registry.register(new Compound("Upimol (PI)", Plastic.getDefaultForType(0)));
	public static final Polymer vespel = (Polymer) registry.register(new Compound("Vespel (PI)", Plastic.getDefaultForType(0)));
	public static final Polymer vistanex = (Polymer) registry.register(new Compound("Vistanex (PI)", Plastic.getDefaultForType(0)));
	public static final Polymer diakon = (Polymer) registry.register(new Compound("Diakon (PMMA)", Plastic.getDefaultForType(0)));
	public static final Polymer lucite = (Polymer) registry.register(new Compound("Lucite (PMMA)", Plastic.getDefaultForType(0)));
	public static final Polymer oroglas = (Polymer) registry.register(new Compound("Oroglas (PMMA)", Plastic.getDefaultForType(0)));
	public static final Polymer degalas = (Polymer) registry.register(new Compound("Degalas (PMMA)", Plastic.getDefaultForType(0)));
	public static final Polymer delpet = (Polymer) registry.register(new Compound("Delpet (PMMA)", Plastic.getDefaultForType(0)));
	public static final Polymer astalon = (Polymer) registry.register(new Compound("Astalon (PC)", Plastic.getDefaultForType(7)));
	public static final Polymer anjalon = (Polymer) registry.register(new Compound("Anjalon (PC)", Plastic.getDefaultForType(7)));
	public static final Polymer calibre = (Polymer) registry.register(new Compound("Calibre (PC)", Plastic.getDefaultForType(7)));
	public static final Polymer lexan = (Polymer) registry.register(new Compound("Lexan (PC)", Plastic.getDefaultForType(7)));
	public static final Polymer makrolon = (Polymer) registry.register(new Compound("Makrolon (PC)", Plastic.getDefaultForType(7)));
	public static final Polymer nylon6 = (Polymer) registry.register(new Compound("Nylon 6 (polycaprolactam)", Plastic.getDefaultForType(0)));
	public static final Polymer nylon6_6 = (Polymer) registry.register(new Compound("Nylon 6,6 (PHMA)", Plastic.getDefaultForType(0)));
	// USES" (nylon) stockings, apparel, home furnishings, automobiles parts, gear wheels, oil seals, bearings, and temperature-resistant packaging film
	// automobile and truck tires, ropes, seat belts, parachutes, substrates for coated fabrics such as artificial leather, fire and garden hoses,
	// nonwoven fabrics for carpet underlayments, and disposable garments for the health-care industry
	// bearings, pulleys, gears, zippers, and automobile fan blades
	public static final Polymer nylon4_6 = (Polymer) registry.register(new Compound("Nylon 4,6", Plastic.getDefaultForType(0)));
	public static final Polymer nylon6_10 = (Polymer) registry.register(new Compound("Nylon 6,10", Plastic.getDefaultForType(0)));
	public static final Polymer nylon6_12 = (Polymer) registry.register(new Compound("Nylon 6,12", Plastic.getDefaultForType(0)));
	public static final Polymer nylon12_12 = (Polymer) registry.register(new Compound("Nylon 12,12", Plastic.getDefaultForType(0)));
	public static final Polymer nylon11 = (Polymer) registry.register(new Compound("Nylon 12,12", Plastic.getDefaultForType(0)));
	public static final Polymer nylon12 = (Polymer) registry.register(new Compound("Nylon 12,12", Plastic.getDefaultForType(0)));
	public static final Polymer capron = (Polymer) registry.register(new Compound("Capron (polycaprolactam)", Plastic.getDefaultForType(0)));
	public static final Polymer ultramid = (Polymer) registry.register(new Compound("Ultramid (polycaprolactam)", Plastic.getDefaultForType(0)));
	public static final Polymer duranex = (Polymer) registry.register(new Compound("Duranex (PBT)", Plastic.getDefaultForType(0)));
	public static final Polymer durlex = (Polymer) registry.register(new Compound("Durlex (PBT)", Plastic.getDefaultForType(0)));
	public static final Polymer deniter = (Polymer) registry.register(new Compound("Deniter (PBT)", Plastic.getDefaultForType(0)));
	public static final Polymer bergaform = (Polymer) registry.register(new Compound("Bergaform (POM)", Plastic.getDefaultForType(0)));
	public static final Polymer celcon = (Polymer) registry.register(new Compound("Celcon (POM)", Plastic.getDefaultForType(0)));
	public static final Polymer dafnelan = (Polymer) registry.register(new Compound("Dafnelan (POM)", Plastic.getDefaultForType(0)));
	public static final Polymer serpol = (Polymer) registry.register(new Compound("Serpol (PPO)", Plastic.getDefaultForType(0)));
	public static final Polymer ceramer = (Polymer) registry.register(new Compound("Ceramer (PPS)", Plastic.getDefaultForType(0)));
	public static final Polymer fordon = (Polymer) registry.register(new Compound("Fordon (PPS)", Plastic.getDefaultForType(0)));
	public static final Polymer bearec = (Polymer) registry.register(new Compound("Bearec (PPS)", Plastic.getDefaultForType(0)));
	public static final Polymer adflex = (Polymer) registry.register(new Compound("Adflex (PP)", Plastic.getDefaultForType(5)));
	public static final Polymer addilene = (Polymer) registry.register(new Compound("Addilene (PP)", Plastic.getDefaultForType(5)));
	public static final Polymer adpro = (Polymer) registry.register(new Compound("Adpro (PP)", Plastic.getDefaultForType(0)));
	public static final Polymer avantra = (Polymer) registry.register(new Compound("Avantra (PS)", Plastic.getDefaultForType(6)));
	public static final Polymer cosden = (Polymer) registry.register(new Compound("Cosden (PS)", Plastic.getDefaultForType(6)));
	public static final Polymer daicelStyrol = (Polymer) registry.register(new Compound("Daicel styrol (PS)", Plastic.getDefaultForType(6)));
	public static final Polymer teflon = (Polymer) registry.register(new Compound("Teflon (PTFE)", Plastic.getDefaultForType(0)));
	public static final Polymer fluon = (Polymer) registry.register(new Compound("Fluon (PTFE)", Plastic.getDefaultForType(0)));
	public static final Polymer duraflon = (Polymer) registry.register(new Compound("Duraflon (PTFE)", Plastic.getDefaultForType(0)));
	public static final Polymer dyneon = (Polymer) registry.register(new Compound("Dyneon (PTFE)", Plastic.getDefaultForType(0)));
	public static final Polymer fulton = (Polymer) registry.register(new Compound("Fulton (PTFE)", Plastic.getDefaultForType(0)));
	public static final Polymer apex = (Polymer) registry.register(new Compound("Apex (PVC)", Plastic.getDefaultForType(3)));
	public static final Polymer apiflex = (Polymer) registry.register(new Compound("Apiflex (PVC)", Plastic.getDefaultForType(3)));
	public static final Polymer boltaron = (Polymer) registry.register(new Compound("Boltaron (PVC)", Plastic.getDefaultForType(3)));
	public static final Polymer dyflor = (Polymer) registry.register(new Compound("Dyflor (PVDF)", Plastic.getDefaultForType(0)));
	public static final Polymer kynar = (Polymer) registry.register(new Compound("Kynar (PVDF)", Plastic.getDefaultForType(0)));
	public static final Polymer solef = (Polymer) registry.register(new Compound("Solef (PVDF)", Plastic.getDefaultForType(0)));
	public static final Polymer foraflon = (Polymer) registry.register(new Compound("foraflon (PVDF)", Plastic.getDefaultForType(0)));
	public static final Polymer hylar = (Polymer) registry.register(new Compound("Hylar (PVDF)", Plastic.getDefaultForType(0)));
	public static final Polymer lutonalM = (Polymer) registry.register(new Compound("Lutonal M (PVME)", Plastic.getDefaultForType(0)));
	public static final Polymer plexigum = (Polymer) registry.register(new Compound("Plexigum (PMA)", Plastic.getDefaultForType(0)));
	public static final Polymer perspex = (Polymer) registry.register(new Compound("Perspex (PMMA)", Plastic.getDefaultForType(0)));
	public static final Polymer plexiglas = (Polymer) registry.register(new Compound("plexiglas (PMMA)", Plastic.getDefaultForType(0)));
	public static final Polymer bunaS = (Polymer) registry.register(new Compound("Buna S (SBR)", Plastic.getDefaultForType(0)));
	public static final Polymer bunaN = (Polymer) registry.register(new Compound("Buna N (NBR)", Plastic.getDefaultForType(0)));
	public static final Polymer santoprene = (Polymer) registry.register(new Compound("Santoprene (EPDM)", Plastic.getDefaultForType(0)));
	public static final Polymer formica = (Polymer) registry.register(new Compound("Formica (MFP)", Plastic.getDefaultForType(0)));
	public static final Polymer celanese = (Polymer) registry.register(new Compound("Celanese (CDAP)", Plastic.getDefaultForType(0)));
	public static final Polymer tricel = (Polymer) registry.register(new Compound("Tricel (CTAP)", Plastic.getDefaultForType(0)));
	public static final Polymer arnel = (Polymer) registry.register(new Compound("Arnel (CTAP)", Plastic.getDefaultForType(0)));

	// Cellulose based trade name polymers
	public static final Polymer rayon = (Polymer) registry.register(new Compound("Rayon (cellulose, 'artifical silk')", Plastic.getDefaultForType(0)));
	public static final Polymer cellophane = (Polymer) registry.register(new Compound("cellophane (rayon)", Plastic.getDefaultForType(0)));
	public static final Polymer chadonnetSilk = (Polymer) registry.register(new Compound("Chadonnet silk (nitrocellulose rayon)", Plastic.getDefaultForType(0)));
	public static final Polymer bembergSilk = (Polymer) registry.register(new Compound("Bemberg silk (cuprammonium rayon)", Plastic.getDefaultForType(0)));
	public static final Polymer viscoseRayon = (Polymer) registry.register(new Compound("British silk (viscose rayon)", Plastic.getDefaultForType(0)));
	public static final Polymer parkesine = (Polymer) registry.register(new Compound("Parkesine (nitrocellulose + wood pulp)", Plastic.getDefaultForType(0)));
	public static final Polymer xylonite = (Polymer) registry.register(new Compound("Xylonite (nitrocellulose + camphor + castor oil)", Plastic.getDefaultForType(0)));
	public static final Polymer celluloid = (Polymer) registry.register(new Compound("Celluloid (nitrocellulose + camphor)", Plastic.getDefaultForType(0)));
	// USES: (celluloid) ping pong balls, nail polish, clear coatings

	public final boolean fluid;
	public final Plastic plastic;

	public Polymer(final String name, final Plastic _plastic) {
		super(name, _plastic);
		this.plastic = _plastic;
		this.fluid = false;
	}

}