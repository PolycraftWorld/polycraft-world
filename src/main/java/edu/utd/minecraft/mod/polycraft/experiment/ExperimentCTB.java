package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.Map;

import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class ExperimentCTB extends Experiment{
	protected int[][] bases;

	public ExperimentCTB(int id, int size, int xPos, int zPos, World world) {
		super(id, size, xPos, zPos, world);
		int maxBases = 20;
		bases = new int[maxBases][2];
		int workarea = size*16;
		int distBtwnBases = (int) ((workarea*1.0)/Math.sqrt(maxBases));
		int counter = 0;
		for (int x = xPos + distBtwnBases; x < (xPos+size*16 - 1);x+=distBtwnBases){
			for (int z = zPos + distBtwnBases; z < (zPos+size*16 - 1);z+=distBtwnBases){
				bases[counter][0] = x;
				bases[counter][1] = z;
				counter++;
			}
		}
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
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 8,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(8), (int)x, 93, (int)z));
	}
	
	@Override
	protected void generateArea(int xPos, int yPos, int zPos, World world){
		super.generateArea(xPos, yPos, zPos, world);
		for(int i = 0; i < bases.length;i++){	//generate bases
			int x = bases[i][0];
			int z = bases[i][1];
			generateBase(x,z);
		}
	}
	
	private void generateBase(int x, int z){
		Block stairs = PolycraftRegistry.getBlock("Stairs (PVC)");
		Block pvc = PolycraftRegistry.getBlock("Block (PVC)");
		world.setBlock(x, yPos, z, pvc, 0, 3);
		world.setBlock(x, yPos+1, z, pvc, 0, 3);
		world.setBlock(x, yPos+2, z, pvc, 0, 3);
		world.setBlock(x, yPos+3, z, pvc, 0, 3);
		world.setBlock(x, yPos+4, z, pvc, 0, 3);
		world.setBlock(x, yPos+5, z, pvc, 0, 3);
		world.setBlock(x, yPos+6, z, pvc, 0, 3);
		world.setBlock(x, yPos+7, z, pvc, 0, 3);
		world.setBlock(x, yPos+8, z, pvc, 0, 3);
		world.setBlock(x, yPos+9, z, pvc, 0, 3);
	}


}
