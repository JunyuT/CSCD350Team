package cs350f20project.controller.cli.parser;

import cs350f20project.controller.ActionProcessor;

public class MyParserHelper extends A_ParserHelper {

	public MyParserHelper(ActionProcessor actionProcessor) {
		super(actionProcessor);
		System.out.println("Using your helper");
	}

	public double parseReal(String in) throws ParseException {
		double out;
		try {
			out = Double.parseDouble(in);
		} catch(Exception e) {
			throw new ParseException("Expected REAL or INTEGER");
		}
		return out;
	}
	
	public boolean parseLogicalToBoolean(String in, String op1, String op2){
		if (in.equals(op1)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkLogical(String in, String op1, String op2) {
		if (in.equals(op1) || in.equals(op2)) {
			return true;
		} else {
			System.out.println("Expected <" + op1 + "> or <" + op2 + ">");
			return false;
		}
	}
}
