package edu.utd.minecraft.mod.polycraft.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
import edu.utd.minecraft.mod.polycraft.config.Compound;
import edu.utd.minecraft.mod.polycraft.config.Config;
import edu.utd.minecraft.mod.polycraft.config.ConfigRegistry;
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
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeSlot;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.heated.distillationcolumn.DistillationColumnInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.extruder.ExtruderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.injectionmolder.InjectionMolderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.steamcracker.SteamCrackerInventory;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.MachiningMillInventory;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerBlock;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerWall;

public class WikiMaker {

	private static final Logger logger = LogManager.getLogger();

	private static final String WIKIPEDIA_SEARCH = "http://en.wikipedia.org/wiki/Special:Search/";
	private static final String MINECRAFT_WIKI = "http://minecraft.gamepedia.com/";
	private static final String CONFIG_DIRECTORY = "src/main/resources/config";
	private static final String IMAGE_EXTENSION = "png";
	private static final String MINECRAFT_TEXTURES_DIRECTORY = "build/tmp/recompSrc/assets/minecraft/textures";
	private static final String[] MINECRAFT_TEXTURES_DIRECTORIES = new String[] {
			MINECRAFT_TEXTURES_DIRECTORY + "/blocks",
			MINECRAFT_TEXTURES_DIRECTORY + "/items",
			MINECRAFT_TEXTURES_DIRECTORY + "/armor",
	};
	private static final String POLYCRAFT_TEXTURES_DIRECTORY = "src/main/resources/assets/polycraft/textures";
	private static final String POLYCRAFT_CUSTOM_TEXTURES_DIRECTORY = "wiki/textures";
	private static final String[] POLYCRAFT_TEXTURES_DIRECTORIES = new String[] {
			POLYCRAFT_CUSTOM_TEXTURES_DIRECTORY,
			POLYCRAFT_TEXTURES_DIRECTORY + "/blocks",
			POLYCRAFT_TEXTURES_DIRECTORY + "/items",
			POLYCRAFT_TEXTURES_DIRECTORY + "/armor",
			POLYCRAFT_CUSTOM_TEXTURES_DIRECTORY + "/gui/container"
	};
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

	public static void generate(final String url, final String scriptPath, final String username, final String password, final boolean overwritePages) {
		try {
			WikiMaker wikiMaker = new WikiMaker(url, scriptPath, username, password, overwritePages);
			//wikiMaker.createImages(MINECRAFT_TEXTURES_DIRECTORIES);
			//wikiMaker.createImages(POLYCRAFT_TEXTURES_DIRECTORIES);
			//wikiMaker.createRecipePage(PolycraftContainerType.CRAFTING_TABLE);
			//wikiMaker.createRecipePage(PolycraftContainerType.FURNACE);
			//wikiMaker.createFuelPage();
			//wikiMaker.createItemTypesPage(ImmutableList.of(
			//		Ore.class, Ingot.class, Catalyst.class, ElementVessel.class, CompoundVessel.class,
			//		PolymerPellets.class, PolymerFibers.class, PolymerBlock.class, PolymerSlab.class, PolymerStairs.class, PolymerWall.class,
			//		Mold.class, MoldedItem.class, GrippedTool.class, PogoStick.class, Inventory.class, CustomObject.class));
			/*
			wikiMaker.createItemPages(Inventory.registry);
			wikiMaker.createItemPages(Ore.registry);
			wikiMaker.createItemPages(Ingot.registry);
			wikiMaker.createItemPages(Catalyst.registry);
			wikiMaker.createItemPages(ElementVessel.registry);
			wikiMaker.createItemPages(CompoundVessel.registry);
			wikiMaker.createItemPages(PolymerPellets.registry);
			wikiMaker.createItemPages(PolymerFibers.registry);
			wikiMaker.createItemPages(PolymerBlock.registry);
			wikiMaker.createItemPages(PolymerSlab.registry);
			wikiMaker.createItemPages(PolymerStairs.registry);
			wikiMaker.createItemPages(PolymerWall.registry);
			wikiMaker.createItemPages(Mold.registry);
			wikiMaker.createItemPages(MoldedItem.registry);
			wikiMaker.createItemPages(GrippedTool.registry);
			wikiMaker.createItemPages(PogoStick.registry);
			wikiMaker.createItemPages(CustomObject.registry);
			*/
			wikiMaker.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Failed: {}", ex.getMessage());
		}
	}

