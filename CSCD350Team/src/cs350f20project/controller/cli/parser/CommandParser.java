package cs350f20project.controller.cli.parser;
import cs350f20project.controller.command.*;
import cs350f20project.controller.command.behavioral.*;
import cs350f20project.controller.command.creational.*;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.timing.Time;

public class CommandParser {
	private String commandText;
	private MyParserHelper parserHelper;

	public CommandParser(MyParserHelper parserHelper, String commandText) {
		this.commandText = commandText;
		this.parserHelper = parserHelper;
	}
	
	public void parse() throws ParseException {
		String[] commands  = this.commandText.toLowerCase().split(";");
		int comnum = 0; //Also for testing purposes
		for (String instruction : commands) {
			instruction = instruction.trim();
			String[] comArray = instruction.split(" ");
			for (String s : comArray) { //For testing purposes
				System.out.println(comnum + ". " + s);
				comnum++;
			}
			if (comArray[0].equals("@exit")) {   // Exit command
				A_Command command = new CommandMetaDoExit();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (comArray[0].equals("@wait")) {   // Wait command
				double REAL = parserHelper.parseReal(comArray[1]);
				System.out.println(REAL);
				Time time = new Time(REAL);
				A_Command command = new CommandMetaDoWait(time);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (comArray[0].equals("do")) { // DO BRAKE
				parseDoCommands(comArray);
			}
			
			if (comArray[0].equals("create")) {
				parseCreateCommands(comArray);
			}
		}
	}
	
	private void parseDoCommands(String[] comArray) {
		if (comArray[1].equals("brake")) {
			A_Command command = new CommandBehavioralBrake(comArray[2]);
			System.out.println("BRAKE");
			this.parserHelper.getActionProcessor().schedule(command);
		}
		if (comArray[1].equals("select")) {
			parseDoSelectCommands(comArray);
			System.out.println("SELECT");
		}
		if (comArray[1].equals("set")) {
			parseDoSetCommands(comArray);
			System.out.println("SET");
		}
	}
	
	private void parseDoSelectCommands(String[] comArray) {
		String id = comArray[3];
		if (comArray[2].equals("switch")) {  //DO SELECT SWITCH id PATH
			if (comArray[4].equals("path")) {
				if (parserHelper.checkLogical(comArray[5], "primary", "secondary")) {
					A_Command command = new CommandBehavioralSelectSwitch(id, parserHelper.parseLogicalToBoolean(comArray[5], "primary", "secondary"));
					System.out.println("CHANGING SWITCH");
					this.parserHelper.getActionProcessor().schedule(command);
				}
			} else {
				System.out.println("expected <path>");
			}
		}
	}
	
	private void parseDoSetCommands(String[] comArray){
		if (false) { //if it is a specific command then go here
			
		} else { //otherwise, it must be an ID so go here
			if (comArray[3].equals("speed")) {
				try {
					double speed = parserHelper.parseReal(comArray[4]);
					A_Command command = new CommandBehavioralSetSpeed(comArray[2], speed);
					this.parserHelper.getActionProcessor().schedule(command);
				} catch (Exception e) {
					System.out.println("Expected integer");
				}
			}
		}
	}
	
	private void parseCreateCommands(String[] comArray) {
		if (comArray[1].equals("stock")) {
			parseStockCommands(comArray);
		}
	}
	
	private void parseStockCommands(String[] comArray) {
		if (comArray[2].equals("car")) {
			parseCarCommands(comArray);
		}
	}
	
	private void parseCarCommands(String[] comArray) {
		String id = comArray[3];
		if (comArray[4].equals("as")) {
			if (comArray[5].equals("caboose")) {
				A_Command command = new CommandCreateStockCarCaboose(id);
				this.parserHelper.getActionProcessor().schedule(command);
			} else if (comArray[5].equals("tank")) {
				A_Command command = new CommandCreateStockCarTank(id);
				this.parserHelper.getActionProcessor().schedule(command);
			} else {
				System.out.println("Expected stock type.");
			}
		} else {
			System.out.println("Expected <as>");
		}
	}
}
