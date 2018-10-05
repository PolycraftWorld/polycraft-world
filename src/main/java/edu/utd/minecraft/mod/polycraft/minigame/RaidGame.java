package edu.utd.minecraft.mod.polycraft.minigame;

import java.util.concurrent.ThreadLocalRandom;

import edu.utd.minecraft.mod.polycraft.entity.boss.TestTerritoryFlagBoss;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RaidGame extends PolycraftMinigame {

	public static final int id = 3;

	private EntityMob boss = null;

	@Override
	public void init() {
		PolycraftMinigameManager.INSTANCE = new RaidGame();
	}

	@Override
	public void start(World world, int[] args, String envoker) {
		start(world, args[0], args[1], args[2]);
	}

	private void start(World world, double x, double z, int radius) {
		if (world.isRemote || radius < 1)
			return;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		x += 0.5;
		z += 0.5;
		for (int i = 0; i < world.playerEntities.size(); i++) {
			EntityPlayerMP p = (EntityPlayerMP) world.playerEntities.get(i);
			if (p.capabilities.isCreativeMode)
				continue;
			p.inventory.mainInventory = new ItemStack[36];
			p.inventory.armorInventory = new ItemStack[] { new ItemStack(Items.iron_boots),
					new ItemStack(Items.iron_leggings), new ItemStack(Items.iron_chestplate),
					new ItemStack(Items.iron_helmet) };
			p.inventory.addItemStackToInventory(new ItemStack(Items.diamond_sword));
			p.inventory.addItemStackToInventory(new ItemStack(Items.bow));
			p.inventory.addItemStackToInventory(new ItemStack(Items.cooked_beef, 16));
			p.inventory.addItemStackToInventory(new ItemStack(Items.golden_apple, 1));
			healPlayer(p);
			// Spread players in a circle around the boss point.
			double angle = random.nextDouble() * 2D * Math.PI;
			double xp = Math.round(x + Math.sin(angle) * radius) + 0.5;
			double zp = Math.round(z + Math.cos(angle) * radius) + 0.5;
			// p.setLocationAndAngles(xp, world.getTopSolidOrLiquidBlock(xp, zp), zp, 0, 0);
			p.playerNetServerHandler.setPlayerLocation(xp,
					world.getTopSolidOrLiquidBlock((int) Math.floor(xp), (int) Math.floor(zp)), zp,
					(float) Math.toDegrees(-Math.atan2(x - xp, z - zp)), 0);
			// p.setPositionAndUpdate(xp, world.getTopSolidOrLiquidBlock(xp, zp), zp);
		}
		if (boss != null)
			boss.setDead();
		boss = new TestTerritoryFlagBoss(world);
		boss.setPosition(x, world.getTopSolidOrLiquidBlock((int) x, (int) z), z);
		world.spawnEntityInWorld(boss);
	}

	@Override
	public void stop() {
		if (boss != null)
			boss.setDead();
	}

	public static void healPlayer(EntityPlayer player) {
		player.getFoodStats().addExhaustion(40F);
		player.getFoodStats().addExhaustion(-40F);
		player.getFoodStats().addStats(20, 20);
		player.setHealth(player.getMaxHealth());
	}
}
