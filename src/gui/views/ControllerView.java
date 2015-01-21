package gui.views;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import ctrl.Controller;
import ctrl.Driver;
import ctrl.Notice;
import ctrl.RosterGenerator;
import ctrl.Service;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;

import org.joda.time.DateTime;

import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.joda.time.DateTime;

import db.BusStopInfo;
import db.NoticeInfo;
import db.RosterInfo;
import db.TimetableInfo;
import db.database;
import sun.awt.DisplayChangedListener;

public class ControllerView extends JTabbedPane {
	
	private Controller controller;
    
	/**
	 * @wbp.parser.constructor
	 */
	public ControllerView(Controller newCtrl) {
		initLayout();
		this.controller = newCtrl;
	}
    
	public ControllerView(int tabPlacement, Controller newCtrl) {
		super(tabPlacement);
		initLayout();
		this.controller = newCtrl;
	}
    
	public ControllerView(int tabPlacement, int tabLayoutPolicy, Controller newCtrl) {
		super(tabPlacement, tabLayoutPolicy);
		initLayout();
		this.controller = newCtrl;
	}
	
	private void initLayout() {
		JPanel pRosters = new JPanel();
		this.addTab("Rosters", null, pRosters, null);
		
		JTable rostersTbl = new JTable();
		pRosters.add(rostersTbl);
		
		JPanel pRequests = new JPanel();
		this.addTab("Requests", null, pRequests, null);
		pRequests.setLayout(null);
		//route combobox
        int []nnn=BusStopInfo.getRoutes();
        int len=nnn.length;
        int i=0;
        String[] RouteName=new String[20];
        final JComboBox Route = new JComboBox();
        while(i<len)
        {
        	RouteName[i]=BusStopInfo.getRouteName(nnn[i]);
        	Route.addItem(RouteName[i]);
        	i++;
        }
		/*JButton btnAddNotice = new JButton("Add Notice");
         btnAddNotice.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         new NoticeView();
         setVisible(false);
         
         }
         });
         pRequests.add(btnAddNotice, "12, 12");*/
        //label route
        JLabel lblRoute = new JLabel("Route");
        lblRoute.setBounds(24, 80, 61, 15);
        Route.setBounds(103, 71, 94, 24);
        final JComboBox Service = new JComboBox();
        Service.setBounds(103, 124, 94, 24);
        pRequests.add(Service);
        pRequests.add(Route);
        pRequests.add(lblRoute);
        //Listen to Route to change service
        ActionListener AreaActionListener1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                String s = (String) Route.getSelectedItem();
                Service.removeAllItems();
                //System.out.println(s);
    			int ss=BusStopInfo.findRoute(s);
    			int[] abc=TimetableInfo.getServices(ss,TimetableInfo.timetableKind(database.today()));
                
                int i=0;
                while(i<abc.length)
                {
                    
                    //System.out.println(abc[i]);
                    Service.addItem(Integer.toString(abc[i]));
                    i++;}
                
                
            }
        };
        Route.addActionListener(AreaActionListener1);
        JLabel lblAdd = new JLabel("Add Notice");
        lblAdd.setBounds(12, 26, 94, 15);
        pRequests.add(lblAdd);
        JLabel lblService = new JLabel("service");
        lblService.setBounds(24, 121, 55, 30);
        pRequests.add(lblService);
        final TextArea textArea_Reason= new TextArea();
        textArea_Reason.setBounds(24, 304, 218, 75);
        pRequests.add(textArea_Reason);
        JLabel lblReason = new JLabel("Reason");
        lblReason.setBounds(24, 283, 61, 15);
        pRequests.add(lblReason);
        String[] Type={"delay","cancel"};
        final JComboBox TimeType = new JComboBox(Type);
        TimeType.setBounds(103, 173, 94, 24);
        pRequests.add(TimeType);
        
        final JLabel lblSss = new JLabel();
        lblSss.setBounds(24, 422, 218, 15);
        pRequests.add(lblSss);
        JLabel lblType = new JLabel("Type");
        lblType.setBounds(24, 178, 61, 15);
        pRequests.add(lblType);
        
        JButton Add = new JButton("Add Notice");
        Add.setBounds(49, 385, 144, 25);
        pRequests.add(Add);
        
        JLabel lblDelayTime = new JLabel("delay time");
        lblDelayTime.setBounds(24, 224, 73, 15);
        pRequests.add(lblDelayTime);
        
        final TextArea NoticeInf = new TextArea();
        NoticeInf.setBounds(288, 71, 309, 289);
        NoticeInf.setEditable(false);
        pRequests.add(NoticeInf);
        final JComboBox hour = new JComboBox();
        hour.setBounds(103, 219, 45, 24);
        pRequests.add(hour);
        int hourtime=0;
        while(hourtime<25)
        {
           	hour.addItem(Integer.toString(hourtime));
           	hourtime++;
        }
        
        JLabel lblHour = new JLabel("hour");
        lblHour.setBounds(150, 224, 61, 15);
        pRequests.add(lblHour);
        
        final JComboBox minute = new JComboBox();
        minute.setBounds(103, 255, 45, 24);
        pRequests.add(minute);
        int minutetime=0;
        while(minutetime<60)
        {
           	minute.addItem(Integer.toString(minutetime));
           	minutetime++;
        }
        
        JLabel lblMinutes = new JLabel("minutes");
        lblMinutes.setBounds(150, 260, 61, 15);
        pRequests.add(lblMinutes);
        class NoticeAction implements ActionListener {
   			
   			public void actionPerformed(ActionEvent e) {
   				NoticeInf.setText(" ");
                DateTime date = new DateTime();
                int a= Service.getSelectedIndex();
                //System.out.println("a"+a);
   				String reason=textArea_Reason.getText();
                //System.out.println("reason"+reason);
   				int a2=hour.getSelectedIndex();
   				int a3=minute.getSelectedIndex();
   				int delay=Integer.parseInt((String)hour.getSelectedItem())*60+Integer.parseInt((String)minute.getSelectedItem());
                //System.out.println("delay"+delay);
                int timetyp = 0;
                if(TimeType.getSelectedIndex()==0)
                {      timetyp=0;
   			        lblSss.setText(" ");}
                if(TimeType.getSelectedIndex()==1)
                { 	timetyp=1;
                    lblSss.setText(" ");}
                else
                {
   			        lblSss.setText("input wrong");
   			        lblSss.setForeground(Color.red);
                }
                //System.out.println(TimeType.getSelectedItem());
                //System.out.println(TimeType.getSelectedIndex());
   				if(a==-1||a2==-1||a3==-1||TimeType.getSelectedIndex()==-1)
   				{
   					lblSss.setText("input wrong");
   			        lblSss.setForeground(Color.red);
   				}
   				if(Integer.parseInt((String)hour.getSelectedItem())==0&&Integer.parseInt((String)minute.getSelectedItem())==0)
   				{
   					lblSss.setText("input wrong");
   			        lblSss.setForeground(Color.red);
   				}
   				else
   				{	String t=(String)Service.getSelectedItem();
                    lblSss.setText(" ");
   					int tt=Integer.parseInt(t);
   					//delay=;
   					NoticeInfo.setNewNotice(tt,date, delay, reason, timetyp);}
   			    ArrayList<Notice> notices = new ArrayList<Notice>();
   				notices = NoticeInfo.getAllNotices();
   				for (Notice n : notices)
   				{
   					NoticeInf.append(Integer.toString(n.getServiceId())+"\n");
   					NoticeInf.append("Date::");
   					NoticeInf.append(n.getDate().toString()+"\n");
   					NoticeInf.append("delay time:");
   					NoticeInf.append(Integer.toString(n.getDelayTime())+"\n");
   					NoticeInf.append("Reason:");
   					NoticeInf.append(n.getReason()+"\n");
   					NoticeInf.append("Type:");
   					NoticeInf.append(Integer.toString(n.getType()) + "\n");
   				}
   				database.busDatabase.close();
   			}
        }
   		Add.addActionListener(new NoticeAction());
        
        
    }
    
}
