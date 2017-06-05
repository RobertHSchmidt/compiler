package boa.datagen.forges.github;

import java.io.File;

public class LanguageListParser {
	public static void main(String[] args){
		int numberOfThreads = 1;
		String inputDir = args[0];
		int numFiles = new File(inputDir).listFiles().length;
		int shareSize = numFiles / numberOfThreads;
		int from = 0, to = 0;
		for (int i = 0; i < numberOfThreads - 1; i++ ){
			from = to + 1;
			to = from + shareSize;
			LanguageListParserWorker worker = new LanguageListParserWorker(inputDir, from, to);
			new Thread(worker).start();
		}
		from = to + 1;
		to = numFiles;
		LanguageListParserWorker worker = new LanguageListParserWorker(inputDir, from, to);
		new Thread(worker).start();
	}
}
