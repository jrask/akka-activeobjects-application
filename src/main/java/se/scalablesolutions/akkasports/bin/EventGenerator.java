package se.scalablesolutions.akkasports.bin;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import scala.runtime.RichUnit;

public class EventGenerator {

	char [] chars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','X','Y'};
	Random random = new Random();
	BufferedWriter writer;

	
	private ExecutorService executor = Executors.newFixedThreadPool(10);
	
	
	GenericObjectPool pool = new GenericObjectPool(new PoolableObjectFactory() {
		
		@Override
		public boolean validateObject(Object arg0) {
			return true;
		}
		
		@Override
		public void passivateObject(Object arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public Object makeObject() throws Exception {
			return new DefaultHttpClient();
		}
		
		@Override
		public void destroyObject(Object arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void activateObject(Object arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}
	},10);
	
	HttpClient client = new DefaultHttpClient();
	
	int totalEvents = 0;
	NumberFormat format = new DecimalFormat("000000000");
	
	List<String> tickets = Collections.synchronizedList(new ArrayList<String>());
	
	public static void main(String[] args) throws Exception {
		EventGenerator gen = new EventGenerator();
		gen.generateEvents("MATCH", 501, 500);
		//Thread.sleep(5000);
		gen.generateComments(30000);
		gen.generateGoals(30000);
		
	}


	public EventGenerator() throws IOException {
		File f = new File("genEvents.txt");
		FileWriter writer = new FileWriter(f);
		this.writer = new BufferedWriter(writer);
		
	}
	
	private void post(final String msg) throws ClientProtocolException, IOException {
		
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				HttpPost post = new HttpPost("http://localhost:8877/camel/akkasports");
				HttpClient client = null;
				try {
					post.setEntity(new StringEntity(msg));
					client = (HttpClient)pool.borrowObject();
					HttpResponse resp = client.execute(post);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					IOUtils.copy(resp.getEntity().getContent(),baos);
					
					tickets.add(baos.toString());
					post.abort();
				

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						pool.returnObject(client);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		executor.execute(run);
	}
	
	public void readStatus() {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String[] str = tickets.toArray(new String[0]);
				for (String string : str) {
					// Do read status for each ticket here.
				}
			}
		};
	}
	
	
	private void write(String msg,String dir) throws IOException {
		File f = File.createTempFile("akka-sample", ".txt");
		FileWriter fw = new FileWriter(f);
		fw.write(msg);
		fw.close();
		FileUtils.moveFileToDirectory(f, new File(dir), true);
	}
	
	private void close() throws Exception {
		writer.close();
		pool.close();
	}


	
	void generateEvents(String eventPrefix, int startUid, int eventCnt) throws IOException {
		totalEvents = eventCnt;
		for(int i = 0; i < eventCnt; i++) {
			post("NEW:"+ eventPrefix + format.format((i+startUid)) + ":" + getEvent() + "\n");
		}
	}

	void generateComments(int commentCnt) throws IOException {
		for(int i = 0; i < commentCnt; i++) {
			post("COMMENT:" + getMatchUid() + ":" + generateString(50) + "\n");
		}
	}
	
	void generateGoals(int goalCnt) throws IOException {
		for(int i = 0; i < goalCnt; i++) {
			post(("GOAL:" + getMatchUid() + ":MFF:1-0"));
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
