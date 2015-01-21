package gui.views;


import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import org.joda.time.DateTime;

import db.BusStopInfo;
import db.NoticeInfo;
import db.TimetableInfo;
import db.database;
import ctrl.Notice;

public class NoticeView extends JFrame{
     String Notice="\n";
     TextArea textArea_Reason= new TextArea();
     String reason="he";
     String a1="he";
     int a2;
     int a;
     int timetyp,id,delay;
     int tt;
    JComboBox Service = new JComboBox();
    private static final JDesktopPane DESKTOP_PANE = new JDesktopPane();
    

    public NoticeView() { 

        super();
        setSize(640, 480);
        /*menu102.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addIFame(new InternalFrame());
            }
        });*/
        
        //Area and Label Area(start stop)
        
        this.getContentPane().setLayout(null);  
        this.getContentPane().add(DESKTOP_PANE);
        String[] RouteName=new String[20];
        int []nnn=BusStopInfo.getRoutes();
        int len=nnn.length;
        int i=0;
        final JComboBox Route = new JComboBox();
        while(i<len)
        {
        	RouteName[i]=BusStopInfo.getRouteName(nnn[i]);
        	Route.addItem(RouteName[i]); 
        	i++;
        }  
        Route.setBounds(103, 71, 94, 24);
        getContentPane().add(Route);        
        JLabel lblRoute = new JLabel("Route");
        lblRoute.setBounds(24, 80, 61, 15);
        getContentPane().add(lblRoute);
        Service.setBounds(103, 124, 94, 24);
        getContentPane().add(Service);
        ActionListener AreaActionListener1 = new ActionListener() {
        public void actionPerformed(ActionEvent e) {

        String s = (String) Route.getSelectedItem();
        Service.removeAllItems();
        System.out.println(s);
			int ss=BusStopInfo.findRoute(s);       
			int[] abc=TimetableInfo.getServices(ss,TimetableInfo.timetableKind(database.today()));

                int i=0;
               while(i<abc.length)
               {
            	   
            	   System.out.println(abc[i]);
                   Service.addItem(Integer.toString(abc[i])); 
               i++;}
               
        
        }
       }; 
       Route.addActionListener(AreaActionListener1);

       
      
       
      
    
        /*
        JLabel lblTimeType = new JLabel("Time Type");
        lblTimeType.setBounds(24, 132, 61, 15);
        getContentPane().add(lblTimeType);
        String Time[] = {"0","1","2"};
        final JComboBox TimeType = new JComboBox(Time);

        TimeType.setBounds(103, 127, 94, 24);
        getContentPane().add(TimeType);
        */
        
        JLabel lblAdd = new JLabel("Add Notice");
        lblAdd.setBounds(12, 26, 94, 15);
        getContentPane().add(lblAdd);
        
        JLabel lblService = new JLabel("service");
        lblService.setBounds(24, 121, 55, 30);
        getContentPane().add(lblService);
        
       

        
        
        
        textArea_Reason.setBounds(24, 304, 218, 75);
        getContentPane().add(textArea_Reason);
        
        
        JLabel lblReason = new JLabel("Reason");
        lblReason.setBounds(24, 283, 61, 15);
        getContentPane().add(lblReason);

        String[] Type={"delay","cancel"};
        final JComboBox TimeType = new JComboBox(Type);
        TimeType.setBounds(103, 173, 94, 24);
        getContentPane().add(TimeType);
        
        final JLabel lblSss = new JLabel();
        lblSss.setBounds(24, 422, 218, 15);
        getContentPane().add(lblSss);
        
        
        JLabel lblType = new JLabel("Type");
        lblType.setBounds(24, 178, 61, 15);
        getContentPane().add(lblType);
        
        JButton Add = new JButton("Add Notice");
        Add.setBounds(49, 385, 144, 25);
        getContentPane().add(Add);
        
        JLabel lblDelayTime = new JLabel("delay time");
        lblDelayTime.setBounds(24, 224, 73, 15);
        getContentPane().add(lblDelayTime);
        
        final TextArea NoticeInf = new TextArea();
        NoticeInf.setBounds(288, 71, 309, 289);
        NoticeInf.setEditable(false);
        getContentPane().add(NoticeInf);
        
        JButton btnBackToHome = new JButton("Back to home");
        btnBackToHome.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		new MainWindow();
    			setVisible(false);
        	}
        });
        btnBackToHome.setBounds(450, 385, 134, 25);
        getContentPane().add(btnBackToHome);
        
        final JComboBox hour = new JComboBox();
        hour.setBounds(103, 219, 45, 24);
        getContentPane().add(hour);
        int hourtime=0;
        while(hourtime<25)
        {
        	hour.addItem(Integer.toString(hourtime));
        	hourtime++;
        }
        
        JLabel lblHour = new JLabel("hour");
        lblHour.setBounds(150, 224, 61, 15);
        getContentPane().add(lblHour);
        
        final JComboBox minute = new JComboBox();
        minute.setBounds(103, 255, 45, 24);
        getContentPane().add(minute);
        int minutetime=0;
        while(minutetime<60)
        {
        	minute.addItem(Integer.toString(minutetime));
        	minutetime++;
        }
        
        JLabel lblMinutes = new JLabel("minutes");
        lblMinutes.setBounds(150, 260, 61, 15);
        getContentPane().add(lblMinutes);
        
        
        
        
		
	
        
    
		class NoticeAction implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {
				NoticeInf.setText(" ");
				 DateTime date = new DateTime();
				 int a= Service.getSelectedIndex();
				 System.out.println("a"+a);
				 reason=textArea_Reason.getText();
				 System.out.println("reason"+reason);
				a2=hour.getSelectedIndex();
				int a3=minute.getSelectedIndex();
				delay=Integer.parseInt((String)hour.getSelectedItem())*60+Integer.parseInt((String)minute.getSelectedItem());
				 System.out.println("delay"+delay);
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
					 System.out.println(TimeType.getSelectedItem());
					 System.out.println(TimeType.getSelectedIndex());
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

                
                

        this.setVisible(true);
    }

    public static void addIFame(JInternalFrame iframe) { 
		DESKTOP_PANE.add(iframe);
    }


}

