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

import javax.swing.JButton;

// Launch this once the Server is running.
public class ClientLogin extends JFrame {

	private JPanel contentPane;
	private JTextField email;
	private JPasswordField password;

	// Launches Login window
	public static void main(String[] args) {
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

	// Creates Login frame
	public ClientLogin() {
		setTitle("Login Page");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 330);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Chatter Label
		JLabel lblClient = new JLabel("Chatter");
		lblClient.setFont(new Font("Arial Black", Font.BOLD, 34));
		lblClient.setBounds(100, 35, 171, 54);
		contentPane.add(lblClient);
		
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
		
		// password field
		password = new JPasswordField();
		password.setBounds(105, 159, 171, 25);
		contentPane.add(password);
		password.setColumns(10);
		
		// Login button
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// Hashes user's password
				String pass = Integer.toString(String.valueOf(password.getPassword()).hashCode());
				ClientSide client;
				try {
					client = new ClientSide(email.getText(), pass);
					client.setVisible(true);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
			}
			
		});
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnLogin.setBounds(135, 195, 89, 23);
		contentPane.add(btnLogin);
		
		// Enter default to Login
		JRootPane rootPane = SwingUtilities.getRootPane(btnLogin); 
		rootPane.setDefaultButton(btnLogin);
		
		// Sign Up button
		JButton btnSignup = new JButton("Sign Up");
		btnSignup.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				ClientSignUp signup = new ClientSignUp();
				signup.setVisible(true);
			}
			
		});
		btnSignup.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSignup.setBounds(135, 230, 89, 23);
		contentPane.add(btnSignup);
	}
}
