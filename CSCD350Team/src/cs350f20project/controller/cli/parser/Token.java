package cs350f20project.controller.cli.parser;

import cs350f20project.controller.cli.parser.MyParserHelper.TokenType;

public class Token {
	public TokenType type;
	public String data;
	
	public Token(String data, TokenType type) {
		this.type = type;
		this.data = data;
	}

}