	private static String[] HEADING_FORMATS = new String[] {
			"=%s=" + WIKI_NEWLINE,
			"==%s==" + WIKI_NEWLINE,
			"===%s===" + WIKI_NEWLINE,
			"====%s====" + WIKI_NEWLINE,
			"=====%s=====" + WIKI_NEWLINE,
			"======%s======" + WIKI_NEWLINE,
	};

	private static String getHeading(final int level, final String text) {
		return String.format(HEADING_FORMATS[level - 1], text);
	}

	private static String getItemStackLocation(final ItemStack itemStack) {
		if (PolycraftRegistry.minecraftItems.contains(itemStack.getItem()))
			return MINECRAFT_WIKI + itemStack.getDisplayName().replaceAll(" ", "%20");
		return getItemStackName(itemStack);
	}

	private static String getConfigLocation(Config config) {
		if (config instanceof Fuel)
			config = ((Fuel) config).source;
		if (config instanceof GameIdentifiedConfig)
			return config.name;
		if (config instanceof Element || config instanceof Compound || config instanceof Mineral || config instanceof Alloy || config instanceof Polymer)
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
		if (isExternalLocation(location))
			return String.format(LINK_EXTERNAL_FORMAT, location.replaceAll(" ", "%20"));
		return String.format(LINK_INTERNAL_FORMAT, location);
	}

	private static String getLink(final Config config) {
		return getLink(getConfigLocation(config), config.name);
	}

	private static String LINK_INTERNAL_FORMAT_ALT = "[[%s|%s]]";
	private static String LINK_EXTERNAL_FORMAT_ALT = "[%s %s]";

