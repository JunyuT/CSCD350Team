package cs350f20project.controller.cli.parser;
import java.util.ArrayList;

import cs350f20project.controller.command.*;
import cs350f20project.controller.command.behavioral.*;
import cs350f20project.controller.command.creational.*;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.command.structural.*;
import cs350f20project.controller.timing.Time;
import cs350f20project.datatype.*;

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
				case "COORDINATESDELTA": commandCode += ":"; break;
				case "COORDINATESWORLD": commandCode += "/"; break;
				case "STRING": commandCode += "STR"; break;
				case "REFERENCE": commandCode += "$"; break;
				default: commandCode += "?"; break;
				}
			}
			
			System.out.println(commandCode);
			
			if (commandCode.equals("DOBRAKEID")) { //COMMAND 2
				A_Command command = new CommandBehavioralBrake(tokens.get(2).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
						
			if (commandCode.matches("DOSELECTSWITCHIDPATH(PRIMARY|SECONDARY)")) { //COMMAND 8
				boolean primary = false;
				if (tokens.get(5).getData().toUpperCase().equals("PRIMARY")) {
					primary = true;
				}
				A_Command command = new CommandBehavioralSelectSwitch(tokens.get(3).getData(), primary);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if(commandCode.equals("DOSETREFERENCEENGINEID")) { //COMMAND 12
				A_Command command = new CommandBehavioralSetReference(tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("DOSETIDSPEEDNB")) { //COMMAND 15
				double speed = Double.parseDouble(tokens.get(4).getData());
				A_Command command = new CommandBehavioralSetSpeed(tokens.get(2).getData(), speed);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.matches("CREATEPOWERSTATIONIDREFERENCE(LX/LX|\\$ID)DELTA(NB|INT):(NB|INT)WITH(SUBSTATION|SUBSTATIONS)ID.*")) {  //COMMAND 24
				System.out.println(tokens.get(5).getData());
				String id1 = tokens.get(3).getData();
				CoordinatesWorld wCoords;
				CoordinatesDelta dCoords;
				ArrayList<String> ids = new ArrayList<String>();
				if (tokens.get(5).getType().toString().equals("LONGITUDE")) {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					wCoords = new CoordinatesWorld(lat, lon);
					dCoords = new CoordinatesDelta(Double.parseDouble(tokens.get(9).getData()), Double.parseDouble(tokens.get(11).getData()));
					for (Token id : tokens.subList(14, tokens.size())) {
						if (id.getType().toString().equals("ID")) {
							ids.add(id.getData());
						} else {
							throw new RuntimeException("Expected IDs");
						}
					}
				} else {
					wCoords = this.parserHelper.getReference(tokens.get(6).getData());
					dCoords = new CoordinatesDelta(Double.parseDouble(tokens.get(8).getData()), Double.parseDouble(tokens.get(10).getData()));
					for (Token id : tokens.subList(13, tokens.size())) {
						if (id.getType().toString().equals("ID")) {
							ids.add(id.getData());
						} else {
							throw new RuntimeException("Expected IDs");
						}
					}
				}
				System.out.println(ids);
				A_Command command = new CommandCreatePowerStation(id1, wCoords, dCoords, ids);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if(commandCode.equals("CREATESTOCKCARIDASBOX")) { //COMMAND 28
				String id = tokens.get(3).getData();
				A_Command command = new CommandCreateStockCarPassenger(id);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("CREATESTOCKCARIDASCABOOSE")) { //COMMAND 29
				A_Command command = new CommandCreateStockCarCaboose(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if(commandCode.equals("CREATESTOCKCARIDASPASSENGER")) { //COMMAND 31
				String id = tokens.get(3).getData();
				A_Command command = new CommandCreateStockCarPassenger(id);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("CREATESTOCKCARIDASCABOOSE")) { //COMMAND 32
				A_Command command = new CommandCreateStockCarTank(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			
			
			
			
			
			
			
			
			if (commandCode.contains("CREATETRACKLAYOUTIDWITHTRACKSID")) { //COMMAND 45
				System.out.println(tokens.subList(6, tokens.size()));
				
			}
			
			
			
			
			if (commandCode.equals("@EXIT")) { //COMMAND 51
				A_Command command = new CommandMetaDoExit();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			
			
			
			if (commandCode.equals("@RUNSTR")) { //COMMAND 52
				System.out.println("test");
				A_Command command = new CommandMetaDoRun(tokens.get(1).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			
			
			
			
			if (commandCode.equals("COMMIT")) { //COMMAND 60
				A_Command command = new CommandStructuralCommit();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			
			
			if (commandCode.equals("UNCOUPLESTOCKIDANDID")) { //COMMAND 65
				A_Command command = new CommandStructuralUncouple(tokens.get(2).getData(), tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			

			
			int counter = 0;
			while (tokens.size() > 0) {
				System.out.println(tokens.get(0).toString() + " " + counter);
				tokens.remove(0);
				counter++;
			}
			
		}
		
		
	}
}
