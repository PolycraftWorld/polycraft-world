package edu.utd.minecraft.mod.polycraft.scoreboards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class CustomScoreboard {

	private ArrayList<Team> teams;
	//private HashMap<EntityPlayerMP, Team> playerList; // TODO: can I replace playerENtity with UUID?
	//private HashMap<String, Team> playerList; // TODO: can I replace playerENtity with UUID?
	private HashMap<Team, Float> teamScores;
	public boolean needToSendUpdate = false;

	/**
	 * Team inner-class assumes that each CustomScoreboard has unique team names.
	 * All comparisons are done on the team name. External references only deal with
	 * teams w.r.t their names Please use the CustomScoreboard
	 * 
	 * @author dnarayanan
	 *
	 */

	public CustomScoreboard() {
		teams = new ArrayList<Team>();
		//playerList = new HashMap<EntityPlayerMP, Team>();
		//playerList = new HashMap<String, Team>();
		teamScores = new HashMap<Team, Float>();
	}

	public HashMap<Team, Float> getTeamScores() {
		return teamScores;
	}

	public CustomScoreboard(ArrayList<String> teamNameInput) {
		this.teams = new ArrayList<Team>();
		//this.playerList = new HashMap<EntityPlayerMP, Team>();
		//this.playerList = new HashMap<String, Team>();
		this.teamScores = new HashMap<Team, Float>();

		setTeams(teamNameInput);
		resetScores(0);
	}

	public void setTeams(ArrayList<String> teamNameInput) {
		int counter = 0;
		this.teams.clear(); // Reset the teamNames!
		for (String value : teamNameInput) {
			this.teams.add(new Team(value, counter));
			counter++;
		}
		this.needToSendUpdate = true;
	}
	
	public void addTeam(Team team) {
		this.teams.add(team);
		this.needToSendUpdate = true;
	}
	
	public void addNewTeam() {
		this.teams.add(new Team());
		this.needToSendUpdate = true;
	}

	public void addPlayer(String player, String teamName) throws IOException {
		Team team = null;
		for (Team tm : this.teams) {
			if (tm.equals(teamName)) {
				team = tm;
				//playerList.put(player, team);
				break;
			}
		}
		if (team == null) {
			throw new IOException();
		}
		this.needToSendUpdate = true;
	}

	public void resetScores(float initialScore) {
		for (Team tm : this.teams) {
			if (tm != null) {
				this.teamScores.put(tm, initialScore);
			}
		}
	}

	public ArrayList<String> getPlayers(){
		ArrayList<String> players = new ArrayList<String>();
		for(Team team: teams) {
			for(String player: team.getPlayers()) {
				players.add(player);
			}
		}
		return players;
	}
	
	public Collection<EntityPlayer> getPlayersAsEntity(){
		Collection<EntityPlayer> playerEntities = Lists.newLinkedList();
		for(Team team: teams) {
			if(team.getPlayers().isEmpty()) continue;
			for(String player: team.getPlayers()) {
				playerEntities.add(ExperimentManager.INSTANCE.getPlayerEntity(player));
			}
		}
			
		return playerEntities;
	}

	public ArrayList<Float> getScores() {
		return new ArrayList<Float>(teamScores.values());
	}

	//This function increments the score by the input value. You can input negative values!
	public void updateScore(Team tm, float value) {
		float val = teamScores.put(tm, teamScores.get(tm) + value);
		this.needToSendUpdate = true;
	}
	
	/**
	 * Update score given the teamName as the input instead of a Team object.
	 * @param tm
	 * @param value
	 */
	public void updateScore(String tm, float value) {
		try {
			Team team = getTeam(tm);
			this.updateScore(team, value);
		}catch(IllegalArgumentException ex) {
			System.out.println("Error, INVALID ARGUMENT!");
			ex.printStackTrace();
		}
	}

	public Team getPlayerTeam(String player) {
		for(Team team: teams) {
			if(team.getPlayers().contains(player))
				return team;
		}
		return null;
	}
	
	public Team getTeam(String teamName) throws IllegalArgumentException {
		for (Team tm : teams) {
			if(tm.equals(teamName)){
				return tm;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public ArrayList<Team> getTeams(){
		return teams;
	}
	
	public void clearPlayers() {
		for(Team tm : teams) {
			tm.getPlayers().clear();
		}
	}
}
