package cs350f20project.controller.cli.parser;
import java.util.ArrayList;

import cs350f20project.controller.cli.TrackLocator;
import cs350f20project.controller.command.*;
import cs350f20project.controller.command.behavioral.*;
import cs350f20project.controller.command.creational.CommandCreatePowerPole;
import cs350f20project.controller.command.creational.CommandCreateStockCarPassenger;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.timing.Time;
import cs350f20project.datatype.Angle;

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
				case "COORDINATEDELTA": commandCode += ":"; break;
				case "COORDINATEWORLD": commandCode += "/"; break;
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
			
			if (commandCode.equals("DOSELECTSWITCHIDPATHPRIMARY") || commandCode.equals("DOSELECTSWITCHIDPATHSECONDARY")) { //COMMAND 8
				boolean primary = false;
				if (tokens.get(5).getData().toUpperCase().equals("PRIMARY")) {
					primary = true;
				}
				A_Command command = new CommandBehavioralSelectSwitch(tokens.get(3).getData(), primary);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("DOSETIDSPEEDNB") || commandCode.equals("DOSETIDSPEEDINT")) { //COMMAND 15
				double speed = Double.parseDouble(tokens.get(4).getData());
				A_Command command = new CommandBehavioralSetSpeed(tokens.get(2).getData(), speed);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("CREATESTOCKCARIDASCABOOSE")) { //COMMAND 29
				A_Command command = new CommandBehavioralBrake(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			if (commandCode.equals("@EXIT")) { //COMMAND 51
				A_Command command = new CommandMetaDoExit();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			if (commandCode.equals("DOSELECTROUNDHOUSEIDCLOCKWISEINT") || commandCode.equals("DOSELECTROUNDHOUSEIDCOUNTERCLOCKWISEINT")) { //COMMAND 7
				boolean cw = false;
				if(tokens.get(4).getData().toUpperCase().equals("CLOCKWISE")) {
					cw = true;
				}
				Angle ang = new Angle(Double.parseDouble(tokens.get(5).getData()));
				A_Command command  = new CommandBehavioralSelectRoundhouse(tokens.get(3).getData(),ang,cw);
				this.parserHelper.getActionProcessor().schedule(command);
			}		
			if(commandCode.equals("DOSETREFERENCEENGINEID")) { //COMMAND 12
				A_Command command = new CommandBehavioralSetReference(tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			if(commandCode.equals("CREATEPOWERPOLEIDONTRACKIDDISTANCEINTFROMSTART")||commandCode.equals("CREATEPOWERPOLEIDONTRACKIDDISTANCEINTFROMEND")) { //COMMAND 23
				String poleID = tokens.get(3).getData();
				String trackID = tokens.get(6).getData();
				double distance = Double.parseDouble(tokens.get(8).getData());
				boolean start = false;
				if(tokens.get(10).getData().toUpperCase().equals(("START"))){
					start = true;
				}
				TrackLocator tl = new TrackLocator(trackID,distance,start);
				A_Command command = new CommandCreatePowerPole(poleID,tl);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			if(commandCode.equals("CREATESTOCKIDIDASBOX")) { //COMMAND 28
				String id = tokens.get(3).getData();
				A_Command command = new CommandCreateStockCarPassenger(id);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			if(commandCode.equals("CREATESTOCKIDIDASPASSENGER")) { //COMMAND 31
				String id = tokens.get(3).getData();
				A_Command command = new CommandCreateStockCarPassenger(id);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			if(commandCode.equals("CREATESTOCKENGINEIDASDIESELONTRACKIDDISTANCEINTFROMSTARTFACINGSTART")||commandCode.equals("CREATESTOCKENGINEIDASDIESELONTRACKIDDISTANCEINTFROMENDFACINGEND")||commandCode.equals("CREATESTOCKENGINEIDASDIESELONTRACKIDDISTANCEINTFROMSTARTFACINGEND")||commandCode.equals("CREATESTOCKENGINEIDASDIESELONTRACKIDDISTANCEINTFROMENDFACINGSTART")) {
				System.out.println("caught");
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
