package cs350f20project.controller.cli.parser;
import java.io.File;
import java.util.ArrayList;

import cs350f20project.controller.cli.TrackLocator;
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
				case "COORDINATESDELTA": commandCode += token.getData().toUpperCase(); break;
				case "COORDINATESWORLD": commandCode += token.getData().toUpperCase(); break;
				case "STRING": commandCode += "STR"; break;
				case "REFERENCE": commandCode += "$"; break;
				default: commandCode += "?"; break;
				}
			}
			
			//System.out.println(commandCode); ENABLE FOR TROUBLE SHOOTING
			
			if (commandCode.equals("DOBRAKEID")) { //COMMAND 2
				A_Command command = new CommandBehavioralBrake(tokens.get(2).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("DOSELECTDRAWBRIDGEIDPOSITION(UP|DOWN)")) { //COMMAND 6
				boolean isBridgeUp = false;
				if (tokens.get(5).getData().toUpperCase().equals("UP")) {
					isBridgeUp = true;
				}
				A_Command command = new CommandBehavioralSelectBridge(tokens.get(3).getData(), isBridgeUp);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("DOSELECTROUNDHOUSEIDPOSITION(NB|INT)(CLOCKWISE|COUNTERCLOCKWISE)")) { //COMMAND 7
				boolean cw = false;
				if(tokens.get(6).getData().toUpperCase().equals("CLOCKWISE")) {
					cw = true;
				}
				Angle ang = new Angle(Double.parseDouble(tokens.get(5).getData()));
				
				A_Command command  = new CommandBehavioralSelectRoundhouse(tokens.get(3).getData(),ang,cw);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
						
			if (commandCode.matches("DOSELECTSWITCHIDPATH(PRIMARY|SECONDARY)")) { //COMMAND 8
				boolean primary = false;
				if (tokens.get(5).getData().toUpperCase().equals("PRIMARY")) {
					primary = true;
				}
				A_Command command = new CommandBehavioralSelectSwitch(tokens.get(3).getData(), primary);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("DOSETIDDIRECTION(FORWARD|BACKWARD)")) { //COMMAND 11
				boolean isDirectionForward = false;
				if (tokens.get(5).getData().toUpperCase().equals("FORWARD")) {
					isDirectionForward = true;
				}
				A_Command command = new CommandBehavioralSetDirection(tokens.get(3).getData(), isDirectionForward);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if(commandCode.equals("DOSETREFERENCEENGINEID")) { //COMMAND 12
				A_Command command = new CommandBehavioralSetReference(tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("DOSETIDSPEED(NB|INT)")) { //COMMAND 15
				double speed = Double.parseDouble(tokens.get(4).getData());
				A_Command command = new CommandBehavioralSetSpeed(tokens.get(2).getData(), speed);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("CREATEPOWERCATENARYIDWITHPOLES(ID)+")) { //COMMAND 22
				String catenaryID = tokens.get(3).getData();
				ArrayList<String> ids = new ArrayList<String>();
				for (Token id : tokens.subList(6, tokens.size())) {

					if (id.getType().toString().equals("ID")) {
						ids.add(id.getData());
					} else {
						throw new RuntimeException("Expected IDs");
					}
				}
				A_Command command = new CommandCreatePowerCatenary(catenaryID, ids);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if(commandCode.matches("CREATEPOWERPOLEIDONTRACKIDDISTANCE(NB|INT)FROM(START|END)")) { //COMMAND 23
				String poleID = tokens.get(3).getData();
				String trackID = tokens.get(6).getData();
				double distance = Double.parseDouble(tokens.get(8).getData());
				boolean start = false;
				if(tokens.get(10).getData().toUpperCase().equals(("START"))){
					start = true;
				}
				TrackLocator tl = new TrackLocator(trackID,distance,start);
				
				A_Command command = new CommandCreatePowerPole(poleID, tl);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("CREATEPOWERSTATIONIDREFERENCE(LX/LX|\\$ID)DELTA(NB|INT):(NB|INT)WITH(SUBSTATION|SUBSTATIONS)(ID)+")) {  //COMMAND 24
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
					if (this.parserHelper.hasReference(tokens.get(6).getData())) {
						wCoords = this.parserHelper.getReference(tokens.get(6).getData());
					} else {
						throw new RuntimeException("Invalid Reference ID");
					}
					dCoords = new CoordinatesDelta(Double.parseDouble(tokens.get(8).getData()), Double.parseDouble(tokens.get(10).getData()));
					for (Token id : tokens.subList(13, tokens.size())) {
						if (id.getType().toString().equals("ID")) {
							ids.add(id.getData());
						} else {
							throw new RuntimeException("Expected IDs");
						}
					}
				}
				A_Command command = new CommandCreatePowerStation(id1, wCoords, dCoords, ids);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("CREATEPOWERSUBSTATIONIDREFERENCE(LX/LX|\\$ID)DELTA(NB|INT):(NB|INT)WITHCATENARIES(ID+)")) {  //COMMAND 25
				String id1 = tokens.get(3).getData();
				CoordinatesWorld wCoords;
				CoordinatesDelta dCoords;
				ArrayList<String> ids = new ArrayList<String>();
				if (tokens.get(5).getType().toString().equals("LONGITUDE")) {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					wCoords = new CoordinatesWorld(lat, lon);
					dCoords = new CoordinatesDelta(Double.parseDouble(tokens.get(9).getData()), Double.parseDouble(tokens.get(11).getData()));
					for (Token id : tokens.subList(13, tokens.size())) {
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
				A_Command command = new CommandCreatePowerSubstation(id1, wCoords, dCoords, ids);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if(commandCode.equals("CREATESTOCKCARIDASBOX")) { //COMMAND 28
				String id = tokens.get(3).getData();
				A_Command command = new CommandCreateStockCarBox(id);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.equals("CREATESTOCKCARIDASCABOOSE")) { //COMMAND 29
				A_Command command = new CommandCreateStockCarCaboose(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.equals("CREATESTOCKCARIDASFLATBED")) { //COMMAND 30	
				A_Command command = new CommandCreateStockCarFlatbed(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if(commandCode.equals("CREATESTOCKCARIDASPASSENGER")) { //COMMAND 31
				String id = tokens.get(3).getData();
				A_Command command = new CommandCreateStockCarPassenger(id);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.equals("CREATESTOCKCARIDASTANK")) { //COMMAND 32
				A_Command command = new CommandCreateStockCarTank(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.equals("CREATESTOCKCARIDASTENDER")) { //COMMAND 33
				A_Command command = new CommandCreateStockCarTender(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if(commandCode.matches("CREATESTOCKENGINEIDASDIESELONTRACKIDDISTANCE(NB|INT)FROM(START|END)FACING(START|END)")) { //COMMAND 34
				String dieselID = tokens.get(3).getData();
				String trackID = tokens.get(8).getData();
				Double distance = Double.parseDouble(tokens.get(10).getData());
				boolean from = false;
				boolean end = false;
				if(tokens.get(12).getData().equalsIgnoreCase("start")) {
					from = true;
				}
				if(tokens.get(14).getData().equalsIgnoreCase("start")) {
					end = true;
				}
				TrackLocator tl = new TrackLocator(trackID,distance,from);
				A_Command command = new CommandCreateStockEngineDiesel(dieselID,tl,end);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("CREATETRACKBRIDGEDRAWIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)ANGLE(NB|INT)")) { //COMMAND 39
				String id = tokens.get(4).getData();
				CoordinatesWorld reference;
				int idxoffset = 0;
				if (tokens.get(6).getData().equals("$")) {
					if (this.parserHelper.hasReference(tokens.get(7).getData())) {
						reference = this.parserHelper.getReference(tokens.get(7).getData());
						idxoffset = -1;
					} else {
						throw new RuntimeException("invalid reference ID");
					}
				} else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(6));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(8));
					reference = new CoordinatesWorld(lat, lon);
				}
				CoordinatesDelta dCoords1 = new CoordinatesDelta(Double.parseDouble(tokens.get(11 + idxoffset).getData()), Double.parseDouble(tokens.get(13 + idxoffset).getData()));
				CoordinatesDelta dCoords2 = new CoordinatesDelta(Double.parseDouble(tokens.get(15 + idxoffset).getData()), Double.parseDouble(tokens.get(17 + idxoffset).getData()));
				PointLocator point = new PointLocator(reference, dCoords1, dCoords2);
				
				Angle angle = new Angle(Double.parseDouble(tokens.get(19).getData()));
				
				A_Command command = new CommandCreateTrackBridgeDraw(id, point, angle);
				this.parserHelper.getActionProcessor().schedule(command);
				
			} else
			
			if (commandCode.matches("CREATETRACKBRIDGEIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)")) { //COMMAND 40
				String id = tokens.get(3).getData();
				CoordinatesWorld reference;
				int idxoffset = 0;
				if (tokens.get(5).getData().equals("$")) {
					if (this.parserHelper.hasReference(tokens.get(6).getData())) {
						reference = this.parserHelper.getReference(tokens.get(6).getData());
						idxoffset = -1;
					} else {
						throw new RuntimeException("invalid reference");
					}
				} else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					reference = new CoordinatesWorld(lat, lon);
				}
				CoordinatesDelta dCoords1 = new CoordinatesDelta(Double.parseDouble(tokens.get(10 + idxoffset).getData()), Double.parseDouble(tokens.get(12 + idxoffset).getData()));
				CoordinatesDelta dCoords2 = new CoordinatesDelta(Double.parseDouble(tokens.get(14 + idxoffset).getData()), Double.parseDouble(tokens.get(16 + idxoffset).getData()));
				PointLocator point = new PointLocator(reference, dCoords1, dCoords2);

				A_Command command = new CommandCreateTrackBridgeFixed(id, point);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("CREATETRACKCROSSINGIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)")) { //COMMAND 41
				String crossingID = tokens.get(3).getData();
				CoordinatesWorld reference;
				int idxoffset = 0; //had to add offset and fix parsing. $ID and LX/LX have a different number of tokens, so we need to change how we parse after they show up.
				if(tokens.get(5).getData().equals("$")) {
					idxoffset = -1;
					if(this.parserHelper.hasReference(tokens.get(6).getData())) {
						reference = this.parserHelper.getReference(tokens.get(6).getData());

					}else {
						throw new RuntimeException("invalid reference");
					}
				}else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					reference = new CoordinatesWorld(lat,lon);
				} 
			
				CoordinatesDelta start = new CoordinatesDelta(Double.parseDouble(tokens.get(10 + idxoffset).getData()),Double.parseDouble(tokens.get(12 + idxoffset).getData()));
				CoordinatesDelta end = new CoordinatesDelta(Double.parseDouble(tokens.get(14 + idxoffset).getData()),Double.parseDouble(tokens.get(16 + idxoffset).getData()));
				
				PointLocator pl = new PointLocator(reference,start,end);
				A_Command command = new CommandCreateTrackCrossing(crossingID,pl);
				
				this.parserHelper.getActionProcessor().schedule(command);
				
			} else
			
			if (commandCode.matches("CREATETRACKCROSSOVERIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)START(NB|INT):(NB|INT)END(NB|INT):(NB|INT)")) { //COMMAND 42
				String id = tokens.get(3).getData();
				CoordinatesWorld reference;
				int idxoffset = 0;
				if (tokens.get(5).getData().equals("$")) {
					if (this.parserHelper.hasReference(tokens.get(6).getData())) {
						reference = this.parserHelper.getReference(tokens.get(6).getData());
						idxoffset = -1;
					} else {
						throw new RuntimeException("invalid reference ID");
					}
				} else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					reference = new CoordinatesWorld(lat, lon);
				}
				CoordinatesDelta dCoords1 = new CoordinatesDelta(Double.parseDouble(tokens.get(10 + idxoffset).getData()), Double.parseDouble(tokens.get(12 + idxoffset).getData()));
				CoordinatesDelta dCoords2 = new CoordinatesDelta(Double.parseDouble(tokens.get(14 + idxoffset).getData()), Double.parseDouble(tokens.get(16 + idxoffset).getData()));
				CoordinatesDelta dCoords3 = new CoordinatesDelta(Double.parseDouble(tokens.get(18 + idxoffset).getData()), Double.parseDouble(tokens.get(20 + idxoffset).getData()));
				CoordinatesDelta dCoords4 = new CoordinatesDelta(Double.parseDouble(tokens.get(22 + idxoffset).getData()), Double.parseDouble(tokens.get(24 + idxoffset).getData()));
				
				A_Command command = new CommandCreateTrackCrossover(id, reference, dCoords1, dCoords2, dCoords3, dCoords4);
				this.parserHelper.getActionProcessor().schedule(command);
				
			} else
			 
			if (commandCode.matches("CREATETRACKCURVEIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)((DISTANCEORIGIN(NB|INT))|(ORIGIN(NB|INT):(NB|INT)))")) { //COMMAND 43 NEEDS FIXING
				String id = tokens.get(3).getData();
				CoordinatesWorld reference;
				int idxoffset = 0;
				if (tokens.get(5).getData().equals("$")) {
					if (this.parserHelper.hasReference(tokens.get(6).getData())) {
						reference = this.parserHelper.getReference(tokens.get(6).getData());
						idxoffset = -1;
					} else
						throw new RuntimeException("invalid reference");
				} else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					reference = new CoordinatesWorld(lat, lon);
				}
	            CoordinatesDelta delta1 = new CoordinatesDelta(Double.parseDouble(tokens.get(10 + idxoffset).getData()),Double.parseDouble(tokens.get(12 + idxoffset).getData()));
	            CoordinatesDelta delta2 = new CoordinatesDelta(Double.parseDouble(tokens.get(14 + idxoffset).getData()),Double.parseDouble(tokens.get(16 + idxoffset).getData()));
	            double ortho1;
				CoordinatesDelta ortho2;
	            A_Command createCurve;
	            if (tokens.get(17 + idxoffset).getData().toUpperCase().equals("DISTANCE")) {
	            	ortho1 = Double.parseDouble(tokens.get(19 + idxoffset).getData());
	                createCurve = new CommandCreateTrackCurve(id, reference, delta1, delta2, ortho1);
	            } else {
	                ortho2 = new CoordinatesDelta(Double.parseDouble(tokens.get(18 + idxoffset).getData()),Double.parseDouble(tokens.get(20 + idxoffset).getData()));
	                createCurve = new CommandCreateTrackCurve(id, reference, delta1, delta2, ortho2);
	            }
	            this.parserHelper.getActionProcessor().schedule(createCurve);
			} else
			
			if(commandCode.matches("CREATETRACKENDIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)")) { //COMMAND 44
				String id = tokens.get(3).getData();
				CoordinatesWorld reference;
				int idxoffset = 0;
				if(tokens.get(5).getData().equals("$")) {
					if(this.parserHelper.hasReference(tokens.get(6).getData())) {
						reference = this.parserHelper.getReference(tokens.get(6).getData());
						idxoffset = -1;
					}else {
						throw new RuntimeException("invalid reference");
					}

				}else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					reference = new CoordinatesWorld(lat,lon);
				}
				CoordinatesDelta start = new CoordinatesDelta(Double.parseDouble(tokens.get(10 + idxoffset).getData()),Double.parseDouble(tokens.get(12 + idxoffset).getData()));
				CoordinatesDelta end = new CoordinatesDelta(Double.parseDouble(tokens.get(14 + idxoffset).getData()),Double.parseDouble(tokens.get(16 + idxoffset).getData()));
				
				PointLocator pl = new PointLocator(reference,start,end);
				
				A_Command command = new CommandCreateTrackEnd(id,pl);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("CREATETRACKLAYOUTIDWITHTRACKS(ID)+")) { //COMMAND 45
				String id = tokens.get(3).getData();
				ArrayList<String> tracks = new ArrayList<String>();
				for (Token trackid : tokens.subList(6, tokens.size())) {
					tracks.add(trackid.getData());
				}
				A_Command command = new CommandCreateTrackLayout(id, tracks);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("CREATETRACKROUNDHOUSEIDREFERENCE(LX/LX|\\$ID)DELTAORIGIN(NB|INT):(NB|INT)ANGLEENTRY(NB|INT)START(NB|INT)END(NB|INT)WITHINTSPURSLENGTH(NB|INT)TURNTABLELENGTH(NB|INT)")) { //COMMAND 46 NEEDS FIXING
				String id = tokens.get(3).getData();
				CoordinatesWorld reference;
				int idxoffset = 0;
				if (tokens.get(5).getData().equals("$")) {
					if (this.parserHelper.hasReference(tokens.get(6).getData())) {
						reference = this.parserHelper.getReference(tokens.get(6).getData());
						idxoffset = -1;
					} else {
						throw new RuntimeException("invalid reference");
					}
				} else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					reference = new CoordinatesWorld(lat,lon);
				}
				CoordinatesDelta delta1 = new CoordinatesDelta(Double.parseDouble(tokens.get(10 + idxoffset).getData()),Double.parseDouble(tokens.get(12 + idxoffset).getData()));
	            Angle angle1 = new Angle(Double.parseDouble(tokens.get(15 + idxoffset).getData()));
	            Angle angle2 = new Angle(Double.parseDouble(tokens.get(17 + idxoffset).getData()));
	            Angle angle3 = new Angle(Double.parseDouble(tokens.get(19 + idxoffset).getData()));
	            int integer = Integer.parseInt(tokens.get(21 + idxoffset).getData());
	            double num1 = Double.parseDouble(tokens.get(24 + idxoffset).getData());
	            double num2 = Double.parseDouble(tokens.get(27 + idxoffset).getData());
	            A_Command createRound = new CommandCreateTrackRoundhouse(id, reference, delta1, angle1, angle2, angle3, integer, num1, num2);
	            this.parserHelper.getActionProcessor().schedule(createRound);
			} else
			
			if(commandCode.matches("CREATETRACKSTRAIGHTIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)")) { //COMMAND 47
				String id = tokens.get(3).getData();
				CoordinatesWorld reference;
				int idxoffset = 0;
				if(tokens.get(5).getData().equals("$")) {
					if(this.parserHelper.hasReference(tokens.get(6).getData())) {
						reference = this.parserHelper.getReference(tokens.get(6).getData());
						idxoffset = -1;
					}else {
						throw new RuntimeException("invalid reference");
					}

				}else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					reference = new CoordinatesWorld(lat,lon);
				}
				CoordinatesDelta start = new CoordinatesDelta(Double.parseDouble(tokens.get(10 + idxoffset).getData()),Double.parseDouble(tokens.get(12 + idxoffset).getData()));
				CoordinatesDelta end = new CoordinatesDelta(Double.parseDouble(tokens.get(14 + idxoffset).getData()),Double.parseDouble(tokens.get(16 + idxoffset).getData()));
				
				PointLocator pl = new PointLocator(reference,start,end);
				
				A_Command command = new CommandCreateTrackStraight(id,pl);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.matches("CREATETRACKSWITCHTURNOUTIDREFERENCE(LX/LX|\\$ID)STRAIGHTDELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)CURVEDELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)DISTANCEORIGIN(NB|INT)")) { //COMMAND 48
				String id = tokens.get(3).getData();
				CoordinatesWorld reference;
				int idxoffset = 0;
				if (tokens.get(6).getData().equals("$")) {
					if (this.parserHelper.hasReference(tokens.get(7).getData())) {
						reference = this.parserHelper.getReference(tokens.get(7).getData());
						idxoffset = -1;
					} else {
						throw new RuntimeException("invalid reference");
					}
				} else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(6));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(8));
					reference = new CoordinatesWorld(lat, lon);
				}
				CoordinatesDelta dCoords1 = new CoordinatesDelta(Double.parseDouble(tokens.get(12 + idxoffset).getData()), Double.parseDouble(tokens.get(14 + idxoffset).getData()));
				CoordinatesDelta dCoords2 = new CoordinatesDelta(Double.parseDouble(tokens.get(16 + idxoffset).getData()), Double.parseDouble(tokens.get(18 + idxoffset).getData()));
				CoordinatesDelta dCoords3 = new CoordinatesDelta(Double.parseDouble(tokens.get(22 + idxoffset).getData()), Double.parseDouble(tokens.get(24 + idxoffset).getData()));
				CoordinatesDelta dCoords4 = new CoordinatesDelta(Double.parseDouble(tokens.get(26 + idxoffset).getData()), Double.parseDouble(tokens.get(28 + idxoffset).getData()));
				double origindistance = Double.parseDouble(tokens.get(31 + idxoffset).getData());
				
				
				CoordinatesDelta originCoords = ShapeArc.calculateDeltaOrigin(reference, dCoords3, dCoords4, origindistance); 
				
				A_Command command = new CommandCreateTrackSwitchTurnout(id, reference, dCoords1, dCoords2, dCoords3, dCoords4, originCoords);
				this.parserHelper.getActionProcessor().schedule(command);	
			} else
			
			if (commandCode.matches("CREATETRACKSWITCHWYEIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)DISTANCEORIGIN(NB|INT)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)DISTANCEORIGIN(NB|INT)")) { //COMMAND 49
				String id = tokens.get(4).getData();
				CoordinatesWorld reference;
				int idxoffset = 0;
				if (tokens.get(6).getData().equals("$")) {
					if (this.parserHelper.hasReference(tokens.get(7).getData())) {
						reference = this.parserHelper.getReference(tokens.get(7).getData());
						idxoffset = -1;
					} else {
						throw new RuntimeException("invalid reference");
					}
				} else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(6));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(8));
					reference = new CoordinatesWorld(lat, lon);
				}
				CoordinatesDelta dCoords1 = new CoordinatesDelta(Double.parseDouble(tokens.get(11 + idxoffset).getData()), Double.parseDouble(tokens.get(13 + idxoffset).getData()));
				CoordinatesDelta dCoords2 = new CoordinatesDelta(Double.parseDouble(tokens.get(15 + idxoffset).getData()), Double.parseDouble(tokens.get(17 + idxoffset).getData()));
				CoordinatesDelta dCoords3 = new CoordinatesDelta(Double.parseDouble(tokens.get(23 + idxoffset).getData()), Double.parseDouble(tokens.get(25 + idxoffset).getData()));
				CoordinatesDelta dCoords4 = new CoordinatesDelta(Double.parseDouble(tokens.get(27 + idxoffset).getData()), Double.parseDouble(tokens.get(29 + idxoffset).getData()));
				double origindistance1 = Double.parseDouble(tokens.get(20 + idxoffset).getData());
				double origindistance2 = Double.parseDouble(tokens.get(32 + idxoffset).getData());

				CoordinatesDelta originCoords1 = ShapeArc.calculateDeltaOrigin(reference, dCoords1, dCoords2, origindistance1);
				CoordinatesDelta originCoords2 = ShapeArc.calculateDeltaOrigin(reference, dCoords3, dCoords4, origindistance2);
				
				A_Command command = new CommandCreateTrackSwitchWye(id, reference, dCoords1, dCoords2, originCoords1, dCoords3, dCoords4, originCoords2);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.equals("@EXIT")) { //COMMAND 51
				A_Command command = new CommandMetaDoExit();
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			
			if (commandCode.equals("@RUNSTR")) { //COMMAND 52
				String input = tokens.get(1).getData();
				input = input.substring(1, input.length() - 1);
				System.out.println(input);
				File file = new File(input);
				if (file.exists()) {
					A_Command command = new CommandMetaDoRun(input);
					this.parserHelper.getActionProcessor().schedule(command);
				} else {
					throw new RuntimeException("Invalid File");
				}
			} else
			
			if (commandCode.matches("@WAIT(NB|INT)")) { //COMMAND 54 *OPTIONAL but seemed useful so I added it.
				Time time = new Time(Double.parseDouble(tokens.get(1).getData()));
				A_Command command = new CommandMetaDoWait(time);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.equals("CLOSEVIEWID")) { //COMMAND 55
				A_Command command = new CommandMetaViewDestroy(tokens.get(2).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if(commandCode.matches("OPENVIEWIDORIGIN(LX/LX|\\$ID)WORLDWIDTHINTSCREENWIDTHINTHEIGHTINT")) { //COMMAND 56
				String id = tokens.get(2).getData();
				CoordinatesWorld reference;
				int worldWidth, length, height;
				int idxoffset = 0;

				if(tokens.get(4).getData().equals("$")) {
					if(this.parserHelper.hasReference(tokens.get(5).getData())) {
						reference = this.parserHelper.getReference(tokens.get(5).getData());
						idxoffset = -1;
					}else {
						throw new RuntimeException("invalid reference");
					}
				}else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(4));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(6));
					reference = new CoordinatesWorld(lat, lon);
				}
				worldWidth = Integer.parseInt(tokens.get(9 + idxoffset).getData());
				length = Integer.parseInt(tokens.get(12 + idxoffset).getData());
				height = Integer.parseInt(tokens.get(14 + idxoffset).getData());
				
				
				CoordinatesScreen ss = new CoordinatesScreen(length,height);
				
				A_Command command = new CommandMetaViewGenerate(id,reference,worldWidth,ss);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
						
			if (commandCode.equals("COMMIT")) { //COMMAND 60
				A_Command command = new CommandStructuralCommit();
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.equals("COUPLESTOCKIDANDID")) { //COMMAND 61
				A_Command command = new CommandStructuralCouple(tokens.get(2).getData(), tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if(commandCode.matches("LOCATESTOCKIDONTRACKIDDISTANCE(NB|INT)FROM(START|END)")) { //COMMAND 62
				String id = tokens.get(2).getData();
				String trackID = tokens.get(5).getData();
				double distance = Double.parseDouble(tokens.get(7).getData());
				boolean start = false;
				if(tokens.get(9).getData().equalsIgnoreCase("start")) {
					start = true;
				}
				TrackLocator tl = new TrackLocator(trackID,distance,start);
				
				A_Command command = new CommandStructuralLocate(id,tl);
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.equals("UNCOUPLESTOCKIDANDID")) { //COMMAND 65
				A_Command command = new CommandStructuralUncouple(tokens.get(2).getData(), tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			} else
			
			if (commandCode.equals("USEIDASREFERENCELX/LX")) { //COMMAND 66
				CoordinatesWorld reference;
				Latitude lat = this.parserHelper.parseLatitude(tokens.get(4));
				Longitude lon = this.parserHelper.parseLongitude(tokens.get(6));
				reference = new CoordinatesWorld(lat, lon);
				this.parserHelper.addReference(tokens.get(1).getData(), reference);
			} else {
				throw new RuntimeException("Invalid Command");
			}
			
			while (tokens.size() > 0) {
				//System.out.println(tokens.get(0).toString());	//enable for verbose checking
				tokens.remove(0);
			}
			
		}
		
		
	}
}
