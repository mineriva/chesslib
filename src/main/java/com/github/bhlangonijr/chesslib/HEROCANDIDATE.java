package com.github.bhlangonijr.chesslib;

import java.io.IOException;

//This Class has a single method that:
// receives the current position as a fen and the pgn line that got there. It also recieves the stockfish object for calculations.
// it returns the chosen move as a SAN

public class HEROCANDIDATE {

	public static String Find_Hero_candidates (String current_move_pgn, String current_move_fen, STOCKFISH engine ) throws IOException  {
		
		// String[] stockfish_return = engine.getEvaluation("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

		// int evaluation = Integer.parseInt(stockfish_return[0]);
		// String best_move_uci = stockfish_return[1];
		
		
		// System.out.println(best_move_uci + evaluation);
		
		
	
		String hero_candidate = "h6";
				
		// Create an ArrayList for all the candidates of villian moves.
		

		//System.out.println(current_move_pgn + current_move_fen);
		
		return hero_candidate;
			
}
}