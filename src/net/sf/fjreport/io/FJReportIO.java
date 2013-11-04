package net.sf.fjreport.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import net.sf.fjreport.FJReport;

public class FJReportIO {

	public static void saveReport(String fileName, FJReport report) throws ParserConfigurationException, TransformerException {
		if (fileName.toLowerCase().endsWith(".xml"))
			XmlRW.saveReportToXml(fileName, report);
		else
			XmlRW.saveReportToXml(fileName + ".xml", report);
	}

	public static void loadReport(String fileName, FJReport report) throws SAXException, ParserConfigurationException, IOException {
		if (fileName.toLowerCase().endsWith(".xml"))
			XmlRW.loadReportFromXml(fileName, report);
	}

	public static void loadReport(InputStream is, FJReport report) throws SAXException, ParserConfigurationException, IOException {
		XmlRW.loadReportFromXml(is, report);
	}
	
}
