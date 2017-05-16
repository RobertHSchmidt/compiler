package boa.datagen.forges.github;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;



/**
 * Simple snippet which shows how to clone a repository from a remote source
 *
 * @author dominik.stadler at gmx.at
 */
public class RepositoryCloner {

    private static  String REMOTE_URL = "";
    private HttpURLConnection connection = null;

    
    public static void clone(String input, String output) throws InvalidRemoteException, TransportException, IOException, GitAPIException{
    File in;
   // TokenList tokens = new TokenList(tokenFile);
   // Token tok = null;
   // File out = new File(output);
    String urlHeader = "https://github.com/";
    String urlFooter = ".git";
    String outFilePath= output;
    String name = "";
    String nameAndValue[];
    int pageNumber;
    Scanner sc = null;
    File dir = new File(input);
    if (!dir.exists())
        dir.mkdirs();
    File[] files = dir.listFiles();
    
    
    for(int i = 0; i < files.length ; i++){
    	System.out.println("proccesing file number " + i );
    	in = files[i];
    	try {		
    		sc = new Scanner(in);
    	} catch (FileNotFoundException e) {		
    		// TODO Auto-generated catch block		
    		System.out.println(input +" file not found");
    		e.printStackTrace();
    	}
    	sc.useDelimiter(",");
    	while(sc.hasNext()){
    		name = sc.next();
    	//	System.out.println(name);
    		nameAndValue = name.split(":");
    		String keyw = nameAndValue[0].substring(1, nameAndValue[0].length()-1) ;//name.substring(1, 10);
    	//	System.out.println("keyword: " + keyw);
    		if(keyw.equals("full_name") ){
    			//sc.next();
    			name = nameAndValue[1].substring(1, nameAndValue[1].length() - 1);
    			outFilePath = output + "/" + name ; 
    			System.out.println("url: "+ urlHeader + name + urlFooter);
    			//tok = tokens.getNextAuthenticToken(urlHeader + name + urlFooter);
    			 String[] args = { urlHeader + name + urlFooter, outFilePath};
    			 clone(args);
    		}
    	} 
    	System.out.println("finished file " + i );
    }
    } 
    
    public static void clone(String[] args) throws IOException, InvalidRemoteException, TransportException, GitAPIException {
    	// prepare a new folder for the cloned repository
    	String localpaths=args[1];
    	String url=args[0];
    	REMOTE_URL=url;
        File localPath = new File(localpaths);
        if(!localPath.exists())
        	localPath.mkdir();
        // then clone
        Git result = null;
        try {
        	result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .call();
	        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!

            // workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=474093
	        result.getRepository().close();
        } catch (Exception e) {
        	e.printStackTrace();
		} finally {
			if (result != null && result.getRepository() != null)
				result.getRepository().close();
		}
    }
    
    public static void main(String[] args) throws IOException, InvalidRemoteException, TransportException, GitAPIException {
      String input = args[0];
      String output= args[1];
      clone(input, output);
    	
    	
    	/*
    	// prepare a new folder for the cloned repository
    	String localpath=args[1];
    	String url=args[0];
    	REMOTE_URL=url;
        File localPath = new File(localpath);
        if(!localPath.exists())
        	localPath.mkdir();
        // then clone
        Git result = null;
        try {
        	result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .call();
	        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!

            // workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=474093
	        result.getRepository().close();
        } catch (Exception e) {
        	e.printStackTrace();
		} finally {
			if (result != null && result.getRepository() != null)
				result.getRepository().close();
		}
		*/
    }
    
}