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
	    
		DO("do"),
		SET("set"),
		CREATE("create"),
		POWERPOLE("power pole"),
		STOCKCAR("stock car"),
		BOX("as box"),
		PASSENGER("as passenger"),
		STOCKENGINE("stock engine"),
		DIESEL("as diesel"),
		TRACK("on track"),
		TRACKCROSSING("track crossing"),
		TRACKEND("track end"),
		TRACKSTRAIGHT("track straight"),
		OPENVIEW("open view"),
		COMMIT("commit"),
		
		id("([_,$])|([a-z]*)([0-9]*)"),
		angle("\\d+"),
		longitude("[0-9]+[*][0-9]+['][0-9]+[.]?[0-9]+?[']")
		;
		
		private final Pattern pattern;
		
		private TokenType(final String regex) {
			this.pattern = Pattern.compile(regex);
		}
		
		public Pattern getPattern() {
			return this.pattern;
		}
		
	}
	
	protected void getContext(String input) {
		HashMap<String, String> regs = new HashMap<String, String>();
		//enable or disable checking here
		String _do = "do";
		String _set = "set";
		String _put = "put";
		String _id = "(([a-zA-Z_]+(?=\\d)[0-9]+))";
		
		
		/**quick explanation 
		 * (do)|(set)|(create)|(([a-zA-Z_]+(?=\\d)[0-9]+))
		 * every () is a regex group
		 * this will extract the input when correct syntax is matched
		 * it will also give the index of the variables 
		 * i will declare everything as a string, and we can comment out or uncomment the string to check for specific variables/literals.
		 * (?i) will ignore case for specific group. enable for literals only
		**/

	    ArrayList<Token> tokens = new ArrayList<Token>();
		
	    StringBuffer tokenPatternsBuffer = new StringBuffer();
	    for (TokenType tokenType : TokenType.values())
	      tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
	    Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));
	    		
		Matcher m = tokenPatterns.matcher(input);
		while (m.find()) {
			if (m.group(TokenType.CREATE.name()) != null) {
				tokens.add(new Token(m.group(TokenType.CREATE.name()), TokenType.CREATE));
				continue;
			} else if (m.group(TokenType.STOCKCAR.name()) != null) {
				tokens.add(new Token(m.group(TokenType.STOCKCAR.name()), TokenType.STOCKCAR));
				continue;
			} else if (m.group(TokenType.longitude.name()) != null) {
				tokens.add(new Token(m.group(TokenType.longitude.name()), TokenType.longitude));
				continue;
			}
		}
		
		for (Token token : tokens) {
		      System.out.println(token.type);
		}
	}

}