	private static String getLink(final String location, final String text) {
		if (isExternalLocation(location))
			return String.format(LINK_EXTERNAL_FORMAT_ALT, location.replaceAll(" ", "%20"), text);
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

	private static String getLinkImage(final String location, final String image, int size) {
		return getLinkImage(location, image, location, location, size);
	}

	private static String getLinkImage(final String text, final String image, final String location, final String alt, final int size) {
		return String.format(LINK_FORMAT_IMAGE_ALT, image, location, size, alt, text);
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

	private static Collection<String> getCategories(final Config config, final int maxDepth) {
		return getCategories(config, maxDepth, 0);
	}

	private static Collection<String> getCategories(final Config config, final int maxDepth, final int depth) {
		final Collection categories = Sets.newLinkedHashSet();
		categories.add(getTitle(config.getClass(), false));
		if (config instanceof SourcedConfig) {
			if (config instanceof SourcedVesselConfig) {
				categories.add("Vessel");
				categories.add(getTitle(((SourcedVesselConfig) config).vesselType.toString(), false));
			}
			if (maxDepth == -1 || depth < maxDepth) {
				final Config source = ((SourcedConfig) config).source;
				if (source != null) {
					categories.add(source.name);
					categories.addAll(getCategories(source, maxDepth, depth + 1));
				}
			}
		}
		return categories;
	}

	private static String getCategoriesAsString(final Collection<String> categories) {
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
			if (Character.isLetter(title.charAt(i)) && Character.isUpperCase(title.charAt(i))) {
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

	private static String getTable(final Collection<String> headers, final Collection<Collection<String>> data) {
		return getTable(headers, data, true, true);
	}

	private static String getTable(final Collection<String> headers, final Collection<Collection<String>> data, final boolean sortable, final boolean collapsible) {
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
		return String.format(TABLE_FORMAT, sortable ? " sortable" : "", collapsible ? " collapsible" : "", table.toString());
	}

	private static final String INVENTORY_SLOT_FORMAT = "{{Inventory/Slot|index=%d|title=%s|image=%s|link=%s}}";
	private static final String INVENTORY_SLOT_WITH_AMOUNT_FORMAT = "{{Inventory/Slot|index=%d|title=%s|image=%s|link=%s|amount=%d}}";

	private static String getInventorySlot(final int slotIndex, final String title, final String image, final String link, final int amount) {
		if (amount > 1)
			return String.format(INVENTORY_SLOT_WITH_AMOUNT_FORMAT, slotIndex, title, image, link, amount);
		return String.format(INVENTORY_SLOT_FORMAT, slotIndex, title, image, link);
	}

	private static String getItemStackName(final ItemStack itemStack) {
		final Item item = itemStack.getItem();
		if (item instanceof ItemPolymerBlock)
			return ((ItemPolymerBlock) item).blockPolymer.polymerBlock.name;
		if (item instanceof ItemPolymerWall)
			return ((ItemPolymerWall) item).blockPolymerWall.polymerWall.name;
		return itemStack.getDisplayName();
	}

	private static String getInventorySlot(final int slotIndex, final ItemStack itemStack) {
		return getInventorySlot(slotIndex, getItemStackName(itemStack), getTextureImageName(getTexture(itemStack)), getItemStackLocation(itemStack), itemStack.stackSize);
	}

	private static String getInventoryWaterSlot(final int slotIndex) {
		return getInventorySlot(slotIndex, new ItemStack(Items.water_bucket));
	}

	private static String getInventoryFuelSlot(final int slotIndex) {
		return getInventorySlot(slotIndex, "Fuel", "Fuel.png", getListOfTypeTitle(Fuel.class), 1);
	}

	private static String getSpecialInventorySlots(final PolycraftContainerType containerType) {
		final StringBuilder slots = new StringBuilder();
		switch (containerType) {
		case MACHINING_MILL:
			slots.append(getInventoryWaterSlot(MachiningMillInventory.slotIndexCoolingWater));
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
		case CHEMICAL_PROCESSOR:
			break;
		default:
			break;
		}
		return slots.toString();
	}

	private static final String INVENTORY_FORMAT = "{{Inventory|%s|type=%s|shapeless=%s}}";

	private static String getInventory(final PolycraftContainerType containerType, final String slots, final boolean shapeless) {
		return String.format(INVENTORY_FORMAT, slots, containerType.toString().toLowerCase().replaceAll(" ", "-"), String.valueOf(shapeless));
	}

	private static Collection<String> getRecipeGridRow(final PolycraftRecipe recipe) {
		final Collection<String> row = Lists.newLinkedList();
		final StringBuilder slots = new StringBuilder();
		final Map<String, String> inputs = Maps.newLinkedHashMap();
		final LinkedList<ContainerSlot> intputSlots = Lists.newLinkedList();
		intputSlots.addAll(recipe.getContainerType().getSlots(SlotType.INPUT));
		for (final RecipeInput input : recipe.getInputs()) {
			for (final ItemStack inputStack : input.inputs) {
				inputs.put(getItemStackName(inputStack), getItemStackLocation(inputStack));
				int slotIndex = input.slot.getSlotIndex();
				if (slotIndex == RecipeSlot.ANY.slotIndex)
					slotIndex = intputSlots.remove().getSlotIndex();
				slots.append(getInventorySlot(slotIndex, inputStack));
			}
		}
		final Map<String, String> outputs = Maps.newLinkedHashMap();
		for (final RecipeComponent output : recipe.getOutputs(null)) {
			outputs.put(getItemStackName(output.itemStack), getItemStackLocation(output.itemStack));
			slots.append(getInventorySlot(output.slot.getSlotIndex(), output.itemStack));
		}
		slots.append(getSpecialInventorySlots(recipe.getContainerType()));
		final StringBuilder inputList = new StringBuilder();
		for (final Entry<String, String> input : inputs.entrySet())
			inputList.append(WIKI_NEWLINE).append("* ").append(getLink(input.getValue(), input.getKey()));
		final StringBuilder outputList = new StringBuilder();
		for (final Entry<String, String> output : outputs.entrySet())
			outputList.append(WIKI_NEWLINE).append("* ").append(getLink(output.getValue(), output.getKey()));
		row.add(inputList.toString());
		row.add(outputList.toString());
		row.add(getInventory(recipe.getContainerType(), slots.toString(), recipe.getContainerType() == PolycraftContainerType.CRAFTING_TABLE && !recipe.isShapedOnly()));
		return row;
	}

	private static <C extends GameIdentifiedConfig> String getTexture(C config) {
		return config instanceof Inventory ? "gui_" + config.name : getTexture(config.getItemStack());
	}

	private static String getTexture(final ItemStack itemStack) {
		String iconName = itemStack.getItem().getIcon(itemStack, 0).getIconName();
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

	public WikiMaker(final String url, final String scriptPath, final String username, final String password, final boolean overwritePages) throws FailedLoginException, IOException {
		this.wiki = new Wiki(url, scriptPath);
		this.wiki.login(username, password);
		this.editSummary = PolycraftMod.MODID + " " + PolycraftMod.VERSION;
		this.overwritePages = overwritePages;
		this.recipesByIngredientContainerType = PolycraftMod.recipeManager.getRecipesByIngredientContainerType();
	}

	private void close() {
		wiki.logout();
	}

	private static String getTextureImageName(final String texture) {
		return Character.toUpperCase(texture.charAt(0)) + texture.substring(1).toLowerCase().replaceAll(" ", "_") + "." + IMAGE_EXTENSION;
	}

	/**
	 * Uploads the images in the given paths to the wikipedia site. Will overwrite any existing images.
	 */
	private void createImages(final String[] paths) throws IOException, LoginException {
		for (final String path : paths)
			for (final File imageFile : new File(path).listFiles())
				uploadImage(imageFile);
	}

	private void uploadImage(final File imageFile) throws LoginException, IOException {
		if (imageFile.getAbsolutePath().endsWith("." + IMAGE_EXTENSION)) {
			final String name = imageFile.getName().replaceAll("." + IMAGE_EXTENSION, "");
			logger.info("Uploading image: {}", name);
			wiki.upload(imageFile, name, "", editSummary);
		}
	}

	private final Collection<String> FUEL_HEADERS = ImmutableList.of("Fuel", "Heat Intensity", "Heat Duration (secs)");

	private void createFuelPage() throws LoginException, IOException {
		final Collection<Collection<String>> data = Lists.newLinkedList();
		for (final Entry<Item, QuantifiedFuel> fuelEntry : Fuel.quantifiedFuelsByItem.entrySet()) {
			final Collection<String> row = Lists.newLinkedList();
			final ItemStack fuelStack = new ItemStack(fuelEntry.getKey());
			row.add(getLink(getItemStackLocation(fuelStack), getItemStackName(fuelStack)));
			row.add(PolycraftMod.numFormat.format(fuelEntry.getValue().fuel.heatIntensity));
			row.add(PolycraftMod.numFormat.format(fuelEntry.getValue().getHeatDuration()));
			data.add(row);
		}
		wiki.edit(getListOfTypeTitle(Fuel.class), getTable(FUEL_HEADERS, data), editSummary);
	}

	private final Collection<String> RECIPE_GRID_HEADERS = ImmutableList.of("Inputs", "Outputs", "Recipe");

	private boolean createSectionRecipesGrid(final ItemStack ingredient, final String pageName, final String sectionHeader, final int sectionIndex,
			final PolycraftContainerType forceIncludeType) throws LoginException, IOException {
		final Collection<PolycraftRecipe> forceIncludedRecipes = forceIncludeType == null ? null : PolycraftMod.recipeManager.getRecipesByContainerType(forceIncludeType);
		final Map<PolycraftContainerType, Collection<PolycraftRecipe>> recipesByContainerType = recipesByIngredientContainerType.get(ingredient.getItem());
		if (forceIncludedRecipes != null || recipesByContainerType != null) {
			final StringBuilder page = new StringBuilder(getHeading(2, sectionHeader));
			if (recipesByContainerType != null) {
				for (final Entry<PolycraftContainerType, Collection<PolycraftRecipe>> recipeEntry : recipesByContainerType.entrySet()) {
					final PolycraftContainerType containerType = recipeEntry.getKey();
					if (containerType != forceIncludeType) {
						page.append(WIKI_NEWLINE).append(getHeading(3, containerType.toString()));
						final Collection<Collection<String>> data = Lists.newLinkedList();
						for (final PolycraftRecipe recipe : recipeEntry.getValue())
							data.add(getRecipeGridRow(recipe));
						page.append(WIKI_NEWLINE).append(getTable(RECIPE_GRID_HEADERS, data));
					}
				}
			}
			if (forceIncludedRecipes != null) {
				page.append(WIKI_NEWLINE).append(getHeading(3, forceIncludeType.toString()));
				final Collection<Collection<String>> data = Lists.newLinkedList();
				for (final PolycraftRecipe recipe : forceIncludedRecipes)
					data.add(getRecipeGridRow(recipe));
				page.append(WIKI_NEWLINE).append(getTable(RECIPE_GRID_HEADERS, data));
			}
			wiki.edit(pageName, page.toString(), editSummary, sectionIndex);
			return true;
		}
		return false;
	}

	private void createRecipePage(final PolycraftContainerType containerType) throws LoginException, IOException {
		final Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManager.getRecipesByContainerType(containerType);
		if (recipes != null) {
			final StringBuilder page = new StringBuilder();
			final Collection<Collection<String>> data = Lists.newLinkedList();
			for (final PolycraftRecipe recipe : recipes)
				data.add(getRecipeGridRow(recipe));
			page.append(WIKI_NEWLINE).append(getTable(RECIPE_GRID_HEADERS, data));
			wiki.edit(containerType.toString(), page.toString(), editSummary);
		}
	}

	private void createItemTypesPage(final Collection<Class<? extends GameIdentifiedConfig>> types) throws LoginException, IOException {
		final StringBuilder list = new StringBuilder();
		for (final Class<? extends GameIdentifiedConfig> type : types)
			list.append(WIKI_NEWLINE).append("* ").append(getLink(getListOfTypeTitle(type), getTitle(type, true)));
		wiki.edit("List of Item Types", list.toString(), editSummary);
	}

	private boolean gameIdentifiedConfigHasItem(final GameIdentifiedConfig config) {
		if (config.getItemStack().getItem() == null) {
			logger.warn("Unable to find item for: {}", config.name);
			return false;
		}
		return true;
	}

	private <C extends GameIdentifiedConfig> void createItemPages(final ConfigRegistry<C> registry) throws LoginException, IOException {
		createItemPageList(registry);
		for (final C config : registry.values())
			if (gameIdentifiedConfigHasItem(config))
				createItemPage(config);
	}

	private final Collection<String> PROPERTIES_HEADERS = ImmutableList.of("Name", "Value");

	private <C extends GameIdentifiedConfig> void createItemPage(final C config) throws LoginException, IOException {
		final String title = config.name;
		int index = 1;
		int recipeSectionIndex = -1;
		if (overwritePages || !wiki.exists(new String[] { title })[0]) {
			logger.info("{} item page: {}", overwritePages ? "Overwriting" : "Creating", title);
			final StringBuilder page = new StringBuilder();
			for (final PageSectionItem section : PageSectionItem.values()) {
				if (section == PageSectionItem.External) {
					if (config instanceof SourcedConfig) {
						final Config source = ((SourcedConfig) config).source;
						if (source != null) {
							final String location = getConfigLocation(source);
							if (isExternalLocation(location)) {
								page.append(getHeading(2, section.heading));
								index++;
								page.append(getLink(location, source.name + " on " + getUrlHost(location))).append(WIKI_NEWLINE);
							}
						}
					}
				}
				else if (section == PageSectionItem.Properties) {
					if (config.hasProperties() || config.params != null) {
						page.append(getHeading(2, section.heading));
						index++;
						final Collection<Collection<String>> propertiesData = Lists.newLinkedList();
						if (config.hasProperties()) {
							final List<String> propertyNames = config.getPropertyNames();
							final List<String> propertyValues = config.getPropertyValues();
							for (int i = 0; i < propertyValues.size(); i++)
								propertiesData.add(ImmutableList.of(propertyNames.get(i), propertyValues.get(i)));
						}
						if (config.params != null) {
							for (int i = 0; i < config.params.values.size(); i++)
								propertiesData.add(ImmutableList.of(config.params.names[i], config.params.getPretty(i)));
						}
						page.append(getTable(PROPERTIES_HEADERS, propertiesData)).append(WIKI_NEWLINE);
					}
				}
				else {
					if (section == PageSectionItem.Recipes)
						recipeSectionIndex = index;
					page.append(getHeading(2, section.heading));
					index++;
					if (section == PageSectionItem.Gallery)
						page.append(getLinkFile(getTextureImageName(getTexture(config)))).append(WIKI_NEWLINE);
				}
			}
			page.append(WIKI_NEWLINE).append(getCategoriesAsString(getAllCategories(config)));
			wiki.edit(title, page.toString(), editSummary);
		}
		//TODO the section index could change due to sub sections!
		createSectionRecipesGrid(config.getItemStack(), title, PageSectionItem.Recipes.heading, recipeSectionIndex,
				config instanceof Inventory ? ((Inventory) config).containerType : null);
	}

	private <C extends GameIdentifiedConfig> void createItemPageList(final ConfigRegistry<C> registry) throws LoginException, IOException {
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
						}
						else if (singleSourceType != sourceType)
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
				}
				else {
					headers.add(getTitle(singleSourceType, false));
				}
			}
			if (isVesseled)
				headers.add("Vessel");
			if (firstConfig.hasProperties())
				headers.addAll(firstConfig.getPropertyNames());

			final Collection<Collection<String>> data = Lists.newLinkedList();
			for (final C config : registry.values()) {
				if (gameIdentifiedConfigHasItem(config)) {
					final Collection<String> row = Lists.newLinkedList();
					row.add(config.gameID);
					if (config instanceof Inventory)
						row.add(getLinkImage(config.name, getTextureImageName(getTexture(config)), 350));
					else
						row.add(getLinkImage(config.name, getTextureImageName(getTexture(config))));
					row.add(getLink(config.name));
					if (isSourced) {
						final Config source = ((SourcedConfig) config).source;
						if (singleSourceType == null) {
							row.add(source == null ? "" : getLink(source));
							row.add(source == null ? "" : getLinkCategory(getTitle(source.getClass(), false)));
						}
						else {
							row.add(source == null ? "" : getLink(source));
						}
					}
					if (isVesseled)
						row.add(getLinkCategory(getTitle(((SourcedVesselConfig) config).vesselType.toString(), false)));
					if (config.hasProperties())
						row.addAll(config.getPropertyValues());
					data.add(row);
				}
			}
			final StringBuilder page = new StringBuilder(getTable(headers, data));
			page.append(WIKI_NEWLINE).append(getCategoriesAsString(categories));
			wiki.edit(getListOfTypeTitle(type), page.toString(), editSummary);
		}
	}
}