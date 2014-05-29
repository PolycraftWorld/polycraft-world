package edu.utd.minecraft.mod.polycraft.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wikipedia.Wiki;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Config;
import edu.utd.minecraft.mod.polycraft.config.ConfigRegistry;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;

public class WikiMaker {

	private static final Logger logger = LogManager.getLogger();

	private static final String CONFIG_DIRECTORY = "src/main/resources/config";
	private static final String IMAGE_EXTENSION = "png";
	private static final String MINECRAFT_TEXTURES_DIRECTORY = "build/tmp/recompSrc/assets/minecraft/textures";
	private static final String[] MINECRAFT_TEXTURES_DIRECTORIES = new String[] {
			//TODO uncomment to enable upload
			//MINECRAFT_TEXTURES_DIRECTORY + "/blocks",
			//MINECRAFT_TEXTURES_DIRECTORY + "/items",
			//MINECRAFT_TEXTURES_DIRECTORY + "/armor",
			};
	private static final String POLYCRAFT_TEXTURES_DIRECTORY = "src/main/resources/assets/polycraft/textures";
	private static final String POLYCRAFT_CUSTOM_TEXTURES_DIRECTORY = "wiki/textures";
	private static final String[] POLYCRAFT_TEXTURES_DIRECTORIES = new String[] {
			//TODO uncomment to enable upload
			//POLYCRAFT_TEXTURES_DIRECTORY + "/blocks",
			//POLYCRAFT_TEXTURES_DIRECTORY + "/items",
			//POLYCRAFT_TEXTURES_DIRECTORY + "/armor",
			//POLYCRAFT_CUSTOM_TEXTURES_DIRECTORY + "/gui/container"
			};
	private static final String WIKI_NEWLINE = "\n";

	private enum Section {
		Description, Recipes, History, Gallery, References;

		public final String heading;

		private Section() {
			this.heading = this.toString();
		}

		private Section(final String heading) {
			this.heading = heading;
		}
	};

	private static String[] HEADING_FORMATS = new String[] {
			"== %s ==",
			"=== %s ===",
			"==== %s ====",
			"===== %s =====",
			"====== %s ======",
	};

	private static String createHeading(final int level, final String text) {
		return String.format(HEADING_FORMATS[level - 2], text);
	}

	private static String LINK_FORMAT = "[[%s]]";

	private static String createLink(final String location) {
		return String.format(LINK_FORMAT, location);
	}

	private static String LINK_FORMAT_ALT = "[[%s|%s]]";

	private static String createLink(final String location, final String text) {
		return String.format(LINK_FORMAT_ALT, location, text);
	}

	private static String createLinkFile(final String file) {
		return createLink("File:" + file);
	}

	private static final String RECIPE_CELL_FORMAT = "|SLOT-%1$s=%2$s|SLOT-%1$s-image=%3$s|SLOT-%1$s-link=%4$s";
	private static final String RECIPE_CELL_AMOUNT_FORMAT = RECIPE_CELL_FORMAT + "|SLOT-%1$s-amount=%5$d";

	private static String getRecipeCell(final int slotIndex, final String itemName, final int amount, final String image, final String link) {
		if (amount > 1)
			return String.format(RECIPE_CELL_AMOUNT_FORMAT, slotIndex, itemName, image, link, amount);
		return String.format(RECIPE_CELL_FORMAT, slotIndex, itemName, image, link);
	}

	private static String getRecipeCell(final int slotIndex, final ItemStack itemStack) {
		return getRecipeCell(slotIndex, itemStack.getDisplayName(), itemStack.stackSize, getTextureImageName(getTexture(itemStack)), itemStack.getDisplayName());
	}

	private static final String GRID_TEMPLATE_FORMAT = "{{Grid/%1$s%2$s%3$s}}";

	private static String getGridTemplate(final PolycraftContainerType containerType, final String content) {
		return String.format(GRID_TEMPLATE_FORMAT, containerType.toString().replaceAll(" ", "_"), content, WIKI_NEWLINE);
	}

