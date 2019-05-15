package edu.utd.minecraft.mod.polycraft.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wikipedia.Wiki;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Alloy;
import edu.utd.minecraft.mod.polycraft.config.Armor;
import edu.utd.minecraft.mod.polycraft.config.CellCultureDish;
import edu.utd.minecraft.mod.polycraft.config.Compound;
import edu.utd.minecraft.mod.polycraft.config.Config;
import edu.utd.minecraft.mod.polycraft.config.ConfigRegistry;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Fuel.QuantifiedFuel;
import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.MinecraftBlock;
import edu.utd.minecraft.mod.polycraft.config.MinecraftItem;
import edu.utd.minecraft.mod.polycraft.config.Mineral;
import edu.utd.minecraft.mod.polycraft.config.Polymer;
import edu.utd.minecraft.mod.polycraft.config.SourcedConfig;
import edu.utd.minecraft.mod.polycraft.config.SourcedVesselConfig;
import edu.utd.minecraft.mod.polycraft.config.Tool;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeSlot;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.heated.chemicalprocessor.ChemicalProcessorInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.distillationcolumn.DistillationColumnInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.extruder.ExtruderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.injectionmolder.InjectionMolderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.meroxtreatmentunit.MeroxTreatmentUnitInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.steamcracker.SteamCrackerInventory;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.MachiningMillInventory;
import edu.utd.minecraft.mod.polycraft.inventory.tradinghouse.TradingHouseInventory;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerBlock;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerBrick;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerSlab;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerStairs;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerWall;

public class WikiMaker {
	// TODO: John to edit WikiMaker
	private static final Logger logger = LogManager.getLogger();

	private static final String WIKIPEDIA_SEARCH = "http://en.wikipedia.org/wiki/Special:Search/";
	private static final String MINECRAFT_WIKI = "http://minecraft.gamepedia.com/";
	private static final String CONFIG_DIRECTORY = "src/main/resources/config";
	private static final String IMAGE_EXTENSION = "png";
	private static final String MINECRAFT_TEXTURES_DIRECTORY = "build/tmp/recompSrc/assets/minecraft/textures";
	private static final String[] MINECRAFT_TEXTURES_DIRECTORIES = new String[] {
			// MINECRAFT_TEXTURES_DIRECTORY + "/blocks",
			// MINECRAFT_TEXTURES_DIRECTORY + "/items",
			// MINECRAFT_TEXTURES_DIRECTORY + "/models/armor",
			};
	private static final String POLYCRAFT_TEXTURES_DIRECTORY = "src/main/resources/assets/polycraft/textures";
	private static final String POLYCRAFT_SCREENSHOTS_DIRECTORY = "wiki/screenshots";
	private static final String POLYCRAFT_GUI_TEXTURES_DIRECTORY = "wiki/textures/gui/container";
	private static final String POLYCRAFT_CUSTOM_TEXTURES_DIRECTORY = "wiki/textures/temp";
	private static final String[] POLYCRAFT_TEXTURES_DIRECTORIES = new String[] {
			// POLYCRAFT_TEXTURES_DIRECTORY + "/blocks",
			// POLYCRAFT_TEXTURES_DIRECTORY + "/items",
			// POLYCRAFT_TEXTURES_DIRECTORY + "/models/armor",
			// POLYCRAFT_GUI_TEXTURES_DIRECTORY,
			//POLYCRAFT_CUSTOM_TEXTURES_DIRECTORY,
			// POLYCRAFT_SCREENSHOTS_DIRECTORY
			};

	// These blacklists could probably be regex's or even Item base types... but
	// are necessary for
	// filtering out the mass of items being generated on the recipe pages
	private static final String[] CRAFTING_TABLE_BLACKLIST = new String[] {
			"Block (", "Block of ", "Bag (", "Beaker (", "Canister (",
			"Drum (", "Flask (", "Powder Keg (", "Drum (", "Flask (",
			"Powder Keg (", "Sack (", "Slab (", "Stairs (", "Vial (", "Wall (",
			"Cartridge (", "Gripped ", "Plastic Brick " };

	private static final String[] NO_BLACKLIST = new String[] {};

	private static final String WIKI_NEWLINE = "\n";

	private enum PageSectionItem {
		Description, External, Properties, Recipes, History, Gallery, References;

		public final String heading;

		private PageSectionItem() {
			this.heading = this.toString();
		}

		private PageSectionItem(final String heading) {
			this.heading = heading;
		}
	};

	public static void generate(final String url, final String scriptPath,
			final String username, final String password,
			final boolean overwritePages, final String debugOutputDirectory) {
		try {
			WikiMaker wikiMaker = new WikiMaker(url, scriptPath, username,
					password, overwritePages, debugOutputDirectory);
			// wikiMaker.createImages(MINECRAFT_TEXTURES_DIRECTORIES);
			//wikiMaker.createImages(POLYCRAFT_TEXTURES_DIRECTORIES);

			//wikiMaker.createRecipePage(PolycraftContainerType.CRAFTING_TABLE,
			//		CRAFTING_TABLE_BLACKLIST, false);
			// wikiMaker.createRecipePage(PolycraftContainerType.FURNACE,
			// NO_BLACKLIST, false);
			// wikiMaker.createRecipePage(PolycraftContainerType.MACHINING_MILL,
			// NO_BLACKLIST, false);
			// wikiMaker.createRecipePage(PolycraftContainerType.EXTRUDER,
			// NO_BLACKLIST, false);
			//wikiMaker.createRecipePage(PolycraftContainerType.INJECTION_MOLDER,
			//		NO_BLACKLIST, false);
			// wikiMaker.createRecipePage(PolycraftContainerType.DISTILLATION_COLUMN,
			// NO_BLACKLIST, true);
			// wikiMaker.createRecipePage(PolycraftContainerType.STEAM_CRACKER,
			// NO_BLACKLIST, true);
			// wikiMaker.createRecipePage(PolycraftContainerType.MEROX_TREATMENT_UNIT,
			// NO_BLACKLIST, false);
			// wikiMaker.createRecipePage(PolycraftContainerType.CHEMICAL_PROCESSOR,
			// NO_BLACKLIST, false);
			//wikiMaker.createRecipePage(PolycraftContainerType.CONTACT_PRINTER,
			//		NO_BLACKLIST, false);

			//			wikiMaker.createRecipePage(PolycraftContainerType.MASK_WRITER,
			//					NO_BLACKLIST, false);

			//wikiMaker.createRecipePage(PolycraftContainerType.PRINTING_PRESS,
			//		NO_BLACKLIST, false);

			//wikiMaker.createFuelPage();
			//			wikiMaker.createItemTypesPage(ImmutableList.of(
			//					CompressedBlock.class,
			//					Ore.class, Ingot.class, Catalyst.class, ElementVessel.class,
			//					CompoundVessel.class,
			//					PolymerPellets.class, PolymerBlock.class,
			//					PolymerSlab.class, PolymerStairs.class, PolymerBrick.class,
			//					PolymerWall.class,
			//					Mold.class, MoldedItem.class, GrippedTool.class, PogoStick.class,
			//					Inventory.class, CustomObject.class, WaferItem.class));

			//wikiMaker.createItemPages(CompressedBlock.registry);
			//wikiMaker.createItemPages(Inventory.registry);
			//wikiMaker.createItemPages(Ore.registry);
			//wikiMaker.createItemPages(Ingot.registry);
			//wikiMaker.createItemPages(Nugget.registry);
			//wikiMaker.createItemPages(Catalyst.registry);
			//wikiMaker.createItemPages(ElementVessel.registry);
			//wikiMaker.createItemPages(CompoundVessel.registry);
			//wikiMaker.createItemPages(PolymerPellets.registry);
			//wikiMaker.createItemPages(PolymerBlock.registry);
			//wikiMaker.createItemPages(PolymerSlab.registry);
			//wikiMaker.createItemPages(PolymerStairs.registry);
			//wikiMaker.createItemPages(PolymerBrick.registry);
			//wikiMaker.createItemPages(PolymerWall.registry);
			//wikiMaker.createItemPages(Mold.registry);
			//wikiMaker.createItemPages(MoldedItem.registry);
			//wikiMaker.createItemPages(WaferItem.registry);
			//wikiMaker.createItemPages(Mask.registry);
			//wikiMaker.createItemPages(DNASampler.registry);
			wikiMaker.createItemPages(CellCultureDish.registry);

			//wikiMaker.createItemPages(GrippedTool.registry);
			//			wikiMaker.createItemPages(PogoStick.registry);
			//wikiMaker.createItemPages(CustomObject.registry);
			//			wikiMaker.createArmor(Armor.registry);
			//wikiMaker.createTools(Tool.registry);
			//wikiMaker.createPortalMap(PolycraftRegistry.registryIdToNameUpper);

			wikiMaker.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Failed: {}", ex.getMessage());
		}
	}

