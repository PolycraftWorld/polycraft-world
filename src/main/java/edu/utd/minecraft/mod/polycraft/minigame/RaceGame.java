package edu.utd.minecraft.mod.polycraft.minigame;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class RaceGame extends PolycraftMinigame{

	public static final int id=2;
	//public static final RaceGame INSTANCE = new RaceGame();
	//private boolean active = false;
	private int completed = 0;
	private HashMap<String, Integer> places  = new HashMap<String, Integer>();;
	private int gx1 = 0;
	private int gz1 = 0;
	private int gx2 = 0;
	private int gz2 = 0;

	public RaceGame() {
		
	}
	
	@Override
	public void init()
	{
		PolycraftMinigameManager.INSTANCE = new RaceGame();
		//ServerEnforcer.INSTANCE.minigameUpdate(this.id);
	}
	
	@Override
	public double getDouble() // this is not correct. we need a system to Get specific Minigame Information.
	{
		return 0;
	}
	

	public boolean isActive() {
		return this.active;
	}

	public boolean isInGoal(EntityPlayer player) {
		return player.posX >= gx1 && player.posX <= gx2 && player.posZ >= gz1 && player.posZ <= gz2;
	}

	public void markCompleted(final EntityPlayer player) {
		if (places.containsKey(player.getDisplayNameString()))
			return;
		System.out.println(player.getDisplayNameString());
		completed++;
		places.put(player.getDisplayNameString(), completed);
		// player.addChatComponentMessage(new ChatComponentText(String.format("You are
		// #%d!", completed)));
		MinecraftServer.getServer().getConfigurationManager()
				.sendChatMsg(new ChatComponentText(String.format(player.getDisplayNameString() + " is #%d!", completed)));
		;
	}
	
	@Override
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

			p.setPositionAndUpdate(x, p.worldObj.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY() + 4, z);
		}
		active = true;
	}

	@Override
	public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			
			EntityPlayer player = event.player;
			if (PolycraftMinigameManager.INSTANCE.active && ((RaceGame) PolycraftMinigameManager.INSTANCE).isInGoal(player)) {
				((RaceGame) PolycraftMinigameManager.INSTANCE).markCompleted(player);
				ServerEnforcer.INSTANCE.minigameUpdate(this.id);
			}
		}
	}

//	public void updateRaceGame(final String raceGameJSON) {
//		Gson gson = new Gson();
//		Type typeOfKillWall = new TypeToken<RaceGame>() {
//		}.getType();
//		RaceGame temp = gson.fromJson(raceGameJSON, typeOfKillWall);
//
//		PolycraftMinigameManager.INSTANCE.active = temp.active;
//		((RaceGame) PolycraftMinigameManager.INSTANCE).gx1 = temp.gx1;
//		((RaceGame) PolycraftMinigameManager.INSTANCE).gx2 = temp.gx2;
//		((RaceGame) PolycraftMinigameManager.INSTANCE).gz1 = temp.gz1;
//		((RaceGame) PolycraftMinigameManager.INSTANCE).gz2 = temp.gz2;
//	}

	/**
	 * Stop and clear race data.
	 */
	@Override
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
	public void onServerTick(ServerTickEvent event) {
		ServerEnforcer.INSTANCE.minigameUpdate(this.id);
	}
	
	
	@Override
	public boolean shouldUpdatePackets()//true if is on tick that needs to update packets
	{
		
		return true;
	}
	
	@Override
	public void render(Entity entity) {
		renderRaceGameGoal(entity);
		
	}
	
	private static void renderRaceGameGoal(Entity entity) {
		if (entity.worldObj.isRemote) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glLineWidth(4.0F);
			GL11.glBegin(GL11.GL_LINES);// Gl_Line_Loop
			double dy = 64; // 16;
			double y1 = Math.floor(entity.posY - dy / 2);
			double y2 = y1 + dy;
			if (y1 < 0) {
				y1 = 0;
				y2 = dy;
			}
			if (y1 > entity.worldObj.getHeight()) {
				y2 = entity.worldObj.getHeight();
				y1 = y2 - dy;
			}

			int gx1 = ((RaceGame) PolycraftMinigameManager.INSTANCE).getGx1();
			int gx2 = ((RaceGame) PolycraftMinigameManager.INSTANCE).getGx2();
			int gz1 = ((RaceGame) PolycraftMinigameManager.INSTANCE).getGz1();
			int gz2 = ((RaceGame) PolycraftMinigameManager.INSTANCE).getGz2();

			GL11.glColor4d(0, 0.9, 0, 0.5);
			double offset = (entity.ticksExisted % 20) / 20D;
			for (double y = (int) y1; y <= y2; y++) {
				GL11.glVertex3d(gx1, y - offset, gz1);
				GL11.glVertex3d(gx2, y - offset, gz1);

				GL11.glVertex3d(gx2, y - offset, gz1);
				GL11.glVertex3d(gx2, y - offset, gz2);

				GL11.glVertex3d(gx2, y - offset, gz2);
				GL11.glVertex3d(gx1, y - offset, gz2);

				GL11.glVertex3d(gx1, y - offset, gz2);
				GL11.glVertex3d(gx1, y - offset, gz1);
			}

			GL11.glEnd();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
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
