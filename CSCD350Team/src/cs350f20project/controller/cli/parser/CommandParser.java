package cs350f20project.controller.cli.parser;
import java.util.ArrayList;

import cs350f20project.controller.cli.TrackLocator;
import cs350f20project.controller.command.*;
import cs350f20project.controller.command.behavioral.*;
import cs350f20project.controller.command.creational.CommandCreatePowerCatenary;
import cs350f20project.controller.command.creational.CommandCreatePowerPole;
import cs350f20project.controller.command.creational.CommandCreatePowerStation;
import cs350f20project.controller.command.creational.CommandCreatePowerSubstation;
import cs350f20project.controller.command.creational.CommandCreateStockCarCaboose;
import cs350f20project.controller.command.creational.CommandCreateStockCarFlatbed;
import cs350f20project.controller.command.creational.CommandCreateStockCarPassenger;
import cs350f20project.controller.command.creational.CommandCreateStockCarTank;
import cs350f20project.controller.command.creational.CommandCreateStockCarTender;
import cs350f20project.controller.command.creational.CommandCreateStockEngineDiesel;
import cs350f20project.controller.command.creational.CommandCreateTrackBridgeDraw;
import cs350f20project.controller.command.creational.CommandCreateTrackBridgeFixed;
import cs350f20project.controller.command.creational.CommandCreateTrackCrossing;
import cs350f20project.controller.command.creational.CommandCreateTrackCrossover;
import cs350f20project.controller.command.creational.CommandCreateTrackCurve;
import cs350f20project.controller.command.creational.CommandCreateTrackEnd;
import cs350f20project.controller.command.creational.CommandCreateTrackLayout;
import cs350f20project.controller.command.creational.CommandCreateTrackRoundhouse;
import cs350f20project.controller.command.creational.CommandCreateTrackStraight;
import cs350f20project.controller.command.creational.CommandCreateTrackSwitchTurnout;
import cs350f20project.controller.command.creational.CommandCreateTrackSwitchWye;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.command.structural.CommandStructuralCommit;
import cs350f20project.controller.command.structural.CommandStructuralCouple;
import cs350f20project.controller.command.structural.CommandStructuralLocate;
import cs350f20project.controller.command.structural.CommandStructuralUncouple;
import cs350f20project.controller.timing.Time;
import cs350f20project.datatype.Angle;
import cs350f20project.datatype.CoordinatesDelta;
import cs350f20project.datatype.CoordinatesScreen;
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
			
			//6
			if (commandCode.equals("DOSELECTDRAWBRIDGEIDPOSITIONUP") || commandCode.equals("DOSELECTDRAWBRIDGEIDPOSITIONDOWN")) { //COMMAND 6
				boolean isBridgeUp = false;
				if (tokens.get(5).getData().toUpperCase().equals("UP")) {
					isBridgeUp = true;
				}
				A_Command command = new CommandBehavioralSelectBridge(tokens.get(3).getData(), isBridgeUp);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			//11
			if (commandCode.equals("DOSETDIRECTION(FORWARD|BACKWARD)")) { //COMMAND 11
				boolean isDirectionForward = false;
				if (tokens.get(5).getData().toUpperCase().equals("FORWARD")) {
					isDirectionForward = true;
				}
				A_Command command = new CommandBehavioralSetDirection(tokens.get(3).getData(), isDirectionForward);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			//22
			if (commandCode.equals("CREATEPOWERCATENARYIDWITHPOLESID+")) { //COMMAND 22
				String catenaryID = tokens.get(3).getData();
				ArrayList<String> ids = new ArrayList<String>();
				for (Token id : tokens.subList(5, tokens.size())) {
					if (id.getType().toString().equals("ID")) {
						ids.add(id.getData());
					} else {
						throw new RuntimeException("Expected IDs");
					}
				}
				A_Command command = new CommandCreatePowerCatenary(catenaryID, ids);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			//25
			if (commandCode.matches("CREATEPOWERSUBSTATIONIDREFERENCE(LX/LX|\\$ID)DELTA(NB|INT):(NB|INT)WITHCATENARIESID+")) {  //COMMAND 24
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
				System.out.println(ids);
				A_Command command = new CommandCreatePowerSubstation(id1, wCoords, dCoords, ids);
				this.parserHelper.getActionProcessor().schedule(command);
			}
			//30
			if (commandCode.equals("CREATESTOCKCARIDASFLATBED")) { //COMMAND 30	
				A_Command command = new CommandCreateStockCarFlatbed(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			//33
			if (commandCode.equals("CREATESTOCKCARIDASTENDER")) { //COMMAND 33
				A_Command command = new CommandCreateStockCarTender(tokens.get(3).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			//40
			if (commandCode.matches("CREATETRACKBRIDGEIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)ANGLE(NB|INT)")) { //COMMAND 40
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
			}
			//43
			if (commandCode.matches("CREATETRACKCURVEIDREFERENCE(LX/LX|\\$ID)DELTASTART(NB|INT):(NB|INT)END(NB|INT):(NB|INT)((DISTANCEORIGIN(NB|INT))|(ORIGIN(NB|INT):(NB|INT))"));  {
				String id = tokens.get(3).getData();
				CoordinatesWorld reference;
				if (tokens.get(5).getData().equals("$")) {
					if (this.parserHelper.hasReference(tokens.get(6).getData())) {
						reference = this.parserHelper.getReference(tokens.get(6).getData());
					} else
						throw new RuntimeException("invalid reference");
				} else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					reference = new CoordinatesWorld(lat, lon);
				}
	            CoordinatesDelta delta1 = new CoordinatesDelta(Double.parseDouble(tokens.get(10).getData()),Double.parseDouble(tokens.get(12).getData()));
	            CoordinatesDelta delta2 = new CoordinatesDelta(Double.parseDouble(tokens.get(14).getData()),Double.parseDouble(tokens.get(16).getData()));
	            double ortho1;
				CoordinatesDelta ortho2;
	            A_Command createCurve;
	            if (tokens.get(5).getData().equals("$") && tokens.get(17).getData().equals("ORIGIN")) {
	                ortho1 = Double.parseDouble(tokens.get(18).getData());
	                createCurve = new CommandCreateTrackCurve(id, reference, delta1, delta2, ortho1);
	            } else if (!tokens.get(5).getData().equals("$") && tokens.get(18).getData().equals("ORIGIN")) {
	            	ortho1 = Double.parseDouble(tokens.get(19).getData());
	                createCurve = new CommandCreateTrackCurve(id, reference, delta1, delta2, ortho1);
	            } else if (!tokens.get(5).getData().equals("$") && tokens.get(17).getData().equals("ORIGIN")) {
	            	ortho2 = new CoordinatesDelta(Double.parseDouble(tokens.get(18).getData()),Double.parseDouble(tokens.get(20).getData()));
	                createCurve = new CommandCreateTrackCurve(id, reference, delta1, delta2, ortho2);
	            } else {
	                ortho2 = new CoordinatesDelta(Double.parseDouble(tokens.get(17).getData()),Double.parseDouble(tokens.get(19).getData()));
	                createCurve = new CommandCreateTrackCurve(id, reference, delta1, delta2, ortho2);
	            }
	            this.parserHelper.getActionProcessor().schedule(createCurve);
			}
			//46
			if (commandCode.equals("CREATETRACKROUNDHOUSEIDREFERENCE(LX/LX|\\$ID)DELTAORIGIN(NB|INT):(NB|INT)ANGLEENTRY(NB|INT)START(NB|INT)END(NB|INT)WITHINTSPURSLENGTH(NB|INT)TURNTABLELENGTH(NB|INT)")) { //COMMAND 46
				String id = tokens.get(3).getData();
				CoordinatesWorld reference;
				if (tokens.get(5).getData().equals("$")) {
					if (this.parserHelper.hasReference(tokens.get(6).getData()))
						reference = this.parserHelper.getReference(tokens.get(6).getData());
					else
						throw new RuntimeException("invalid reference");
				} else {
					Latitude lat = this.parserHelper.parseLatitude(tokens.get(5));
					Longitude lon = this.parserHelper.parseLongitude(tokens.get(7));
					reference = new CoordinatesWorld(lat,lon);
				}
				CoordinatesDelta delta1 = new CoordinatesDelta(Double.parseDouble(tokens.get(10).getData()),Double.parseDouble(tokens.get(12).getData()));
	            Angle angle1 = new Angle(Double.parseDouble(tokens.get(15).getData()));
	            Angle angle2 = new Angle(Double.parseDouble(tokens.get(17).getData()));
	            Angle angle3 = new Angle(Double.parseDouble(tokens.get(19).getData()));
	            int integer = Integer.parseInt(tokens.get(21).getData());
	            double num1 = Double.parseDouble(tokens.get(24).getData());
	            double num2 = Double.parseDouble(tokens.get(27).getData());
	            A_Command createRound = new CommandCreateTrackRoundhouse(id, reference, delta1, angle1, angle2, angle3, integer, num1, num2);
	            this.parserHelper.getActionProcessor().schedule(createRound);
			}
			//49
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
			}
			//55
			if (commandCode.equals("CLOSEVIEWID")) { //COMMAND 55
				A_Command command = new CommandMetaViewDestroy(tokens.get(2).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			//61
			if (commandCode.equals("COUPLESTOCKIDANDID")) { //COMMAND 61
				A_Command command = new CommandStructuralCouple(tokens.get(2).getData(), tokens.get(4).getData());
				this.parserHelper.getActionProcessor().schedule(command);
			}
			//66
			if (commandCode.equals("USEIDASREFERENCE(LX/LX)")) { //COMMAND 66
				CoordinatesWorld reference;
				//coordinates = parserHelper.parseWorldCoordinates(tokens.get(4).getData());
				Latitude lat = this.parserHelper.parseLatitude(tokens.get(4));
				Longitude lon = this.parserHelper.parseLongitude(tokens.get(6));
				reference = new CoordinatesWorld(lat, lon);
				this.parserHelper.addReference(tokens.get(1).getData(), reference);
			}
			
			while (tokens.size() > 0) {
				//System.out.println(tokens.get(0).toString());	//enable for verbose checking
				tokens.remove(0);
			}
		}
	}
}