	private void createPortalMap(Map<String, String> registry)
			throws LoginException, IOException {
		final StringBuilder list = new StringBuilder();

		final Collection<String> headers = Lists.newLinkedList();

		final Collection<Collection<String>> data = Lists.newLinkedList();
		final Collection<Collection<String>> data2 = Lists.newLinkedList();
		final Collection<String> categories = Sets.newLinkedHashSet();

		headers.add("Minecraft-ID");
		headers.add("Display Name");
		headers.add("Portal Image Name");
		categories.add("Portal");
		categories.add("Economics");

		for (Entry<String, String> e : registry.entrySet())
		{
			if (e.getKey().startsWith(PolycraftMod.MC_PREFIX))
			{
				final Collection<String> row = Lists.newLinkedList();
				row.add(e.getKey());
				row.add(e.getValue());
				row.add(PolycraftMod.getFileSafeName(e.getValue()));
				data.add(row);
			}
			else
			{
				final Collection<String> row = Lists.newLinkedList();
				row.add(e.getKey());
				row.add(e.getValue());
				row.add(PolycraftMod.getFileSafeName(e.getValue()));
				data2.add(row);
			}

		}

		final StringBuilder page = new StringBuilder(
				getTable(headers, data));
		page.append(WIKI_NEWLINE).append(getCategoriesAsString(categories));
		edit("Minecraft Mapping", page.toString(), editSummary);

		final StringBuilder page2 = new StringBuilder(
				getTable(headers, data2));
		page.append(WIKI_NEWLINE).append(getCategoriesAsString(categories));
		edit("Polycraft Mapping", page2.toString(), editSummary);

	}

	private static String[] HEADING_FORMATS = new String[] {
			"=%s=" + WIKI_NEWLINE, "==%s==" + WIKI_NEWLINE,
			"===%s===" + WIKI_NEWLINE, "====%s====" + WIKI_NEWLINE,
			"=====%s=====" + WIKI_NEWLINE, "======%s======" + WIKI_NEWLINE, };

	private static String getHeading(final int level, final String text) {
		return String.format(HEADING_FORMATS[level - 1], text);
	}

	private static String getItemStackLocation(final ItemStack itemStack) {
		if (PolycraftRegistry.minecraftItems.contains(itemStack.getItem())) {
			if (itemStack.getItem() instanceof net.minecraft.item.ItemDye) {
				return MINECRAFT_WIKI + "Dyeing";
			}
			return MINECRAFT_WIKI
					+ itemStack.getDisplayNameString().replaceAll(" ", "%20");
		}
		return getItemStackName(itemStack);
	}

	private static String getConfigLocation(Config config) {
		if (config instanceof Fuel)
			config = ((Fuel) config).source;
		if (config instanceof GameIdentifiedConfig)
			return config.name;
		if (config instanceof Element || config instanceof Compound
				|| config instanceof Mineral || config instanceof Alloy
				|| config instanceof Polymer)
			return WIKIPEDIA_SEARCH + config.name;
		if (config instanceof MinecraftItem || config instanceof MinecraftBlock)
			return MINECRAFT_WIKI + config.name;
		throw new Error("Config has no location!");
	}

