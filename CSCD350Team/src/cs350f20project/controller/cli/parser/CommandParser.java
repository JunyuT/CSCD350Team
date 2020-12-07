package cs350f20project.controller.cli.parser;
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
			
			System.out.println(commandCode);
			
			if (commandCode.equals("DOBRAKEID")) { //COMMAND 2
				A_Command command = new CommandBehavioralBrake(tokens.get(2).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			//COMMAND 6
			
			if (commandCode.matches("DOSELECTROUNDHOUSEIDPOSITION(NB|INT)(CLOCKWISE|COUNTERCLOCKWISE)")) { //COMMAND 7
				boolean cw = false;
				if(tokens.get(6).getData().toUpperCase().equals("CLOCKWISE")) {
					cw = true;
				}
				Angle ang = new Angle(Double.parseDouble(tokens.get(5).getData()));
				
				A_Command command  = new CommandBehavioralSelectRoundhouse(tokens.get(3).getData(),ang,cw);
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
			
			//COMMAND 11
			
			if(commandCode.equals("DOSETREFERENCEENGINEID")) { //COMMAND 12
				A_Command command = new CommandBehavioralSetReference(tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.matches("DOSETIDSPEED(NB|INT)")) { //COMMAND 15
				double speed = Double.parseDouble(tokens.get(4).getData());
				A_Command command = new CommandBehavioralSetSpeed(tokens.get(2).getData(), speed);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			//COMMAND 22
			
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
			}
			
			if (commandCode.matches("CREATEPOWERSTATIONIDREFERENCE(LX/LX|\\$ID)DELTA(NB|INT):(NB|INT)WITH(SUBSTATION|SUBSTATIONS)ID+")) {  //COMMAND 24
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
				System.out.println(ids);
				A_Command command = new CommandCreatePowerStation(id1, wCoords, dCoords, ids);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			//COMMAND 25
			
			if(commandCode.equals("CREATESTOCKCARIDASBOX")) { //COMMAND 28
				String id = tokens.get(3).getData();
				A_Command command = new CommandCreateStockCarPassenger(id);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("CREATESTOCKCARIDASCABOOSE")) { //COMMAND 29
				A_Command command = new CommandCreateStockCarCaboose(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			//COMMAND 30
			
			if(commandCode.equals("CREATESTOCKCARIDASPASSENGER")) { //COMMAND 31
				String id = tokens.get(3).getData();
				A_Command command = new CommandCreateStockCarPassenger(id);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.equals("CREATESTOCKCARIDASTANK")) { //COMMAND 32
				A_Command command = new CommandCreateStockCarTank(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			//COMMAND 33
			
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
			}
			
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
				
			}
			
			//COMMAND 40
			
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
				
			}
			
			if (commandCode.matches("CREATETRACKCROSSOVERIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)START(NB|INT):(NB|INT)END(NB|INT):(NB|INT)")) { //COMMAND 42
				System.out.println("COMMAND 42");
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
				
			}
			
			//COMMAND 43
			
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
			}
			
			if (commandCode.matches("CREATETRACKLAYOUTIDWITHTRACKS(ID)+")) { //COMMAND 45
				String id = tokens.get(3).getData();
				ArrayList<String> tracks = new ArrayList<String>();
				for (Token trackid : tokens.subList(6, tokens.size())) {
					tracks.add(trackid.getData());
				}
				A_Command command = new CommandCreateTrackLayout(id, tracks);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			//COMMAND 46
			
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
			}
			
			if (commandCode.matches("CREATETRACKSWITCHTURNOUTIDREFERENCE(LX/LX|\\$ID)STRAIGHTDELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)CURVEDELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)DISTANCEORIGIN(NB|INT)")) { //COMMAND 48
				System.out.println("test");
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
			}
			
			//COMMAND 49
			
			if (commandCode.equals("@EXIT")) { //COMMAND 51
				A_Command command = new CommandMetaDoExit();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			
			if (commandCode.equals("@RUNSTR")) { //COMMAND 52
				A_Command command = new CommandMetaDoRun(tokens.get(1).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			if (commandCode.matches("@WAIT(NB|INT)")) { //COMMAND 54 *OPTIONAL but seemed useful so I added it.
				Time time = new Time(Double.parseDouble(tokens.get(1).getData()));
				A_Command command = new CommandMetaDoWait(time);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			//COMMAND 55
			
			if(commandCode.matches("OPENVIEWIDORIGIN(LX/LX|\\$ID)WORLDWIDTHINTSCREENWIDTHINTHEIGHTINT")) { //COMMAND 56
				String id = tokens.get(2).getData();
				CoordinatesWorld reference;
				int worldWidth, length, height;
				int idxoffset = 0;

				if(tokens.get(4).equals('$')) {
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
				
				System.out.println("here");
			}
						
			if (commandCode.equals("COMMIT")) { //COMMAND 60
				A_Command command = new CommandStructuralCommit();
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			//COMMAND 61
			
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
			}
			
			if (commandCode.equals("UNCOUPLESTOCKIDANDID")) { //COMMAND 65
				A_Command command = new CommandStructuralUncouple(tokens.get(2).getData(), tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			
			//COMMAND 66
			
			int counter = 0; //DEBUGGING TOOL
			while (tokens.size() > 0) {
				System.out.println(tokens.get(0).toString() + " " + counter);
				tokens.remove(0);
				counter++;
			}
			
		}
		
		
	}
}
