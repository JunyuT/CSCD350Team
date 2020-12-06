package cs350f20project.controller.cli.parser;
import java.util.ArrayList;
import cs350f20project.controller.cli.TrackLocator;
import cs350f20project.controller.command.*;
import cs350f20project.controller.command.behavioral.*;
import cs350f20project.controller.command.creational.CommandCreatePowerPole;
import cs350f20project.controller.command.creational.CommandCreatePowerStation;
import cs350f20project.controller.command.creational.CommandCreateStockCarCaboose;
import cs350f20project.controller.command.creational.CommandCreateStockCarPassenger;
import cs350f20project.controller.command.creational.CommandCreateStockCarTank;
import cs350f20project.controller.command.creational.CommandCreateTrackBridgeDraw;
import cs350f20project.controller.command.creational.CommandCreateTrackCrossover;
import cs350f20project.controller.command.creational.CommandCreateTrackLayout;
import cs350f20project.controller.command.creational.CommandCreateTrackSwitchTurnout;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.command.structural.CommandStructuralCommit;
import cs350f20project.controller.command.structural.CommandStructuralUncouple;
import cs350f20project.controller.timing.Time;
import cs350f20project.datatype.Angle;
import cs350f20project.datatype.CoordinatesDelta;
import cs350f20project.datatype.CoordinatesWorld;
import cs350f20project.datatype.Latitude;
import cs350f20project.datatype.Longitude;
import cs350f20project.datatype.ShapeArc;

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
			
			if (commandCode.matches("DOSELECTSWITCHIDPATH(PRIMARY|SECONDARY)")) { //COMMAND 8
				System.out.println("reached");
//				boolean primary = false;
//				if (tokens.get(5).getData().toUpperCase().equals("PRIMARY")) {
//					primary = true;
//				}
//				A_Command command = new CommandBehavioralSelectSwitch(tokens.get(3).getData(), primary);
//				this.parserHelper.getActionProcessor().schedule(command);
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
			if (commandCode.matches("CREATETRACKBRIDGEDRAWIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)ANGLE(NB|INT)")) { //COMMAND 39
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
				PointLocator point = new PointLocator(reference, dCoords1, dCoords2);

				Angle angle = new Angle(Double.parseDouble(tokens.get(19).getData()));

				A_Command command = new CommandCreateTrackBridgeDraw(id, point, angle);
				this.parserHelper.getActionProcessor().schedule(command);

			}

			if (commandCode.matches("CREATETRACKCROSSOVERIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)START(NB|INT):(NB|INT)END(NB|INT):(NB|INT)")) { //COMMAND 42
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
				CoordinatesDelta dCoords3 = new CoordinatesDelta(Double.parseDouble(tokens.get(18 + idxoffset).getData()), Double.parseDouble(tokens.get(20 + idxoffset).getData()));
				CoordinatesDelta dCoords4 = new CoordinatesDelta(Double.parseDouble(tokens.get(22 + idxoffset).getData()), Double.parseDouble(tokens.get(24 + idxoffset).getData()));

				A_Command command = new CommandCreateTrackCrossover(id, reference, dCoords1, dCoords2, dCoords3, dCoords4);
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
			if(commandCode.matches("CREATEPOWERPOLEIDONTRACKIDDISTANCEINTFROM(START|END)")) { //COMMAND 23
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
			if(commandCode.equals("CREATESTOCKIDASBOX")) { //COMMAND 28
				System.out.println("create box");
				String id = tokens.get(2).getData();
				A_Command command = new CommandCreateStockCarPassenger(id);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			if(commandCode.equals("CREATESTOCKIDASPASSENGER")) { //COMMAND 31
				System.out.println("create passenger");
				String id = tokens.get(2).getData();
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
}
