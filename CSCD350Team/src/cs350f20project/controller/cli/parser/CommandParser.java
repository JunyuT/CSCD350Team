package cs350f20project.controller.cli.parser;
import cs350f20project.controller.command.*;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.timing.Time;

public class CommandParser {
	private String commandText;
	private MyParserHelper parserHelper;

	public CommandParser(MyParserHelper parserHelper, String commandText) {
		this.commandText = commandText;
		this.parserHelper = parserHelper;
	}
	
	public void parse() {
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
				Time time = new Time(Integer.parseInt(comArray[1]));
				A_Command command = new CommandMetaDoWait(time);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			
		}
	}
}
