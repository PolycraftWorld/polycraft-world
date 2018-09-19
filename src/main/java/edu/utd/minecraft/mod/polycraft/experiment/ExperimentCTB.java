package edu.utd.minecraft.mod.polycraft.experiment;

import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class ExperimentCTB extends Experiment{

	public ExperimentCTB(int id, int size, int xPos, int zPos, World world) {
		super(id, size, xPos, zPos, world);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void start(){
		for(EntityPlayerMP player: players){
			spawnPlayer(player);
		}
	}
	
	private void spawnPlayer(EntityPlayerMP player){
		double x = Math.random()*(size*16 - 10) + xPos + 5;
		double z = Math.random()*size*16 + zPos;
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 8,	new PolycraftTeleporter(player.mcServer.worldServerForDimension(8), (int)x, 93, (int)z));
	}

}
