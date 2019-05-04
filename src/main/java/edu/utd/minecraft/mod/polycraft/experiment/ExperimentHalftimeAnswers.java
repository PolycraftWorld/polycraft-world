// handles experiment halftime answers, outputs to respective teams chats and opens the text box for players

/**
 * create a custom class for the answers
 * player, team
 * question
 * answer
 * compile answers by team
 */

package edu.utd.minecraft.mod.polycraft.experiment;

public class ExperimentHalftimeAnswers {
	static int playerCount;
	static String[][] answersArray; // 0 - name 1 - team name 2-5 answers
	static String[][][] compiledAnswers; //[] team [] question # [] 0/answ1 1/answ2
	int player;
	public ExperimentHalftimeAnswers() {
		// reference experiements then get scoreboard and how many players there are from that
	}
	public ExperimentHalftimeAnswers(int playerCount) {
		this.playerCount = playerCount;
		this.answersArray= new String[this.playerCount+1][5]; //TODO you may want to maker the #of questions as a param for this constructor.
		this.compiledAnswers= new String[2][3][2]; //TODO you may wan to make the # of Teams and Ans as a param of this constructor.
		this.player = 0;
	}
	public void inputAnswers(String[] answers) {
		this.player=this.player+1;
		for(int i = 0 ; i < answers.length; i++) {
			answersArray[player][i] = answers[i];
		}
		// time to compile the answers
		String team1 = null;
		int teamNum = 0;
		System.out.println(this.player + " " + this.playerCount);
		if(this.player == this.playerCount) {
			// each players answers loop
			for(int i = 1; i <= player; i++) {
				if (i == 1) {
					teamNum = 0;
					team1 = answersArray[i][1];
					// go through each answer and put it in the array with their teammates answers
					for (int j = 2; j<answers.length;j++) { // starts at 2 because 0-1 are player name and team 
						// question 1 yes/no
						if(answersArray[i][j].equals("Yes")) {
							int answerNum = 0;
							int temp;
							System.out.println(compiledAnswers[teamNum][j-2][answerNum]);
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						else if(answersArray[i][j].equals("No")){
							int answerNum = 1;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						// question 2 +Offense/Defense
						else if(answersArray[i][j].equals("Offense")) {
							int answerNum = 0;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						else if(answersArray[i][j].equals("Defense")){
							int answerNum = 1;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						// question 3 make more / make less
						else if(answersArray[i][j].equals("Make more items")) {
							int answerNum = 0;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						else if(answersArray[i][j].equals("Make less items")){
							int answerNum = 1;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
					}
				}
				//team 2
				else if(answersArray[i][1].equals(team1) && !(i==1)) {
					teamNum = 0;
					// go through each answer and put it in the array with their teammates answers
					for (int j = 2; j<answers.length;j++) { // starts at 2 because 0-1 are player name and team 
						// question 1 yes/no
						if(answersArray[i][j].equals("Yes")) {
							int answerNum = 0;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						else if(answersArray[i][j].equals("No")){
							int answerNum = 1;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						// question 2 +Offense/Defense
						else if(answersArray[i][j].equals("Offense")) {
							int answerNum = 0;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						else if(answersArray[i][j].equals("Defense")){
							int answerNum = 1;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						// question 3 make more / make less
						else if(answersArray[i][j].equals("Make more items")) {
							int answerNum = 0;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						else if(answersArray[i][j].equals("Make less items")){
							int answerNum = 1;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
					}
				}
				//the rest of team 1
				else {
					teamNum = 1;
					// go through each answer and put it in the array with their teammates answers
					for (int j = 2; j<answers.length;j++) { // starts at 2 because 0-1 are player name and team 
						// question 1 yes/no
						if(answersArray[i][j].equals("Yes")) {
							int answerNum = 0;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						else if(answersArray[i][j].equals("No")){
							int answerNum = 1;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						// question 2 +Offense/Defense
						else if(answersArray[i][j].equals("Offense")) {
							int answerNum = 0;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						else if(answersArray[i][j].equals("Defense")){
							int answerNum = 1;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						// question 3 make more / make less
						else if(answersArray[i][j].equals("Make more items")) {
							int answerNum = 0;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
						else if(answersArray[i][j].equals("Make less items")){
							int answerNum = 1;
							int temp;
							if(compiledAnswers[teamNum][j-2][answerNum] != null) {
								temp = Integer.parseInt(compiledAnswers[teamNum][j-2][answerNum]);
							}
							else {
								temp = 0;
							}
							temp++;
							compiledAnswers[teamNum][j-2][answerNum] = Integer.toString(temp);
						}
					}
				}
			}
			// output answers for testing when done
			System.out.println(compiledAnswers.length + " " + compiledAnswers[1].length + " " + compiledAnswers[1][0].length);
			for(int i = 0; i < compiledAnswers.length; i++ ) { // teams
				for(int j = 0; j < compiledAnswers[1].length; j++) { // questions
					for(int k = 0; k < compiledAnswers[1][0].length; k++) { // answer
						if(compiledAnswers[i][j][k] != null) {
							System.out.println("Team " + i + " Question " + j + " Answer " + k + " Amount of answers: " + compiledAnswers[i][j][k]);
						}
						else {
							continue;
						}
					}
				}				
			}
		}
	}	
}
	