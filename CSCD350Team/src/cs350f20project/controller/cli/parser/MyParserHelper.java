package cs350f20project.controller.cli.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs350f20project.controller.ActionProcessor;
import cs350f20project.datatype.CoordinatesDelta;
import cs350f20project.datatype.CoordinatesWorld;
import cs350f20project.datatype.Latitude;
import cs350f20project.datatype.Longitude;

public class MyParserHelper extends A_ParserHelper {

	public MyParserHelper(ActionProcessor actionProcessor) {
		super(actionProcessor);
	}
	
	public static enum TokenType{
		KEYWORD("(?i)(AND|ANGLE|AS|"
				+ "BOX|BRAKE|BRIDGE|"
				+ "CABOOSE|CATENARY|CATENARIES|CLOCKWISE|COMMIT|CLOSE|COUPLE|COUNTERCLOCKWISE|CREATE|CROSSING|CROSSOVER|CURVE|"
				+ "DELTA|DIESEL|DIRECTION|DISTANCE|DO|DOWN|DRAW|DRAWBRIDGE|"
				+ "END|ENGINE|ENTRY|@EXIT|"
				+ "FACING|FLATBED|FROM|"
				+ "HEIGHT|"
				+ "LAYOUT|LENGTH|LOCATE|"
				+ "OFF|ON|OPEN|ORIGIN|"
				+ "PASSENGER|PATH|POSITION|POLE|POLES|POWER|PRIMARY|"
				+ "REFERENCE|ROUNDHOUSE|@RUN|"
				+ "SCREEN|SECONDARY|SELECT|SET|SPEED|SPURS|STATION|START|STOCK|STRAIGHT|SUBSTATION|SUBSTATIONS|SWITCH|"
				+ "TANK|TENDER|TRACK|TRACKS|TURNOUT|TURNTABLE|"
				+ "UNCOUPLE|UP|USE|"
				+ "VIEW|"
				+ "@WAIT|WIDTH|WITH|WORLD|WYE)\\b"),
		LONGITUDE("[0-9]+[*][0-9]+['][0-9]+[\\.]*[0-9]*[\"]"),
		REFERENCE("\\$"),
		STRING("'[\\w/]+'"),
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
			} else if (m.group(TokenType.STRING.name()) != null) {
				tokens.add(new Token(m.group(TokenType.STRING.name()), TokenType.STRING));
				continue;
			} else if (m.group(TokenType.REFERENCE.name()) != null) {
				tokens.add(new Token(m.group(TokenType.REFERENCE.name()), TokenType.REFERENCE));
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
	
	public Latitude parseLatitude(Token token) {
		String parsable = token.getData();
		int houridx = parsable.indexOf('*');
		int minuteidx = parsable.indexOf('\'');
		int secondidx = parsable.indexOf('"');
		int hour = Integer.parseInt(parsable.substring(0, houridx));
		int minute = Integer.parseInt(parsable.substring(houridx + 1, minuteidx));
		double second = Double.parseDouble(parsable.substring(minuteidx + 1, secondidx));
		System.out.println(hour + " : " +  minute + " : " + second);
		System.out.println(houridx + " : " +  minuteidx + " : " + secondidx);
		return new Latitude(hour, minute, second);
	}

	public Longitude parseLongitude(Token token) {
		String parsable = token.getData();
		int houridx = parsable.indexOf('*');
		int minuteidx = parsable.indexOf('\'');
		int secondidx = parsable.indexOf('"');
		int hour = Integer.parseInt(parsable.substring(0, houridx));
		int minute = Integer.parseInt(parsable.substring(houridx + 1, minuteidx));
		double second = Double.parseDouble(parsable.substring(minuteidx + 1, secondidx));
		System.out.println(hour + " : " +  minute + " : " + second);
		System.out.println(houridx + " : " +  minuteidx + " : " + secondidx);
		return new Longitude(hour, minute, second);
	}
	
	public CoordinatesWorld parseWorldCoordinates(String coordinates) {
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
	
	public CoordinatesDelta parseDeltaCoordinates(String coordinates) {
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
	
	public String removeUnwantedChars(ArrayList<Character> list) {
		String str = "";
		str = list.toString().replace("[", "").replace("]", "").replace(",", "").replace(" ", "");
		return str;
	}

}