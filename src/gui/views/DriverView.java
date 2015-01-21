package gui.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.joda.time.DateTime;

import com.javaswingcomponents.calendar.JSCCalendar;

import ctrl.Driver;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import gui.views.DriverTableModel;

import java.awt.GridLayout;

public class DriverView extends JTabbedPane {
	
	private Driver driver;
	
	private JSCCalendar calendar;
	
	private JLabel lblStartDate, lblEndDate;
	private JLabel lblRemaining;;
	private JButton bSetStart, bSetEnd, bSubmit;
	private JTextArea taReqStatus;
	private DateTime startDate, endDate;
	private JTable table;

	public DriverView(Driver newDriver) {
		super();
		this.driver = newDriver;
		initLayout();
		initBehaviour();
	}

	public DriverView(int tabPlacement, int tabLayoutPolicy, Driver newDriver) {
		super(tabPlacement, tabLayoutPolicy);
		this.driver = newDriver;
		initLayout();
		initBehaviour();		
	}

	/**
	 * @wbp.parser.constructor
	 */
	public DriverView(int tabPlacement, Driver newDriver) {
		super(JTabbedPane.TOP);
		//super(tabPlacement);
		this.driver = newDriver;
		initLayout();
		initBehaviour();
	}
	
	private void setDateError(JLabel lblDate, String error) {
		lblDate.setText(error);
		lblDate.setForeground(Color.RED);
		bSubmit.setEnabled(false);
	}
	
	private void initLayout() {

		JPanel pHoliday = new JPanel();            // keeps components related to holidays
		this.addTab("Holiday", null, pHoliday, null);
		GridBagLayout gbl_pHoliday = new GridBagLayout();
		gbl_pHoliday.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_pHoliday.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_pHoliday.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_pHoliday.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pHoliday.setLayout(gbl_pHoliday);
		
		calendar = new JSCCalendar();
		GridBagConstraints gbc_calendar = new GridBagConstraints();
		gbc_calendar.gridwidth = 2;
		gbc_calendar.insets = new Insets(10, 10, 5, 10);
		gbc_calendar.gridx = 0;
		gbc_calendar.gridy = 0;
		pHoliday.add(calendar, gbc_calendar);
		
		JLabel lblDaysLeftFor = new JLabel("Days left for holiday:");
		GridBagConstraints gbc_lblDaysLeftFor = new GridBagConstraints();
		gbc_lblDaysLeftFor.insets = new Insets(0, 0, 5, 5);
		gbc_lblDaysLeftFor.gridx = 2;
		gbc_lblDaysLeftFor.gridy = 0;
		pHoliday.add(lblDaysLeftFor, gbc_lblDaysLeftFor);
		
		lblRemaining = new JLabel("");
		GridBagConstraints gbc_lblRemaining = new GridBagConstraints();
		gbc_lblRemaining.insets = new Insets(0, 0, 5, 0);
		gbc_lblRemaining.gridx = 3;
		gbc_lblRemaining.gridy = 0;
		pHoliday.add(lblRemaining, gbc_lblRemaining);
		
		bSetStart = new JButton("Set start date");
		
		GridBagConstraints gbc_bSetStart = new GridBagConstraints();
		gbc_bSetStart.ipadx = 15;
		gbc_bSetStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_bSetStart.insets = new Insets(0, 10, 5, 5);
		gbc_bSetStart.gridx = 0;
		gbc_bSetStart.gridy = 1;
		pHoliday.add(bSetStart, gbc_bSetStart);
		
		bSetEnd = new JButton("Set end date");
		bSetEnd.setEnabled(false);
		GridBagConstraints gbc_bSetEnd = new GridBagConstraints();
		gbc_bSetEnd.fill = GridBagConstraints.HORIZONTAL;
		gbc_bSetEnd.insets = new Insets(0, 5, 5, 10);
		gbc_bSetEnd.gridx = 1;
		gbc_bSetEnd.gridy = 1;
		pHoliday.add(bSetEnd, gbc_bSetEnd);
		
		bSubmit = new JButton("Request selected holiday");
		bSubmit.setEnabled(false);
		GridBagConstraints gbc_bSubmit = new GridBagConstraints();
		gbc_bSubmit.fill = GridBagConstraints.HORIZONTAL;
		gbc_bSubmit.gridwidth = 2;
		gbc_bSubmit.insets = new Insets(0, 10, 5, 10);
		gbc_bSubmit.gridx = 0;
		gbc_bSubmit.gridy = 2;
		pHoliday.add(bSubmit, gbc_bSubmit);
		
		lblStartDate = new JLabel("Select a start date for a new holiday.");
		GridBagConstraints gbc_lblStartDate = new GridBagConstraints();
		gbc_lblStartDate.gridwidth = 2;
		gbc_lblStartDate.insets = new Insets(0, 10, 5, 10);
		gbc_lblStartDate.gridx = 0;
		gbc_lblStartDate.gridy = 3;
		pHoliday.add(lblStartDate, gbc_lblStartDate);
		
		lblEndDate = new JLabel("");
		GridBagConstraints gbc_lblEndDate = new GridBagConstraints();
		gbc_lblEndDate.gridwidth = 2;
		gbc_lblEndDate.insets = new Insets(0, 10, 5, 10);
		gbc_lblEndDate.gridx = 0;
		gbc_lblEndDate.gridy = 4;
		pHoliday.add(lblEndDate, gbc_lblEndDate);
		
		taReqStatus = new JTextArea("");
		taReqStatus.setLineWrap(true);
		taReqStatus.setEditable(false);
		taReqStatus.setWrapStyleWord(true);
		GridBagConstraints gbc_lblReqStatus = new GridBagConstraints();
		gbc_lblReqStatus.fill = GridBagConstraints.BOTH;
		gbc_lblReqStatus.gridwidth = 2;
		gbc_lblReqStatus.gridheight = 2;
		gbc_lblReqStatus.insets = new Insets(0, 10, 0, 10);
		gbc_lblReqStatus.gridx = 0;
		gbc_lblReqStatus.gridy = 5;
		pHoliday.add(taReqStatus, gbc_lblReqStatus);
		
		JPanel pTimetable = new JPanel();          // keeps components related to timetable
		this.addTab("Timetable", null, pTimetable, null);
		
		/* ======================================================== */
		
		DriverTableModel dtm = new DriverTableModel(this.driver);
		table = new JTable(dtm);
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		table.setVisible(true);
		pTimetable.setLayout(new GridLayout(1, 1, 0, 0));
		
		pTimetable.add(new JScrollPane(table));
	}