	private static String getRecipeGrid(final PolycraftRecipe recipe) {
		final StringBuilder grid = new StringBuilder();
		for (final RecipeInput input : recipe.getInputs())
			for (final ItemStack inputStack : input.inputs)
				grid.append(WIKI_NEWLINE).append(getRecipeCell(input.slot.getSlotIndex(), inputStack));
		for (final RecipeComponent output : recipe.getOutputs(null))
			grid.append(WIKI_NEWLINE).append(getRecipeCell(output.slot.getSlotIndex(), output.itemStack));
		if (!recipe.isShapedOnly())
			grid.append(WIKI_NEWLINE).append("|").append("Shapeless=Yes");
		return getGridTemplate(recipe.getContainerType(), grid.toString());
	}

	private static String getTexture(final ItemStack itemStack) {
		final String iconName = itemStack.getItem().getIcon(itemStack, 0).getIconName();
		final int namespaceIndex = iconName.indexOf(":");
		if (namespaceIndex > -1)
			return iconName.substring(namespaceIndex + 1);
		return iconName;
	}

	private final Map<Object, Map<PolycraftContainerType, Set<PolycraftRecipe>>> recipesByIngredientContainerType;
	private final Wiki wiki;
	private final String editSummary;
	private final boolean overwritePages;

	public static void generate(final String url, final String scriptPath, final String username, final String password, final boolean overwritePages) {
		try {
			WikiMaker wikiMaker = new WikiMaker(url, scriptPath, username, password, overwritePages);
			wikiMaker.createImages(MINECRAFT_TEXTURES_DIRECTORIES);
			wikiMaker.createImages(POLYCRAFT_TEXTURES_DIRECTORIES);
			wikiMaker.createIngots();
			wikiMaker.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Failed: {}", ex.getMessage());
		}
	}

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

	private boolean createPage(final String title) throws LoginException, IOException {
		if (overwritePages || !wiki.exists(new String[] { title })[0]) {
			logger.info("{} page: {}", overwritePages ? "Overwriting" : "Creating", title);
			final StringBuilder page = new StringBuilder();
			for (final Section section : Section.values())
				page.append(createHeading(2, section.heading)).append(WIKI_NEWLINE);
			wiki.edit(title, page.toString(), editSummary);
			return true;
		}
		return false;
	}

	private <C extends Config> void createListPageConfigs(final ConfigRegistry<C> registry) throws LoginException, IOException {
		if (registry.size() > 0) {
			String title = null;
			final StringBuilder list = new StringBuilder();
			for (final C config : registry.values()) {
				if (title == null)
					title = String.format("List of %ss", config.getClass().getSimpleName());
				list.append(WIKI_NEWLINE).append("* ").append(createLink(config.name));
			}
			wiki.edit(title, list.toString(), editSummary);
		}
	}

	private boolean createRecipesGrid(final String pageName, final ItemStack ingredient) throws LoginException, IOException {
		final Map<PolycraftContainerType, Set<PolycraftRecipe>> recipesByContainerType = recipesByIngredientContainerType.get(PolycraftRegistry.itemOrBlockByItem.get(ingredient.getItem()));
		if (recipesByContainerType != null) {
			final StringBuilder page = new StringBuilder(createHeading(2, Section.Recipes.heading));
			for (final Entry<PolycraftContainerType, Set<PolycraftRecipe>> recipeEntry : recipesByContainerType.entrySet()) {
				page.append(WIKI_NEWLINE).append(createHeading(3, recipeEntry.getKey().toString()));
				for (final PolycraftRecipe recipe : recipeEntry.getValue())
					page.append(WIKI_NEWLINE).append(WIKI_NEWLINE).append(getRecipeGrid(recipe));
			}
			//TODO section update isn't working? it just adds another section...
			wiki.edit(pageName, page.toString(), editSummary, Section.Recipes.ordinal());
			return true;
		}
		return false;
	}

	private void createIngots() throws LoginException, IOException {
		for (final Ingot ingot : Ingot.registry.values()) {
			createPage(ingot.name);
			createRecipesGrid(ingot.name, ingot.getItemStack());
			wiki.edit(ingot.name, createHeading(2, Section.Gallery.heading) + WIKI_NEWLINE + createLinkFile(getTextureImageName(getTexture(ingot.getItemStack()))), editSummary, Section.Gallery.ordinal());
		}
		createListPageConfigs(Ingot.registry);
	}
}
