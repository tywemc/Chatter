package core;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;

// Class used to Register new user
public class ClientSignUp extends JFrame {

	private JPanel contentPane;
	private JTextField name;
	private JTextField email;
	private JPasswordField password;
	private JPasswordField passwordCheck;
	private int PORT = 4444;

	// Launch Register window
	public static void main(String[] args)
			throws InterruptedException{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientLogin frame = new ClientLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Create Sign up frame
	public ClientSignUp() {
		setTitle("Sign Up");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 330);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Chatter Label
		JLabel lblClient = new JLabel("Chatter");
		lblClient.setFont(new Font("Arial Black", Font.BOLD, 34));
		lblClient.setBounds(100, 30, 171, 54);
		contentPane.add(lblClient);
		
		// Name:
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 22));
		nameLabel.setBounds(10, 85, 197, 38);
		contentPane.add(nameLabel);
		
		// Name field
		name = new JTextField();
		name.setBounds(105, 95, 171, 25);
		contentPane.add(name);
		name.setColumns(10);
		
		// Email:
		JLabel userLabel = new JLabel("Email:");
		userLabel.setFont(new Font("Times New Roman", Font.PLAIN, 22));
		userLabel.setBounds(10, 118, 197, 38);
		contentPane.add(userLabel);
		
		// Email field
		email = new JTextField();
		email.setBounds(105, 127, 171, 25);
		contentPane.add(email);
		email.setColumns(10);
		
		// Password:
		JLabel passLabel = new JLabel("Password:");
		passLabel.setFont(new Font("Times New Roman", Font.PLAIN, 22));
		passLabel.setBounds(10, 150, 197, 38);
		contentPane.add(passLabel);
		
		// Password field
		password = new JPasswordField();
		password.setBounds(105, 159, 171, 25);
		contentPane.add(password);
		password.setColumns(10);
		
		// Password field
		passwordCheck = new JPasswordField();
		passwordCheck.setBounds(105, 190, 171, 25);
		contentPane.add(passwordCheck);
		passwordCheck.setColumns(10);
		
		// Register button
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String pass = Integer.toString(String.valueOf(password.getPassword()).hashCode());
				String pass2 = Integer.toString(String.valueOf(passwordCheck.getPassword()).hashCode());
				System.out.println(pass.equals(pass2));
				if (pass.equals(pass2)) {
					try {
						// Socket below is used for remote server
						// Socket signup = new Socket("carteetest4.misc.iastate.edu", PORT);
						// For localhost hosting on port 4444
						Socket signup = new Socket("localhost", PORT);
						// input stream not needed
						TimeUnit.SECONDS.sleep(1);
						PrintWriter out = new PrintWriter(signup.getOutputStream());
						// template: "REGISTER <email> <name> <password>"
						String register = "REGISTER " + email.getText() + " " + name.getText() + " " + pass;
						byte[] creds = Crypto.encrypt(register);
						String bytecreds = "";
						for (byte b : creds)
							bytecreds += b + " ";
						out.println(bytecreds);
						out.flush();
						System.exit(0);
					} catch (IOException | InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnRegister.setBounds(135, 220, 89, 23);
		contentPane.add(btnRegister);
		
		// Enter default to Register
		JRootPane rootPane = SwingUtilities.getRootPane(btnRegister); 
		rootPane.setDefaultButton(btnRegister);
		
	}
}
