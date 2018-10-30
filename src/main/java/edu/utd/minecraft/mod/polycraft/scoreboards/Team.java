package edu.utd.minecraft.mod.polycraft.scoreboards;

import java.awt.Color;
import java.util.Collection;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class Team {
	private String name;
	private int id;
	//protected final Collection<EntityPlayerMP> players = Lists.newLinkedList();	//List of players participating in experiment instance
	protected final Collection<String> players = Lists.newLinkedList();
	private Color color;
	private static ColorEnum colorlist = ColorEnum.Blue;
	private static enum ColorEnum {
		Cyan,
		Blue,
		Red,
		Green,
		Orange;
		
		public ColorEnum next() {
		    if (ordinal() == values().length - 1)
		    	return values()[0];
		    return values()[ordinal() + 1];
		}
		
		public ColorEnum previus() {
		    if (ordinal() == 0)
		    	return values()[values().length - 1];
		    return values()[ordinal() - 1];
		}
	}
	
	public Team() {
		initColor();
		this.name = colorlist.previus().toString();
	}

	public Team(String name) {
		this.name = name;
		initColor();
	}

	public Team(String name, int id) {
		this.id = id;
		this.name = name;
		initColor();
	}
	
	private void initColor() {
		switch(colorlist) {
			case Blue:
				setColor(Color.BLUE);
				break;
			case Green:
				setColor(Color.GREEN);
				break;
			case Red:
				setColor(Color.RED);
				break;
			case Orange:
				setColor(Color.ORANGE);
				break;
			case Cyan:
				setColor(Color.CYAN);
			default:
				setColor(Color.PINK);
		}
		colorlist = colorlist.next();
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		else if (o instanceof String) {
			return this.name.equals(o); // string comparison.
		} else if (o instanceof Team) {
			return this.name.equals(((Team) o).getName());
		} else
			return false;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public Collection<String> getPlayers(){
		return players;
	}
	
	public Collection<EntityPlayer> getPlayersAsEntity(){
		Collection<EntityPlayer> playerEntities = Lists.newLinkedList();
		System.out.println(players);
		for(String player: players) {
			playerEntities.add(ExperimentManager.INSTANCE.getPlayerEntity(player));
		}
		return playerEntities;
	}
	
	public int getSize(){
		return players.size();
	}
}