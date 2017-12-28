package ie.gmit.os;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
	JSONObject jsonObject;

	ClientServiceHandler(Socket s) {
		clientSocket = s;
	}

	private JSONObject readData() throws FileNotFoundException, IOException, ParseException {
		// parse the source file into an object, then cast to a json object
		JSONParser parser = new JSONParser();		
		Object obj = parser.parse(new FileReader(JSON_FILE));
		JSONObject jsonObject = (JSONObject) obj;
		return jsonObject;
	}
	
	private List<User> getListData() {		
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
		    if (jsonFitnessRecords != null) {
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
		    }
		    JSONArray jsonMealRecords = (JSONArray) jsonUser.get("mealRecords");
		    // create an array of the meal record objects
		    if (jsonMealRecords != null) {
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
		    }
		    // add all user's data into the list
		    userList.add(user);
		}
		return userList;
	}

	private void writeData(User user) throws IOException, ParseException {
		JSONObject newJsonObject = jsonObject;
		JSONArray jsonUserList = (JSONArray) newJsonObject.get("users");		
		JSONObject jsonUser = new JSONObject();
		jsonUser.put("index", user.getIndex());
		jsonUser.put("name", user.getName());
		jsonUser.put("address", user.getAddress());
		jsonUser.put("ppsn", user.getPpsn());
		jsonUser.put("password", user.getPassword());
		jsonUser.put("age", user.getAge());
		jsonUser.put("weight", user.getWeight());
		jsonUser.put("height", user.getHeight());
		
		JSONArray jsonFitnessRecords = new JSONArray();
		jsonUser.put("fitnessRecords", null);
		
		JSONArray jsonMealRecords = new JSONArray();
		jsonUser.put("mealRecords", null);
		
		// append a new json user object to an already existing json array object
		jsonUserList.add(jsonUser);
		// put the json array object called 'users' into the top level of a json object
		newJsonObject.put("users", jsonUserList);
		
		// write the top level json object to a json file (over-write)
	    FileWriter fw = new FileWriter(JSON_FILE);
	    fw.write(newJsonObject.toJSONString());
	    fw.flush();
	    fw.close();
	}
	
	private void addRecord(User user, FitnessRecord[] firnessRecords, MealRecord[] mealRecords, String mode) {
		System.out.println(user.toString());
		System.out.println(user.getFitness().toString());
	}
	
	private void showRecord(User user, String mode) {
		
	}
	
	private void deleteRecord(User user, String mode) {
		
	}
	/*private void addSomething() {
		JSONArray jsonFitnessRecords = new JSONArray();		
		for (FitnessRecord fitnessRecord : user.getFitness()) {		
			JSONObject jsonFitnessRecord = new JSONObject();
			jsonFitnessRecord.put("index", fitnessRecord.getIndex());
			jsonFitnessRecord.put("mode", fitnessRecord.getMode());
			jsonFitnessRecord.put("duration", fitnessRecord.getDuration());
			jsonFitnessRecords.add(jsonFitnessRecord);
			System.out.println(fitnessRecord.toString());
		}
		jsonUser.put("fitnessRecords", jsonFitnessRecords);
		
		JSONArray jsonMealRecords = new JSONArray();
		for (MealRecord mealRecord : user.getMeal()) {
			JSONObject jsonMealRecord = new JSONObject();
			jsonMealRecord.put("index", mealRecord.getIndex());
			jsonMealRecord.put("typeOfMeal", mealRecord.getTypeOfMeal());
			jsonMealRecord.put("description", mealRecord.getDescription());
			jsonMealRecords.add(jsonMealRecord);
			System.out.println(mealRecord.toString());
		}
		jsonUser.put("mealRecords", jsonMealRecords);
	}*/

	private void sendMessage(String msg) {
		try {
			outputToClient.writeObject(msg);
			outputToClient.flush();			
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
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
			jsonObject = new JSONObject();
			userList = new ArrayList<User>();
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
					jsonObject = readData();				
					userList = getListData();
					// send a first menu message to the client
					sendMessage("Welcome to The Fitness Tracker\n"
							+ "Press 1 for Login\n"
							+ "Press 2 for Registration\n"
							+ "Press 3 to exit");
					// receive a message from the client
					message = (String)inputFromClient.readObject(); 
					// login stage
					if(message.compareToIgnoreCase("1") == 0) {
						// read the user list\						
						boolean authorised = false;				
						System.out.println("Client wishes to login");
						sendMessage("Please enter your ID(same as PPSN)");
						String loginID = (String)inputFromClient.readObject();
						sendMessage("Please enter the password");
						String password = (String)inputFromClient.readObject();
																
						for (User user: userList) {
							// find authorised user
							if(loginID.equals(user.getPpsn()) && password.equals(user.getPassword())) {								
								List<FitnessRecord> fitnessList = new ArrayList<FitnessRecord>(Arrays.asList(user.getFitness()));
								List<MealRecord> mealList = new ArrayList<MealRecord>(Arrays.asList(user.getMeal()));
								int lastFitnessRecordIndex = user.getFitness().length-1;
								int lastMealRecordIndex = user.getMeal().length-1;
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
											+ "Press 7 to exit and confirm changes");
									message = (String)inputFromClient.readObject();
									
									if (message.compareToIgnoreCase("1") == 0) {
										FitnessRecord[] fitnessRecords = new FitnessRecord[user.getFitness().length+1];
										System.out.println("new length: "+fitnessRecords.length);
										fitnessRecords[lastFitnessRecordIndex+1] = new FitnessRecord();

										sendMessage("Enter a mode of fitness");
										String mode = (String)inputFromClient.readObject();
										
										sendMessage("Enter the duration of fitness");
										long duration = Long.parseLong((String)inputFromClient.readObject());
										
										fitnessRecords[lastFitnessRecordIndex+1].setIndex(lastFitnessRecordIndex+1);
										fitnessRecords[lastFitnessRecordIndex+1].setMode(mode);
										fitnessRecords[lastFitnessRecordIndex+1].setDuration(duration);
																				
										System.arraycopy(user.getFitness(), 0, fitnessRecords, 0, user.getFitness().length);
										
										user.setFitness(fitnessRecords);
										
										addRecord(user, fitnessRecords, null, "fitness");
									}
									else if (message.compareToIgnoreCase("2") == 0) {
										MealRecord[] mealRecords = new MealRecord[user.getMeal().length+1];
										System.out.println("new length: "+mealRecords.length);
										mealRecords[lastMealRecordIndex+1] = new MealRecord();

										sendMessage("Enter a type of meal");
										String typeOfMeal = (String)inputFromClient.readObject();
										
										sendMessage("Enter the duration of fitness");
										String description = (String)inputFromClient.readObject();
										
										mealRecords[lastMealRecordIndex+1].setIndex(lastMealRecordIndex+1);
										mealRecords[lastMealRecordIndex+1].setTypeOfMeal(typeOfMeal);;
										mealRecords[lastMealRecordIndex+1].setDescription(description);;
																				
										System.arraycopy(user.getMeal(), 0, mealRecords, 0, user.getMeal().length);
										
										user.setMeal(mealRecords);;
										
										addRecord(user, null, mealRecords, "meal");
									}
									else if (message.compareToIgnoreCase("3") == 0) {
										showRecord(user, "fitness");
									}
									else if (message.compareToIgnoreCase("4") == 0) {
										showRecord(user, "meal");									
									}
									else if (message.compareToIgnoreCase("5") == 0) {
										deleteRecord(user, "fitness");
									}
									else if (message.compareToIgnoreCase("6") == 0) {
										deleteRecord(user, "meal");
									}
									
								} while (!message.equals("7"));
								break;				
							}							
						}
						if (authorised != true) {
							System.out.println("Client ID: " + loginID + " and Password " + password + " not authorised");
							sendMessage("Client ID: " + loginID + " and Password " + password + " not authorised");
							sendMessage("notAuthorised");
						}
					}					
					// registration stage
					else if(message.compareToIgnoreCase("2") == 0) {
						int lastIndex = userList.size()-1;
						boolean ppsnFound;
						User user = new User();
						
						System.out.println("Client wishes to register");						
						// generate a new index number from the server system
						user.setIndex(++lastIndex);
						
						sendMessage("Please enter your name");
						user.setName((String)inputFromClient.readObject());
						
						sendMessage("Please enter your address");
						user.setAddress((String)inputFromClient.readObject());
						
						sendMessage("Please enter your pps number - will be used as your login ID");
						do {
							ppsnFound = false;
							user.setPpsn((String)inputFromClient.readObject());							
							for (User tempUser : userList) {
								// check if a pps number already exists
								if (tempUser.getPpsn().equalsIgnoreCase(user.getPpsn())) {
									ppsnFound = true;									
								}					
							}
							if (ppsnFound == true) {
								sendMessage("ppsnFound");								
							}
							else {
								sendMessage("ppsnNotFound");								
							}
						} while (ppsnFound == true);
						
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
						writeData(user);
					}					
				}
				catch(ClassNotFoundException cnfe){					
					cnfe.printStackTrace();
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
