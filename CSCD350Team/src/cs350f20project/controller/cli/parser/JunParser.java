package cs350f20project.controller.cli.parser;


import java.util.ArrayList;

import cs350f20project.controller.cli.TrackLocator;
import cs350f20project.controller.cli.parser.MyParserHelper;
import cs350f20project.controller.command.A_Command;
import cs350f20project.controller.command.behavioral.CommandBehavioralSelectRoundhouse;
import cs350f20project.controller.command.behavioral.CommandBehavioralSetReference;
import cs350f20project.controller.command.creational.CommandCreatePowerPole;
import cs350f20project.controller.command.creational.CommandCreateStockCarBox;
import cs350f20project.datatype.Angle;

public class JunParser {
	private String commandText;
	private MyParserHelper parserHelper;

	public JunParser(MyParserHelper parserHelper, String commandText) {
		this.commandText = commandText;
		this.parserHelper = parserHelper;
		System.out.println("Using your helper");
	}
	
	public void parse() {
	
		
		
		
	}
	

	

}