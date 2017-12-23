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
	String message="";
	String serverAddress;
	Scanner stdin;
	Client(){}
	void run()
	{
		stdin = new Scanner(System.in);
		try{
			//1. creating a socket to connect to the server
			System.out.println("Please Enter your IP Address");
			serverAddress = stdin.next();
			clientSocket = new Socket(serverAddress, 2004); // create socket with a server port, 2004
			System.out.println("Connected to "+serverAddress+" in port 2004");
			//2. get Input and Output streams
			outputToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			outputToServer.flush();
			inputFromServer = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Hello");
			//3: Communicating with the server
			do{
				try
				{
					message = (String)inputFromServer.readObject();
					System.out.println(message);
					message = stdin.next();
					sendMessage(message);

					if(message.compareToIgnoreCase("1")==0)
					{
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = stdin.next();
						sendMessage(message);

						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = stdin.next();
						sendMessage(message);

						message = (String)inputFromServer.readObject();
						System.out.println(message);

					}
					else if(message.compareToIgnoreCase("2")==0)
					{
						message = (String)inputFromServer.readObject();
						System.out.println(message);
						message = stdin.next();
						sendMessage(message);

						if(message.equalsIgnoreCase("1"))
						{
							message = (String)inputFromServer.readObject();
							System.out.println(message);
							message = stdin.next();
							sendMessage(message);

							message = (String)inputFromServer.readObject();
							System.out.println(message);
							message = stdin.next();
							sendMessage(message);

							message = (String)inputFromServer.readObject();
							System.out.println(message);

						}

						else if(message.equalsIgnoreCase("2"))
						{
							message = (String)inputFromServer.readObject();
							System.out.println(message);
							message = stdin.next();
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
