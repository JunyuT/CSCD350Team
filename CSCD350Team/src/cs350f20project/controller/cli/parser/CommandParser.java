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
			if (this.parserHelper.isCorrectKeyword(tokens.get(0), "DO")) { // DO COMMANDS
				tokens.remove(0);
				if (tokens.size() != 0) {
					parseDoCommands(tokens);
				}
			} else if (this.parserHelper.isCorrectKeyword(tokens.get(0), "@EXIT")) { // EXIT
				if (tokens.size() == 1) {
					A_Command command = new CommandMetaDoExit();
					this.parserHelper.getActionProcessor().schedule(command);	
				} else {
					throw new RuntimeException("Expected end of command.");
				}
			}

		}
		
		
	}
	
	
	public void parseDoCommands(ArrayList<Token> tokens) {
		if (this.parserHelper.isCorrectKeyword(tokens.get(0), "BRAKE")) { //BRAKE
			tokens.remove(0);
			if (tokens.size() != 0) {
				if (tokens.get(0).type.name() == "ID") {
					A_Command command = new CommandBehavioralBrake(tokens.get(0).data);
					this.parserHelper.getActionProcessor().schedule(command);
				}
			}
		}

		if (this.parserHelper.isCorrectKeyword(tokens.get(0), "SELECT")) { //SELECT COMMANDS
			tokens.remove(0);
			if (tokens.size() != 0) {
				parseDoSelectCommands(tokens);
			}
		}
		
	}
	
	public void parseDoSelectCommands(ArrayList<Token> tokens) {
		if (this.parserHelper.isCorrectKeyword(tokens.get(0), "SWITCH")) { //SWITCH
			tokens.remove(0);
			if (tokens.size() != 0) {
				if (tokens.get(0).type.name() == "ID") {
					A_Command command = new CommandBehavioralBrake(tokens.get(0).data);
					this.parserHelper.getActionProcessor().schedule(command);
				}
			}
		}
	}
	
}
//}
