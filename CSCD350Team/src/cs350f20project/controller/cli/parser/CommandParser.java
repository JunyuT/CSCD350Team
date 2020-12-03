package cs350f20project.controller.cli.parser;
import java.util.ArrayList;

import cs350f20project.controller.command.*;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.timing.Time;

public class CommandParser {
	private String commandText;
	private MyParserHelper parserHelper;

	public CommandParser(MyParserHelper parserHelper, String commandText) {
		this.commandText = commandText;
		this.parserHelper = parserHelper;
		//System.out.println("Junyu's Parser");
	}
	
	public void parse() {
		String[] commands = this.commandText.split(";");
		for (String instruction : commands) {
			ArrayList<Token> tokens = this.parserHelper.getContext(instruction);
			if (tokens.get(0).type.name().toString().equals("KEYWORD")) {
				String keyword = tokens.get(0).data;
				if (keyword.toUpperCase().equals("@EXIT")) {
					A_Command command = new CommandMetaDoExit();
					this.parserHelper.getActionProcessor().schedule(command);
				}
				
			}
		}
		
//		String[] commands  = this.commandText.split(";");
//		int comnum = 0; //Also for testing purposes
//		for (String instruction : commands) {
//			comnum++;
//			instruction = instruction.trim();
//			String[] comArray = instruction.split(" ");
//			for (String s : comArray) { //For testing purposes
//				System.out.println(comnum + ". " + s);
//			}
//			
//			
//			if (comArray[0].equalsIgnoreCase("@exit")) {   // Exit command
//				A_Command command = new CommandMetaDoExit();
//				this.parserHelper.getActionProcessor().schedule(command);
//			}
//			
//			
//			if (comArray[0].equalsIgnoreCase("@wait")) {   // Wait command
//				Time time = new Time(Integer.parseInt(comArray[1]));
//				A_Command command = new CommandMetaDoWait(time);
//				this.parserHelper.getActionProcessor().schedule(command);
//				System.out.println("here");
//			}

		}
	}
//}
