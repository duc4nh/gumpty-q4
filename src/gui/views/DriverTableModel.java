package gui.views;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import ctrl.Driver;
import ctrl.Service;

public class DriverTableModel extends AbstractTableModel {
	private ArrayList<ArrayList<Service>> services;
	private ArrayList<Service> day;

	public DriverTableModel(Driver d) {
		super();
		services = d.getServices();
	}

	@Override
	public int getColumnCount() {
        return services.size();
    }

	@Override
    public int getRowCount() {
        int maxRow = 0;
        for (ArrayList<Service> s : services)
        	if (s.size() > maxRow)
        		maxRow = s.size();
        return maxRow;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
	        case 0: return "Mon";
	        case 1: return "Tues";
	        case 2: return "Wed";
	        case 3: return "Thurs";
	        case 4: return "Fri";
	        case 5: return "Sat";
	        case 6: return "Sun";
        }
        return "Unidentified day";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
    	//return (Object) "here";
    	day = services.get(columnIndex);
    	if (rowIndex >= day.size())
    		return "";
    	return day.get(rowIndex).getRouteName();
    }
}

