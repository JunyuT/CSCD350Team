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
		KEYWORD("(?i)ANGLE|AS|"
				+ "BOX|BREAK|BRIDGE"
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
				+ "@WAIT|WIDTH|WITH|WORLD|WYE"),

		LONGITUDE("[0-9]+[*][0-9]+['][0-9]+[\\.][0-9]+[\"]"),
		NUMBER("(-*[0-9]+\\.[0-9]*)"),
		INTEGER("-*[0-9]+"),
		COORDINATESDELTA(":"),
		COORDINATESWORLD("/"),
		ID("_*[_a-zA-Z^/]+[0-9]*");

		;
		
		private final Pattern pattern;
		
		private TokenType(final String regex) {
			this.pattern = Pattern.compile(regex);
		}
		
		public Pattern getPattern() {
			return this.pattern;
		}
		
	}		
	
	/**quick explanation 
	 * (do)|(set)|(create)|(([a-zA-Z_]+(?=\\d)[0-9]+))
	 * every () is a regex group
	 * this will extract the input when correct syntax is matched
	 * it will also give the index of the variables 
	 * i will declare everything as a string, and we can comment out or uncomment the string to check for specific variables/literals.
	 * (?i) will ignore case for specific group. enable for literals only
	**/
	
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
		
		for (Token token : tokens) {
		      System.out.println(token.type + ": " + token.data);
		}
		
		return tokens;
	}

}