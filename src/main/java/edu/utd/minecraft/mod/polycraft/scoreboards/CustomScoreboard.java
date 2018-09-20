package edu.utd.minecraft.mod.polycraft.scoreboards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayerMP;

public class CustomScoreboard {

	private ArrayList<Team> teamNames;
	private HashMap<EntityPlayerMP, Team> playerList; // TODO: can I replace playerENtity with UUID?
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
		teamNames = new ArrayList<Team>();
		playerList = new HashMap<EntityPlayerMP, Team>();
		teamScores = new HashMap<Team, Float>();
	}

	public HashMap<Team, Float> getTeamScores() {
		return teamScores;
	}

	public CustomScoreboard(ArrayList<String> teamNameInput) {
		this.teamNames = new ArrayList<Team>();
		this.playerList = new HashMap<EntityPlayerMP, Team>();
		this.teamScores = new HashMap<Team, Float>();

		setTeams(teamNameInput);
		resetScores(0);
	}

	public void setTeams(ArrayList<String> teamNameInput) {
		int counter = 0;
		this.teamNames.clear(); // Reset the teamNames!
		for (String value : teamNameInput) {
			this.teamNames.add(new Team(value, counter));
			counter++;
		}
		this.needToSendUpdate = true;

	}

	@SuppressWarnings("unlikely-arg-type") // you can compare a Team to a String because that's easy.
	public void addPlayer(EntityPlayerMP player, String teamName) throws IOException {
		Team team = null;
		for (Team tm : this.teamNames) {
			if (tm.equals(teamName)) {
				team = tm;
				playerList.put(player, team);
				break;
			}
			if (team == null) {
				throw new IOException();
			}
		}
		this.needToSendUpdate = true;
	}

	public void resetScores(float initialScore) {
		for (Team tm : this.teamNames) {
			if (tm != null) {
				this.teamScores.put(tm, initialScore);
			}
		}
	}

	public ArrayList<EntityPlayerMP> getPlayers() {
		return new ArrayList<EntityPlayerMP>(playerList.keySet());
	}

	public ArrayList<Float> getScores() {
		return new ArrayList<Float>(teamScores.values());
	}

	@SuppressWarnings("unlikely-arg-type")
	public void updateScore(String team, float value) {
		for (Team tm : teamNames) {
			if (tm.equals(team)) {
				float val = teamScores.put(tm, teamScores.get(tm) + value);
			}
		}
		this.needToSendUpdate = true;
	}

	public Team getPlayerTeam(EntityPlayerMP player) {
		return this.playerList.get(player);
	}
}
