package edu.utd.minecraft.mod.polycraft.experiment;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class Base {

	public enum State{ //TODO: to be used after adding teams
		Neutral,	//unclaimed base
		Contested,	//claimed but Occupied by another Team
		Occupied,	//Occupied by Team that already Owns Base
		Claimed;	//claimed and unoccupied
	}
	
	public int tickCount = 0;
	
	public State currentState;
	
	private String currentTeam;
	
	private BoundingBox box;
	private Color color;
	public int xPos,yPos,zPos;
	
	public Base(int x, int y, int z, BoundingBox box, Color color){
		this.box = box;
		this.xPos = x;
		this.yPos = y;
		this.zPos = z;
		this.color = color;
		this.currentState = State.Neutral;
	}
	
	public void setRendering(boolean rendering){
		this.box.setRendering(rendering);
	}
	
	public boolean isInBase(Entity entity){
		return box.isInBox(entity);
	}
	
	public Color getColor(){
		return box.getColor();
	}
	
	public void setColor(Color color){
		box.setColor(color);
	}
	
	public void setHardColor(Color color){
		this.color = color;
		box.setColor(color);
	}
	
	public void resetColor(){
		box.setColor(color);
	}
	
	public void render(Entity entity){
		box.render(entity);
	}
	
	public void generate(World world){
		Block stairs = PolycraftRegistry.getBlock("Stairs (PVC)");
		Block pvc = PolycraftRegistry.getBlock("Block (PVC)");
		world.setBlock(xPos, yPos, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+1, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+2, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+3, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+4, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+5, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+6, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+7, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+7, zPos+1, pvc, 1, 3);
		world.setBlock(xPos, yPos+7, zPos-1, pvc, 1, 3);
		world.setBlock(xPos+1, yPos+7, zPos, pvc, 1, 3);
		world.setBlock(xPos-1, yPos+7, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+8, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+8, zPos+1, pvc, 1, 3);
		world.setBlock(xPos, yPos+8, zPos-1, pvc, 1, 3);
		world.setBlock(xPos+1, yPos+8, zPos, pvc, 1, 3);
		world.setBlock(xPos-1, yPos+8, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+9, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+9, zPos+1, pvc, 1, 3);
		world.setBlock(xPos, yPos+9, zPos-1, pvc, 1, 3);
		world.setBlock(xPos+1, yPos+9, zPos, pvc, 1, 3);
		world.setBlock(xPos-1, yPos+9, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+9, zPos, pvc, 1, 3);
		world.setBlock(xPos, yPos+10, zPos, pvc, 1, 3);
		world.setBlock(xPos-1, yPos+1, zPos, stairs, 0, 3);
		world.setBlock(xPos-1, yPos+1, zPos-1, stairs, 0, 3);
		world.setBlock(xPos-1, yPos+1, zPos+1, stairs, 0, 3);
		world.setBlock(xPos+1, yPos+1, zPos, stairs, 1, 3);
		world.setBlock(xPos+1, yPos+1, zPos+1, stairs, 1, 3);
		world.setBlock(xPos+1, yPos+1, zPos-1, stairs, 1, 3);
		world.setBlock(xPos, yPos+1, zPos-1, stairs, 2, 3);
		world.setBlock(xPos, yPos+1, zPos+1, stairs, 3, 3);
		world.setBlock(xPos-1, yPos+6, zPos, stairs, 4, 3);
		world.setBlock(xPos-1, yPos+6, zPos+1, stairs, 4, 3);
		world.setBlock(xPos-1, yPos+6, zPos-1, stairs, 4, 3);
		world.setBlock(xPos+1, yPos+6, zPos, stairs, 5, 3);
		world.setBlock(xPos+1, yPos+6, zPos+1, stairs, 5, 3);
		world.setBlock(xPos+1, yPos+6, zPos-1, stairs, 5, 3);
		world.setBlock(xPos, yPos+6, zPos-1, stairs, 6, 3);
		world.setBlock(xPos, yPos+6, zPos+1, stairs, 7, 3);
	}

	public String getCurrentTeam() {
		return currentTeam;
	}

	public void setCurrentTeam(String currentTeam) {
		this.currentTeam = currentTeam;
	}
	
}
