package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.awt.Color;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class FeatureBase extends ExperimentFeature {

	public enum State{ //TODO: to be used after adding teams
		Neutral,	//unclaimed base
		Contested,	//claimed but Occupied by another Team
		Occupied,	//Occupied by Team that already Owns Base
		Claimed;	//claimed and unoccupied
	}
	
	public int tickCount = 0;
	
	public State currentState;
	
	public String currentTeam;
	
	private BoundingBox box;
	private Color color;
	
	public FeatureBase(Integer x, Integer y, Integer z) {
		super(x,y,z);
		this.color = Color.GRAY;
		this.box = new BoundingBox(x+0.5, z + 0.5, 6, y, y + 1, this.color);
		this.currentState = State.Neutral;
				//BoundingBox box = new BoundingBox(xPos + 95.5, zPos + 142.5, 6,y, y+1, Color.GRAY);
	}
	
	public FeatureBase(Integer x, Integer y, Integer z, Integer baseSize, Integer baseHeight) {
		this(x,y,z);
		if(baseSize == null || baseHeight == null)
			return;
		this.color = Color.GRAY;
		this.box = new BoundingBox(x+0.5, z + 0.5, baseSize, y, y + baseHeight, this.color);
		this.currentState = State.Neutral;
				//BoundingBox box = new BoundingBox(xPos + 95.5, zPos + 142.5, 6,y, y+1, Color.GRAY);
	}
	
	public FeatureBase(int x, int y, int z, BoundingBox box, Color color){
		super(x,y,z);
		this.box = box;
		this.color = color;
		this.currentState = State.Neutral;
	}
	
	public String getCurrentTeamName() {
		return currentTeam;
	}

	public void setCurrentTeam(String currentTeam) {
		this.currentTeam = currentTeam;
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
	
	public double getBoundingBoxRadius() {
		return box.getRadius();
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
	
	@Deprecated
	public void generate(World world){
		Block stairs = PolycraftRegistry.getBlock("Stairs (PVC)");
		Block pvc = PolycraftRegistry.getBlock("Block (PVC)");
		world.setBlock(xPos, yPos, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+1, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+2, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+3, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+4, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+5, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+6, zPos, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos-1, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos+1, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos-1, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos+1, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos-1, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos+1, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos-1, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos+1, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos+2, pvc, 15, 3);
		
	}
	
	@Deprecated
	public void generate2(World world){
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

	@Override
	public void build(World world) {
		// TODO Auto-generated method stub
		Block stairs = PolycraftRegistry.getBlock("Stairs (PVC)");
		Block pvc = PolycraftRegistry.getBlock("Block (PVC)");
		world.setBlock(xPos, yPos, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+1, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+2, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+3, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+4, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+5, zPos, pvc, 15, 3);
		world.setBlock(xPos, yPos+6, zPos, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos-1, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos+1, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos-1, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos+1, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos-1, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos+1, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos-2, pvc, 15, 3);
		world.setBlock(xPos-2, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos-1, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos+1, yPos, zPos+2, pvc, 15, 3);
		world.setBlock(xPos+2, yPos, zPos+2, pvc, 15, 3);

	}
	
	public class FeatureDeserializer implements JsonDeserializer<FeatureBase> {

		@Override
		public FeatureBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	
}
