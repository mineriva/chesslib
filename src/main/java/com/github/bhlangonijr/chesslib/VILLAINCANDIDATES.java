package com.github.bhlangonijr.chesslib;


import java.util.ArrayList;

//This class contains a single method that receives the current position as a fen and a pgn of how it got there.
// It returns a list of candidate moves for villian

public class VILLAINCANDIDATES {

	public static ArrayList<String> Find_Vilain_candidates (String current_move_pgn, String current_move_fen )  {
		
				
		// Create an ArrayList for all the candidates of villian moves.
		ArrayList<String> villian_candidates = new ArrayList<>();
			

		// Add some elements to the list
		villian_candidates.add("Nc3");
		villian_candidates.add("d3");
		villian_candidates.add("0-0");

		//System.out.println(villian_candidates);
		
		return villian_candidates;
			
}
}
