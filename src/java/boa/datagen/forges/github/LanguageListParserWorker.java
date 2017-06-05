package boa.datagen.forges.github;

import boa.datagen.util.FileIO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;

public class LanguageListParserWorker implements Runnable {
	public String inDirPath;
	public int to, from;

	public LanguageListParserWorker(String inDir, int from, int to) {
		this.inDirPath = inDir;
		this.to = to;
		this.from = from;
	}

	public void parse() {
		File inDir = new File(inDirPath);
		File[] files = inDir.listFiles();
		for (int i = from - 1; i < to; i++) {
			String content = FileIO.readFileContents(files[i]);
			Gson parser = new Gson();
			JsonArray repos = parser.fromJson(content, JsonElement.class).getAsJsonArray();
			for (JsonElement repoE : repos) {
				JsonObject repo = repoE.getAsJsonObject();
				JsonObject langList = repo.getAsJsonObject("language_list");
				repo.remove("language_list");
				repo.add("language_list", langList);
			}
		}
	}

	
	
	@Override
	public void run() {
		parse();
	}
}
