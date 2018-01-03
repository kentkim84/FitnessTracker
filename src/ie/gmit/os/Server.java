package ie.gmit.os;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
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
	private final String JSON_FILE = "data.json";
	private Socket clientSocket;
	private String message;
	private ObjectOutputStream outputToClient;
	private ObjectInputStream inputFromClient;
	private List<User> userList;
	private JSONArray usersJsonObj;
	private boolean writeMode;

	ClientServiceHandler(Socket cs) {
		clientSocket = cs;		
	}

	private List<User> readData() throws FileNotFoundException, IOException, ParseException {
		String msg = "";
		// test if file exists
		File file = new File(JSON_FILE);		
		if (file.exists()) {
			// parse the source file into an object, then cast to a json object
			JSONParser parser = new JSONParser();
			FileReader fr = new FileReader(file);
			
			try {
				usersJsonObj = (JSONArray) parser.parse(fr);
				for (Object userObj : usersJsonObj) {
					// obtaining the n-th user
					JSONObject userJsonObj = (JSONObject) userObj;
					User user = new User();			
					user.setName((String)userJsonObj.get("name"));
					user.setAddress((String)userJsonObj.get("address"));
					user.setPpsn((String)userJsonObj.get("ppsn"));
					user.setPassword((String)userJsonObj.get("password"));
					user.setAge((Long)userJsonObj.get("age"));
					user.setWeight((Double)userJsonObj.get("weight"));
					user.setHeight((Double)userJsonObj.get("height"));

					// obtaining the n-th fitness record
					JSONArray fitnessRecordsJsonObj = (JSONArray) userJsonObj.get("fitnessRecords");
					List<FitnessRecord> fitnessRecordList = new ArrayList<FitnessRecord>();
					if (fitnessRecordsJsonObj != null) {
						for (Object fitnessRecordObj : fitnessRecordsJsonObj) {
							// obtaining the n-th fitness record
							JSONObject fitnessRecordJsonObj = (JSONObject) fitnessRecordObj;
							FitnessRecord fitnessRecord = new FitnessRecord();					
							fitnessRecord.setMode((String)fitnessRecordJsonObj.get("mode"));
							fitnessRecord.setDuration((Long)fitnessRecordJsonObj.get("duration"));
							fitnessRecordList.add(fitnessRecord);
						}
					}
					user.setFitnessRecordList(fitnessRecordList);

					// obtaining the n-th meal record
					JSONArray mealRecordsJsonObj = (JSONArray) userJsonObj.get("mealRecords");
					List<MealRecord> mealRecordList = new ArrayList<MealRecord>();
					if (mealRecordsJsonObj != null) {
						for (Object mealRecordObj : mealRecordsJsonObj) {
							// obtaining the n-th meal record
							JSONObject mealRecordJsonObj = (JSONObject) mealRecordObj;
							MealRecord mealRecord = new MealRecord();
							mealRecord.setTypeOfMeal((String)mealRecordJsonObj.get("typeOfMeal"));
							mealRecord.setDescription((String)mealRecordJsonObj.get("description"));
							mealRecordList.add(mealRecord);
						}
					}
					user.setMealRecordList(mealRecordList);

					// add all user's data into the list		    
					userList.add(user);
				}
				msg = "#File opening succeeded#";
			} catch (Exception e) {				
				if (e.toString().contains("Unexpected token END OF FILE") || e.toString().contains("Unexpected character")) {
					System.out.println("Creating a new file");
					msg = "#File opening failed#\n#Create a new file#";
					file.createNewFile();
					usersJsonObj = new JSONArray();
				}
			}			
		}
		else {
			System.out.println("file not exists\nCreating a new file");		
			msg = "#File not exists#\n#Create a new file#";
			file.createNewFile();
			usersJsonObj = new JSONArray();
		}
		sendMessage(msg);
		return userList;
	}

	private synchronized void writeData(User user, int index) throws IOException {
		// do not let other users write a file, while someone is writing
		while (writeMode == true) {
			try {
				System.out.println("File is being written");
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// turn this user to write
		writeMode = true;

		JSONObject userJsonObj = new JSONObject();
		userJsonObj.put("name", user.getName());
		userJsonObj.put("address", user.getAddress());
		userJsonObj.put("ppsn", user.getPpsn());
		userJsonObj.put("password", user.getPassword());
		userJsonObj.put("age", user.getAge());
		userJsonObj.put("weight", user.getWeight());
		userJsonObj.put("height", user.getHeight());

		JSONArray fitnessRecordsJsonObj = new JSONArray();
		JSONArray mealRecordsJsonObj = new JSONArray();

		if (index > -1) {
			// replace mode			
			fitnessRecordsJsonObj.addAll(user.getFitnessRecordList());		
			mealRecordsJsonObj.addAll(user.getMealRecordList());			
		}
		userJsonObj.put("fitnessRecords", fitnessRecordsJsonObj);		
		userJsonObj.put("mealRecords", mealRecordsJsonObj);

		if (index > -1) {
			// replace a new json user object using a given index
			usersJsonObj.set(index, userJsonObj);
		}
		else {
			// append a new json user object to an already existing json array object
			usersJsonObj.add(userJsonObj);
		}

		// write the top level json object to a json file (over-write)
		FileWriter fw = new FileWriter(JSON_FILE);
		fw.write(usersJsonObj.toJSONString());
		fw.flush();
		fw.close();

		writeMode = false;
		notifyAll();
	}

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
			userList = new ArrayList<User>(); // create an user array list for each thread
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
					// clean up userList
					userList.clear();
					userList = readData();					
					// send a first menu message to the client
					sendMessage("Welcome to The Fitness Tracker\n"
							+ "Press 1 for Login\n"
							+ "Press 2 for Registration\n"
							+ "Press 3 to exit");
					// receive a message from the client
					message = (String)inputFromClient.readObject();
					// login stage
					if(message.compareToIgnoreCase("1") == 0) {					
						boolean authorised = false;				
						System.out.println("Client wishes to login");
						sendMessage("Please enter your ID(same as PPSN)");
						String loginID = (String)inputFromClient.readObject();
						sendMessage("Please enter the password");
						String password = (String)inputFromClient.readObject();

						for (User user: userList) {							
							// find authorised user
							if(loginID.equals(user.getPpsn()) && password.equals(user.getPassword())) {																														
								authorised = true;
								System.out.println("Client ID: " + loginID + " and Password " + password + " authorised");								
								sendMessage("Client ID: " + loginID + " and Password " + password + " authorised");
								sendMessage("authorised");

								/* when user is authorised, then display the main menu
								 * 1. add a fitness record into this user
								 * 2. add a meal record into this user
								 * 3. display the last ten fitness records
								 * 4. dispaly the last ten meal records
								 * 5. delete a fitness record using the index number
								 * 6. delete a meal record using the index number
								 * 7. exit from the menu
								 */								
								do {
									System.out.println("user index: " + userList.indexOf(user) + " size: " + userList.size());
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
										FitnessRecord fitnessRecord = new FitnessRecord();																				

										// prompt a fitness mode and its duration
										sendMessage("Enter a mode of fitness");
										String mode = (String)inputFromClient.readObject();

										sendMessage("Enter the duration of fitness");
										long duration = Long.parseLong((String)inputFromClient.readObject());

										// set a new fitness record
										fitnessRecord.setMode(mode);
										fitnessRecord.setDuration(duration);

										// add the new fitness record object into the existing list
										user.getFitnessRecordList().add(fitnessRecord);																				
									}
									else if (message.compareToIgnoreCase("2") == 0) {
										MealRecord mealRecord = new MealRecord();										

										// prompt a type of meal and its description
										sendMessage("Enter a type of meal");
										String typeOfMeal = (String)inputFromClient.readObject();

										sendMessage("Enter the duration of fitness");
										String description = (String)inputFromClient.readObject();

										// set a new meal record
										mealRecord.setTypeOfMeal(typeOfMeal);;
										mealRecord.setDescription(description);;

										// add the new meal record object into the existing list
										user.getMealRecordList().add(mealRecord);														
									}
									else if (message.compareToIgnoreCase("3") == 0) {										
										sendMessage(""+user.getFitnessRecordList().size());
										if (user.getFitnessRecordList().size() > 0) {
											for (FitnessRecord fitnessRecord : user.getFitnessRecordList()) {											
												sendMessage("Index: " 
														+ user.getFitnessRecordList().indexOf(fitnessRecord)
														+ ", "
														+ fitnessRecord.toString());								
											}											
										}
										else {
											sendMessage("Fitness Record is empty\nPlease add a record first");
										}
											
									}
									else if (message.compareToIgnoreCase("4") == 0) {
										sendMessage(""+user.getMealRecordList().size());
										if (user.getMealRecordList().size() > 0) {
											for (MealRecord mealRecord : user.getMealRecordList()) {											
												sendMessage("Index: "
														+ user.getMealRecordList().indexOf(mealRecord)
														+ ", "
														+ mealRecord.toString());						
											}
										}
										else {
											sendMessage("Meal Record is empty\nPlease add a record first");
										}
																		
									}
									else if (message.compareToIgnoreCase("5") == 0) {
										sendMessage(""+user.getFitnessRecordList().size());
										if (user.getFitnessRecordList().size() > 0) {
											sendMessage("Enter the index of fitness record to be removed");
											int index = Integer.parseInt((String)inputFromClient.readObject());
											if (index <= 0 && index < user.getFitnessRecordList().size()) {
												if (user.getFitnessRecordList().remove(user.getFitnessRecordList().get(index))) {
													sendMessage("Removing a Record succeeded");
												} else {
													sendMessage("Removing a Record failed");
												}
											}
											else {
												sendMessage("Index number is out of range");
											}											
										} else {
											sendMessage("Fitness Record is empty\nPlease add a record first");
										}
																				
									}
									else if (message.compareToIgnoreCase("6") == 0) {
										sendMessage(""+user.getMealRecordList().size());
										if (user.getMealRecordList().size() > 0) {
											sendMessage("Enter the index of meal record to be removed");
											int index = Integer.parseInt((String)inputFromClient.readObject());
											if (index <= 0 && index < user.getFitnessRecordList().size()) {
												if (user.getMealRecordList().remove(user.getFitnessRecordList().get(index))) {
													sendMessage("Removing a Record succeeded");
												} else {
													sendMessage("Removing a Record failed");
												}
											}
											else {
												sendMessage("Index number is out of range");
											}											
										} else {
											sendMessage("Meal Record is empty\nPlease add a record first");
										}
										
									}
									else if (message.compareToIgnoreCase("7") == 0) {
										// transfer changes to json object, and then over-write json file
										System.out.println("confirm changes then break the loop");
										System.out.println("user list index of this: "+userList.indexOf(user));
										writeData(user, userList.indexOf(user));										
									}

								} while (!message.equals("7"));								
								break;				
							}							
						} // end of for loop
						if (authorised != true) {
							System.out.println("Client ID: " + loginID + " and Password " + password + " not authorised");
							sendMessage("Client ID: " + loginID + " and Password " + password + " not authorised");
							sendMessage("notAuthorised");
						}
					}					
					// registration stage
					else if(message.compareToIgnoreCase("2") == 0) {
						boolean ppsnFound;
						User user = new User();

						System.out.println("Client wishes to register");						
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
						writeData(user, -1);
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
