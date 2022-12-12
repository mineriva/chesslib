package com.github.bhlangonijr.chesslib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class STOCKFISH {
  private Process engineProcess;
  private BufferedReader processReader;
  private BufferedWriter processWriter;

  public STOCKFISH() throws IOException {
    // Start the Stockfish engine process and set up input/output streams
    engineProcess = new ProcessBuilder("C:\\Users\\User\\Desktop\\lb\\Line-builder-main\\stockfish15win.exe").start();
    processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
    processWriter = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));
  }

  public void close() throws IOException {
    // Close the input/output streams and destroy the engine process
    processReader.close();
    processWriter.close();
    engineProcess.destroy();
  }

 

  public String[] getEvaluation(String fen) throws IOException {
    // Send the "position" and "go" commands to the engine to set the position and start evaluating it
    processWriter.write("position fen " + fen + "\n");
    processWriter.write("setoption name Hash value 10000\n");
    processWriter.write("setoption name Threads value 3\n");
    processWriter.write("go\n");
    processWriter.flush();

    // Read the engine's output until we find a line that starts with "score"
    String line;
    while ((line = processReader.readLine()) != null) {
    	String [] return_value = new String[2];
      //System.out.println(line);

      if (line.contains("score")) {
    	  
    	  String [] parse_line = line.split(" ");
    	  

    	  int depth = Integer.parseInt(parse_line[2]);    	  
    	  if (depth == 20) {
    		  String best_move_uci = "";
    		  String evaluation = parse_line[9];
    		  
    		  for (int t = 0; t < parse_line.length; t++) {

    				if ( parse_line[t].contains("pv")) {
    					best_move_uci = parse_line[t+1];
    				}
    			} 

    		  return_value[0] = evaluation;
    		  return_value[1] = best_move_uci;

    		  return return_value;
    		  
    	  }

      }
    }

    // If we reach here, something went wrong and we couldn't get the evaluation
    throw new IOException("Failed to get evaluation from engine");
  }
}
