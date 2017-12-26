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
	private List<User> userList;

	ClientServiceHandler(Socket s) {
		clientSocket = s;
	}

	private List<User> readData(String file) throws Exception {
		JSONParser parser = new JSONParser();
		userList = new ArrayList<User>();
		Object obj = parser.parse(new FileReader(file));
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray jsonUserList = (JSONArray) jsonObject.get("users");
		for (int i = 0; i < jsonUserList.size(); i++) {
		    // obtaining the i-th user
			User user = new User();
		    JSONObject jsonUser = (JSONObject) jsonUserList.get(i);
		    //System.out.println("user: "+user);
		    //long userIndex = (Long) jsonUser.get("index");
		    user.setIndex((Long) jsonUser.get("index"));
		    //System.out.println("index: "+userIndex);
		    //String name = (String) jsonUser.get("name");
		    user.setName((String) jsonUser.get("name"));
		    //System.out.println("name: "+name);
		    //String address = (String) jsonUser.get("address");
		    user.setAddress((String) jsonUser.get("address"));
		    //System.out.println("address: "+address);
		    //String ppsn = (String) jsonUser.get("ppsn");
		    user.setPpsn((String) jsonUser.get("ppsn"));
		    //System.out.println("ppsn: "+ppsn);
		    //long age = (Long) jsonUser.get("age");
		    user.setAge((Long) jsonUser.get("age"));
		    //System.out.println("age: "+age);
		    //double weight = (Double) jsonUser.get("weight");
		    user.setWeight((Double) jsonUser.get("weight"));
		    //System.out.println("weight: "+weight);
		    //double height = (Double) jsonUser.get("height");
		    user.setHeight((Double) jsonUser.get("height"));
		    //System.out.println("height: "+height);		    
		    JSONArray jsonFitnessRecords = (JSONArray) jsonUser.get("fitnessRecords");
		    // create an array of the fitness record objects
		    FitnessRecord[] fitnessRecords = new FitnessRecord[jsonFitnessRecords.size()];
		    for (int j = 0; j < jsonFitnessRecords.size(); j++) {
		    	fitnessRecords[j] = new FitnessRecord();
		    	JSONObject jsonFitnessRecord = (JSONObject) jsonFitnessRecords.get(j);
		    	//System.out.println("fitnessRecord: "+fitnessRecord);
		    	//long fitnessIndex = (Long) jsonFitnessRecord.get("index");		    	
		    	fitnessRecords[j].setIndex((Long) jsonFitnessRecord.get("index"));
		    	//System.out.println("obj fitnessIndex: "+fitnessRecords[j].getIndex());
		    	//String mode = (String) jsonFitnessRecord.get("mode");
		    	fitnessRecords[j].setMode((String) jsonFitnessRecord.get("mode"));
		    	//System.out.println("mode: "+mode);
		    	//long duration = (Long) jsonFitnessRecord.get("duration");
		    	fitnessRecords[j].setDuration((Long) jsonFitnessRecord.get("duration"));
		    	//System.out.println("duration: "+duration);
		    	// set a fitness record object into the user object
		    	user.setFitness(fitnessRecords);
		    }		    
		    JSONArray jsonMealRecords = (JSONArray) jsonUser.get("mealRecords");
		    // create an array of the meal record objects
		    MealRecord[] mealRecords = new MealRecord[jsonMealRecords.size()];
		    for (int k = 0; k < jsonMealRecords.size(); k++) {
		    	mealRecords[k] = new MealRecord();
		    	JSONObject jsonMealRecord = (JSONObject) jsonMealRecords.get(k);
		    	//System.out.println("mealRecord: "+mealRecord);
		    	//long mealIndex = (Long) jsonMealRecord.get("index");
		    	mealRecords[k].setIndex((Long) jsonMealRecord.get("index"));
		    	//System.out.println("mealIndex: "+mealIndex);
		    	//String typeOfMeal = (String) jsonMealRecord.get("typeOfMeal");
		    	mealRecords[k].setTypeOfMeal((String) jsonMealRecord.get("typeOfMeal"));
		    	//System.out.println("typeOfMeal: "+typeOfMeal);
		    	//String description = (String) jsonMealRecord.get("description");
		    	mealRecords[k].setDescription((String) jsonMealRecord.get("description"));
		    	//System.out.println("description: "+description);
		    	// set a meal record object into the user object
		    	user.setMeal(mealRecords);
		    }
		    // add all user's data into the list
		    userList.add(user);
		}
		return userList;
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
						// read the user list
						userList = readData(JSON_FILE);
						System.out.println("User wishes to login");
						sendMessage("Please enter your ID(same as PPSN)");
						String loginID = (String)inputFromClient.readObject();
						sendMessage("Please enter the password");
						String password = (String)inputFromClient.readObject();

						for (User user: userList) {
							// client ID found
							if(loginID.equals(user.getPpsn())) {
								System.out.println("Client ID: " + loginID + " found");
								sendMessage("Both strings are the same");
							}							
							else {
								sendMessage("String 2 is bigger");
							}							
						}

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
