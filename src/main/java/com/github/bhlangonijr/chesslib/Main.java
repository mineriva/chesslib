package com.github.bhlangonijr.chesslib;

import java.io.IOException;
import java.util.ArrayList;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;


/* 
 * This is the Main class of our project:
 * 1) It will receive from the CONFIG class a string containing our starting line and get the fen of the position from the CHESS CLASS: ie. '1. e4 e5, rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2'
 * 2) It will then send use the do_branch_builder to: * 2
 * 2.1) create an array of strings named lines_in_repertoire storing all the lines in the repertoire where element [i] [0] is the pgne and [i][1] is the fen
 * 3) It will send the first element to the Find_villain_move class and receive an array of candidate moves or a single candidate move described as 'End of Line'
 * 4) If it receives and 'End of Line' response then it will consider that line solved and proceed to the next element in the lines_in_repertoire array.
 * 5) If it receives multiple candidate moves it will add all the additional (meaning more than one) candidate moves as new elements to the lines_in_repertoire array.
 * 6) It will send the top unsolved element in the lines_in_repertoire to Find_hero_move class and receive a single string containing the Hero Candidate move which will be added to the element.
 * 7) This amended element will again be sent to Find_villain_move class....
 * 8) This process will continue until all the elements have been solved.
 * 9) The complete solved lines_in_repertoire array will be sent to the Print_pgn class that will then print the complete pgn.
 * 
 */

public class Main {
	
	public static void  main(String [] arg) throws IOException  {
		
		// //we setup an engine to be used
		STOCKFISH engine = new STOCKFISH();
			
		// Create a CONFIG object and load the file so that we have the settings available
		CONFIG config = new CONFIG();
		
		// Get the starting line 
		String starting_pgn= config.getProperty("StartingPgn");

        //We create a MoveList object.load our starting pgn and get the starting fen
        MoveList starting_List = new MoveList();
        starting_List.loadFromSan(starting_pgn);
        String starting_fen =  starting_List.getFen();	
        
        System.out.println(starting_fen);
                
		// //We send the starting position to branch builder
		do_branch_builder (starting_pgn, starting_fen, engine);
		
		engine.close();
			
}
    //This Method:
    // Receives the current position as a fen and the pgn to get there. It also receives the stockfish object
    // It creates our Main Object lines_in_repertoire and populates each line untill it is solved 
    // Then sends the Main Object to be printed.
	public static void do_branch_builder (String pgn, String fen, STOCKFISH engine ) throws IOException  {

        		
		//This is our Main Object that will be populated and the written to the PGN file        
		String[][] lines_in_repertoire = new String[1][2];		
        //We start by populating it with our starting
		lines_in_repertoire[0][0] = pgn;
		lines_in_repertoire[0][1] = fen;
		
		// Boolean finished = false;
		// while (finished == false) {

            //looping through all the positions:
			int repertoire_lenth = lines_in_repertoire.length;
			for (int t = 0; t < repertoire_lenth; t++) {
				
				//for each unsolved position
				if (lines_in_repertoire[t][1] != "0") {
                    //we go to the Get_Villain_Candidets method to:
                    //Get the villain candidates
                    //Get the hero candidates for each villain candidate found
                    //extend our Main Obj lines_in_repertoire to include these moves
                    //or if no villain candidates were find to indicate the line as solved
					lines_in_repertoire = Get_Villain_Candidets (lines_in_repertoire[t][0], lines_in_repertoire[t][1], lines_in_repertoire, t, engine);

				}
				// for eached solved position
				if (lines_in_repertoire[t][1] == "0") {

					System.out.println("found true");
					
				}	

			} 
            //Until we find that all the positions have been solved
		// 	finished = true;
			
		// }

		//JUST A SANITY PRINT OUT
		for (int t = 0; t < lines_in_repertoire.length; t++) {
			System.out.println(lines_in_repertoire[t][0] + lines_in_repertoire[t][1]);
		} 
		
	
				
				
}
	
		
    // This Class:
    // Receives the current position in fen and the moves that got there and the stockfish object
    // it returns the best hero move as a string containing hero_move [0] = the pgn and hero_move [1] = the fen after the move
	private static String[] Get_Hero_Move(String pgn, String fen, STOCKFISH engine) throws IOException {
			
			//create the return strings
			String[] hero_move = new String[2];
			
			// We ask the HEROCANDIDATE class for our best move 
			String hero_candidate = HEROCANDIDATE.Find_Hero_candidates(pgn, fen, engine);

            // We setup a new boad and notation sheet
            Board board = new Board();
            MoveList notation = new MoveList();		

            //we setup our board so far (excluding villain move)
            board.loadFromFen(fen);
            //We write down all the moves so far (excluding villain move)
            notation.loadFromSan(pgn);
            //We get the new fen by playing it on the board							
            notation.addSanMove(hero_candidate);
            String fen_after_move = notation.getFen();
            

           //We revers colours because a move just got played
			if (board.getSideToMove().toString() == "BLACK") {
					
				hero_move[0] = pgn + " " + hero_candidate;	
				hero_move[1] = fen_after_move;

					
			}else {
					
				// This is White to move and only 1 candidate returned.
				//we amend the pgn line to include the villain candidate

				int move_number = board.getMoveCounter();
					
				hero_move[0] = pgn + " " + move_number + ". " + hero_candidate;
				hero_move[1] = fen_after_move;


			}
			
			
		return hero_move;
	}

