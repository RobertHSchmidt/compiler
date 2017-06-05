package boa.datagen.forges.github;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import boa.datagen.util.FileIO;

public class NameListBuilderWorker implements Runnable {

	private String inFileDir;
	private String outDir;
	private int from, to;
	HashSet<String> names = new HashSet<String>();
	
	NameListBuilderWorker(String in, String out, int from, int to){
		this.inFileDir = in;
		this.outDir = out;
		this.from = from;
		this.to = to;
	}
	
	public void buildList() throws FileNotFoundException {
		File inDir = new File(inFileDir);
		File[] files = inDir.listFiles();
		File namesFile = new File(outDir + "/names.txt");
		for (int i = from; i < to; i++) {
			System.out.println(Thread.currentThread().getId() + " processing :" + files[i].getName());
			String content = FileIO.readFileContents(files[i]);
			Gson parser = new Gson();
			JsonArray repos = parser.fromJson(content, JsonElement.class).getAsJsonArray();
			for (JsonElement repoE : repos) {
				JsonObject repo = repoE.getAsJsonObject();
				names.add(repo.get("full_name").getAsString());
			}

			/*
			Scanner sc = new Scanner(files[i]);
			sc.useDelimiter(",");
			while (sc.hasNext()) {
				String[] keyAndValue = sc.next().split(":");
				if(keyAndValue[0].equals("\"full_name\"")){
					String name = keyAndValue[1].substring(1, keyAndValue[1].length() - 1);
					//System.out.println("name: " + name);
					//FileIO.writeFileContents(namesFile, name+"\n", true);
					names.add(name);
				}
			}
			sc.close();
			*/
		}
		FileIO.writeFileContents(namesFile, names.toString() , true);
	}
	
	
	@Override
	public void run() {
		try {
			buildList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
