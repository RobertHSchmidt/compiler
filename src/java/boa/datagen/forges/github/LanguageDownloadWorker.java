package boa.datagen.forges.github;

import boa.datagen.util.FileIO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * Created by nmtiwari on 9/15/16. This class reads repository names from a
 * directory and downloads the languages for each repository available in the
 * directory. Point to note here is that repository names are read from github's
 * repository metadata json files.
 */
public class LanguageDownloadWorker implements Runnable {
	private TokenList tokens;
	private String repository_location;
	private final String output;
	JSONArray javarepos;
	JSONArray jsrepos;
	JSONArray phprepos;
	JSONArray scalarepos;
	JSONArray otherrepos;
	String language_url_header = "https://api.github.com/repos/";
	String language_url_footer = "/languages";
	String stateFile = "";
	int threadNum;
	int javaCounter = 1;
	int jsCounter = 1;
	int phpCounter = 1;
	int scalaCounter = 1;
	int other = 1;
	int index;
	final static int RECORDS_PER_FILE = 100;
	final int startFileNumber;
	final int endFileNumber;

	public LanguageDownloadWorker(String repoPath, String output, TokenList tokenList, int start, int end,int index){
		
		this.output = output;
		this.tokens = tokenList;
		this.repository_location = repoPath;
		this.javarepos = new JSONArray();
		this.jsrepos = new JSONArray();
		this.phprepos = new JSONArray();
		this.scalarepos = new JSONArray();
		this.otherrepos = new JSONArray();
		this.startFileNumber = start;
		this.endFileNumber = end;
		this.index = index;
	}

	public void downloadLangForRepoIn(int from, int to) throws FileNotFoundException {
		System.out.println(Thread.currentThread().getId() + " Responsible for processing: " + from + " and " + to);
		String fileHeader = this.repository_location + "/page-";
		String fileFooter = ".json";
		int pageNumber = from;
		boolean recover = false;
		int startInd = 0;
		File state = new File(output + "/state-" + index);
		if(state.exists()){
			String in = FileIO.readFileContents(state);
			String[] recovoryState = in.split(" ");
			int startPageNum = Integer.parseInt(recovoryState[0]);
			startInd = Integer.parseInt(recovoryState[1]);
			pageNumber = startPageNum;
			recover = true;
		}
		Token tok = this.tokens.getNextAuthenticToken("https://api.github.com/repositories");
		while (pageNumber <= to) {
			File repoFile = new File(fileHeader + pageNumber + fileFooter);
			String content = FileIO.readFileContents(repoFile);
			Gson parser = new Gson();
			JsonArray repos = parser.fromJson(content, JsonElement.class).getAsJsonArray();
			MetadataCacher mc = null;
			int size = repos.size();
			for (int i = 0; i < size; i++) {
				if(recover){
					i = startInd;
					recover = false;
				}
				FileIO.writeFileContents(state, pageNumber + " " + i);
				JsonObject repo = repos.get(i).getAsJsonObject();
				String langurl = this.language_url_header + repo.get("full_name").getAsString()
						+ this.language_url_footer;
				if (tok.getNumberOfRemainingLimit() <= 0) {
					tok = this.tokens.getNextAuthenticToken("https://api.github.com/repositories");
				}
				mc = new MetadataCacher(langurl, tok.getUserName(), tok.getToken());
				boolean authnticationResult = mc.authenticate();
				if (authnticationResult) {
					mc.getResponse();
					String pageContent = mc.getContent();
					repo.addProperty("language_list", pageContent);
					addRepoInReleventLangList(pageContent, output, repo);
					tok.setLastResponseCode(mc.getResponseCode());
					tok.setnumberOfRemainingLimit(mc.getNumberOfRemainingLimit());
					tok.setResetTime(mc.getLimitResetTime());
				} else {
					final int responsecode = mc.getResponseCode();
					System.err.println("authntication error " + responsecode);
					mc = new MetadataCacher("https://api.github.com/repositories", tok.getUserName(), tok.getToken());
					if (mc.authenticate()) { // if authenticate doesn't pass then token is exhausted.
						tok.setnumberOfRemainingLimit(mc.getNumberOfRemainingLimit());
					} else {
						System.out.println("token: " + tok.getId() + " exhausted");
						tok.setnumberOfRemainingLimit(0);
						i--;
					}
				}
			}
			pageNumber++;
			System.out.println(Thread.currentThread().getId() + " processing: " + pageNumber);
		}
		this.writeRemainingRepos(output);
		System.out.print(Thread.currentThread().getId() + "others: " + this.other);
	}

