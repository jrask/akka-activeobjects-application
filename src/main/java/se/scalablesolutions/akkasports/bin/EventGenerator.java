package se.scalablesolutions.akkasports.bin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class EventGenerator {

	char [] chars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','X','Y'};
	Random random = new Random();
	BufferedWriter writer;

	int totalEvents = 0;
	NumberFormat format = new DecimalFormat("000000000");
	
	public static void main(String[] args) throws IOException, InterruptedException {
		EventGenerator gen = new EventGenerator();
		gen.generateEvents("MATCH", 1, 100);
		//Thread.sleep(5000);
		//gen.generateComments(100);
		//gen.generateGoals(100);
		gen.close();
	}
	
	private void close() throws IOException {
		writer.close();
	}

	public EventGenerator() throws IOException {
		File f = new File("genEvents.txt");
		FileWriter writer = new FileWriter(f);
		this.writer = new BufferedWriter(writer);
	}
	
	void generateEvents(String eventPrefix, int startUid, int eventCnt) throws IOException {
		totalEvents = eventCnt;
		for(int i = 0; i < eventCnt; i++) {
			File f = File.createTempFile("akka-sample", ".txt");
			FileWriter fw = new FileWriter(f);
			fw.write(("NEW:"+ eventPrefix + format.format((i+startUid)) + ":" + getEvent() + "\n"));
			fw.close();
			FileUtils.moveFileToDirectory(f, new File("data/new"), true);
		}
	}

	void generateComments(int commentCnt) throws IOException {
		for(int i = 0; i < commentCnt; i++) {
			File f = File.createTempFile("akka-sample", ".txt");
			FileWriter fw = new FileWriter(f);
			fw.write(("COMMENT:" + getMatchUid() + ":" + generateString(50) + "\n"));
			fw.close();
			FileUtils.moveFileToDirectory(f, new File("data/events"), true);
		}
	}
	
	void generateGoals(int goalCnt) throws IOException {
		for(int i = 0; i < goalCnt; i++) {
			File f = File.createTempFile("akka-sample", ".txt");
			FileWriter fw = new FileWriter(f);
			fw.write(("GOAL:" + getMatchUid() + ":MFF:1-0"));
			fw.close();
			FileUtils.moveFileToDirectory(f, new File("data/events"), true);
		}
	}
	
	private String getMatchUid() {
		return "MATCH" + format.format(getRnd(totalEvents) +1);
	}
	
	private String generateString(int length) {
		char[] chars = new char[length];
		for(int i = 0; i < length;i++) {
			chars[i] = getChar();
		}
		return new String(chars);
	}
	
	private String getEvent() {
		char [] chars = new char[7];
		chars[0] = getChar();
		chars[1] = getChar();
		chars[2] = getChar();
		chars[3] = ':';
		chars[4] = getChar();
		chars[5] = getChar();
		chars[6] = getChar();
		return new String(chars);
	}
	
	char getChar() {
		return chars[getRnd(this.chars.length)];
	}
	
	int getRnd(int max) {
		int rnd = random.nextInt();
		return (rnd < 0 ? -rnd : rnd) % max;
	}
}
