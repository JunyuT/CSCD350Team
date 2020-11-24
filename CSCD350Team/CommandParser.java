package cs350f20project.controller.cli.parser;
import cs350f20project.controller.command.*;
import cs350f20project.controller.command.behavioral.*;
import cs350f20project.controller.command.creational.CommandCreateStockCarFlatbed;
import cs350f20project.controller.command.creational.CommandCreateStockCarTender;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.command.structural.CommandStructuralCouple;
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
			comnum++;
			instruction = instruction.trim();
			String[] comArray = instruction.split(" ");
			for (String s : comArray) { //For testing purposes
				System.out.println(comnum + ". " + s);
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
			
			if (comArray[0].equals("DO")) { // DO BRAKE wip
				if (comArray[1].equals("BRAKE")) {
					A_Command command = new CommandBehavioralBrake(comArray[2]);
					this.parserHelper.getActionProcessor().schedule(command);
				}
			}
			
			// 6
			if (comArray[0].equals("DO")) { 
				if (comArray[1].equals("SELECT")) {
					if (comArray[2].equals("DRAWBRIDGE")) {
						if (comArray[4].equals("POSITION")) {
							if (comArray[5] == "UP") {
								A_Command command = new CommandBehavioralSelectBridge(comArray[3], true);
								this.parserHelper.getActionProcessor().schedule(command);
							}
							if (comArray[5] == "DOWN") {
								A_Command command = new CommandBehavioralSelectBridge(comArray[3], false);
								this.parserHelper.getActionProcessor().schedule(command);
							}
						}
					}
				}
			}
			
			// 11
			if (comArray[0].equals("DO")) {
				if (comArray[1].equals("SET")) {
					if (comArray[3].equals("DIRECTION")) {
						if (comArray[4] == "FORWARD") {
							A_Command command = new CommandBehavioralSetDirection(comArray[2], true);
							this.parserHelper.getActionProcessor().schedule(command);
						}
						if (comArray[4] == "BACKWARD") {
							A_Command command = new CommandBehavioralSetDirection(comArray[2], false);
							this.parserHelper.getActionProcessor().schedule(command);
						}
					}
				}
			}
			
			// 30
			if (comArray[0].equals("CREATE")) {
				if (comArray[1].equals("STOCK")) {
					if (comArray[2].equals("CAR")) {
						if (comArray[4].equals("AS")) {
							if (comArray[5].equals("FLATBED")) {
								A_Command command = new CommandCreateStockCarFlatbed(comArray[3]);
								this.parserHelper.getActionProcessor().schedule(command);
							}
						}
					}
				}
			}
			
			// 33
			if (comArray[0].equals("CREATE")) {
				if (comArray[1].equals("STOCK")) {
					if (comArray[2].equals("CAR")) {
						if (comArray[4].equals("AS")) {
							if (comArray[5].equals("TENDER")) {
								A_Command command = new CommandCreateStockCarTender(comArray[3]);
								this.parserHelper.getActionProcessor().schedule(command);
							}
						}
					}	
				}
			}
			
			// 55
			if (comArray[0].equals("CLOSE")) {
				if (comArray[1].equals("VIEW")) {
					A_Command command = new CommandMetaViewDestroy(comArray[2]);
					this.parserHelper.getActionProcessor().schedule(command);
				}
			}
			
			// 61
			if (comArray[0].equals("COUPLE")) {
				if (comArray[1].equals("STOCK")) {
					if (comArray[3].equals("AND")) {
						A_Command command = new CommandStructuralCouple(comArray[2], comArray[4]);
						this.parserHelper.getActionProcessor().schedule(command);
					}
				}
			}
		}
	}
}
