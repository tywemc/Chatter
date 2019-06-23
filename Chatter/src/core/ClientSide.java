package core;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Client that runs locally and connects to Server
public class ClientSide extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private String email = "";
	private JTextArea textArea;
	private Thread serverListener;
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	private String[] users;
	private JList<String> usersList;
	private String MSG = "";
	private String userSelected = "GROUP";
	private String SENDER = "";

	// Creates Client window
	public ClientSide(String email, String password) throws InterruptedException {
		int PORT = 4444;
		try {
			
			String login = "";
			String bytecreds = "";
			// Use for localhost on port 4444
			socket = new Socket("localhost", PORT);
			// Use for remote Server
			// socket = new Socket("carteetest4.misc.iastate.edu", PORT);
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream());
			login = "LOGIN" + " " + email + " " + password;
			byte[] creds = Crypto.encrypt(login);
			for (byte b : creds)
				bytecreds += b + " ";
			out.println(bytecreds);
			out.flush();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Listens for info from Server
		serverListener = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String s = in.nextLine();
				String[] arr = s.split("\\s+");
				byte[] userbytes = new byte[arr.length];
				for (int i = 0; i < arr.length; i++) {
					userbytes[i] = (byte)(int) Integer.valueOf(arr[i]);
				}
				String u = Crypto.decrypt(userbytes);
				users = u.split("\\s+");
				while (true) {
						parseForm(in);
						
						userSelected = SENDER;
						addText(SENDER + ": " + MSG);
				}
			}
		});
		serverListener.start();
		// Sleep for one second so users list can populate
		TimeUnit.SECONDS.sleep(1);
		
		// Creates main panel
		this.email = email;
		setTitle(this.email);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		// top panel that holds text area and users list
		JPanel topPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) topPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		// Side panel (list) of users in database\
		usersList = new JList<String>(users);
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					userSelected = usersList.getSelectedValue();
					textArea.setText("");
				}
			}
		};
		usersList.addMouseListener(mouseListener);
		
		// Messages show up here, part of top half of window
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setColumns(20);
		textArea.setRows(7);
		
		// Adds text area and users list to top panel
		topPanel.add(textArea);
		topPanel.add(usersList);
		// Adds top panel to window
		contentPane.add(topPanel);
		
		// Bottom panel that holds message field and send button
		JPanel bottomPanel = new JPanel();
		// Add bottom panel to window
		contentPane.add(bottomPanel);
		bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		// Creates message label
		JLabel lblMessage = new JLabel("Message");
		bottomPanel.add(lblMessage);

		// Creates text field for messages
		textField = new JTextField();
		bottomPanel.add(textField);
		textField.setColumns(10);

		// Send Button
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// send string/message to server
				try {
					MSG = textField.getText();
					String byteform = "";
					byte[] form = createForm(userSelected, MSG);
					for (byte b : form)
						byteform += b + " ";
					System.out.println(byteform);
					out.println(byteform);
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		bottomPanel.add(btnSend);
		
		
		
		
	}
	
	/**
	 * FORM:
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
		SENDER = dec.nextLine();
		dec.nextLine();
		MSG = dec.nextLine();
		dec.nextLine();
		if (dec.nextLine().equals("END")) {
			// End of form
		}
		dec.close();
	}

	// Email REGEX pattern
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	// Returns true if email is in email format
	public static boolean validate(String emailStr) {
	        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
	        return matcher.find();
	}

	// Adds text to text area
	public void addText(String text) {
		textArea.setText(textArea.getText() + text + "\n");
	}

}
