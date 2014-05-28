package edu.utd.minecraft.mod.polycraft.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.wikipedia.Wiki;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.config.Ingot;


public class WikiMaker {

	private static final String CONFIG_DIRECTORY = "src/main/resources/config";
	private static final String EDIT_REASON = "Polycraft 1.0";
	
	private static final int WIKI_SECTION_DESCRIPTION = 0;
	private static final int WIKI_SECTION_OBTAINING = 1;
	private static final int WIKI_SECTION_HISTORY = 2;
	private static final int WIKI_SECTION_GALLERY = 3;
	private static final int WIKI_SECTION_REFERENCES = 4;
	
	private Wiki wiki;
	
	/**
	 * If true, deletes all pages before editing them
	 */
	private boolean createFromScratch = true;

	public static void main(String[] args) {
		try {
			WikiMaker wikiMaker = new WikiMaker();
			wikiMaker.uploadIngots();
			//wikiMaker.uploadMinecraftImages();
		} catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed: " + ex.getMessage());
		}
	}

	public WikiMaker() throws FailedLoginException, IOException {
		wiki = new Wiki("www.polycraftworld.com", "/wiki");
		wiki.login("Polycraftbot", "gmratst6zf");
		
	}

	public void close() {
		wiki.logout();
	}
	
	private void createWikiPage(String title) throws LoginException, IOException {
		wiki.edit(title, "==Obtaining==\n==History==\n==Gallery==\n==References==\n", EDIT_REASON);
	}

	public static Collection<String[]> readResourceFileDelimeted(final String directory, final String name) throws FileNotFoundException {
		return readResourceFileDelimeted(directory, name, "tsv", "\t");
	}

	public static Collection<String[]> readResourceFileDelimeted(final String directory, final String name, final String extension, final String delimeter) throws FileNotFoundException {
		Collection<String[]> config = new LinkedList<String[]>();
		// TODO: Use classloader ?
		//final BufferedReader br = new BufferedReader(new InputStreamReader(PolycraftMod.class.getClassLoader().getResourceAsStream(directory + "/" + name + "." + extension)));
		final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(directory, name + "." + extension))));
		try {
			br.readLine();//skip the first line (headers)
			for (String line; (line = br.readLine()) != null;) {
				config.add(line.split(delimeter));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return config;
	}
	
	/**
	 * Uploads ingots to the wiki
	 */
	public void uploadIngots() throws LoginException, IOException {
		List<String> allIngots = Lists.newArrayList();
		for (final String[] line : readResourceFileDelimeted(CONFIG_DIRECTORY, Ingot.class.getSimpleName().toLowerCase())) {
			if (line.length == 0) {
				continue;
			}
			
			String ingotName = line[1];
			String sourceType = line[2];
			String sourceName = line[3];
			int modDamagePerUse = Integer.parseInt(line[4]);

			System.out.println("Creating page for " + ingotName);
			if (createFromScratch || !wiki.exists(new String [] { ingotName } )[0]) {
				createWikiPage(ingotName);
			}
			wiki.edit(ingotName, "==Gallery==\n[[File:" + ingotName.toLowerCase() + ".png]]", EDIT_REASON, WIKI_SECTION_GALLERY);
			
			StringBuilder sb = new StringBuilder();
			sb.append("== Obtaining ==\n=== Smelting ===\n");
			sb.append("{{Grid/Furnace\n");
			sb.append("|A1=ore_element|A1-link=");
			sb.append(sourceName);
			if (sourceType.equals("Element")) {
				sb.append(" Ore");
			}
			sb.append("\n|A2=coal|A2-link=Coal\n");
			sb.append("|Output=" + ingotName.toLowerCase() + "|Output-link=" + ingotName + "\n");
			sb.append("}}\n");
			wiki.edit(ingotName, sb.toString(), EDIT_REASON, WIKI_SECTION_OBTAINING);
			
			allIngots.add(ingotName);
		}
		
		// Create the list of ingots
		StringBuilder sb = new StringBuilder();		
		sb.append("== List of Ingots ==\n");
		for (String ingot : allIngots) {
			sb.append("* [[" + ingot + "]]\n");
		}
		wiki.edit("List of Ingots", sb.toString(), EDIT_REASON);		
	}

	/**
	 * Uploads the original minecraft images from the disassembled source to the wikipedia site. Will overwrite
	 * any existing images.
	 */
	public void uploadMinecraftImages() throws IOException, LoginException {
		String [] paths = new String [] {
			"build/tmp/recompSrc/assets/minecraft/textures/blocks",
			"build/tmp/recompSrc/assets/minecraft/textures/gui/container",
			"build/tmp/recompSrc/assets/minecraft/textures/items",
			"build/tmp/recompSrc/assets/minecraft/textures/models/armor",			
		};
		for (String path : paths) {
			for (File imageFile : new File(path).listFiles()) {
				if (imageFile.getAbsolutePath().endsWith(".png")) {
					System.out.println("Uploading " + imageFile.getName().replaceAll(".png", ""));
					wiki.upload(imageFile, imageFile.getName().replaceAll(".png", ""), "", EDIT_REASON);
				}
			}
		}
	}
	/**
	 * Uploads the images in the resource folders to the wikipedia site. Will overwrite
	 * any existing images.
	 */
	public void uploadImages() throws IOException, LoginException {
		String [] paths = new String [] {
			"src/main/resources/assets/polycraft/textures/items",
			"src/main/resources/assets/polycraft/textures/blocks",
			"src/main/resources/assets/polycraft/textures/gui/container",
			"src/main/resources/assets/polycraft/textures/models/armor",
		};
		for (String path : paths) {
			for (File imageFile : new File(path).listFiles()) {
				if (imageFile.getAbsolutePath().endsWith(".png")) {
					System.out.println("Uploading " + imageFile.getName().replaceAll(".png", ""));
					wiki.upload(imageFile, imageFile.getName().replaceAll(".png", ""), "", EDIT_REASON);
				}
			}
		}
	}
}
