package boa.datagen.forges.github;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import boa.datagen.util.FileIO;




public class RepositoryClonerWorker implements Runnable{
	private String outPath = "";
	private String inPath = "";
	private int from, to;
	
	public RepositoryClonerWorker(String out, String in, int from, int to){
		this.outPath = out;
		this.inPath = in;
		this.from = from;
		this.to = to;
	}
	
	  public void clone(int from, int to) throws InvalidRemoteException, TransportException, IOException, GitAPIException{
		    File in;
		    String urlHeader = "https://github.com/";
		    String urlFooter = ".git";
		    String outFilePath = "";
		    String name = "";
		    String nameAndValue[];
		    Scanner sc = null;
		    File dir = new File(inPath);
		    File[] files = dir.listFiles();
		    for(int i = from ; i < to  ; i++){
		    //	System.out.println("Processing file number " + i );
		    	in = files[i];
		    	try {		
		    		sc = new Scanner(in);
		    	} catch (FileNotFoundException e) {		
		    		// TODO Auto-generated catch block		
		    		System.out.println(inPath +" file not found");
		    		e.printStackTrace();
		    	}
		    
		    	sc.useDelimiter(",");
		    	while(sc.hasNext()){
		    		name = sc.next();
		    		nameAndValue = name.split(":");
		    		String keyw = nameAndValue[0].substring(1, nameAndValue[0].length()-1) ;
		    		if(keyw.equals("full_name") ){
		    			name = nameAndValue[1].substring(1, nameAndValue[1].length() - 1);
		    			outFilePath = outPath + "/" + name ;
		    			File outFile = new File(outFilePath);
		    			if(!outFile.exists()){
		    				System.out.println("Thread-" + Thread.currentThread().getId() +
		    						"handling :" + "url: "+ urlHeader + name + urlFooter);
		    				String[] args = { urlHeader + name + urlFooter, outFilePath};
		    				RepositoryCloner.clone(args);
		    			}
		    		}
		    	}
		    }
		    		
		    	/*
		    	String content = FileIO.readFileContents(files[i]);
				Gson parser = new Gson();
				JsonArray repos = parser.fromJson(content, JsonElement.class).getAsJsonArray();
				for(int j = 0; j < repos.size(); j++){
					JsonObject repo = repos.get(j).getAsJsonObject();
					name = repo.get("full_name").getAsString();
					outFilePath = outPath + "/" + name ; 
	    			System.out.println("Thread-" + Thread.currentThread().getId() + "handling :" + "url: "+ urlHeader + name + urlFooter);
	    			String[] args = { urlHeader + name + urlFooter, outFilePath};
	    			RepositoryCloner.clone(args);
					}
		    	}  */
		    //	System.out.println("finished file " + i );
		    }


	@Override
	public void run() {
		try {
			clone(from,to);
		} catch (InvalidRemoteException e) {			
			e.printStackTrace();
		} catch (TransportException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (GitAPIException e) {			
			e.printStackTrace();
		}
	}
}
