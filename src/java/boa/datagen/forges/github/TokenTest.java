package boa.datagen.forges.github;

public class TokenTest {

	public static void main(String[] args){
		MetadataCacher mc;
		TokenList tokens = new TokenList(args[0]);
		for(int i = 0; i < tokens.size(); i++ ){
			Token tok = tokens.getAuthenticatedToken(Thread.currentThread().getId());
			mc = new MetadataCacher( "https://api.github.com/repositories"+ "?since=" + 0, tok.getUserName(), tok.getToken());
			mc.authenticate();
			System.out.println("Remaining requests: " + mc.getNumberOfMaxLimit());
		}
		
	}
}
