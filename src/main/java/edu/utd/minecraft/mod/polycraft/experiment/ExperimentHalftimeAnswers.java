// handles experiment halftime answers, outputs to respective teams chats and opens the text box for players

/**
 * create a custom class for the answers
 * player, team
 * question
 * answer
 * compile answers by team
 */

package edu.utd.minecraft.mod.polycraft.experiment;

import net.minecraft.client.resources.I18n;

public class ExperimentHalftimeAnswers {
	static int playerCount;
	static String[][] answersArray; // 0 - name 1 - team name 2-5 answers
	static String[][][] compiledAnswers; //[] team [] question # [] 0/answ1 1/answ2
	static int player = 0;
	public ExperimentHalftimeAnswers() {
		// reference experiements then get scoreboard and how many players there are from that
	}
	public ExperimentHalftimeAnswers(int playerCount) {
		this.playerCount = playerCount;
		this.answersArray= new String[this.playerCount][5]; //TODO you may want to maker the #of questions as a param for this constructor.
		this.compiledAnswers= new String[2][5][2]; //TODO you may wan to make the # of Teams and Ans as a param of this constructor.
	}
	public void inputAnswers(String[] answers) {
		for(int i = 0 ; i < answers.length; i++) {
			answersArray[player][i] = answers[i];
		}
		player++;
		// time to compile the answers
		String team1 = null;
		int teamNum = 0;
		if(player == playerCount) {
			// each players answers loop
			for(int i = 1; i <= player; i++) {
				if (i == 1) {
					teamNum = 1;
					team1 = answersArray[i][1];
					// go through each answer and put it in the array with their teammates answers
					for (int j = 2; j<answers.length;j++) { // starts at 2 because 0-1 are player name and team 
						// question 1 yes/no
						if(answersArray[i][j].equals(I18n.format("gui.yes"))) {
							int answerNum = 0;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						else if(answersArray[i][j].equals(I18n.format("gui.no"))){
							int answerNum = 1;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						// question 2 +Offense/Defense
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question20"))) {
							int answerNum = 2;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question21"))){
							int answerNum = 3;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						// question 3 make more / make less
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question30"))) {
							int answerNum = 4;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question31"))){
							int answerNum = 5;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
					}
				}
				//team 2
				else if(team1 == answersArray[i][1] && !(i==1)) {
					teamNum = 2;
					// go through each answer and put it in the array with their teammates answers
					for (int j = 2; j<answers.length;j++) { // starts at 2 because 0-1 are player name and team 
						// question 1 yes/no
						if(answersArray[i][j].equals(I18n.format("gui.yes"))) {
							int answerNum = 0;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						else if(answersArray[i][j].equals(I18n.format("gui.no"))){
							int answerNum = 1;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						// question 2 +Offense/Defense
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question20"))) {
							int answerNum = 2;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question21"))){
							int answerNum = 3;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						// question 3 make more / make less
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question30"))) {
							int answerNum = 4;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question31"))){
							int answerNum = 5;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
					}
				}
				//the rest of team 1
				else {
					teamNum = 1;
					// go through each answer and put it in the array with their teammates answers
					for (int j = 2; j<answers.length;j++) { // starts at 2 because 0-1 are player name and team 
						// question 1 yes/no
						if(answersArray[i][j].equals(I18n.format("gui.yes"))) {
							int answerNum = 0;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						else if(answersArray[i][j].equals(I18n.format("gui.no"))){
							int answerNum = 1;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						// question 2 +Offense/Defense
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question20"))) {
							int answerNum = 2;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question21"))){
							int answerNum = 3;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						// question 3 make more / make less
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question30"))) {
							int answerNum = 4;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
						else if(answersArray[i][j].equals(I18n.format("gui.halftime.question31"))){
							int answerNum = 5;
							int temp;
							try{
								temp = Integer.valueOf(compiledAnswers[teamNum][j-3][answerNum]);
							}catch(NullPointerException e) {
								temp = 0;
							}
							compiledAnswers[teamNum][j-3][answerNum] = Integer.toString(temp++);
						}
					}
				}
			}
			// output answers for testing when done
			for(int i = 1; i <= compiledAnswers.length; i++ ) { // teams
				for(int j = 0; j < compiledAnswers[1].length; j++) { // questions
					for(int k = 0; k < compiledAnswers[1][0].length; k++) { // answer
						System.out.println("Team " + i + " Question " + j + " Answer " + k + " Amount of answers: " + compiledAnswers[i][j][k]);
					}
				}				
			}
		}
	}	
}
	