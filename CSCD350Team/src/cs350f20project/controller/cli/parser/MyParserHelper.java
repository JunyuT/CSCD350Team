package cs350f20project.controller.cli.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs350f20project.controller.ActionProcessor;

public class MyParserHelper extends A_ParserHelper {

	public MyParserHelper(ActionProcessor actionProcessor) {
		super(actionProcessor);
	}
	
	public static enum TokenType{
		KEYWORD("(?i)(ANGLE|AS|"
				+ "BOX|BRAKE|BRIDGE"
				+ "CABOOSE|CATENARY|CATENARIES|CLOCKWISE|COMMIT|CLOSE|COUPLE|COUNTERCLOCKWISE|CREATE|CROSSING|CURVE"
				+ "DELTA|DIESEL|DIRECTION|DISTANCE|DO|DOWN|DRAW|DRAWBRIDGE|"
				+ "END|ENGINE|ENTRY|@EXIT|"
				+ "FACING|FLATBED|FROM|"
				+ "HEIGHT|"
				+ "LAYOUT|LENGTH|"
				+ "OFF|ON|OPEN|ORIGIN"
				+ "PASSENGER|PATH|POSITION|POLE|POLES|POWER|PRIMARY|"
				+ "REFERENCE|ROUNDHOUSE|@RUN"
				+ "SCREEN|SECONDARY|SELECT|SET|SPEED|SPURS|STATION|START|STOCK|STRAIGHT|SUBSTATION|SUBSTATIONS|SWITCH|"
				+ "TANK|TENDER|TRACK|TURNOUT|TURNTABLE|"
				+ "UP|USE|"
				+ "VIEW|"
				+ "@WAIT|WIDTH|WITH|WORLD|WYE(?!.*\\1))*$)"),
		LONGITUDE("[0-9]+[*][0-9]+['][0-9]+[\\.][0-9]+[\"]"),
		NUMBER("(-*[0-9]+\\.[0-9]*)"),
		INTEGER("-*[0-9]+"),
		COORDINATESDELTA(":"),
		COORDINATESWORLD("/"),
		ID("[_$a-zA-Z]+[_$a-zA-Z0-9]*");

		private final Pattern pattern;
		
		private TokenType(final String regex) {
			this.pattern = Pattern.compile(regex);
		}
		
		public Pattern getPattern() {
			return this.pattern;
		}
		
	}		
	
	protected ArrayList<Token> getContext(String input) {


	    ArrayList<Token> tokens = new ArrayList<Token>();
		
	    StringBuffer tokenPatternsBuffer = new StringBuffer();
	    for (TokenType tokenType : TokenType.values())
	      tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
	    Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));
	    		
		Matcher m = tokenPatterns.matcher(input);
		while (m.find()) {
			if (m.group(TokenType.KEYWORD.name()) != null) {
				tokens.add(new Token(m.group(TokenType.KEYWORD.name()), TokenType.KEYWORD));
				continue;
			} else if (m.group(TokenType.ID.name()) != null) {
				tokens.add(new Token(m.group(TokenType.ID.name()), TokenType.ID));
				continue;
			} else if (m.group(TokenType.COORDINATESWORLD.name()) != null) {
				tokens.add(new Token(m.group(TokenType.COORDINATESWORLD.name()), TokenType.COORDINATESWORLD));
				continue;
			} else if (m.group(TokenType.COORDINATESDELTA.name()) != null) {
				tokens.add(new Token(m.group(TokenType.COORDINATESDELTA.name()), TokenType.COORDINATESDELTA));
				continue;
			} else if (m.group(TokenType.LONGITUDE.name()) != null) {
				tokens.add(new Token(m.group(TokenType.LONGITUDE.name()), TokenType.LONGITUDE));
				continue;
			} else if (m.group(TokenType.NUMBER.name()) != null) {
				tokens.add(new Token(m.group(TokenType.NUMBER.name()), TokenType.NUMBER));
				continue;
			} else if (m.group(TokenType.INTEGER.name()) != null) {
				tokens.add(new Token(m.group(TokenType.INTEGER.name()), TokenType.INTEGER));
				continue;
			}
		}
		
		return tokens;
	}
	
	public boolean isCorrectKeyword(Token token, String input) {
		if (token.getType().name().equals("KEYWORD") && token.getData().compareToIgnoreCase(input) == 0) {
			return true;
		}
		return false;
	}
	
//	public String codifyKeyword(Token input) {
//		switch (input.getData().toUpperCase()) {
//		case "ANGLE": break;
//		case "AS": break;
//		case "BOX": break;
//		case "BRAKE": break;
//		case "BRIDGE": break;
//		case "CABOOSE": break;
//		case "CATENARY": break;
//		case "CATENARIES": break;
//		case "CLOCKWISE": break;
//		case "COMMIT": break;
//		case "CLOSE": break;
//		case "COUPLE": break;
//		case "COUNTERCLOCKWISE": break;
//		case "CREATE": break;
//		case "CROSSING": break;
//		case "CURVE": break;
//		case "DELTA": break;
//		case "DIESEL": break;
//		case "DIRECTION": break;
//		case "DISTANCE": break;
//		case "DO": break;
//		case "DOWN": break;
//		case "DRAW": break;
//		case "DRAWBRIDGE": break;
//		case "END": break;
//		case "ENGINE": break;
//		case "ENTRY": break;
//		case "@EXIT": break;
//		case "FACING": break;
//		case "FLATBED": break;
//		case "FROM": break;
//		case "HEIGHT": break;
//		case "LAYOUT": break;
//		case "LENGTH": break;
//		case "OFF": break;
//		case "ON": break;
//		case "OPEN": break;
//		case "ORIGIN": break;
//		case "PASSENGER": break;
//		case "PATH": break;
//		case "POSITION": break;
//		case "POLE": break;
//		case "POLES": break;
//		case "POWER": break;
//		case "PRIMARY": break;
//		case "REFERENCE": break;
//		case "ROUNDHOUSE": break;
//		case "@RUN": break;
//		case "SCREEN": break;
//		case "SECONDARY": break;
//		case "SELECT": break;
//		case "SET": break;
//		case "SPEED": break;
//		case "SPURS": break;
//		case "STATION": break;
//		case "START": break;
//		case "STOCK": break;
//		case "STRAIGHT": break;
//		case "SUBSTATION": break;
//		case "SUBSTATIONS": break;
//		case "SWITCH": break;
//		case "TANK": break;
//		case "TENDER": break;
//		case "TRACK": break;
//		case "TURNOUT": break;
//		case "TURNTABLE": break;
//		case "UP": break;
//		case "USE": break;
//		case "VIEW": break;
//		case "@WAIT": break;
//		case "": break;
//		
//		
//		}
//		return "??";
//	}
}