	private static String getUrlHost(final String url) {
		URL netUrl = null;
		try {
			netUrl = new URL(url);
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		String host = netUrl.getHost();
		if (host.startsWith("www")) {
			host = host.substring("www".length() + 1);
		}
		return host;
	}

	private static boolean isExternalLocation(final String location) {
		return location.startsWith("http");
	}

	private static String LINK_INTERNAL_FORMAT = "[[%s]]";
	private static String LINK_EXTERNAL_FORMAT = "[%s]";

	private static String getLink(final String location) {
		if (isExternalLocation(location)) {
			return String.format(LINK_EXTERNAL_FORMAT,
					location.replaceAll(" ", "%20"));
		}
		return String.format(LINK_INTERNAL_FORMAT, location);
	}

	private static String getLink(final Config config) {
		return getLink(getConfigLocation(config), config.name);
	}

	private static String LINK_INTERNAL_FORMAT_ALT = "[[%s|%s]]";
	private static String LINK_EXTERNAL_FORMAT_ALT = "[%s %s]";

	private static String getLink(final String location, final String text) {
		if (isExternalLocation(location)) {
			return String.format(LINK_EXTERNAL_FORMAT_ALT,
					location.replaceAll(" ", "%20"), text);
		}
		return String.format(LINK_INTERNAL_FORMAT_ALT, location, text);
	}

	private static String getLinkCategory(final String category) {
		return getLink(":Category:" + category, category);
	}

	private static String getLinkFile(final String file) {
		return getLink("File:" + file);
	}

	private static String LINK_FORMAT_IMAGE_ALT = "[[Image:%s|link=%s|%dpx|alt=%s|%s]]";

	private static String getLinkImage(final String location, final String image) {
		return getLinkImage(location, image, 32);
	}

	private static String getLinkImage(final String location,
			final String image, int size) {
		return getLinkImage(location, image, location, location, size);
	}

	private static String getLinkImage(final String text, final String image,
			final String location, final String alt, final int size) {
		return String.format(LINK_FORMAT_IMAGE_ALT, image, location, size, alt,
				text);
	}

	private static String CATEGORY_FORMAT_ = "[[Category:%s]]";

	private static String getCategory(final String category) {
		return String.format(CATEGORY_FORMAT_, category);
	}

	private static Collection<String> getCategories(final Config config) {
		return getCategories(config, 0);
	}

	private static Collection<String> getAllCategories(final Config config) {
		return getCategories(config, -1);
	}

	private static Collection<String> getCategories(final Config config,
			final int maxDepth) {
		return getCategories(config, maxDepth, 0);
	}

	private static Collection<String> getCategories(final Config config,
			final int maxDepth, final int depth) {
		final Collection categories = Sets.newLinkedHashSet();
		categories.add(getTitle(config.getClass(), false));
		if (config instanceof SourcedConfig) {
			if (config instanceof SourcedVesselConfig) {
				categories.add("Vessel");
				categories.add(getTitle(
						((SourcedVesselConfig) config).vesselType.toString(),
						false));
			}
			if (maxDepth == -1 || depth < maxDepth) {
				final Config source = ((SourcedConfig) config).source;
				if (source != null) {
					categories.add(source.name);
					categories
							.addAll(getCategories(source, maxDepth, depth + 1));
				}
			}
		}
		return categories;
	}

	private static String getCategoriesAsString(
			final Collection<String> categories) {
		final StringBuilder categoriesString = new StringBuilder();
		for (final String category : categories)
			categoriesString.append(WIKI_NEWLINE).append(getCategory(category));
		return categoriesString.toString();
	}

	private static String getTitle(final Class type, final boolean plural) {
		return getTitle(type.getSimpleName(), plural);
	}

	private static String getTitle(final String camelTitle, final boolean plural) {
		final StringBuilder title = new StringBuilder(camelTitle);
		for (int i = 1; i < title.length(); i++) {
			if (Character.isLetter(title.charAt(i))
					&& Character.isUpperCase(title.charAt(i))) {
				title.insert(i, ' ');
				i++;
			}
			if (plural && i == title.length() - 1) {
				if (title.charAt(i) == 'y') {
					title.deleteCharAt(i);
					title.append("ies");
					break;
				}
				if (title.charAt(i) != 's') {
					title.append('s');
					break;
				}
			}
		}
		return title.toString();
	}

	private static final String LIST_OF_TYPE_FORMAT = "List of %s";

	private static String getListOfTypeTitle(final Class type) {
		return String.format(LIST_OF_TYPE_FORMAT, getTitle(type, true));
	}

	private static final String TABLE_FORMAT = "{| class=\"wikitable%s%s\"%s|}";

	private static String getTable(final Collection<String> headers,
			final Collection<Collection<String>> data) {
		return getTable(headers, data, true, true);
	}

	private static String getTable(final Collection<String> headers,
			final Collection<Collection<String>> data, final boolean sortable,
			final boolean collapsible) {
		final StringBuilder table = new StringBuilder();
		table.append(WIKI_NEWLINE).append("|-");
		for (final String header : headers)
			table.append(WIKI_NEWLINE).append("! ").append(header);
		for (final Collection<String> row : data) {
			table.append(WIKI_NEWLINE).append("|-");
			for (final String cell : row)
				table.append(WIKI_NEWLINE).append("| ").append(cell);
		}
		table.append(WIKI_NEWLINE);
		return String.format(TABLE_FORMAT, sortable ? " sortable" : "",
				collapsible ? " collapsible" : "", table.toString());
	}

	private static final String INVENTORY_SLOT_FORMAT = "{{Inventory/Slot|index=%d|title=%s|image=%s|link=%s}}";
	private static final String INVENTORY_SLOT_WITH_AMOUNT_FORMAT = "{{Inventory/Slot|index=%d|title=%s|image=%s|link=%s|amount=%d}}";

	private static String getInventorySlot(final int slotIndex,
			final String title, final String image, final String link,
			final int amount) {
		if (amount > 1)
			return String.format(INVENTORY_SLOT_WITH_AMOUNT_FORMAT, slotIndex,
					title, image, link, amount);
		return String.format(INVENTORY_SLOT_FORMAT, slotIndex, title, image,
				link);
	}

	private static String getItemStackName(final ItemStack itemStack) {
		final Item item = itemStack.getItem();
		if (item instanceof ItemPolymerBlock)
			return ((ItemPolymerBlock) item).blockPolymer.polymerBlock.name;
		if (item instanceof ItemPolymerBrick)
			return ((ItemPolymerBrick) item).blockBrick.Brick.name;
		if (item instanceof ItemPolymerWall)
			return ((ItemPolymerWall) item).blockPolymerWall.polymerWall.name;
		if (item instanceof net.minecraft.item.ItemDye)
			return "Dye";
		return itemStack.getDisplayNameString();
	}

	private static String getInventorySlot(final int slotIndex,
			final ItemStack itemStack) {
		return getInventorySlot(slotIndex, getItemStackName(itemStack),
				getTextureImageName(getTexture(itemStack)),
				getItemStackLocation(itemStack), itemStack.stackSize);
	}

	private static String getInventoryWaterSlot(final int slotIndex) {
		return getInventorySlot(slotIndex, new ItemStack(Items.water_bucket));
	}

	private static String getInventoryCoinSlot(final int slotIndex) {
		return getInventorySlot(slotIndex, CustomObject.registry.get("Coins (Copper)").getItemStack()); //TODO: ensure we dont need new operator here
	}

	private static String getInventoryFuelSlot(final int slotIndex) {
		return getInventorySlot(slotIndex, "Fuel", "Fuel.png",
				getListOfTypeTitle(Fuel.class), 1);
	}

	private static String getSpecialInventorySlots(
			final PolycraftContainerType containerType) {
		final StringBuilder slots = new StringBuilder();
		switch (containerType) {
		case MACHINING_MILL:
			slots.append(getInventoryWaterSlot(MachiningMillInventory.slotIndexCoolingWater));
			break;
		case TRADING_HOUSE:
			slots.append(getInventoryCoinSlot(TradingHouseInventory.slotIndexInputFee));
			break;
		case DISTILLATION_COLUMN:
			slots.append(getInventoryWaterSlot(DistillationColumnInventory.slotIndexCoolingWater));
			slots.append(getInventoryWaterSlot(DistillationColumnInventory.slotIndexHeatingWater));
			slots.append(getInventoryFuelSlot(DistillationColumnInventory.slotIndexHeatSource));
			break;
		case INJECTION_MOLDER:
			slots.append(getInventoryWaterSlot(InjectionMolderInventory.slotIndexCoolingWater));
			slots.append(getInventoryFuelSlot(InjectionMolderInventory.slotIndexHeatSource));
			break;
		case EXTRUDER:
			slots.append(getInventoryWaterSlot(ExtruderInventory.slotIndexCoolingWater));
			slots.append(getInventoryFuelSlot(ExtruderInventory.slotIndexHeatSource));
			break;
		case STEAM_CRACKER:
			slots.append(getInventoryWaterSlot(SteamCrackerInventory.slotIndexCoolingWater));
			slots.append(getInventoryWaterSlot(SteamCrackerInventory.slotIndexHeatingWater));
			slots.append(getInventoryFuelSlot(SteamCrackerInventory.slotIndexHeatSource));
			break;
		case MEROX_TREATMENT_UNIT:
			slots.append(getInventoryWaterSlot(MeroxTreatmentUnitInventory.slotIndexCoolingWater));
			slots.append(getInventoryWaterSlot(MeroxTreatmentUnitInventory.slotIndexHeatingWater));
			slots.append(getInventoryFuelSlot(MeroxTreatmentUnitInventory.slotIndexHeatSource));
			break;
		case CHEMICAL_PROCESSOR:
			slots.append(getInventoryWaterSlot(ChemicalProcessorInventory.slotIndexCoolingWater));
			slots.append(getInventoryWaterSlot(ChemicalProcessorInventory.slotIndexHeatingWater));
			slots.append(getInventoryFuelSlot(ChemicalProcessorInventory.slotIndexHeatSource));
			break;
		case CONTACT_PRINTER:
			slots.append(getInventoryFuelSlot(ChemicalProcessorInventory.slotIndexHeatSource));
			break;
		case INDUSTRIAL_OVEN:
			slots.append(getInventoryWaterSlot(DistillationColumnInventory.slotIndexHeatingWater));
			slots.append(getInventoryFuelSlot(DistillationColumnInventory.slotIndexHeatSource));
			break;
		case PLASTIC_CHEST:
			break;
		case TIER_CHEST:
			break;
		default:
			break;
		}
		return slots.toString();
	}

	private static final String INVENTORY_FORMAT = "{{Inventory|%s|type=%s|shapeless=%s}}";

	private static String getInventory(
			final PolycraftContainerType containerType, final String slots,
			final boolean shapeless) {
		return String.format(INVENTORY_FORMAT, slots, containerType.toString()
				.toLowerCase().replaceAll(" ", "-"), String.valueOf(shapeless));
	}

	/**
	 * Grid row for the main crafting table page.
	 */
	private static Collection<String> getRecipePageGridRow(
			final PolycraftRecipe recipe) {
		final Collection<String> row = Lists.newLinkedList();
		final StringBuilder slots = new StringBuilder();
		final Map<String, String> inputs = Maps.newLinkedHashMap();
		final LinkedList<ContainerSlot> intputSlots = Lists.newLinkedList();
		intputSlots.addAll(recipe.getContainerType().getSlots(SlotType.INPUT));
		for (final RecipeInput input : recipe.getInputs()) {
			for (final ItemStack inputStack : input.inputs) {
				inputs.put(getItemStackName(inputStack),
						getItemStackLocation(inputStack));
				int slotIndex = input.slot.getSlotIndex();
				if (slotIndex == RecipeSlot.ANY.slotIndex) {
					slotIndex = intputSlots.remove().getSlotIndex();
				}
				slots.append(getInventorySlot(slotIndex, inputStack));
			}
		}
		final Map<String, String> outputs = Maps.newLinkedHashMap();
		for (final RecipeComponent output : recipe.getOutputs(null)) {
			outputs.put(
					getLink(getItemStackLocation(output.itemStack),
							getItemStackName(output.itemStack)),
					getLinkImage(
							getLink(getItemStackName(output.itemStack),
									getItemStackLocation(output.itemStack)),
							getTextureImageName(getTexture(output.itemStack)), // image
							// getItemStackName(output.itemStack), // location
							getItemStackLocation(output.itemStack),
							getItemStackName(output.itemStack), // alt
							32 // size
					));

			slots.append(getInventorySlot(output.slot.getSlotIndex(),
					output.itemStack));
		}
		slots.append(getSpecialInventorySlots(recipe.getContainerType()));
		final StringBuilder inputList = new StringBuilder();
		for (final Entry<String, String> input : inputs.entrySet()) {
			inputList.append(WIKI_NEWLINE).append("* ")
					.append(getLink(input.getValue(), input.getKey()));
		}
		final StringBuilder outputList = new StringBuilder();
		for (final Entry<String, String> output : outputs.entrySet()) {
			outputList.append(WIKI_NEWLINE).append("* ")
					.append(output.getValue() + " " + output.getKey());
		}
		row.add(outputList.toString());
		row.add(inputList.toString());
		row.add(getInventory(
				recipe.getContainerType(),
				slots.toString(),
				recipe.getContainerType() == PolycraftContainerType.CRAFTING_TABLE
						&& !recipe.isShapedOnly()));
		return row;
	}

	private static <C extends GameIdentifiedConfig> String getTexture(C config) {
		return config instanceof Inventory ? "gui_" + config.name
				: getTexture(config.getItemStack());
	}

	private static String getTexture(final ItemStack itemStack) {
		final Item item = itemStack.getItem();
		if (item instanceof ItemPolymerStairs)
			return "Polymer_stairs";
		if (item instanceof ItemPolymerSlab)
			return "Polymer_slab";
		if (item instanceof ItemPolymerWall)
			return "Polymer_wall";
		if (item instanceof ItemPolymerBlock)
			return "Polymer_block";
		if (item instanceof ItemPolymerBrick)
			return "Plastic_brick";
		if (item instanceof net.minecraft.item.ItemDye)
			return "Dyes";
		String iconName = item.getIcon(itemStack, 0).getIconName();
		final int namespaceIndex = iconName.indexOf(":");
		if (namespaceIndex > -1)
			iconName = iconName.substring(namespaceIndex + 1);
		final int colorIndex = iconName.indexOf("_black");
		if (colorIndex > -1)
			iconName = iconName.replace("_black", "_white");
		return iconName;
	}

	private final Map<Object, Map<PolycraftContainerType, Collection<PolycraftRecipe>>> recipesByIngredientContainerType;
	private final Wiki wiki;
	private final String editSummary;
	private final boolean overwritePages;
	private final String debugOutputDirectory;

	public WikiMaker(final String url, final String scriptPath,
			final String username, final String password,
			final boolean overwritePages, final String debugOutputDirectory)
			throws FailedLoginException, IOException {
		if (StringUtils.isEmpty(debugOutputDirectory)) {
			this.wiki = new Wiki(url, scriptPath);
			this.wiki.login(username, password);
		}
		else
			this.wiki = null;
		this.editSummary = PolycraftMod.MODID + " " + PolycraftMod.VERSION;
		this.overwritePages = overwritePages
				|| !StringUtils.isEmpty(debugOutputDirectory);
		this.debugOutputDirectory = debugOutputDirectory;
		this.recipesByIngredientContainerType = PolycraftMod.recipeManagerRuntime
				.getRecipesByIngredientContainerType();
	}

	private void close() {
		if (wiki != null)
			wiki.logout();
	}

	private void edit(final String title, final String text,
			final String summary) throws LoginException, IOException {
		if (StringUtils.isEmpty(debugOutputDirectory)) {
			wiki.edit(title, text, summary);
		} else {
			final String file = debugOutputDirectory
					+ PolycraftMod.getFileSafeName(title) + ".txt";
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(text);
			osw.close();
			fos.close();
			logger.info("Wrote {}", file);
		}
	}

	private static String getTextureImageName(final String texture) {
		return Character.toUpperCase(texture.charAt(0))
				+ texture.substring(1).toLowerCase().replaceAll(" ", "_") + "."
				+ IMAGE_EXTENSION;
	}

	/**
	 * Uploads the images in the given paths to the wikipedia site. Will overwrite any existing images.
	 * 
	 * @throws InterruptedException
	 */
	private void createImages(final String[] paths) throws IOException,
			LoginException, InterruptedException {
		for (final String path : paths)
			for (final File imageFile : new File(path).listFiles())
			{
				uploadImage(imageFile);
				Thread.sleep(5000);
			}

	}

	private void uploadImage(final File imageFile) throws LoginException,
			IOException {
		if (imageFile.getAbsolutePath().endsWith("." + IMAGE_EXTENSION)) {
			final String name = imageFile.getName().replaceAll(
					"." + IMAGE_EXTENSION, "");
			logger.info("Uploading image: {}", name);
			wiki.upload(imageFile, name, "", editSummary);
		}
	}

	private final Collection<String> FUEL_HEADERS = ImmutableList.of("Fuel",
			"Heat Intensity", "Heat Duration (secs)");

	private void createFuelPage() throws LoginException, IOException {
		final Collection<Collection<String>> data = Lists.newLinkedList();
		for (final Entry<Item, QuantifiedFuel> fuelEntry : Fuel.quantifiedFuelsByItem
				.entrySet()) {
			final Collection<String> row = Lists.newLinkedList();
			if (fuelEntry.getKey() != null)
			{
				final ItemStack fuelStack = new ItemStack(fuelEntry.getKey());
				row.add(getLink(getItemStackLocation(fuelStack),
						getItemStackName(fuelStack)));
				row.add(PolycraftMod.numFormat.format(fuelEntry.getValue().fuel.heatIntensity));
				row.add(PolycraftMod.numFormat.format(fuelEntry.getValue()
						.getHeatDuration()));
				data.add(row);
			}
		}
		edit(getListOfTypeTitle(Fuel.class), getTable(FUEL_HEADERS, data),
				editSummary);
	}

	private final Collection<String> RECIPE_GRID_HEADERS = ImmutableList.of(
			"Outputs", "Components", "Recipe");

	private String getRecipesGrid(final ItemStack ingredient,
			final PolycraftContainerType forceIncludeType)
			throws LoginException, IOException {
		final Collection<PolycraftRecipe> forceIncludedRecipes = forceIncludeType == null ? null
				: PolycraftMod.recipeManagerRuntime
						.getRecipesByContainerType(forceIncludeType);
		final Map<PolycraftContainerType, Collection<PolycraftRecipe>> recipesByContainerType = recipesByIngredientContainerType
				.get(ingredient.getItem());
		if (forceIncludedRecipes != null || recipesByContainerType != null) {
			final StringBuilder page = new StringBuilder();
			if (recipesByContainerType != null) {
				for (final Entry<PolycraftContainerType, Collection<PolycraftRecipe>> recipeEntry : recipesByContainerType
						.entrySet()) {
					final PolycraftContainerType containerType = recipeEntry
							.getKey();
					if (containerType != forceIncludeType) {
						page.append(WIKI_NEWLINE).append(
								getHeading(3, containerType.toString()));
						final Collection<Collection<String>> data = Lists
								.newLinkedList();
						for (final PolycraftRecipe recipe : recipeEntry
								.getValue()) {
							data.add(getRecipePageGridRow(recipe));
						}
						page.append(WIKI_NEWLINE).append(
								getTable(RECIPE_GRID_HEADERS, data));
					}
				}
			}
			if (forceIncludedRecipes != null) {
				page.append(WIKI_NEWLINE).append(
						getHeading(3, forceIncludeType.toString()));
				final Collection<Collection<String>> data = Lists
						.newLinkedList();
				for (final PolycraftRecipe recipe : forceIncludedRecipes)
					data.add(getRecipePageGridRow(recipe));
				page.append(WIKI_NEWLINE).append(
						getTable(RECIPE_GRID_HEADERS, data));
			}
			return page.toString();
		}
		return null;
	}

	private boolean displayOnRecipePage(ItemStack itemStack, String[] filters) {
		Item item = itemStack.getItem();
		String itemName = getItemStackName(itemStack);
		// TODO: This isn't a great way of creating a blacklist for the main
		// page. But it works for Monday's demo.
		// Maybe can use instanceof for all the types?
		for (String filter : filters) {
			if (itemName.startsWith(filter)) {
				return false;
			}
		}
		return true;
	}

	private void createRecipePage(final PolycraftContainerType containerType,
			final String[] filters, final boolean sortByInput)
			throws LoginException, IOException {
		final List<PolycraftRecipe> recipes = Lists
				.newArrayList(PolycraftMod.recipeManagerRuntime
						.getRecipesByContainerType(containerType));
		Collections.sort(recipes, new Comparator<PolycraftRecipe>() {
			@Override
			public int compare(PolycraftRecipe o1, PolycraftRecipe o2) {
				if (sortByInput) {
					// Sort by the FIRST input. Only really useful if there
					// generally aren't more than one input for
					// the crafting table type.
					String item1Name = getItemStackName(o1.getInputs()
							.iterator().next().inputs.iterator().next());
					String item2Name = getItemStackName(o2.getInputs()
							.iterator().next().inputs.iterator().next());
					return item1Name.compareTo(item2Name);
				} else {
					// Sort by the FIRST output. TODO: Should there be a
					// "priority" output that can be used for sorting?
					// This seems to work well enough, at least.
					String item1Name = getItemStackName(o1.getOutputs(null)
							.iterator().next().itemStack);
					String item2Name = getItemStackName(o2.getOutputs(null)
							.iterator().next().itemStack);
					return item1Name.compareTo(item2Name);
				}
			}
		});

		if (recipes != null) {
			final Set<String> createdRecipes = Sets.newHashSet();
			final StringBuilder page = new StringBuilder();
			final Collection<Collection<String>> data = Lists.newLinkedList();
			for (final PolycraftRecipe recipe : recipes) {
				RecipeComponent output = recipe.getOutputs(null).iterator()
						.next();
				String outputName = getItemStackName(output.itemStack);
				if (createdRecipes.contains(outputName)) {
					// TODO: This causes the many variations of ways to create
					// different crafting tables
					// to be only shown once. Need a better way to display
					// these.
					continue;
				}
				createdRecipes.add(outputName);
				if (!displayOnRecipePage(output.itemStack, filters)) {
					continue;
				}
				data.add(getRecipePageGridRow(recipe));
			}
			page.append(WIKI_NEWLINE).append(
					getTable(RECIPE_GRID_HEADERS, data));
			edit(containerType.toString(), page.toString(), editSummary);
		}
	}

	private void createItemTypesPage(
			final Collection<Class<? extends GameIdentifiedConfig>> types)
			throws LoginException, IOException {
		final StringBuilder list = new StringBuilder();
		for (final Class<? extends GameIdentifiedConfig> type : types)
			list.append(WIKI_NEWLINE)
					.append("* ")
					.append(getLink(getListOfTypeTitle(type),
							getTitle(type, true)));
		edit("List of Item Types", list.toString(), editSummary);
	}

	private boolean gameIdentifiedConfigHasItem(
			final GameIdentifiedConfig config) {
		if (config.getItemStack().getItem() == null) {
			logger.warn("Unable to find item for: {}", config.name);
			return false;
		}
		return true;
	}

	private <C extends GameIdentifiedConfig> void createItemPages(
			final ConfigRegistry<C> registry) throws LoginException,
			IOException {
		createItemPageList(registry);
		boolean stopped = true;
		for (final C config : registry.values())
			if (gameIdentifiedConfigHasItem(config)) {

				//if (PolycraftMod.getVersionText(config.version).equalsIgnoreCase(PolycraftMod.getVersionText(new int[] { 1, 3, 5 })))
				createItemPage(config);

			}
	}

	private <C extends Config> void createArmor(
			final ConfigRegistry<C> registry) throws LoginException,
			IOException {
		createArmorList(registry);
		for (final C config : registry.values())
			createArmorPage(config);

	}

	private <C extends Config> void createTools(
			final ConfigRegistry<C> registry) throws LoginException,
			IOException {
		createToolsList(registry);
		for (final C config : registry.values())
			createToolsPage(config);

	}

	private final Collection<String> PROPERTIES_HEADERS = ImmutableList.of(
			"Name", "Value");

	private <C extends GameIdentifiedConfig> void createItemPage(final C config)
			throws LoginException, IOException {
		final String title = config.name;
		final Config source;

		if (config instanceof SourcedConfig)
		{
			source = ((SourcedConfig) config).source;
		}
		else
			source = null;

		if (overwritePages || !wiki.exists(new String[] { title })[0])
		{
			logger.info("{} item page: {}", overwritePages ? "Overwriting"
					: "Creating", title);
			final StringBuilder page = new StringBuilder();
			for (final PageSectionItem section : PageSectionItem.values())
			{
				//Looks up the source on wikipedia if it can...
				if (section == PageSectionItem.External)
				{
					if (source != null)
					{
						final String location = getConfigLocation(source);
						if (isExternalLocation(location))
						{
							page.append(getHeading(2, section.heading));
							page.append(
									getLink(location, source.name + " on "
											+ getUrlHost(location)))
									.append(WIKI_NEWLINE);
						}
					}

				} else if (section == PageSectionItem.Properties)
				{
					final Collection<Collection<String>> propertiesData = Lists
							.newLinkedList();
					page.append(getHeading(2, section.heading));

					if (config.hasProperties() || config.params != null)
					{

						if (config.hasProperties())
						{
							final List<String> propertyNames = config.getPropertyNames();
							final List<String> propertyValues = config.getPropertyValues();
							for (int i = 0; i < propertyValues.size(); i++)
								propertiesData.add(ImmutableList.of(propertyNames.get(i), propertyValues.get(i)));
						}
						if (config.params != null)
						{
							for (int i = 0; i < config.params.values.size(); i++)
								propertiesData.add(ImmutableList.of(config.params.names[i], config.params.getPretty(i)));
						}
					}
					propertiesData.add(ImmutableList.of("Release Version",
							PolycraftMod.getVersionText(config.version)));
					page.append(getTable(PROPERTIES_HEADERS, propertiesData))
							.append(WIKI_NEWLINE);
				} else {
					page.append(getHeading(2, section.heading));
					if (section == PageSectionItem.Gallery)
						page.append(
								getLinkFile(getTextureImageName(getTexture(config))))
								.append(WIKI_NEWLINE);
					else if (section == PageSectionItem.Recipes)
						page.append(
								getRecipesGrid(
										config.getItemStack(),
										config instanceof Inventory ? ((Inventory) config).containerType
												: null)).append(WIKI_NEWLINE);

				}
			}
			page.append(WIKI_NEWLINE).append(
					getCategoriesAsString(getAllCategories(config)));
			edit(title, page.toString(), editSummary);
		}
	}

	private <C extends GameIdentifiedConfig> void createItemPageList(
			final ConfigRegistry<C> registry) throws LoginException,
			IOException {
		boolean isSourced = false;
		boolean isVesseled = false;
		C firstConfig = null;
		Class type = null;
		Class singleSourceType = null;
		final Collection<String> categories = Sets.newLinkedHashSet();
		for (final C config : registry.values()) {
			if (firstConfig == null)
				firstConfig = config;
			if (gameIdentifiedConfigHasItem(config)) {
				isSourced = (config instanceof SourcedConfig);
				if (isSourced) {
					final Config source = ((SourcedConfig) config).source;
					if (source != null) {
						final Class sourceType = source.getClass();
						if (singleSourceType == null) {
							if (type == null)
								singleSourceType = sourceType;
						} else if (singleSourceType != sourceType)
							singleSourceType = null;
					}
				}
				if (type == null) {
					type = config.getClass();
					isVesseled = config instanceof SourcedVesselConfig;
				}
				categories.addAll(getCategories(config));
			}
		}

		if (type != null) {
			final Collection<String> headers = Lists.newLinkedList();
			headers.add("ID");
			headers.add("Icon");
			headers.add(getTitle(type, false));
			if (isSourced) {
				if (singleSourceType == null) {
					headers.add("Source");
					headers.add("Type");
				} else {
					headers.add(getTitle(singleSourceType, false));
				}
			}
			if (isVesseled)
				headers.add("Vessel");
			if (firstConfig.hasProperties())
				headers.addAll(firstConfig.getPropertyNames());
			headers.add("Release Version");

			final Collection<Collection<String>> data = Lists.newLinkedList();
			for (final C config : registry.values()) {
				if (gameIdentifiedConfigHasItem(config)) {
					final Collection<String> row = Lists.newLinkedList();
					row.add(config.gameID);
					if (config instanceof Inventory)
						row.add(getLinkImage(config.name,
								getTextureImageName(getTexture(config)), 350));
					else
						row.add(getLinkImage(config.name,
								getTextureImageName(getTexture(config))));
					row.add(getLink(config.name));
					if (isSourced) {
						final Config source = ((SourcedConfig) config).source;
						if (singleSourceType == null) {
							row.add(source == null ? "" : getLink(source));
							row.add(source == null ? ""
									: getLinkCategory(getTitle(
											source.getClass(), false)));
						} else {
							row.add(source == null ? "" : getLink(source));
						}
					}
					if (isVesseled)
						row.add(getLinkCategory(getTitle(
								((SourcedVesselConfig) config).vesselType
										.toString(), false)));
					if (config.hasProperties())
						row.addAll(config.getPropertyValues());
					row.add(PolycraftMod.getVersionText(config.version));
					data.add(row);
				}
			}
			final StringBuilder page = new StringBuilder(
					getTable(headers, data));
			page.append(WIKI_NEWLINE).append(getCategoriesAsString(categories));
			edit(getListOfTypeTitle(type), page.toString(), editSummary);
		}
	}

	private <C extends Config> void createArmorList(
			final ConfigRegistry<C> registry) throws LoginException,
			IOException {
		final Collection<String> categories = Sets.newLinkedHashSet();
		for (final C config : registry.values())
		{
			categories.addAll(getCategories(config));
		}

		final Collection<String> headers = Lists.newLinkedList();

		headers.add("Crafting Item");
		headers.add("Armor Adjective");
		headers.add("Headgear");
		headers.add("Chest");
		headers.add("Leggings");
		headers.add("Feet");
		headers.add("Durability");
		headers.add("Enchantability");
		headers.add("Recuction Headgear");
		headers.add("Recuction Chest");
		headers.add("Recuction Leggings");
		headers.add("Recuction Feet");
		headers.add("Aqua Affinity");
		headers.add("Release Version");
		headers.add("Headgear ID");
		headers.add("Chest ID");
		headers.add("Leggings ID");
		headers.add("Feet ID");

		final Collection<Collection<String>> data = Lists.newLinkedList();
		for (final C config : registry.values())
		{
			final Collection<String> row = Lists.newLinkedList();

			row.add(((Armor) config).craftingItemName);
			row.add(((Armor) config).name);
			row.add(((Armor) config).componentNames[ArmorSlot.HEAD.getValue()]);
			row.add(((Armor) config).componentNames[ArmorSlot.CHEST.getValue()]);
			row.add(((Armor) config).componentNames[ArmorSlot.LEGS.getValue()]);
			row.add(((Armor) config).componentNames[ArmorSlot.FEET.getValue()]);
			row.add(Integer.toString(((Armor) config).durability));
			row.add(Integer.toString(((Armor) config).enchantability));
			row.add(Integer.toString(((Armor) config).reductionAmounts[ArmorSlot.HEAD.getValue()]));
			row.add(Integer.toString(((Armor) config).reductionAmounts[ArmorSlot.FEET.getValue()]));
			row.add(Integer.toString(((Armor) config).reductionAmounts[ArmorSlot.CHEST.getValue()]));
			row.add(Integer.toString(((Armor) config).reductionAmounts[ArmorSlot.LEGS.getValue()]));
			row.add(Integer.toString(((Armor) config).aquaAffinityLevel));
			row.add(PolycraftMod.getVersionText(config.version));
			row.add(((Armor) config).componentGameIDs[ArmorSlot.HEAD.getValue()]);
			row.add(((Armor) config).componentGameIDs[ArmorSlot.CHEST.getValue()]);
			row.add(((Armor) config).componentGameIDs[ArmorSlot.LEGS.getValue()]);
			row.add(((Armor) config).componentGameIDs[ArmorSlot.FEET.getValue()]);
			data.add(row);

		}
		final StringBuilder page = new StringBuilder(
				getTable(headers, data));
		page.append(WIKI_NEWLINE).append(getCategoriesAsString(categories));
		edit(getListOfTypeTitle(Armor.class), page.toString(), editSummary);

	}

	private <C extends Config> void createArmorPage(final C config)
			throws LoginException, IOException {
		final String title = PolycraftMod.getFileSafeName(config.name);
		final String headgear = PolycraftMod.getFileSafeName(((Armor) config).componentNames[ArmorSlot.HEAD.getValue()]);
		final String footwear = PolycraftMod.getFileSafeName(((Armor) config).componentNames[ArmorSlot.FEET.getValue()]);
		final String vest = PolycraftMod.getFileSafeName(((Armor) config).componentNames[ArmorSlot.CHEST.getValue()]);
		final String pants = PolycraftMod.getFileSafeName(((Armor) config).componentNames[ArmorSlot.LEGS.getValue()]);

		if (overwritePages || !wiki.exists(new String[] { title + " Armor" })[0])
		{
			logger.info("{} item page: {}", overwritePages ? "Overwriting"
					: "Creating", title + " Armor");
			final StringBuilder page = new StringBuilder();
			for (final PageSectionItem section : PageSectionItem.values())
			{
				//Looks up the source on wikipedia if it can...
				if (section == PageSectionItem.External)
				{
					boolean heading = false;
					final String nameLocation = WIKIPEDIA_SEARCH + config.name;
					if (isExternalLocation(nameLocation))
					{

						if (!heading)
							page.append(getHeading(2, section.heading));
						page.append(getLink(nameLocation, config.name + " on " + getUrlHost(nameLocation)) + WIKI_NEWLINE).append(WIKI_NEWLINE);
						heading = true;
					}

					final String headLocation = WIKIPEDIA_SEARCH + headgear;
					if (isExternalLocation(headLocation))
					{
						if (!heading)
							page.append(getHeading(2, section.heading));
						page.append(getLink(headLocation, headgear + " on " + getUrlHost(headLocation)) + WIKI_NEWLINE).append(WIKI_NEWLINE);
						heading = true;
					}
					final String chestLocation = WIKIPEDIA_SEARCH + vest;
					if (isExternalLocation(chestLocation))
					{
						if (!heading)
							page.append(getHeading(2, section.heading));
						page.append(getLink(chestLocation, vest + " on " + getUrlHost(chestLocation)) + WIKI_NEWLINE).append(WIKI_NEWLINE);
						heading = true;
					}
					final String legLocation = WIKIPEDIA_SEARCH + pants;
					if (isExternalLocation(legLocation))
					{
						if (!heading)
							page.append(getHeading(2, section.heading));
						page.append(getLink(legLocation, pants + " on " + getUrlHost(legLocation)) + WIKI_NEWLINE).append(WIKI_NEWLINE);
						heading = true;
					}
					final String footLocation = WIKIPEDIA_SEARCH + footwear;
					if (isExternalLocation(headLocation))
					{
						if (!heading)
							page.append(getHeading(2, section.heading));
						page.append(getLink(footLocation, footwear + " on " + getUrlHost(footLocation)) + WIKI_NEWLINE).append(WIKI_NEWLINE);
						heading = true;
					}

				} else if (section == PageSectionItem.Properties)
				{
					final Collection<Collection<String>> propertiesData = Lists
							.newLinkedList();
					page.append(getHeading(2, section.heading));

					if (config.hasProperties() || config.params != null)
					{

						if (config.hasProperties())
						{
							final List<String> propertyNames = config.getPropertyNames();
							final List<String> propertyValues = config.getPropertyValues();
							for (int i = 0; i < propertyValues.size(); i++)
								propertiesData.add(ImmutableList.of(propertyNames.get(i), propertyValues.get(i)));
						}
						if (config.params != null)
						{
							for (int i = 0; i < config.params.values.size(); i++)
								propertiesData.add(ImmutableList.of(config.params.names[i], config.params.getPretty(i)));
						}
					}
					propertiesData.add(ImmutableList.of("Release Version",
							PolycraftMod.getVersionText(config.version)));
					page.append(getTable(PROPERTIES_HEADERS, propertiesData))
							.append(WIKI_NEWLINE);
				}
				else
				{
					page.append(getHeading(2, section.heading));
					if (section == PageSectionItem.Gallery)
					{
						page.append(getLinkFile(getTextureImageName(PolycraftMod.getFileSafeName(
								((Armor) config).getFullComponentName(ArmorSlot.HEAD))))).append(WIKI_NEWLINE);
						page.append(getLinkFile(getTextureImageName(PolycraftMod.getFileSafeName(
								((Armor) config).getFullComponentName(ArmorSlot.CHEST))))).append(WIKI_NEWLINE);
						page.append(getLinkFile(getTextureImageName(PolycraftMod.getFileSafeName(
								((Armor) config).getFullComponentName(ArmorSlot.LEGS))))).append(WIKI_NEWLINE);
						page.append(getLinkFile(getTextureImageName(PolycraftMod.getFileSafeName(
								((Armor) config).getFullComponentName(ArmorSlot.FEET))))).append(WIKI_NEWLINE);
					}

					else if (section == PageSectionItem.Recipes)
					{
						page.append(
								getRecipesGrid(
										new ItemStack(PolycraftRegistry.getItem(((Armor) config).getFullComponentName(ArmorSlot.HEAD))),
										null)).append(WIKI_NEWLINE);
						page.append(
								getRecipesGrid(
										new ItemStack(PolycraftRegistry.getItem(((Armor) config).getFullComponentName(ArmorSlot.CHEST))),
										null)).append(WIKI_NEWLINE);
						page.append(
								getRecipesGrid(
										new ItemStack(PolycraftRegistry.getItem(((Armor) config).getFullComponentName(ArmorSlot.LEGS))),
										null)).append(WIKI_NEWLINE);
						page.append(
								getRecipesGrid(
										new ItemStack(PolycraftRegistry.getItem(((Armor) config).getFullComponentName(ArmorSlot.FEET))),
										null)).append(WIKI_NEWLINE);
					}

				}
			}
			//			page.append(WIKI_NEWLINE).append(
			//					getCategoriesAsString(getAllCategories(config)));
			edit(title + " Armor", page.toString(), editSummary);
		}
	}

	private <C extends Config> void createToolsList(
			final ConfigRegistry<C> registry) throws LoginException,
			IOException {
		final Collection<String> categories = Sets.newLinkedHashSet();
		for (final C config : registry.values())
		{
			categories.addAll(getCategories(config));
		}

		final Collection<String> headers = Lists.newLinkedList();
		headers.add("Release Version");
		headers.add("Hoe ID");
		headers.add("Sword ID");
		headers.add("Spade ID");
		headers.add("Pickaxe ID");
		headers.add("Axe ID");
		headers.add("Crafting Item Shaft");
		headers.add("Crafting Item Head");
		headers.add("Tool Adjective");
		headers.add("Harvest Level");
		headers.add("Max Uses");
		headers.add("Efficiency");
		headers.add("Dmg vs. Entity");
		headers.add("Enchantability");

		final Collection<Collection<String>> data = Lists.newLinkedList();
		for (final C config : registry.values())
		{
			final Collection<String> row = Lists.newLinkedList();
			row.add(PolycraftMod.getVersionText(config.version));
			row.add(((Tool) config).typeGameIDs[Tool.Type.HOE.getValue()]);
			row.add(((Tool) config).typeGameIDs[Tool.Type.SWORD.getValue()]);
			row.add(((Tool) config).typeGameIDs[Tool.Type.SHOVEL.getValue()]);
			row.add(((Tool) config).typeGameIDs[Tool.Type.PICKAXE.getValue()]);
			row.add(((Tool) config).typeGameIDs[Tool.Type.AXE.getValue()]);
			row.add(((Tool) config).craftingShaftItemName);
			row.add(((Tool) config).craftingHeadItemName);
			row.add(((Tool) config).name);
			row.add(Integer.toString(((Tool) config).harvestLevel));
			row.add(Integer.toString(((Tool) config).maxUses));
			row.add(Integer.toString(((Tool) config).efficiency));
			row.add(Integer.toString(((Tool) config).damage));
			row.add(Integer.toString(((Tool) config).enchantability));
			data.add(row);

		}
		final StringBuilder page = new StringBuilder(
				getTable(headers, data));
		page.append(WIKI_NEWLINE).append(getCategoriesAsString(categories));
		edit(getListOfTypeTitle(Tool.class), page.toString(), editSummary);

	}

	private <C extends Config> void createToolsPage(final C config)
			throws LoginException, IOException {
		final String title = PolycraftMod.getFileSafeName(config.name) + " Tools";

		if (overwritePages || !wiki.exists(new String[] { title })[0])
		{
			logger.info("{} item page: {}", overwritePages ? "Overwriting"
					: "Creating", title);
			final StringBuilder page = new StringBuilder();
			for (final PageSectionItem section : PageSectionItem.values())
			{
				//Looks up the source on wikipedia if it can...
				if (section == PageSectionItem.External)
				{
					boolean heading = false;
					final String nameLocation = WIKIPEDIA_SEARCH + config.name;
					if (isExternalLocation(nameLocation))
					{
						if (!heading)
							page.append(getHeading(2, section.heading));
						page.append(getLink(nameLocation, config.name + " on " + getUrlHost(nameLocation)) + WIKI_NEWLINE).append(WIKI_NEWLINE);
						heading = true;
					}

				} else if (section == PageSectionItem.Properties)
				{
					final Collection<Collection<String>> propertiesData = Lists
							.newLinkedList();
					page.append(getHeading(2, section.heading));

					if (config.hasProperties() || config.params != null)
					{

						if (config.hasProperties())
						{
							final List<String> propertyNames = config.getPropertyNames();
							final List<String> propertyValues = config.getPropertyValues();
							for (int i = 0; i < propertyValues.size(); i++)
								propertiesData.add(ImmutableList.of(propertyNames.get(i), propertyValues.get(i)));
						}
						if (config.params != null)
						{
							for (int i = 0; i < config.params.values.size(); i++)
								propertiesData.add(ImmutableList.of(config.params.names[i], config.params.getPretty(i)));
						}
					}
					propertiesData.add(ImmutableList.of("Release Version",
							PolycraftMod.getVersionText(config.version)));
					page.append(getTable(PROPERTIES_HEADERS, propertiesData))
							.append(WIKI_NEWLINE);
				}
				else
				{
					page.append(getHeading(2, section.heading));
					if (section == PageSectionItem.Gallery)
					{
						page.append(getLinkFile(getTextureImageName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(
								((Tool) config).getFullTypeName(Tool.Type.HOE)))))).append(WIKI_NEWLINE);
						page.append(getLinkFile(getTextureImageName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(
								((Tool) config).getFullTypeName(Tool.Type.SWORD)))))).append(WIKI_NEWLINE);
						page.append(getLinkFile(getTextureImageName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(
								((Tool) config).getFullTypeName(Tool.Type.SHOVEL)))))).append(WIKI_NEWLINE);
						page.append(getLinkFile(getTextureImageName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(
								((Tool) config).getFullTypeName(Tool.Type.PICKAXE)))))).append(WIKI_NEWLINE);
						page.append(getLinkFile(getTextureImageName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(
								((Tool) config).getFullTypeName(Tool.Type.AXE)))))).append(WIKI_NEWLINE);
					}

					else if (section == PageSectionItem.Recipes)
					{
						page.append(
								getRecipesGrid(
										new ItemStack(PolycraftRegistry.getItem(((Tool) config).getFullTypeName(Tool.Type.HOE))),
										null)).append(WIKI_NEWLINE);
						page.append(
								getRecipesGrid(
										new ItemStack(PolycraftRegistry.getItem(((Tool) config).getFullTypeName(Tool.Type.SWORD))),
										null)).append(WIKI_NEWLINE);
						page.append(
								getRecipesGrid(
										new ItemStack(PolycraftRegistry.getItem(((Tool) config).getFullTypeName(Tool.Type.SHOVEL))),
										null)).append(WIKI_NEWLINE);
						page.append(
								getRecipesGrid(
										new ItemStack(PolycraftRegistry.getItem(((Tool) config).getFullTypeName(Tool.Type.PICKAXE))),
										null)).append(WIKI_NEWLINE);
						page.append(
								getRecipesGrid(
										new ItemStack(PolycraftRegistry.getItem(((Tool) config).getFullTypeName(Tool.Type.AXE))),
										null)).append(WIKI_NEWLINE);
					}

				}
			}
			//			page.append(WIKI_NEWLINE).append(
			//					getCategoriesAsString(getAllCategories(config)));
			edit(title, page.toString(), editSummary);
		}
	}
}
