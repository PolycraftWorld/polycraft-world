package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class Plastic extends Entity {

	public static final String[] colors = new String[] { "white", "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange" };
	public static final String[] classesOfPlastic = new String[] { "polyolefin", "polyaldehyde", "cellulosic", "polyamide", "polyester", "polyaramid", "polycarbonate", "polyether" };

	public static final String getDefaultColor() {
		return colors[0];
	}

	public static final EntityRegistry<Plastic> registry = new EntityRegistry<Plastic>();

	static {
		for (int i = 0; i < colors.length; i++) {
			registry.register(new Plastic(1, "Polyethylene terephthalate", "PET", colors[i], classesOfPlastic[4], 64, 1, .1f, 1f, .1f, .1f));
			registry.register(new Plastic(2, "High-density polyethylene", "PE-HD", colors[i], classesOfPlastic[0], 32, 1, .2f, 2f, .2f, .2f));
			registry.register(new Plastic(3, "Polyvinyl chloride", "PVC", colors[i], classesOfPlastic[0], 16, 1, .3f, 3f, .3f, .3f));
			registry.register(new Plastic(4, "Low-density polyethylene", "PELD", colors[i], classesOfPlastic[0], 8, 1, .4f, 4f, .4f, .4f));
			registry.register(new Plastic(5, "Polypropylene", "PP", colors[i], classesOfPlastic[0], 4, 1, .5f, 5f, .5f, .5f));
			registry.register(new Plastic(6, "Polystyrene", "PS", colors[i], classesOfPlastic[0], 2, 1, .6f, 6f, .6f, .6f));
			registry.register(new Plastic(7, "Other polycarbonate", "O", colors[i], classesOfPlastic[6], 1, 1, .7f, 7f, .7f, 2f));

			registry.register(new Plastic(8, "Polyolefin", "O", colors[i], classesOfPlastic[0], 1, 1, .7f, 7f, .7f, 2f));
			registry.register(new Plastic(9, "Polyaldehyde", "O", colors[i], classesOfPlastic[1], 1, 1, .7f, 7f, .7f, 2f));
			registry.register(new Plastic(10, "Cellulosic", "O", colors[i], classesOfPlastic[2], 1, 1, .7f, 7f, .7f, 2f));
			registry.register(new Plastic(11, "Polyamide", "O", colors[i], classesOfPlastic[3], 1, 1, .7f, 7f, .7f, 2f));
			registry.register(new Plastic(12, "Polyester", "O", colors[i], classesOfPlastic[4], 1, 1, .7f, 7f, .7f, 2f));
			registry.register(new Plastic(13, "Polyaramid", "O", colors[i], classesOfPlastic[5], 1, 1, .7f, 7f, .7f, 2f));
			registry.register(new Plastic(13, "Polycarbonate", "O", colors[i], classesOfPlastic[6], 1, 1, .7f, 7f, .7f, 2f));
			registry.register(new Plastic(13, "Polyether", "O", colors[i], classesOfPlastic[7], 1, 1, .7f, 7f, .7f, 2f));
		}
	}

	private static String getGameName(final int type, final String color) {
		return "plastic_" + type + "_" + color;
	}

	public static Plastic getDefaultForType(final int type) {
		return registry.get(getGameName(type, getDefaultColor()));
	}

	public final String itemNamePellet;
	public final String itemNameFiber;
	public final String itemNameGrip;
	public final String itemNameKevlarVest;
	public final String itemNameRunningShoes;
	public final String itemNameJetPack;
	public final String itemNameParachute;
	public final String itemNameScubaMask;
	public final String itemNameScubaTank;
	public final String itemNameScubaFlippers;
	public final int type;
	public final String classOfPlastic;
	public final String abbreviation;
	public final String color;
	public final int craftingPelletsPerBlock;
	public final int craftingFibersPerPellet;
	public final float gripDurabilityBuff;
	public final ArmorMaterial kevlarArmorType;
	public final float runningShoesWalkSpeedBuff;
	public final float jetPackFlySpeedBuff;

	public Plastic(final int type, final String name, final String abbrevation, final String color, final String classOfPlastic, final int craftingPelletsPerBlock, final int craftingFibersPerPellet, final float itemDurabilityBonus,
			final float kevlarArmorBuff, final float runningShoesWalkSpeedBuff, final float jetPackFlySpeedBuff) {
		super(getGameName(type, color), name);
		this.itemNamePellet = gameName + "_pellet"; // this makes pellets of different colors
		this.itemNameFiber = gameName + "_fiber"; // this makes fibers of different colors
		this.itemNameGrip = "plastic_" + type + "_grip"; // this makes only one color of grip
		this.itemNameKevlarVest = gameName + "_kevlar_vest"; // this makes vests of different colors
		this.itemNameRunningShoes = gameName + "_running_shoes"; // this makes shoes of different colors
		this.itemNameJetPack = gameName + "_jet_pack"; // this makes jet packs of different colors
		this.itemNameParachute = gameName + "_parachute"; // this makes parachutes of different colors
		this.itemNameScubaMask = gameName + "_scuba_mask"; // this makes scuba masks of different colors
		this.itemNameScubaTank = gameName + "_scuba_tank"; // this makes scuba tanks of different colors
		this.itemNameScubaFlippers = gameName + "_scuba_flippers"; // this makes scuba flippers of different colors
		this.type = type;
		this.classOfPlastic = classOfPlastic;
		this.abbreviation = abbrevation;
		this.color = color;
		this.craftingPelletsPerBlock = craftingPelletsPerBlock;
		this.craftingFibersPerPellet = craftingFibersPerPellet;
		this.gripDurabilityBuff = itemDurabilityBonus;
		// kevlar is buffed off of the DIAMOND values
		final float kevlarArmorBuffPercent = (1 + kevlarArmorBuff);
		final int[] reductionAmounts = new int[] { (int) (3 * kevlarArmorBuffPercent), (int) (8 * kevlarArmorBuffPercent), (int) (6 * kevlarArmorBuffPercent), (int) (3 * kevlarArmorBuffPercent) };
		this.kevlarArmorType = EnumHelper.addArmorMaterial(gameName + "_kevlar", (int) (33 * kevlarArmorBuffPercent), reductionAmounts, (int) (ItemArmor.ArmorMaterial.DIAMOND.getEnchantability() * kevlarArmorBuffPercent));
		this.runningShoesWalkSpeedBuff = runningShoesWalkSpeedBuff;
		this.jetPackFlySpeedBuff = jetPackFlySpeedBuff;
	}

	public boolean isDefaultColor() {
		return color == getDefaultColor();
	}
}
