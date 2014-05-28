package edu.utd.minecraft.mod.polycraft.util;

import java.io.File;
import java.io.IOException;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wikipedia.Wiki;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Config;
import edu.utd.minecraft.mod.polycraft.config.ConfigRegistry;
import edu.utd.minecraft.mod.polycraft.config.Ingot;

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
		Description, Obtaining, History, Gallery, References;

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
		return String.format(HEADING_FORMATS[level], text);
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

	private final Wiki wiki;
	private final String editSummary;
	private final boolean overwritePages;

	public static void generate(final String url, final String scriptPath, final String username, final String password, final boolean overwritePages) {
		try {
			WikiMaker wikiMaker = new WikiMaker(url, scriptPath, username, password, overwritePages);
			wikiMaker.uploadImages(MINECRAFT_TEXTURES_DIRECTORIES);
			wikiMaker.uploadImages(POLYCRAFT_TEXTURES_DIRECTORIES);
			wikiMaker.uploadIngots();
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
	}

	private void close() {
		wiki.logout();
	}

	/**
	 * Uploads the images in the given paths to the wikipedia site. Will overwrite any existing images.
	 */
	private void uploadImages(final String[] paths) throws IOException, LoginException {
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

	private static String createFurnaceGrid(
			final String inputName, final String inputLink,
			final String fuelName, final String fuelLink,
			final String outputName, final String outputLink) {
		final StringBuilder furnace = new StringBuilder(createHeading(3, "Smelting"));
		furnace.append(WIKI_NEWLINE);
		furnace.append("{{Grid/Furnace").append(WIKI_NEWLINE);
		furnace.append("|A1=").append(inputName).append("|A1-link=").append(inputLink).append(WIKI_NEWLINE);
		furnace.append("|A2=").append(fuelName).append("|A2-link=").append(fuelLink).append(WIKI_NEWLINE);
		furnace.append("|Output=").append(outputName).append("|Output-link=").append(outputLink).append(WIKI_NEWLINE);
		furnace.append("}}").append(WIKI_NEWLINE);
		return furnace.toString();
	}

	private void uploadIngots() throws LoginException, IOException {
		for (final Ingot ingot : Ingot.registry.values()) {
			if (createPage(ingot.name)) {
				wiki.edit(ingot.name, createHeading(2, Section.Gallery.heading) + WIKI_NEWLINE + createLinkFile(ingot.name.toLowerCase() + "." + IMAGE_EXTENSION), editSummary, Section.Gallery.ordinal());
				//TODO need to load recipes from recipe manager (populate hashmap by item so we can do a lookup)
				//TODO correct hard coded reference to ore_element
				//TODO correct hard coded reference to coal?
				wiki.edit(ingot.name, createFurnaceGrid("ore_element", ingot.source.name, "coal", "Coal", ingot.name.toLowerCase(), ingot.name), editSummary, Section.Obtaining.ordinal());
			}
		}
		createListPageConfigs(Ingot.registry);
	}
}
