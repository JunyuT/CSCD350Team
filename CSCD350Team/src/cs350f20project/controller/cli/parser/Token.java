package cs350f20project.controller.cli.parser;

import cs350f20project.controller.cli.parser.MyParserHelper.TokenType;

public class Token {
	private TokenType type;
	private String data;
	
	public Token(String data, TokenType type) {
		this.type = type;
		this.data = data;
	}
	
	public TokenType getType() {
		return this.type;
	}
	
	public String getData() {
		return this.data;
	}
	
	public String toString() {
	    return this.type.toString() + ": " + this.data;
	}
}
