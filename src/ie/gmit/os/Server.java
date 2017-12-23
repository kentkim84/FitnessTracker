package ie.gmit.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(2004,10); // listen to connection
		int id = 0; // thread id
		while (true) {
			Socket clientSocket = serverSocket.accept(); // establishing connection
			if (clientSocket != null) {
				System.out.println("Client accepted, ID: " + id);
			}
			ClientServiceHandler clientThread = new ClientServiceHandler(clientSocket, id++); // pass the connection and create a new socket thread with a new thread id
			clientThread.start(); // start the created thread
		}
	}
}

class ClientServiceHandler extends Thread {
	Socket clientSocket;
	String message;
	int clientID = -1;
	boolean running = true;
	ObjectOutputStream outputToClient;
	ObjectInputStream inputFromClient;

	ClientServiceHandler(Socket s, int i) {
		clientSocket = s;
		clientID = i;
	}

	void sendMessage(String msg)
	{
		try{
			outputToClient.writeObject(msg);
			outputToClient.flush();			
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	public void run() {
		try 
		{
			outputToClient = new ObjectOutputStream(clientSocket.getOutputStream());
			outputToClient.flush();
			inputFromClient = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Accepted Client : ID - " + clientID 
					+ " : Host Name - " + clientSocket.getInetAddress().getHostName()
					+ " : Ip Address - " + clientSocket.getInetAddress().getHostAddress());
			do{
				try
				{
					sendMessage("Press 1 for string testing\n Press 2 for the calculator \nPress 3 to exit"); // send a message to the client
					message = (String)inputFromClient.readObject(); // receive a message from the client

					if(message.compareToIgnoreCase("1")==0)
					{
						System.out.println("User wishes to complete the string test");
						sendMessage("Please enter a string");
						String string1 = (String)inputFromClient.readObject();
						sendMessage("Please enter a string");
						String string2 = (String)inputFromClient.readObject();

						if(string1.equals(string2))
							sendMessage("Both strings are the same");
						else if(string1.compareToIgnoreCase(string2)>0)
							sendMessage("String 1 is bigger");
						else
							sendMessage("String 2 is bigger");
					}

					else if(message.compareToIgnoreCase("2")==0)
					{
						System.out.println("User wishes to complete the calculator test");

						sendMessage("Press 1 for Multiply\nPress 2 for square root\n");
						message=(String)inputFromClient.readObject();

						if(message.equalsIgnoreCase("1"))
						{
							sendMessage("Please enter number 1");
							message = (String)inputFromClient.readObject();
							int a = Integer.parseInt(message);

							sendMessage("Please enter number 2");
							message = (String)inputFromClient.readObject();
							int b = Integer.parseInt(message);

							sendMessage(""+(a*b));
						}

						else if(message.equalsIgnoreCase("2"))
						{
							sendMessage("Please enter the number");
							message = (String)inputFromClient.readObject();
							int a = Integer.parseInt(message);

							sendMessage(""+Math.sqrt(a));

						}

					}


				}
				catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}

			}while(!message.equals("3"));

			System.out.println("Ending Client : ID - " + clientID 
					+ " : Host Name - " + clientSocket.getInetAddress().getHostName()
					+ " : Ip Address - " + clientSocket.getInetAddress().getHostAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
