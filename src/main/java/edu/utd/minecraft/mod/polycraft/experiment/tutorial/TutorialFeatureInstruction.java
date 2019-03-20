package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import net.minecraft.util.Vec3;

public class TutorialFeatureInstruction extends TutorialFeature{
	public enum InstructionType{
		MOUSE,
		WASD,
		JUMP,
		SPRINT,
		INVENTORY,
		PLACE_BLOCKS,
		KBB,
		CRAFT_FKB
	};
	private InstructionType type;
	
	public TutorialFeatureInstruction(String name, Vec3 pos, InstructionType type){
		super(name, pos, Color.MAGENTA);
		this.type = type;
		this.featureType = TutorialFeatureType.INSTRUCTION;
	}

	public InstructionType getType() {
		return type;
	}

	public void setType(InstructionType type) {
		this.type = type;
	}
}
