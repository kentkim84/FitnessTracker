package ie.gmit.os;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	Socket clientSocket;
	ObjectOutputStream outputToServer;
	ObjectInputStream inputFromServer;
	String message = "";
	String serverAddress;
	Scanner sc;
	Client(){}
	void run()
	{
		sc = new Scanner(System.in);
		try{			
			System.out.println("Please Enter the Host IP Address");
			serverAddress = sc.next();
			// request a connection to a server
			clientSocket = new Socket(serverAddress, 2004); 
			System.out.println("Connected to "+serverAddress+" in port 2004");
			//2. get Input and Output streams
			outputToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			outputToServer.flush();
			inputFromServer = new ObjectInputStream(clientSocket.getInputStream());			
			//3: Communicating with the server
			do{
				try
				{
					// receive the first menu message from the server
					message = (String)inputFromServer.readObject();
					System.out.println(message);
					message = sc.next();
					// send the chosen option from the menu to the server
					sendMessage(message);

					if(message.compareToIgnoreCase("1")==0)
					{	
						System.out.println("Login");
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						// send the login ID 
						sendMessage(message);

						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						// send the password
						sendMessage(message);

						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = (String)inputFromServer.readObject();
						
						if (message.compareToIgnoreCase("authorised")==0) {
							message = (String)inputFromServer.readObject();
							System.out.println(message);
							
							message = sc.next();
							sendMessage(message);
							
							if (message.compareToIgnoreCase("1") == 0) {
								System.out.println("Add a Fitness Record");
								
								message = (String)inputFromServer.readObject();
								System.out.println(message);								
								message = sc.next();
								sendMessage(message);
								
								message = (String)inputFromServer.readObject();
								System.out.println(message);								
								message = sc.next();
								sendMessage(message);								
							}
							else if (message.compareToIgnoreCase("2") == 0) {
								System.out.println("Add a Meal Record");
								
								message = (String)inputFromServer.readObject();
								System.out.println(message);								
								message = sc.next();
								sendMessage(message);
								
								message = (String)inputFromServer.readObject();
								System.out.println(message);								
								message = sc.next();
								sendMessage(message);
							}
							else if (message.compareToIgnoreCase("3") == 0) {
								int length = Integer.parseInt((String)inputFromServer.readObject());								
								for (int i = 0; i < length; i++) {
									message = (String)inputFromServer.readObject();
									System.out.println(message);
								}
							}
							else if (message.compareToIgnoreCase("4") == 0) {
								int length = Integer.parseInt((String)inputFromServer.readObject());								
								for (int i = 0; i < length; i++) {
									message = (String)inputFromServer.readObject();
									System.out.println(message);
								}
							}
							else if (message.compareToIgnoreCase("5") == 0) {
								message = (String)inputFromServer.readObject();
								System.out.println(message);
								message = sc.next();
								sendMessage(message);
							}
							else if (message.compareToIgnoreCase("6") == 0) {
								message = (String)inputFromServer.readObject();
								System.out.println(message);
								message = sc.next();
								sendMessage(message);
							}
						}
						else if (message.compareToIgnoreCase("notAuthorised")==0) {
						
						}

					}
					else if(message.compareToIgnoreCase("2")==0)
					{
						System.out.println("Registration");
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						sendMessage(message); // send client's name
						
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						sendMessage(message); // send client's address
						
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						String ppsnFound = "";
						do {							
							message = sc.next();
							sendMessage(message); // send client's pps number
							ppsnFound = (String)inputFromServer.readObject();
							if (ppsnFound.equalsIgnoreCase("ppsnFound")) {
								System.out.println(message + " already exists, enter a different pps number");
							}
						} while (ppsnFound.equalsIgnoreCase("ppsnFound"));
						
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						sendMessage(message); // send client's age
						
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						sendMessage(message); // send client's weight
						
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						sendMessage(message); // send client's height
						
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						sendMessage(message); // send client's password
						
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						sendMessage(message); // send client's confirm password
						
						message = (String)inputFromServer.readObject();
						System.out.println(message);					
					}
				}
				catch(ClassNotFoundException classNot)
				{
					System.err.println("data received in unknown format");
				}
			}while(!message.equals("3"));
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				inputFromServer.close();
				outputToServer.close();
				clientSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	
	void sendMessage(String msg)
	{
		try{
			outputToServer.writeObject(msg);
			outputToServer.flush();
			System.out.println("client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		Client client = new Client();
		client.run();
	}
}