	private void addRepoInReleventLangList(String pageContent, String output, JsonObject repo) {
		File fileToWriteJson = null;
		if (pageContent.contains("\"Java\":")) {
			this.javarepos.put(this.javarepos.length(), repo);
			if (this.javarepos.length() % RECORDS_PER_FILE == 0) {
				fileToWriteJson = new File(output + "/java/Thread- " + Thread.currentThread().getId() + "-page-" + javaCounter + ".json");
				while(fileToWriteJson.exists()){
					javaCounter ++;
					fileToWriteJson = new File(output + "/java/Thread- " + Thread.currentThread().getId() + "-page-" + javaCounter + ".json");
				}
				FileIO.writeFileContents(fileToWriteJson, this.javarepos.toString());
				System.out.println(Thread.currentThread().getId() + " java " + javaCounter++);
				this.javarepos = new JSONArray();
			}
		} else if (pageContent.contains("\"JavaScript\":")) {
			this.jsrepos.put(this.jsrepos.length(), repo);
			if (this.jsrepos.length() % RECORDS_PER_FILE == 0) {
				fileToWriteJson = new File(output + "/js/Thread- " + Thread.currentThread().getId() + "-page-" + jsCounter + ".json");
				while(fileToWriteJson.exists()){
					jsCounter ++;
					fileToWriteJson = new File(output + "/js/Thread- " + Thread.currentThread().getId() + "-page-" + jsCounter + ".json");
				}
				FileIO.writeFileContents(fileToWriteJson, this.jsrepos.toString());
				System.out.println(Thread.currentThread().getId() + " js " + jsCounter++);
				this.jsrepos = new JSONArray();
			}
		} else if (pageContent.contains("\"PhP\":")) {
			this.phprepos.put(this.phprepos.length(), repo);
			if (this.phprepos.length() % RECORDS_PER_FILE == 0) {
				fileToWriteJson = new File(output + "/php/Thread- " + Thread.currentThread().getId() + "-page-" + phpCounter + ".json");
				while(fileToWriteJson.exists()){
					phpCounter ++;
					fileToWriteJson = new File(output + "/php/Thread- " + Thread.currentThread().getId() + "-page-" + phpCounter + ".json");
				}
				FileIO.writeFileContents(fileToWriteJson, this.phprepos.toString());
				System.out.println(Thread.currentThread().getId() + " php " + phpCounter++);
				this.phprepos = new JSONArray();
			}
		} else if (pageContent.contains("\"Scala\":")) {
			this.scalarepos.put(this.scalarepos.length(), repo);
			if (this.scalarepos.length() % RECORDS_PER_FILE == 0) {
				fileToWriteJson = new File(output + "/scala/Thread- " + Thread.currentThread().getId() + "-page-" + scalaCounter + ".json");
				while(fileToWriteJson.exists()){
					scalaCounter ++;
					fileToWriteJson = new File(output + "/scala/Thread- " + Thread.currentThread().getId() + "-page-" + scalaCounter + ".json");
				}
				FileIO.writeFileContents(fileToWriteJson, this.scalarepos.toString());
				System.out.println(Thread.currentThread().getId() + " scala: " + scalaCounter++);
				this.scalarepos = new JSONArray();
			}
		} else {
			this.otherrepos.put(this.otherrepos.length(), repo);
			if (this.otherrepos.length() % RECORDS_PER_FILE == 0) {
				fileToWriteJson = new File(output + "/other/Thread- " + Thread.currentThread().getId() + "-page-" + other + ".json");
				while(fileToWriteJson.exists()){
					other ++;
					fileToWriteJson = new File(output + "/other/Thread- " + Thread.currentThread().getId() + "-page-" + other + ".json");
				}
				FileIO.writeFileContents(fileToWriteJson, this.otherrepos.toString());
				System.out.println(Thread.currentThread().getId() + " other: " + other++);
				this.otherrepos = new JSONArray();
			}

			// this.other++;
		}
	}

