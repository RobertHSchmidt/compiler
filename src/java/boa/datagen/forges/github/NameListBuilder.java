package boa.datagen.forges.github;

import java.io.File;
import java.io.IOException;

public class NameListBuilder{
	
	public static void main(String[] args) throws IOException {
		String inPath = args[0];
		String outPath = args[1];
		//File namesFile = new File(outPath+ " /names.txt");
		final int NUMTHREADS = 3 ;
		int numFiles = new File(inPath).listFiles().length;
		int shareSize = numFiles / NUMTHREADS;
		int from = 0, to = 0;
		for (int i = 0; i < NUMTHREADS - 1; i++ ){
			from = to + 1;
			to = from + shareSize;
			NameListBuilderWorker worker = new NameListBuilderWorker(inPath, outPath, from, to);
			new Thread(worker).start();
		}
		from = to + 1;
		to = numFiles;
		NameListBuilderWorker worker = new NameListBuilderWorker(inPath, outPath, from, to);
		new Thread(worker).start();
	}
}
