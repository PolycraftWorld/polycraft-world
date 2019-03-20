package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.item.ItemDevTool.StateEnum;
import net.minecraft.util.Vec3;

public class TutorialFeature {
	private String name;
	private Vec3 pos;
	private Color color;
	
	public enum TutorialFeatureType{
		GENERIC,
		GUIDE,
		INSTRUCTION,
		START;
		
		public TutorialFeatureType next() {
		    if (ordinal() == values().length - 1)
		    	return values()[0];
		    return values()[ordinal() + 1];
		}
		
		public TutorialFeatureType previous() {
		    if (ordinal() == 0)
		    	return values()[values().length - 1];
		    return values()[ordinal() - 1];
		}
	}
	
	public TutorialFeature(String name, Vec3 pos, Color c){
		this.name = name;
		this.pos = pos;
		this.color = c;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vec3 getPos() {
		return pos;
	}

	public void setPos(Vec3 pos) {
		this.pos = pos;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
