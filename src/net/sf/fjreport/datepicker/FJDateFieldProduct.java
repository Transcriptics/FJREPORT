package net.sf.fjreport.datepicker;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FJDateFieldProduct {
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setDateStr(String dateStr, FJDateField fJDateField) {
		try {
			if (dateStr != null && dateStr != "")
				setValue(dateFormat.parse(dateStr), fJDateField);
			else
				setValue(null, fJDateField);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setValue(Date value, FJDateField fJDateField) {
		fJDateField.setValue(value);
		if (value != null)
			fJDateField.setText(dateFormat.format(value));
		else
			fJDateField.setText("");
	}

	public String getDateStr(FJDateField fJDateField, Date value) {
		if (fJDateField.getValue() == null || fJDateField.getValue().equals(""))
			return "";
		else
			return dateFormat.format(value);
	}
}