	private void initBehaviour() {
		bSetStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblStartDate.setText("Here");
				if (calendar.getSelectedDate() == null) {
					setDateError(lblStartDate, "Please select a start date.");
					return;
				}
				
				startDate = new DateTime(calendar.getSelectedDate());
				if (startDate.isBeforeNow()) {
					setDateError(lblStartDate, "Start date should be after today.");
					return;
				}
				if ((endDate != null) && startDate.isAfter(endDate)) {
					setDateError(lblStartDate, "Start date should be before end date.");
					return;
				}
				//table.setFillsViewportHeight(true);
				lblStartDate.setForeground(Color.BLACK);
				lblStartDate.setText(startDate.toString("dd/MMM/yy"));
				bSetEnd.setEnabled(true);
			}
		});
		
		bSetEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (calendar.getSelectedDate() == null) {
					setDateError(lblEndDate, "Please select an end date.");
					return;
				}
				
				endDate = new DateTime(calendar.getSelectedDate());
				if (endDate.isBeforeNow()) {
					setDateError(lblEndDate, "End date should be after today.");
					return;
				}
				if (endDate.isBefore(startDate)) {
					setDateError(lblEndDate,"End date should be after start date.");
					return;
				}
				
				lblEndDate.setForeground(Color.BLACK);
				lblEndDate.setText(endDate.toString("dd/MMM/yy"));
				bSubmit.setEnabled(true);
			}
		});
		
		bSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int status = driver.requestHoliday(startDate, endDate.plusDays(1)); // [startDate, endDate)
				String error = "";
				switch (status) {
				case 4:
					error = "Selecteed period is longer than remaining holiday."; break;
				case 6:
					error = "Reached maximum number of driver on holiday for a day in the selected period."; break;
				case 7:
					error = "You have already requested holiday for some of the dates in the selected period."; break;

				default: error = ""; break;
				}
				
				if (status == 0) {
					taReqStatus.setForeground(Color.BLACK);
					taReqStatus.setText("Requested holiday has been successfully selected.");
					lblRemaining.setText(driver.getHolidayRemaining() + "");
				}
				else {
					taReqStatus.setForeground(Color.RED);
					taReqStatus.setText(error);
				}
			}
		});
	}

	public Driver getDriver() {
		return this.driver;
	}
}

