package gui.views;



import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ListIterator;

import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import db.BusStopInfo;
import db.database;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;

import org.joda.time.DateTime;

import com.sun.xml.internal.ws.wsdl.writer.document.Service;

import ctrl.Journey;
import ctrl.JourneyHandler;
import ctrl.RealTimeInfo;
import ctrl.RosterGenerator;

public class PassengerView extends JFrame{
	 String RealTime=" ";
     TextArea RealTimeArea = new TextArea(" ", 100, 100, TextArea.SCROLLBARS_VERTICAL_ONLY);
     String JourneyPlan=" ";
     TextArea txtJourney = new TextArea(JourneyPlan,100,100,TextArea.SCROLLBARS_VERTICAL_ONLY);
     JComboBox time = new JComboBox(), minute; 
     JFrame parent;
     final JComboBox Area, Area1, BusStop, BusStop1;
     int tttt;

    private static final JDesktopPane DESKTOP_PANE = new JDesktopPane();

//Journey Plan Listener
    class PlanJourneyAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			// txtJourney.setText("click journey planner");
			String text = new String();
			boolean wasException = false;
			int mins = Integer.parseInt( (String)time.getSelectedItem() ) * 60;
			mins += Integer.parseInt((String) minute.getSelectedItem());
			DateTime timeDT = DateTime.now().withTimeAtStartOfDay().plusMinutes(mins);
			try {
				Journey j = (new JourneyHandler()).generate((String)Area.getSelectedItem(), 
						(String) BusStop.getSelectedItem(), (String) Area1.getSelectedItem(), 
						(String) BusStop1.getSelectedItem(), timeDT);
			
			
				ListIterator<ctrl.BusStop> chgIt = j.changes.listIterator();
				ListIterator<DateTime> dtIt = j.times.listIterator();
				
				ctrl.BusStop bs;
				while (chgIt.hasNext()) {
					timeDT = dtIt.next();
					bs = chgIt.next();
					
					if (bs.reachedWithService == -1)
						text += "WALK --> ";
					else {
						text += "RIDE ";
						for (ctrl.Service s : RosterGenerator.weeklyServices[timeDT.getDayOfWeek() - 1])
							if (s.getServiceID() == bs.reachedWithService) {
								text += s.getRouteName() + " --> ";
								break;
							}
					}
					
					text += bs.getFullName() + " " + timeDT.getHourOfDay() + ":" 
							+ ((mins = timeDT.getMinuteOfHour()) < 10 ? "0" + mins : mins  ) + "\n\n";
				}
			}
			catch (Exception ex) {
				wasException = true;
			}
			if (! wasException)
				txtJourney.setText(text);
			else
				txtJourney.setText("Sorry, no route found.");
		}
	}
   

    public PassengerView() { 
        super("Passenger");
        setSize(907, 572);
        if (database.busDatabase == null)
        	database.openBusDatabase();
        /*menu102.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addIFame(new InternalFrame());
            }
        });*/
        
        //Area and Label Area(start stop)
        
        this.getContentPane().setLayout(null);  
        this.getContentPane().add(DESKTOP_PANE);
        String[] areaName=new String[13];
        int []nnn=BusStopInfo.getAreas();
        int len=nnn.length;
        int i=0;
        Area = new JComboBox();
        while(i<len)
        {
        	areaName[i]=BusStopInfo.getAreaName(nnn[i]);
        	Area.addItem(areaName[i]); 
        	i++;
        }  
        Area.setBounds(103, 75, 138, 24);
        getContentPane().add(Area);        
        JLabel lblArea = new JLabel("Area");
        lblArea.setBounds(24, 80, 61, 15);
        getContentPane().add(lblArea);
        
        //Bus stop Label and Bus stop comoboBox and Set Start stop button(start stop)
        JLabel lblBusStop = new JLabel("Bus Stop");
        lblBusStop.setBounds(24, 127, 61, 15);
        getContentPane().add(lblBusStop);
        BusStop = new JComboBox();

                        BusStop.setBounds(103, 122, 220, 24);
                        getContentPane().add(BusStop);
        //
                ActionListener AreaActionListener = new ActionListener() {//add actionlistner to listen for change
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = (String) Area.getSelectedItem();//get the selected item 
                BusStop.removeAllItems();
                int i=BusStopInfo.findAreaByName(s);
            	int []number=BusStopInfo.getBusStopsInArea(i);
                int ss=number.length;
                String[] names=new String[100];
                int ii=0;
                while(ii<ss)
                {
                	names[ii]=BusStopInfo.getFullName(number[ii]);
                	names[ii] = names[ii].split(", ")[1];
                	BusStop.addItem(names[ii]); 
                	ii++;
                }

            }
        };
        Area.addActionListener(AreaActionListener);
        
        
        JLabel lblArea_1 = new JLabel("Area");
        lblArea_1.setBounds(24, 215, 61, 15);
        getContentPane().add(lblArea_1);
        
        JLabel lblBusStop_1 = new JLabel("Bus Stop");
        lblBusStop_1.setBounds(24, 271, 61, 15);
        getContentPane().add(lblBusStop_1);
        String[] areaName1=new String[13];
        int []nnn1=BusStopInfo.getAreas();
        int len1=nnn1.length;
        int i1=0;
        Area1 = new JComboBox();
        while(i1<len1)
        {
        	areaName1[i1]=BusStopInfo.getAreaName(nnn1[i1]);
        	Area1.addItem(areaName1[i1]); 
        	i1++;
        }
        Area1.setBounds(103, 210, 138, 24);
        getContentPane().add(Area1);
        
        BusStop1 = new JComboBox();
        BusStop1.setBounds(103, 266, 220, 24);
        getContentPane().add(BusStop1);
        
        ActionListener AreaActionListener1 = new ActionListener() {//add actionlistner to listen for change
    @Override
    public void actionPerformed(ActionEvent e) {

        String s = (String) Area1.getSelectedItem();//get the selected item

           BusStop1.removeAllItems();
           int i=BusStopInfo.findAreaByName(s);
       	   int []number=BusStopInfo.getBusStopsInArea(i);
           int ss=number.length;
           String[] names=new String[100];
           int ii=0;
           while(ii<ss)
           {
           	names[ii]=BusStopInfo.getFullName(number[ii]);
           	names[ii] = names[ii].split(", ")[1];
           	BusStop1.addItem(names[ii]); 
           	ii++;
           }

       }
   };
   Area1.addActionListener(AreaActionListener1);
        
        JLabel lblSetStartStop = new JLabel("Set Start Stop");
        lblSetStartStop.setBounds(12, 26, 94, 15);
        getContentPane().add(lblSetStartStop);
        
        JLabel lblSetDestination = new JLabel("Set destination");
        lblSetDestination.setBounds(12, 168, 105, 30);
        getContentPane().add(lblSetDestination);
        
        JLabel lblTime = new JLabel("Time");
        lblTime.setBounds(24, 363, 61, 15);
        getContentPane().add(lblTime);
        
        int ii=0;
        while(ii<24)
        {
        	String strI = Integer.toString(ii);
        	time.addItem(strI); 
        	ii++;
        }
        time.setBounds(103, 385, 81, 24);
        getContentPane().add(time);
        
        JLabel lblHour = new JLabel("Hour");
        lblHour.setBounds(53, 390, 41, 15);
        getContentPane().add(lblHour);
        
        JLabel lblMinute = new JLabel("Min");
        lblMinute.setBounds(225, 390, 41, 15);
        getContentPane().add(lblMinute);
        
        String[] min = { "00", "15",  "30", "45","60"};
        minute = new JComboBox(min);
        minute.setBounds(262, 385, 61, 24);
        getContentPane().add(minute);
        
    	
        JButton PlanJourney = new JButton("Plan your journey");
        PlanJourney.setBounds(24, 447, 144, 25);
        getContentPane().add(PlanJourney);
		PlanJourney.addActionListener(new PlanJourneyAction());
        
        JButton CheckTime = new JButton("Check time");
        CheckTime.setBounds(180, 447, 143, 25);
        getContentPane().add(CheckTime);
