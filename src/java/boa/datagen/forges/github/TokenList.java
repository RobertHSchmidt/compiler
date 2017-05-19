package boa.datagen.forges.github;

import boa.datagen.util.FileIO;

import java.io.File;
import java.util.PriorityQueue;

public class TokenList {
	private int lastUsedToken = 0;
	public PriorityQueue<Token> tokens = new PriorityQueue<Token>();

	public int size() {
		return tokens.size();
	}

	public TokenList(String path) {
		String tokenDetails = FileIO.readFileContents(new File(path));
		String[] allTokens = tokenDetails.split("\n");
		String[] usrNameAndToken = null;
		int i = 0;
		for (String token : allTokens) {
			usrNameAndToken = token.split(",");
			this.tokens.add(new Token(usrNameAndToken[0], usrNameAndToken[1], i));
			i++;
		}
	}

	public Token getNextAuthenticToken(String url) {
		MetadataCacher mc = null;
		int tokenNumber = 0;

		for (Token token : tokens) {
			System.out.println("Trying token: " + tokenNumber);
			mc = new MetadataCacher(url, token.getUserName(), token.getToken());
			if (mc.authenticate()) {
				if (this.lastUsedToken != tokenNumber) {
					this.lastUsedToken = tokenNumber;
					System.out.println("now using token: " + tokenNumber);
				}
				return token;
			}
			tokenNumber++;
		}
		throw new IllegalArgumentException();
	}

	public synchronized Token getAuthenticatedToken(long threadId) {
		Token tok = this.tokens.peek();
		if (this.tokens.size() <= 0) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return getAuthenticatedToken(threadId);
		}
		System.out.println(threadId + " using : " + tok.getId());
		tok.setThread_id(threadId);
		this.tokens.remove(tok);
		return tok;
	}

	public synchronized void removeToken(Token tok) {
		this.tokens.remove(tok);
	}

	public synchronized void addToken(Token tok) {
		this.tokens.add(tok);
	}
}