        //This Method:
        // Receives: 
        // The specific position in fen and how it got there in pgn. Also it receives our Main Obj and where this position is found in the array.
        // Finaly it receives the stocfish object to do calculations or to pass it to other classes.
        //Does:
        //Get the villain candidates from the Find_Vilain_candidates method as a list
        //Get the hero candidates from the Get_Hero_Move method for each villain candidate found
        //extend our Main Obj lines_in_repertoire to include these moves
		public static String[][] Get_Villain_Candidets (String pgn, String fen,String[][] lines_in_repertoire, int Index, STOCKFISH engine) throws IOException {
            			
			// Create an ArrayList of Strings holding candidates for villain which we will receive from the Find_Vilain_candidates method
			ArrayList<String> villian_candidates = new ArrayList<String>();
			//we populate the array list with the villain candidate moves
			villian_candidates = VILLAINCANDIDATES.Find_Vilain_candidates(pgn, fen);	
			//We find out how many candidates were returned so we know if the line is solved or how many hero moves we need to find.
			int size = villian_candidates.size();	

            // We setup a new boad and notation sheet
            Board board = new Board();
            MoveList notation = new MoveList();		
            //we setup our board so far (excluding villain move)
            board.loadFromFen(fen);
            //We write down all the moves so far (excluding villain move)
            notation.loadFromSan(pgn);	

			if (size == 0 ) {
                
				//If we received 0 candidate moves then we know it is the End of the Line				
				lines_in_repertoire[Index][1] = "0";
			}
			
			//We go through the villain candidates and put them into our lines_in_repertoire array
			if (size > 0 ) {
				
				//If we received more than 0 than we need to create a new element and write each in turn and then go to HEROCANDIDATE for the first element
				String[][] temp = new String[lines_in_repertoire[0].length + size -2][2];
				// Create a new, tempory  array and copy the elements from the lines_in_repertoire array
				for (int t = 0; t < lines_in_repertoire.length; t++) {
					temp[t][0] = lines_in_repertoire[t][0];
					temp[t][1] = lines_in_repertoire[t][1];
				}
				
				// We populate the temp array with the villian moves
				for (int i = 0; i <size; i++) {

                    String fen_after_move = "0";                    
                    
					//We get the new fen by writing it down  it on the board
                    notation.addSanMove(villian_candidates.get(i));		
					fen_after_move = notation.getFen();
					//we have to remove the last move to go back for next villian move
					notation.removeLast();
                   
                    // System.out.println("here and pgn is  =" + pgn + " side to move is: " + board.getSideToMove().toString());
					if (board.getSideToMove().toString() == "BLACK") {

                        //we populate the temp array which will later populate our Main Obj
                        //first with villain moves
						temp[i+Index][0] = pgn + " " +villian_candidates.get(i);	
						temp[i+Index][1] = fen_after_move;

                        //Then with hero move
						String[] strings =  Get_Hero_Move(temp[i][0], temp[i][1], engine);
						temp[i+Index][0] = strings[0];	
						temp[i+Index][1] = strings[1];
							
					}else {

                        
						int move_number = board.getMoveCounter();	
                        
                        //we populate the temp array which will later populate our Main Obj
                        //first with villain moves
						temp[i+Index][0] = pgn + " " + move_number + ". " + villian_candidates.get(i);;
						temp[i+Index][1] = fen_after_move;
                        
                        //Then with hero move
						String[] strings =  Get_Hero_Move(temp[i][0], temp[i][1], engine);
						temp[i+Index][0] = strings[0];	
						temp[i+Index][1] = strings[1];

					}
						
						
				}
				// We recreate our main object lines_in_repertoire but now including the new villian moves
				lines_in_repertoire = temp;

			
		}
			
			return lines_in_repertoire;
		

			
		}
		
}
