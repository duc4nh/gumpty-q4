package gui.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;

import java.awt.CardLayout;
import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JRadioButton;

import java.awt.Component;

import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.javaswingcomponents.calendar.JSCCalendar;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Date;

import ctrl.*;
import db.database;

import javax.swing.JTable;

public class MainWindow extends JFrame {
	
	private JPanel pContent, pStatus;  // need this reference for cardLayout.show()
	private JTabbedPane pUser;         // a reference to the current view, wither DriverView or ControllerView
	private JTextField tfLoginNo;
    
	private JButton btnLogin, btnLogOut;
	
	private JRadioButton rdbtnDriver, rdbtnController;
	
	private JPanel pRosters;
	private JTable rostersTbl;
	private JLabel lblStatus;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 878, 551);
		this.setTitle("Integrated Bus Management System - Gumpty Q4");
		
		RosterGenerator.createData(true);
		initLayout();
		initBehaviour();
		this.setVisible(true);
		if (database.busDatabase == null)
			database.openBusDatabase();
		
	}
	
	private void initLayout() {
		JPanel contentPane = new JPanel();    // main container
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		pStatus = new JPanel();       // shows who is currently logged in
		contentPane.add(pStatus, BorderLayout.SOUTH);
		pStatus.setLayout(new BoxLayout(pStatus, BoxLayout.X_AXIS));
		
		lblStatus = new JLabel("Not logged in.");  // nobody is logged in initially
		pStatus.add(lblStatus);
		
		btnLogOut = new JButton("Log out");
		btnLogOut.setVisible(false);         // only appears when someone is logged in
		pStatus.add(btnLogOut);
		
		pContent = new JPanel();             // contains available operations, depending on actor (driver / ctrl)
		contentPane.add(pContent, BorderLayout.CENTER);
		pContent.setLayout(new CardLayout(0, 0));  // cardLayout to switch easily between driver / ctrl
		
		JPanel pLogin = new JPanel();              // login panel, displayed when nobody is logged in
		pContent.add(pLogin, "pLogin");
		
		JPanel panel_2 = new JPanel();      // to structure everything nicely in pLogin
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		JLabel lblLoginTitle = new JLabel("Login to use the system");
		lblLoginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(lblLoginTitle);
		lblLoginTitle.setFont(new Font("Dialog", Font.BOLD, 22));
		
		JPanel panel = new JPanel();        // to keep id label & id text field together
		panel_2.add(panel);
		
		JLabel lblId = new JLabel("Id:");
		lblId.setFont(new Font("Dialog", Font.BOLD, 16));
		
		tfLoginNo = new JTextField();            // where the id is entered
		tfLoginNo.setColumns(10);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblId)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfLoginNo, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(36)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfLoginNo)
						.addComponent(lblId))
					.addGap(48))
		);
		panel.setLayout(gl_panel);
		
		JPanel panel_1 = new JPanel();      // to keep the radio buttons together
		panel_2.add(panel_1);
		
		rdbtnDriver = new JRadioButton("Driver");
		panel_1.add(rdbtnDriver);
		rdbtnDriver.setSelected(true);
		rdbtnController = new JRadioButton("Controller");
		panel_1.add(rdbtnController);
		ButtonGroup actor = new ButtonGroup();  // select what kind of actor logs in (driver / ctrl)
		actor.add(rdbtnController);
		actor.add(rdbtnDriver);
		
		btnLogin = new JButton("Login");        // checks the entered id and selects the content accordingly
		btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(btnLogin);
		
		JButton btnPassenger = new JButton("Passenger");
        class BackAction implements ActionListener {
            
            public void actionPerformed(ActionEvent e) {
                new PassengerView();
                // setVisible(false);
            }
        }
        btnPassenger.addActionListener(new BackAction());
		
		GroupLayout gl_pLogin = new GroupLayout(pLogin);
		gl_pLogin.setHorizontalGroup(
                                     gl_pLogin.createParallelGroup(Alignment.LEADING)
                                     .addGroup(gl_pLogin.createSequentialGroup()
                                               .addGap(275)
                                               .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 331, GroupLayout.PREFERRED_SIZE)
                                               .addContainerGap(254, Short.MAX_VALUE))
                                     .addGroup(Alignment.TRAILING, gl_pLogin.createSequentialGroup()
                                               .addContainerGap(386, Short.MAX_VALUE)
                                               .addComponent(btnPassenger)
                                               .addGap(367))
                                     );
		gl_pLogin.setVerticalGroup(
                                   gl_pLogin.createParallelGroup(Alignment.LEADING)
                                   .addGroup(gl_pLogin.createSequentialGroup()
                                             .addGap(128)
                                             .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)
                                             .addGap(18)
                                             .addComponent(btnPassenger)
                                             .addContainerGap(101, Short.MAX_VALUE))
                                   );
		pLogin.setLayout(gl_pLogin);
	}
	
	private void initBehaviour() {
		/* Login button and the id text field will have the same behaviour on the default action,
		 * which is click for button and enter for text field */
		ActionListener loginListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rdbtnDriver.isSelected()) {
					Driver user = Driver.tryLogin(tfLoginNo.getText());
					if (user == null) {    // could not log in
						lblStatus.setText("Could not log you in. Please try again");
						lblStatus.setForeground(Color.RED);
						return;
					}
					pUser = new DriverView(JTabbedPane.TOP, user); // login successful, so user is a driver
				}
				else if (rdbtnController.isSelected()) {
					Controller user = Controller.tryLogin(tfLoginNo.getText());
					if (user == null) {    // could not log in
						lblStatus.setText("Could not log you in. Please try again");
						lblStatus.setForeground(Color.RED);
						return;
					}
					pUser = new ControllerView(JTabbedPane.TOP, user);
				}
				else                        // if by some hack, no radio button is selected
					return;                 // do nothing
				
				// set status label
				lblStatus.setForeground(Color.BLACK);
				lblStatus.setText("Logged in as " + (rdbtnDriver.isSelected() ? "driver " : "controller ") +
				                  tfLoginNo.getText() + ".");
				tfLoginNo.setText("");
				btnLogOut.setVisible(true);
				
				// display the correct view
				pContent.add(pUser, "pUser");
				((CardLayout) pContent.getLayout()).next(pContent);
			}
		}; 
		/* on login, navigate to the right content, setup the status ...*/
		btnLogin.addActionListener(loginListener);
		tfLoginNo.addActionListener(loginListener);
		
		/* on logout, navigate to login content, clear stuff from view*/
		btnLogOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// destroy the user view
				MainWindow thisView = (MainWindow) SwingUtilities.getRoot((Component) e.getSource());
				thisView.initLayout();
				thisView.initBehaviour();
				// TODO: figure out a way of destroying just the user panel, not throwing everything away
//				((CardLayout) pContent.getLayout()).next(pContent);   // go back to login view
//				thisView.getContentPane().remove(pUser);
//				thisView.revalidate();
//				thisView.repaint();
//				lblStatus.setText("Not logged in. ");
//				btnLogOut.setVisible(false);
//				btnLogin.setVisible(true);
			}
		});	
	}
}
