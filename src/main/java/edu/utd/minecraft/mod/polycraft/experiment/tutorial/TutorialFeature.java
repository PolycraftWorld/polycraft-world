package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import net.minecraft.util.Vec3;

public class TutorialFeature {
	private String name;
	private Vec3 pos;
	private Color color;
	
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