	public void writeRemainingRepos(String output) {
		File fileToWriteJson = null;
		if (this.javarepos.length() > 0) {
			fileToWriteJson = new File(output + "/java/Thread- " + Thread.currentThread().getId() + "-page-" + javaCounter + ".json");
			while(fileToWriteJson.exists()){
				javaCounter ++;
				fileToWriteJson = new File(output + "/java/Thread- " + Thread.currentThread().getId() + "-page-" + javaCounter + ".json");
			}
			FileIO.writeFileContents(fileToWriteJson, this.javarepos.toString());
			System.out.println(Thread.currentThread().getId() + " java " + javaCounter++);
		}
		if (this.jsrepos.length() > 0) {
			fileToWriteJson = new File(output + "/js/Thread- " + Thread.currentThread().getId() + "-page-" + jsCounter + ".json");
			while(fileToWriteJson.exists()){
				jsCounter ++;
				fileToWriteJson = new File(output + "/js/Thread- " + Thread.currentThread().getId() + "-page-" + jsCounter + ".json");
			}
			FileIO.writeFileContents(fileToWriteJson, this.jsrepos.toString());
			System.out.println(Thread.currentThread().getId() + " js " + jsCounter++);
		}
		if (this.phprepos.length() > 0) {
			fileToWriteJson = new File(output + "/php/Thread- " + Thread.currentThread().getId() + "-page-" + phpCounter + ".json");
			while(fileToWriteJson.exists()){
				phpCounter ++;
				fileToWriteJson = new File(output + "/php/Thread- " + Thread.currentThread().getId() + "-page-" + phpCounter + ".json");
			}
			FileIO.writeFileContents(fileToWriteJson, this.phprepos.toString());
			System.out.println(Thread.currentThread().getId() + " php " + phpCounter++);
		}
		if (this.scalarepos.length() > 0) {
			fileToWriteJson = new File(output + "/scala/Thread- " + Thread.currentThread().getId() + "-page-" + scalaCounter + ".json");
			while(fileToWriteJson.exists()){
				scalaCounter ++;
				fileToWriteJson = new File(output + "/scala/Thread- " + Thread.currentThread().getId() + "-page-" + scalaCounter + ".json");
			}
			FileIO.writeFileContents(fileToWriteJson, this.scalarepos.toString());
			System.out.println(Thread.currentThread().getId() + " scala:  " + scalaCounter++);
		} else {
			fileToWriteJson = new File(output + "/other/Thread- " + Thread.currentThread().getId() + "-page-" + other + ".json");
			while(fileToWriteJson.exists()){
				other ++;
				fileToWriteJson = new File(output + "/other/Thread- " + Thread.currentThread().getId() + "-page-" + other + ".json");
			}
			FileIO.writeFileContents(fileToWriteJson, this.otherrepos.toString());
			System.out.println(Thread.currentThread().getId() + " other:  " + other++);
		}

		System.out.println("Thread- " + Thread.currentThread().getId() + " others: " + this.other);
	}

	@Override
	public void run() {
		try {
			this.downloadLangForRepoIn(this.startFileNumber, this.endFileNumber);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
