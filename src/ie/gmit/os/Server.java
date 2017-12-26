package ie.gmit.os;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
	public static void main(String[] args) throws Exception {
		// create a server socket
		int port = 2004;
		ServerSocket serverSocket = new ServerSocket(port,10);
		System.out.println("Server Socket created - Port: " + port);
		// thread id
		int count = 0;
		String threadID = count + ""; 
		while (true) {
			// listen for connections
			Socket clientSocket = serverSocket.accept();
			// create and start a new thread for the connection
			Thread clientThread = new Thread(new ClientServiceHandler(clientSocket), threadID); 
			clientThread.start();
			// increment the count to be used as a thread name
			count++;
		}
	}
}

class ClientServiceHandler implements Runnable {
	static final String JSON_FILE = "data.json";
	Socket clientSocket;
	String message;
	boolean running = true;
	ObjectOutputStream outputToClient;
	ObjectInputStream inputFromClient;

	ClientServiceHandler(Socket s) {
		clientSocket = s;		
	}

	private void readData() throws IOException {
		JSONParser parser = new JSONParser();		
		try {
			Object obj = parser.parse(new FileReader("data.json"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray listOfUsers = (JSONArray) jsonObject.get("users");			
			for (int i = 0; i < listOfUsers.size(); i++) {
			    // obtaining the i-th user
			    JSONObject user = (JSONObject) listOfUsers.get(i);
			    System.out.println("user: "+user);
			    long userIndex = (Long) user.get("index");
			    System.out.println("index: "+userIndex);
			    String name = (String) user.get("name");
			    System.out.println("name: "+name);
			    String address = (String) user.get("address");
			    System.out.println("address: "+address);
			    String ppsn = (String) user.get("ppsn");
			    System.out.println("ppsn: "+ppsn);
			    long age = (Long) user.get("age");
			    System.out.println("age: "+age);
			    double weight = (Double) user.get("weight");
			    System.out.println("weight: "+weight);
			    double height = (Double) user.get("height");
			    System.out.println("height: "+height);
			    
			    JSONArray fitnessRecords = (JSONArray) user.get("fitnessRecords");
			    for (int j = 0; j < fitnessRecords.size(); j++) {
			    	JSONObject fitnessRecord = (JSONObject) fitnessRecords.get(j);
			    	System.out.println("fitnessRecord: "+fitnessRecord);
			    	long fitnessIndex = (Long) fitnessRecord.get("index");
			    	System.out.println("fitnessIndex: "+fitnessIndex);
			    	String mode = (String) fitnessRecord.get("mode");
			    	System.out.println("mode: "+mode);
			    	long duration = (Long) fitnessRecord.get("duration");
			    	System.out.println("duration: "+duration);
			    }
			    
			    JSONArray mealRecords = (JSONArray) user.get("mealRecords");
			    for (int k = 0; k < mealRecords.size(); k++) {
			    	JSONObject mealRecord = (JSONObject) mealRecords.get(k);
			    	System.out.println("mealRecord: "+mealRecord);
			    	long mealIndex = (Long) mealRecord.get("index");
			    	System.out.println("mealIndex: "+mealIndex);
			    	String typeOfMeal = (String) mealRecord.get("typeOfMeal");
			    	System.out.println("typeOfMeal: "+typeOfMeal);
			    	String description = (String) mealRecord.get("description");
			    	System.out.println("description: "+description);
			    }
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void writeData() {

	}

	private void sendMessage(String msg) {
		try {
			outputToClient.writeObject(msg);
			outputToClient.flush();			
		}
		catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private String getCurrentTime(long currentTimeMillis) {
		String currentTimeString = "";
		long totalMilliseconds = currentTimeMillis;
		long totalSeconds = totalMilliseconds / 1000;
		long currentSecond = totalSeconds % 60;
		long totalMinutes = totalSeconds / 60;
		long currentMinute = totalMinutes % 60;
		long totalHours = totalMinutes / 60;
		long currentHour = totalHours % 24;

		currentTimeString = currentHour + ":" + currentMinute + ":" + currentSecond + " GMT";
		return currentTimeString;
	}

	public void run() {
		try 
		{
			outputToClient = new ObjectOutputStream(clientSocket.getOutputStream());
			outputToClient.flush();
			inputFromClient = new ObjectInputStream(clientSocket.getInputStream());		
			System.out.println("Accepted Client ID: " + Thread.currentThread().getName() 
					+ ", Host Name: " + clientSocket.getInetAddress().getHostName()
					+ ", Ip Address: " + clientSocket.getInetAddress().getHostAddress()
					+ ", Current Time: " + getCurrentTime(System.currentTimeMillis()));
			do{
				try
				{					
					// send a first menu message to the client
					sendMessage("Welcome to The Fitness Tracker\nPress 1 for Login\nPress 2 for Registration\nPress 3 to exit"); 
					// receive a message from the client
					message = (String)inputFromClient.readObject(); 
					// login stage
					if(message.compareToIgnoreCase("1") == 0) {
						readData();
						System.out.println("User wishes to login");
						sendMessage("Please enter your ID(same as PPSN)");
						String loginID = (String)inputFromClient.readObject();
						sendMessage("Please enter the password");
						String password = (String)inputFromClient.readObject();

						/*for (String user: userList) {
							// client ID found
							if(loginID.equals(user)) {
								System.out.println("Client ID: " + user + " found");

								sendMessage("Both strings are the same");
							}							
							else {
								sendMessage("String 2 is bigger");
							}							
						}*/

					}
					// registration stage
					else if(message.compareToIgnoreCase("2") == 0) {
						System.out.println("User wishes to complete the calculator test");

						sendMessage("Press 1 for Multiply\nPress 2 for square root\n");
						message=(String)inputFromClient.readObject();

						if(message.equalsIgnoreCase("1")) {
							sendMessage("Please enter number 1");
							message = (String)inputFromClient.readObject();
							int a = Integer.parseInt(message);

							sendMessage("Please enter number 2");
							message = (String)inputFromClient.readObject();
							int b = Integer.parseInt(message);

							sendMessage(""+(a*b));
						}

						else if(message.equalsIgnoreCase("2")) {
							sendMessage("Please enter the number");
							message = (String)inputFromClient.readObject();
							int a = Integer.parseInt(message);

							sendMessage(""+Math.sqrt(a));

						}
						writeData();
					}

				}
				catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}

			} while(!message.equals("3"));		
			System.out.println("Finished Client ID: " + Thread.currentThread().getName() 
					+ ", Host Name: " + clientSocket.getInetAddress().getHostName()
					+ ", Ip Address: " + clientSocket.getInetAddress().getHostAddress()
					+ ", Current Time: " + getCurrentTime(System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
