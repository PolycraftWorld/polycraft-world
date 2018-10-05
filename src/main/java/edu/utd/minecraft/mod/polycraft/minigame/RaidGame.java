package edu.utd.minecraft.mod.polycraft.minigame;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;

public class RaidGame extends PolycraftMinigame {

	private EntityMob boss = null;
	
	

	public static void refreshPlayer(EntityPlayer player) {
		player.getFoodStats().addExhaustion(40F);
		player.getFoodStats().addExhaustion(-40F);
		player.getFoodStats().addStats(20, 20);
		player.setHealth(player.getMaxHealth());
	}
}
