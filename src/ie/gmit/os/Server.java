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
		    user.setIndex((Long) jsonUser.get("index"));
		    user.setName((String) jsonUser.get("name"));
		    user.setAddress((String) jsonUser.get("address"));
		    user.setPpsn((String) jsonUser.get("ppsn"));
		    user.setPassword((String) jsonUser.get("password"));
		    user.setAge((Long) jsonUser.get("age"));
		    user.setWeight((Double) jsonUser.get("weight"));
		    user.setHeight((Double) jsonUser.get("height"));	    
		    JSONArray jsonFitnessRecords = (JSONArray) jsonUser.get("fitnessRecords");
		    // create an array of the fitness record objects
		    FitnessRecord[] fitnessRecords = new FitnessRecord[jsonFitnessRecords.size()];
		    for (int j = 0; j < jsonFitnessRecords.size(); j++) {
		    	fitnessRecords[j] = new FitnessRecord();
		    	JSONObject jsonFitnessRecord = (JSONObject) jsonFitnessRecords.get(j);	    	
		    	fitnessRecords[j].setIndex((Long) jsonFitnessRecord.get("index"));
		    	fitnessRecords[j].setMode((String) jsonFitnessRecord.get("mode"));
		    	fitnessRecords[j].setDuration((Long) jsonFitnessRecord.get("duration"));
		    	// set a fitness record object into the user object
		    	user.setFitness(fitnessRecords);
		    }		    
		    JSONArray jsonMealRecords = (JSONArray) jsonUser.get("mealRecords");
		    // create an array of the meal record objects
		    MealRecord[] mealRecords = new MealRecord[jsonMealRecords.size()];
		    for (int k = 0; k < jsonMealRecords.size(); k++) {
		    	mealRecords[k] = new MealRecord();
		    	JSONObject jsonMealRecord = (JSONObject) jsonMealRecords.get(k);
		    	mealRecords[k].setIndex((Long) jsonMealRecord.get("index"));
		    	mealRecords[k].setTypeOfMeal((String) jsonMealRecord.get("typeOfMeal"));
		    	mealRecords[k].setDescription((String) jsonMealRecord.get("description"));
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
					userList = readData(JSON_FILE);
					// send a first menu message to the client
					sendMessage("Welcome to The Fitness Tracker\n"
							+ "Press 1 for Login\n"
							+ "Press 2 for Registration\n"
							+ "Press 3 to exit"); 
					// receive a message from the client
					message = (String)inputFromClient.readObject(); 
					// login stage
					if(message.compareToIgnoreCase("1") == 0) {
						// read the user list
						boolean authorised = false;					
						System.out.println("User wishes to login");
						sendMessage("Please enter your ID(same as PPSN)");
						String loginID = (String)inputFromClient.readObject();
						sendMessage("Please enter the password");
						String password = (String)inputFromClient.readObject();
						
											
						for (User user: userList) {
							System.out.println(user.toString());
							// find authorised user
							if(loginID.equals(user.getPpsn()) && password.equals(user.getPassword())) {
								authorised = true;
								System.out.println("Client ID: " + loginID + " and Password " + password + " authorised");								
								sendMessage("Client ID: " + loginID + " and Password " + password + " authorised");
								sendMessage("authorised");
								
								// when user is authorised, then display the main menu
								/*
								 * 1. add a fitness record into this user
								 * 2. add a meal record into this user
								 * 3. display the last ten fitness records
								 * 4. dispaly the last ten meal records
								 * 5. delete a fitness record using the index number
								 * 6. delete a meal record using the index number
								 * 7. exit from the menu
								 */								
								do {
									sendMessage("Menu\n"
											+ "Press 1 for add a fitness record\n"
											+ "Press 2 for add a meal record\n"
											+ "Press 3 for view the last ten fitness records\n"
											+ "Press 4 for view the last ten meal records\n"
											+ "Press 5 for delete a fitness record\n"
											+ "Press 6 for delete a meal record\n"
											+ "Press 7 to exit");
									message = (String)inputFromClient.readObject();
									
									if (message.compareToIgnoreCase("1") == 0) {
										
									}
									else if (message.compareToIgnoreCase("2") == 0) {
										
									}
									else if (message.compareToIgnoreCase("3") == 0) {
										
									}
									else if (message.compareToIgnoreCase("4") == 0) {
										
									}
									else if (message.compareToIgnoreCase("5") == 0) {
										
									}
									else if (message.compareToIgnoreCase("6") == 0) {
										
									}
									
								} while (!message.equals("7"));
							}		
						}
						if (authorised == false) {
							System.out.println("Client ID: " + loginID + " and Password " + password + " not authorised");
							sendMessage("Client ID: " + loginID + " and Password " + password + " not authorised");
							sendMessage("notAuthorised");
						}

					}
					// registration stage
					else if(message.compareToIgnoreCase("2") == 0) {
						int count = userList.size()-1;						
						User user = new User();
						
						System.out.println("User wishes to register");						
						// generate a new index number from the server system
						user.setIndex(++count);
						
						sendMessage("Please enter your name");
						user.setName((String)inputFromClient.readObject());
						
						sendMessage("Please enter your address");
						user.setAddress((String)inputFromClient.readObject());
						
						sendMessage("Please enter your pps number - will be used as your login ID");
						user.setPpsn((String)inputFromClient.readObject());
						
						sendMessage("Please enter your age");						
						user.setAge(Long.parseLong((String)inputFromClient.readObject()));
						
						sendMessage("Please enter your weight");
						user.setWeight(Double.parseDouble((String)inputFromClient.readObject()));
						
						sendMessage("Please enter your height");
						user.setHeight(Double.parseDouble((String)inputFromClient.readObject()));
							
						String password ="";
						String confirmPassword = "";
						do {
							sendMessage("Please enter your password");
							password = (String)inputFromClient.readObject();
														
							sendMessage("Please enter your password again");
							confirmPassword = (String)inputFromClient.readObject();							
						} while (!password.equals(confirmPassword));
						user.setPassword(password);
						
						sendMessage("Registration successful");
						userList.add(user);
						
						System.out.println(user.toString());
						for (User u : userList) {
							System.out.println(u.toString());
						}
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
