package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Server class for Clients
public class Server {
	
	// Server thread
	public static void main(String[] args) throws IOException {

		ArrayList<ClientHandler> clients = new ArrayList<>();
		ServerSocket serverSocket = null;
		int clientNum = 0; // keeps track of how many clients were created
		int PORT = 4444;
		File database = new File("database.txt");
		
		// Create server socket
		try {
			serverSocket = new ServerSocket(PORT);
												
		} catch (IOException e) {
			System.out.println("Could not listen on port: " + PORT);
			System.exit(-1);
		}
		
		// Keeps Server alive
		while (true) {
			Socket clientSocket = null;
			String flag = "";
			String query = "";
			Scanner dbs = new Scanner(database);
			FileWriter dbfw = null;
			BufferedWriter dbbw = null;
			try {

				if (clientNum == 0) {
					System.out.println("Listening on port: " + PORT);
				}
				
				// Accepts client socket and adds them to Server
				clientSocket = serverSocket.accept();
				clientNum++;
				ClientHandler c = new ClientHandler(clientSocket, clients);
				clients.add(c);
				flag = c.flag;
				
				// Saving user to database
				if (flag.equals("REGISTER")) {
					query = c.email + " " + c.name + " " + c.pass + "\n";
					dbfw = new FileWriter("database.txt", true);
					dbbw = new BufferedWriter(dbfw);
					dbbw.write(query);
					dbbw.close();
					System.out.println(c.email + " has registered");
				} 
				// Checks if user is in database
				else if (flag.equals("LOGIN")) {
					findUser(dbs, c.email, c.pass);;
					System.out.println(c.email + " has connected");
					dbs = new Scanner(database);
					c.users = users(dbs);
				}
				// Creates ClientHandler thread
				Thread t = new Thread(c);
				// Starts thread
				t.start();
			} catch (IOException e) {
				System.out.println("Accept failed: " + PORT);
				System.exit(-1);
			}
		}
	}
	
	// Finds user in database and returns username
	public static String findUser(Scanner s, String email, String password) {
		s.nextLine();
		while (s.hasNextLine()) {
			String[] line = s.nextLine().split("\\s+");
			if (email.equals(line[0]) && password.equals(line[2])) {
				s.close();
				return line[1];
			}
		}
		s.close();
		return "null";
	}
	
	// Find users
	public static String users(Scanner s) {
		String str = "";
		s.nextLine();
		while (s.hasNextLine()) {
			String[] line = s.nextLine().split("\\s+");
			str += line[0] + " ";
		}
		s.close();
		return str;
	}
}


// Handles client connection
class ClientHandler implements Runnable {
	Socket s; // this is socket on the server side that connects to the CLIENT
	ArrayList<ClientHandler> others;
	Scanner in, line;
	PrintWriter out;
	String query = "";
	String flag = "";
	String email = "";
	String name = "";
	String pass = "";
	String users = "";
	String key = "key12345";
	
	// For form use
	String FLAG = "";
	String RECIPIENT = "";
	String MSG = "";
	String END = "END";

	// ClientHandler constructor
	ClientHandler(Socket s, ArrayList<ClientHandler> others) throws IOException {
		this.s = s;
		this.others = others;
		System.out.println(s);
		in = new Scanner(s.getInputStream());
		out = new PrintWriter(s.getOutputStream());
		
		query = in.nextLine();
		line = new Scanner(query);
		parseConnection(line);
	}
	
	// Reads info from initial client connection
	public void parseConnection(Scanner s) {
		String str = s.nextLine();
		String[] arr = str.split("\\s+");
		byte[] enForm = new byte[arr.length];
		for (int i = 0; i < arr.length; i++) {
			enForm[i] = (byte)(int) Integer.valueOf(arr[i]);
		}
		String deForm = Crypto.decrypt(enForm);
		Scanner dec = new Scanner(deForm);
		flag = dec.next();
		if (flag.equals("REGISTER")) {
			email = dec.next();
			name = dec.next();
			pass = dec.next();
		} else if (flag.equals("LOGIN")) {
			email = dec.next();
			pass = dec.next();
		} 
		dec.close();
	}
	
	// Sends the users in the database to client to populate 
	// the list of users in UI
	public void sendUsers() {
		String byteusers = "";
		byte[] creds = Crypto.encrypt(users);
		for (byte b : creds)
			byteusers += b + " ";
		out.println(byteusers);
		out.flush();
	}
	
	// Finds the ClientHandler if its connected to the Server
	public ClientHandler finduser(String email) {
		for (int i = 0; i < others.size(); i++) {
			if (others.get(i).email.equals(email))
				return others.get(i);
		}
		return null;
	}
	
	// Email REGEX pattern
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
	    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	// Returns true if email is in email format
	public static boolean validate(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	// Thread running for client
	public void run() {
		
		sendUsers();

		while (true) {
			byte[] form;
			parseForm(in);
			
			ClientHandler c = null;
			if (validate(RECIPIENT)) {
				c = finduser(RECIPIENT);
			}

			if (validate(RECIPIENT)) {
				String f = email + "_" + RECIPIENT + ".txt";
				try (PrintWriter fileWriter = new PrintWriter(new FileOutputStream(
							new File(f), true));) {
					fileWriter.println(MSG);
					fileWriter.close();
				} catch (FileNotFoundException e) {
					System.out.println("Could not find/open file");
				}

				String byteform = "";
				form = createForm(email, MSG);
				
				for (byte b : form)
					byteform += b + " ";
				
				out.println(byteform);
				out.flush();
				if  (finduser(RECIPIENT) != null) {
					
					System.out.println("RECIPIENT found: " + finduser(RECIPIENT).email);
						// send msg to this client c
					c.out.println(byteform);
					c.out.flush();
				}
				else {
					Email.send(email, RECIPIENT);
				}
			}
			else {
				String f = "chat.txt";
				try (PrintWriter fileWriter = new PrintWriter(new FileOutputStream(
							new File(f), true));) {
					fileWriter.println(MSG);
					fileWriter.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String byteform = "";
				form = createForm(email, MSG);
				
				for (byte b : form)
					byteform += b + " ";
				
				for (ClientHandler o : others) {		
					o.out.println(byteform);
					o.out.flush();
				}
			}
		}
	}
	
	
	/**
	 * FORM TEMPLATE:
	 * 
	 * 	test1@gmail.com/GROUP
	 * 
	 * 	message that is sent
	 * 
	 *  END
	 * @param flag
	 * @param recipient
	 * @param msg
	 * @return
	 */
	public byte[] createForm (String recipient, String msg) {
		String form = "";
		form += recipient + "\n\n";
		form += msg + "\n\n";
		form += "END";
		byte[] enForm = Crypto.encrypt(form);
		return enForm;
	}
	
	// Read form and populate fields
	public void parseForm(Scanner s) {
		String str = s.nextLine();
		String[] arr = str.split("\\s+");
		byte[] enForm = new byte[arr.length];
		for (int i = 0; i < arr.length; i++) {
			enForm[i] = (byte)(int) Integer.valueOf(arr[i]);
		}
		String deForm = Crypto.decrypt(enForm);
		Scanner dec = new Scanner(deForm);
		RECIPIENT = dec.nextLine();
		dec.nextLine();
		MSG = dec.nextLine();
		dec.nextLine();
		if (dec.nextLine().equals(END)) {
			// End of form
		}
		dec.close();
	}
		
}
