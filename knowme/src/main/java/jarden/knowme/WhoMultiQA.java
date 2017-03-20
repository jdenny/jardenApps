package jarden.knowme;

public class WhoMultiQA extends MultiQA {
	private final static String[] whoAnswers = {
			"{name1}", "{name2}", "Both equal"
		}; 

	public WhoMultiQA(String myQuestion, String hisQuestion) {
		super(myQuestion, hisQuestion, whoAnswers);
	}
	@Override
	public String[] getAnswers(Player player) {
		String myName = player.getName();
		String hisName = player.getOtherPlayer().getName();
		String player1Name, player2Name;
		if (player.getPlayerNumber() == 1) {
			player1Name = myName;
			player2Name = hisName;
		} else {
			player2Name = myName;
			player1Name = hisName;
		}
		return new String[] {
			player1Name, player2Name, "Both equal"	
		};
	}

}
