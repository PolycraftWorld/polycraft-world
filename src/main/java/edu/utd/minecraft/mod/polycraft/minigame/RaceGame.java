package edu.utd.minecraft.mod.polycraft.minigame;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class RaceGame extends PolycraftMinigame{

	public static final int id=2;
	public static final RaceGame INSTANCE = new RaceGame();
	//private boolean active = false;
	private int completed = 0;
	private HashMap<String, Integer> places;
	private int gx1 = 0;
	private int gz1 = 0;
	private int gx2 = 0;
	private int gz2 = 0;

	private RaceGame() {
		places = new HashMap<String, Integer>();
	}

	public boolean isActive() {
		return active;
	}

	public boolean isInGoal(EntityPlayer player) {
		return player.posX >= gx1 && player.posX <= gx2 && player.posZ >= gz1 && player.posZ <= gz2;
	}

	public void markCompleted(final EntityPlayer player) {
		if (places.containsKey(player.getDisplayName()))
			return;
		System.out.println(player.getDisplayName());
		completed++;
		places.put(player.getDisplayName(), completed);
		// player.addChatComponentMessage(new ChatComponentText(String.format("You are
		// #%d!", completed)));
		MinecraftServer.getServer().getConfigurationManager()
				.sendChatMsg(new ChatComponentText(String.format(player.getDisplayName() + " is #%d!", completed)));
		;
	}
	
	public void start(World world, int[] args, String envoker) {
		if(args.length>=8)
			start(world,args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7]);
		
	}

	public void start(World world, int x1, int z1, int x2, int z2, int x3, int z3, int x4, int z4) {
		int sx1 = Math.min(x1, x2);
		int sz1 = Math.min(z1, z2);
		int sx2 = Math.max(x1, x2);
		int sz2 = Math.max(z1, z2);
		gx1 = Math.min(x3, x4);
		gz1 = Math.min(z3, z4);
		gx2 = Math.max(x3, x4);
		gz2 = Math.max(z3, z4);

		for (int i = 0; i < world.playerEntities.size(); i++) {
			EntityPlayer p = (EntityPlayer) world.playerEntities.get(i);
			// int x = ThreadLocalRandom.current().nextInt(-radius+20, radius-20 + 1);
			// int z = ThreadLocalRandom.current().nextInt(-radius+20, radius-20 + 1);

			int x = ThreadLocalRandom.current().nextInt(sx1, sx2);
			int z = ThreadLocalRandom.current().nextInt(sz1, sz2);
			p.inventory.mainInventory = new ItemStack[36];
			p.inventory.armorInventory = new ItemStack[4];
			p.inventory.addItemStackToInventory(
					new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("38"))));
			p.getFoodStats().addExhaustion(40F);
			p.getFoodStats().addExhaustion(-40F);
			//p.getFoodStats().setFoodSaturationLevel(5F); //no such method error?
			//p.getFoodStats().setFoodLevel(20);
			p.setHealth(p.getMaxHealth());

			p.setPositionAndUpdate(x, p.worldObj.getTopSolidOrLiquidBlock(x, z) + 4, z);
		}
		active = true;
	}

	@Override
	public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			EntityPlayer player = event.player;
			if (INSTANCE.active && INSTANCE.isInGoal(player)) {
				INSTANCE.markCompleted(player);
			}
		}
	}

	public void updateRaceGame(final String raceGameJSON) {
		Gson gson = new Gson();
		Type typeOfKillWall = new TypeToken<RaceGame>() {
		}.getType();
		RaceGame temp = gson.fromJson(raceGameJSON, typeOfKillWall);

		RaceGame.INSTANCE.active = temp.active;
		RaceGame.INSTANCE.gx1 = temp.gx1;
		RaceGame.INSTANCE.gx2 = temp.gx2;
		RaceGame.INSTANCE.gz1 = temp.gz1;
		RaceGame.INSTANCE.gz2 = temp.gz2;
	}

	/**
	 * Stop and clear race data.
	 */
	public void stop() {
		active = false;
		completed = 0;
		places.clear();
	}

	public int getGx1() {
		return gx1;
	}

	public int getGz1() {
		return gz1;
	}

	public int getGx2() {
		return gx2;
	}

	public int getGz2() {
		return gz2;
	}
	
	@Override
	public Type getType()
	{
		Type t = new TypeToken<RaceGame>() {}.getType();
		return t;
	}
	public static void register(int id, Class c) {
		
		PolycraftMinigameManager.registerMinigame(id, c);
	}
}