//Click Check Time button
//startBusStop,startArea,endArea,endStop,time is in this class, date is today
		  class RealTimeAction implements ActionListener {
				
				public void actionPerformed(ActionEvent e) {
					RealTimeArea.setText("next bus at: ");
					int tme=Integer.parseInt((String)time.getSelectedItem());
					int tt=Integer.parseInt((String)minute.getSelectedItem());
					tttt=(tme*60)+tt;
					RealTimeInfo x;
//					public RealTimeInfo checkServiceTime(String startAreaName, , , , int time) {
					String startAreaName=(String) Area.getSelectedItem();
					String startBusStopName=(String)BusStop.getSelectedItem();
					String desAreaName=(String)Area1.getSelectedItem();
					String desBusStopName=(String)BusStop1.getSelectedItem();
					/*
					System.out.println(startAreaName);
					System.out.println(startBusStopName);
					System.out.println(desAreaName);
					System.out.println(desBusStopName);
					System.out.println(tttt); */
					x=JourneyHandler.checkServiceTime(startAreaName,startBusStopName,desAreaName,desBusStopName,tttt);
					int realtime=x.getTime();
					int realhour=realtime/60;
					int realmin=realtime%60;
					String realmin2 = null;
					if(realmin==0)
							realmin2="00";
					if(realmin==1)
						realmin2="01";
					if(realmin==2)
						realmin2="02";
					if(realmin==3)
						realmin2="03";
					if(realmin==4)
						realmin2="04";
					if(realmin==5)
						realmin2="05";
					if(realmin==6)
						realmin2="06";
					if(realmin==7)
						realmin2="07";
					if(realmin==8)
						realmin2="08";
					if(realmin==9)
						realmin2="09";
					if(realmin>10)
						realmin2=Integer.toString(realmin);
					
					RealTimeArea.append(Integer.toString(realhour)+":"+realmin2+"\n");
					System.out.println(x.getTime());
					System.out.println(x.getDelayTime());
					if(x.getDelayTime()!=0)
					{	
						RealTimeArea.append("service should from: ");
						int original=realtime-x.getDelayTime();
						realhour=original/60;
						realmin=original%60;
						if(realmin==0)
							realmin2="00";
					if(realmin==1)
						realmin2="01";
					if(realmin==2)
						realmin2="02";
					if(realmin==3)
						realmin2="03";
					if(realmin==4)
						realmin2="04";
					if(realmin==5)
						realmin2="05";
					if(realmin==6)
						realmin2="06";
					if(realmin==7)
						realmin2="07";
					if(realmin==8)
						realmin2="08";
					if(realmin==9)
						realmin2="09";
					if(realmin>10)
						realmin2=Integer.toString(realmin);
						RealTimeArea.append(Integer.toString(realhour)+":"+realmin2+"\n");
						RealTimeArea.append("delay time: "+Integer.toString(x.getDelayTime())+"minutes\n");
						RealTimeArea.append("Reason is: "+x.getReason());
					}
					/*if(x.getDelayTime()==0)
					RealTimeArea.append("Not Late");
					if(x.getDelayTime()!=0)
					{RealTimeArea.append(Integer.toString(x.getDelayTime()));
					RealTimeArea.append(x.getReason());}*/

				}
				}
        		CheckTime.addActionListener(new RealTimeAction());
        txtJourney.setText(JourneyPlan);
        //JScrollPane sp = new JScrollPane(txtJourney);
        txtJourney.setBounds(344, 47, 527, 239);
        txtJourney.setEditable(false);
        getContentPane().add(txtJourney);
        
        JLabel lblJourneyPlan = new JLabel("Journey Plan");
        lblJourneyPlan.setBounds(344, 26, 118, 15);
        getContentPane().add(lblJourneyPlan);
        
        JLabel lblRealTime = new JLabel("Real Time");
        lblRealTime.setBounds(344, 321, 94, 15);
        getContentPane().add(lblRealTime);
        
       
        RealTimeArea.setEditable(false);
        RealTimeArea.setBounds(344, 342, 527, 157);
        getContentPane().add(RealTimeArea);
        
        JButton btnBackHomePage = new JButton("Back Home Page");
        btnBackHomePage.setBounds(442, 426, 159, 25);
        //getContentPane().add(btnBackHomePage);
        class BackAction implements ActionListener {
    		
    		public void actionPerformed(ActionEvent e) {
    			database.busDatabase.close();
    			//new MainWindow();
    			//setVisible(false);
    		}
    	}
        btnBackHomePage.addActionListener(new BackAction());

        this.setVisible(true);
    }

    public static void addIFame(JInternalFrame iframe) { 
		DESKTOP_PANE.add(iframe);
    }


}
