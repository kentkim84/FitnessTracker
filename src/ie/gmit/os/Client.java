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

					}
					else if(message.compareToIgnoreCase("2")==0)
					{
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = sc.next();
						sendMessage(message);

						if(message.equalsIgnoreCase("1"))
						{
							message = (String)inputFromServer.readObject();
							System.out.println(message);
							message = sc.next();
							sendMessage(message);

							message = (String)inputFromServer.readObject();
							System.out.println(message);
							message = sc.next();
							sendMessage(message);

							message = (String)inputFromServer.readObject();
							System.out.println(message);

						}

						else if(message.equalsIgnoreCase("2"))
						{
							message = (String)inputFromServer.readObject();
							System.out.println(message);
							message = sc.next();
							sendMessage(message);

							message = (String)inputFromServer.readObject();
							System.out.println(message);

						}
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
