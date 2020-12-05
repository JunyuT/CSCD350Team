package cs350f20project.controller.cli.parser;
import java.util.ArrayList;

import cs350f20project.controller.command.*;
import cs350f20project.controller.command.behavioral.*;
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
			String commandCode = "";
			for (Token token : tokens) {
				switch (token.getType().toString()) {
				case "KEYWORD": commandCode += token.getData().toUpperCase(); break;
				case "ID": commandCode += "ID"; break;
				case "LONGITUDE": commandCode += "LX"; break;
				case "NUMBER": commandCode += "NB"; break;
				case "INTEGER": commandCode += "INT"; break;
				case "COORDINATEDELTA": commandCode += "/"; break;
				case "COORDINATEWORLD": commandCode += "/"; break;
				default: commandCode += "?"; break;
				}
			}
			
			System.out.println(commandCode);
			
			if (commandCode.equals("DOBRAKEID")) { //COMMAND 2
				A_Command command = new CommandBehavioralBrake(tokens.get(2).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("DOSELECTSWITCHIDPATHPRIMARY") || commandCode.equals("DOSELECTSWITCHIDPATHSECONDARY")) { //COMMAND 8
				boolean primary = false;
				if (tokens.get(5).getData().toUpperCase().equals("PRIMARY")) {
					primary = true;
				}
				A_Command command = new CommandBehavioralSelectSwitch(tokens.get(3).getData(), primary);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			if (commandCode.equals("@EXIT")) { //COMMAND 51
				A_Command command = new CommandMetaDoExit();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			
			while (tokens.size() > 0) {
				System.out.println(tokens.get(0).toString());
				tokens.remove(0);
			}
			
		}
		
		
	}
//	
//	
//	public void parseDoCommands(ArrayList<Token> tokens) {
//		if (this.parserHelper.isCorrectKeyword(tokens.get(0), "BRAKE")) { //BRAKE
//			tokens.remove(0);
//			if (tokens.size() != 0) {
//				if (tokens.get(0).getType().name() == "ID") {
//					A_Command command = new CommandBehavioralBrake(tokens.get(0).getData());
//					this.parserHelper.getActionProcessor().schedule(command);
//				}
//			}
//		}
//		
//		if (this.parserHelper.isCorrectKeyword(tokens.get(0), "SET")) {
//			tokens.remove(0);
//			if (tokens.size() != 0) {
//				if (tokens.get(0).getType().name().equals("ID")) {
//					A_Command command = new CommandBehavioralBrake(tokens.get(0).getData());
//					this.parserHelper.getActionProcessor().schedule(command);
//				}
//			}
//		}
//
//		if (this.parserHelper.isCorrectKeyword(tokens.get(0), "SELECT")) { //SELECT COMMANDS
//			tokens.remove(0);
//			if (tokens.size() != 0) {
//				parseDoSelectCommands(tokens);
//			}
//		}
//	}
//	
//	public void parseDoSelectCommands(ArrayList<Token> tokens) {
//		if (this.parserHelper.isCorrectKeyword(tokens.get(0), "SWITCH")) { //SWITCH
//			tokens.remove(0);
//			if (tokens.size() != 0) {
//				if (tokens.get(0).getType().name() == "ID") {
//					String ID = tokens.get(0).getData();
//					tokens.remove(0);
//					if (tokens.size() != 0) {
//						if (this.parserHelper.isCorrectKeyword(tokens.get(0), "PATH")) {
//							tokens.remove(0);
//							if (tokens.size() != 0) {
//								A_Command command = new CommandBehavioralSelectSwitch(ID, parseBinaryKeywords(tokens.get(0)));
//								this.parserHelper.getActionProcessor().schedule(command);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
	
}
//}
