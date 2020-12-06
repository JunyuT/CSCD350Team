package cs350f20project.controller.cli.parser;
import java.util.ArrayList;

import cs350f20project.controller.command.*;
import cs350f20project.controller.command.behavioral.*;
import cs350f20project.controller.command.creational.CommandCreateStockCarFlatbed;
import cs350f20project.controller.command.creational.CommandCreateStockCarTender;
import cs350f20project.controller.command.creational.CommandCreateTrackBridgeDraw;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.command.structural.CommandStructuralCouple;
import cs350f20project.controller.timing.Time;
import cs350f20project.datatype.CoordinatesDelta;
import cs350f20project.datatype.CoordinatesWorld;
import cs350f20project.datatype.Latitude;
import cs350f20project.datatype.Longitude;

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
			
			if (commandCode.equals("DOSELECTDRAWBRIDGEIDPOSITIONUP") || commandCode.equals("DOSELECTDRAWBRIDGEIDPOSITIONDOWN")) { //COMMAND 6
				boolean isBridgeUp = false;
				if (tokens.get(5).getData().toUpperCase().equals("UP")) {
					isBridgeUp = true;
				}
				A_Command command = new CommandBehavioralSelectBridge(tokens.get(3).getData(), isBridgeUp);
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
			
			if (commandCode.equals("DOSETDIRECTIONFORWARD") || commandCode.equals("DOSETDIRECTIONBACKWARD")) { //COMMAND 11
				boolean isDirectionForward = false;
				if (tokens.get(5).getData().toUpperCase().equals("FORWARD")) {
					isDirectionForward = true;
				}
				A_Command command = new CommandBehavioralSetDirection(tokens.get(3).getData(), isDirectionForward);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			/*
			if (commandCode.equals("")) { //COMMAND 22
				A_Command command = new CommandCreatePowerCatenary();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			*/
			/*
			if (commandCode.equals("")) { //COMMAND 25
				A_Command command = new CommandCreatePowerSubstation();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			*/
			if (commandCode.equals("CREATESTOCKCARIDASFLATBED")) { //COMMAND 30	
				A_Command command = new CommandCreateStockCarFlatbed(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("CREATESTOCKCARIDASTENDER")) { //COMMAND 33
				A_Command command = new CommandCreateStockCarTender(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			/*                     CREATE|TRACK|BRIDGE|ID|REFERENCE|COORDINATESWORLD|DELTA|START|COORDINATESDELTA|END|COORDINATESDELTA                 CREATE|TRACK|BRIDGE|ID|REFERENCE|$|ID|DELTA|START|COORDINATESDELTA|END|COORDINATESDELTA
			if (commandCode.equals("CREATETRACKBRIDGEIDREFERENCECOORDINATESWORLDDELTASTARTCOORDINATESDELTAENDCOORDINATESDELTA") || commandCode.equals("CREATETRACKBRIDGEIDREFERENCE$IDDELTASTARTCOORDINATESDELTAENDCOORDINATESDELTA")) { //COMMAND 40
				A_Command command = new CommandCreateTrackBridgeDraw();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			*/
			/*
			if (commandCode.equals("") || commandCode.equals("")) //COMMAND 43
				|| commandCode.equals("")) || commandCode.equals("")) {
				A_Command command = new CommandCreateTrackCurve();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			*/
			/*
			if (commandCode.equals("") || commandCode.equals("")) { //COMMAND 46
				A_Command command = new CommandCreateTrackRoundhouse();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			*/
			/*
			if (commandCode.equals("") || commandCode.equals("")) { //COMMAND 49
				A_Command command = new CommandCreateTrackSwitchWye();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			*/
			if (commandCode.equals("@EXIT")) { //COMMAND 51
				A_Command command = new CommandMetaDoExit();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("CLOSEVIEWID")) { //COMMAND 55
				A_Command command = new CommandMetaViewDestroy(tokens.get(2).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("COUPLESTOCKIDANDID")) { //COMMAND 61
				A_Command command = new CommandStructuralCouple(tokens.get(2).getData(), tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("USEIDASREFERENCECOORDINATEWORLD")) { //COMMAND 66
				CoordinatesWorld coordinates = null;
				coordinates = parseWorldCoordinates(tokens.get(4).getData());
				this.parserHelper.addReference(tokens.get(1).getData(), coordinates);
			}
			
			while (tokens.size() > 0) {
				System.out.println(tokens.get(0).toString());
				tokens.remove(0);
			}
		}
	}

	private CoordinatesWorld parseWorldCoordinates(String coordinates) {
		ArrayList<Character> worldCoordinates = new ArrayList<Character>();   
		for (int x = 0; x < coordinates.length(); x++)
	           worldCoordinates.add(x, coordinates.charAt(x));    
		
		ArrayList<Character> latitude = new ArrayList<Character>();
		ArrayList<Character> longitude = new ArrayList<Character>(); 
		
	    int count = 0;  
	    while (!worldCoordinates.get(count).equals('/')) {
	    	latitude.add(worldCoordinates.get(count));
	    	count++;
	    }
	    while (count < worldCoordinates.size()) {
	        if (worldCoordinates.get(count).equals('/'))
	        	count++;
	        longitude.add(worldCoordinates.get(count));
	        count++;
	    }
	    
	    String latDegString, latMinString, latSecString, longDegString, longMinString, longSecString;
	    ArrayList<Character> latDegList = new ArrayList<Character>();
	    ArrayList<Character> latMinList = new ArrayList<Character>();
	    ArrayList<Character> latSecList = new ArrayList<Character>();
	    ArrayList<Character> longDegList = new ArrayList<Character>();
	    ArrayList<Character> longMinList = new ArrayList<Character>();
	    ArrayList<Character> longSecList = new ArrayList<Character>();
	    
	    int counter = 0; 
	    
	    while (!latitude.get(counter).equals('*')) {
	    	latDegList.add(counter, latitude.get(counter));
	    	counter++;
	    }   
	    if(latitude.get(counter).equals('*'))
	       counter++;
	    
	    int tempCount = 0;
	    
	    while (!latitude.get(counter).equals('\'')) {
	       latMinList.add(tempCount, latitude.get(counter));
	       counter++;
	       tempCount++;
	    } 
	    
	    if (latitude.get(counter).equals('"'))
	       counter++;
	    
	    tempCount = 0;
	    
	    while (!latitude.get(counter).equals(' ')) {
	    	latSecList.add(tempCount, latitude.get(counter));
	        counter++;
	        tempCount++;
	    }
	    
	    latDegString = removeUnwantedChars(latDegList);
	    latMinString = removeUnwantedChars(latMinList);
	    latSecString = removeUnwantedChars(latSecList);
	    int latDeg = Integer.parseInt(latDegString);
	    int latMin = Integer.parseInt(latMinString);
	    double latSec = Double.parseDouble(latSecString);
	    
	    counter = 0;  
	    
	    while (!longitude.get(counter).equals('*')) {
	    	longDegList.add(counter, longitude.get(counter));
	    	counter++;
	    }
	    if (longitude.get(counter).equals('*'))
	    	counter++;
	    
	    tempCount = 0; 
	    
	    while (!longitude.get(counter).equals('\'')) {
	        longMinList.add(tempCount, longitude.get(counter));
	        counter++;
	        tempCount++;
	    }  
	    if (longitude.get(counter).equals('\''))
	    	counter++;  
	    
	    tempCount = 0; 
	    
	    while (!longitude.get(counter).equals('"')) {
	        longSecList.add(tempCount, longitude.get(counter));
	        counter++;
	        tempCount++;
	    } 
	    
	    longDegString = removeUnwantedChars(longDegList);
	    longMinString = removeUnwantedChars(longMinList);
	    longSecString = removeUnwantedChars(longSecList);
	    int longDeg = Integer.parseInt(longDegString);
	    int longMin = Integer.parseInt(longMinString);
	    double longSec = Double.parseDouble(longSecString);
	    
	    return new CoordinatesWorld(new Latitude(latDeg,latMin,latSec), new Longitude(longDeg,longMin,longSec));
	}
	
	private CoordinatesDelta parseDeltaCoordinates(String coordinates) {
		ArrayList<Character> deltaCoordinates = new ArrayList<Character>();
        for(int x = 0; x < coordinates.length(); x++)
            deltaCoordinates.add(x, coordinates.charAt(x));
        
        ArrayList<Character> xCoord = new ArrayList<Character>();
        ArrayList<Character> yCoord = new ArrayList<Character>();
        
        int count = 0;
        
        while(!deltaCoordinates.get(count).equals(':')) {
            xCoord.add(count, deltaCoordinates.get(count));
            count++;
        }
        if(deltaCoordinates.get(count).equals(':'))
            count++;
        
        int tempCount = 0;
        
        for(int x = count; x < deltaCoordinates.size(); x++) {
            yCoord.add(tempCount, deltaCoordinates.get(x));
            tempCount++;
        }

        String strX, strY;

        strX = removeUnwantedChars(xCoord);
        strY = removeUnwantedChars(yCoord);

        double deltaX = Double.parseDouble(strX);
        double deltaY = Double.parseDouble(strY);

        return new CoordinatesDelta(deltaX,deltaY);
	}
	
	private String removeUnwantedChars(ArrayList<Character> list) {
		String cleanStr = "";
		cleanStr = list.toString().replace("[", "").replace("]", "").replace(",", "").replace(" ", "");
		return cleanStr;
	}
}
