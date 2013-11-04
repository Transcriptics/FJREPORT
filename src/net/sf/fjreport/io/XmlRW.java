package net.sf.fjreport.io;

import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.FJReportPage;
import net.sf.fjreport.cell.Cell;
import net.sf.fjreport.line.Line;

import org.w3c.dom.*;
import org.xml.sax.SAXException;


public class XmlRW {

	public static void readReportFromXml(String fileName, FJReport report) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File(fileName));
		Element root = doc.getDocumentElement();
	}
	
	public static void saveReportToXml(String fileName, FJReport report) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();

		Element root = doc.createElement("report");
		doc.appendChild(root);
		
		Element paper = doc.createElement("paper");
		root.appendChild(paper);
		PageFormat pf = report.getPageFormat();
		paper.setAttribute("width", String.valueOf(pf.getWidth()));
		paper.setAttribute("height", String.valueOf(pf.getHeight()));
		paper.setAttribute("imageableX", String.valueOf(pf.getImageableX()));
		paper.setAttribute("imageableY", String.valueOf(pf.getImageableY()));
		paper.setAttribute("imageableWidth", String.valueOf(pf.getImageableWidth()));
		paper.setAttribute("imageableHeight", String.valueOf(pf.getImageableHeight()));
		paper.setAttribute("orientation", String.valueOf(pf.getOrientation()));

		Cell cell;
		Line line;
		Iterator itLines, itCells;
		FJReportPage page;
		Element lineNode, LinesNode, cellNode, cellsNode, pageNode;

		Element pagesNode = doc.createElement("pages");
		root.appendChild(pagesNode);
		
		Iterator itPages = report.getPages().iterator();
		while(itPages.hasNext()) {
			page = (FJReportPage) itPages.next();
			pageNode = doc.createElement("page");
			pagesNode.appendChild(pageNode);
			
			pageNode.setAttribute("lineIDSeed", String.valueOf(page.lineIDSeed));
			
			LinesNode = doc.createElement("vLines");
			pageNode.appendChild(LinesNode);
			itLines = page.vLines.iterator();
			while(itLines.hasNext()) {
				line = (Line) itLines.next();
				lineNode = doc.createElement("line");
				LinesNode.appendChild(lineNode);
				lineNode.setAttribute("id", String.valueOf(line.id));
				lineNode.setAttribute("start.x", String.valueOf(line.getX()));
				lineNode.setAttribute("start.y", String.valueOf(line.getY()));
				lineNode.setAttribute("length", String.valueOf(line.getLength()));
				lineNode.setAttribute("lineType", String.valueOf(line.getLineType()));
				lineNode.setAttribute("lineWidth", String.valueOf(line.getLineWidth()));
				lineNode.setAttribute("invisible", String.valueOf(line.isInVisible()));
				lineNode.setAttribute("borderType", String.valueOf(line.getBorderType()));
			}
			
			LinesNode = doc.createElement("hLines");
			pageNode.appendChild(LinesNode);
			itLines = page.hLines.iterator();
			while(itLines.hasNext()) {
				line = (Line) itLines.next();
				lineNode = doc.createElement("line");
				LinesNode.appendChild(lineNode);
				lineNode.setAttribute("id", String.valueOf(line.id));
				lineNode.setAttribute("start.x", String.valueOf(line.getX()));
				lineNode.setAttribute("start.y", String.valueOf(line.getY()));
				lineNode.setAttribute("length", String.valueOf(line.getLength()));
				lineNode.setAttribute("lineType", String.valueOf(line.getLineType()));
				lineNode.setAttribute("lineWidth", String.valueOf(line.getLineWidth()));
				lineNode.setAttribute("invisible", String.valueOf(line.isInVisible()));
				lineNode.setAttribute("borderType", String.valueOf(line.getBorderType()));
			}
			
			cellsNode = doc.createElement("cells");
			pageNode.appendChild(cellsNode);
			itCells = page.cells.iterator();
			while(itCells.hasNext()) {
				cell = (Cell) itCells.next();
				cellNode = doc.createElement("cell");
				cellsNode.appendChild(cellNode);
				cellNode.setAttribute("font.name", cell.getFont().getFontName());
				cellNode.setAttribute("font.size", String.valueOf(cell.getFont().getSize()));
				cellNode.setAttribute("font.style", String.valueOf(cell.getFont().getStyle()));
				cellNode.setAttribute("type", String.valueOf(cell.getType()));
				cellNode.setAttribute("bound.x", String.valueOf(cell.x));
				cellNode.setAttribute("bound.y", String.valueOf(cell.y));
				cellNode.setAttribute("bound.width", String.valueOf(cell.width));
				cellNode.setAttribute("bound.height", String.valueOf(cell.height));
				cellNode.setAttribute("alignment", String.valueOf(cell.getAlignment()));
				cellNode.setAttribute("name", cell.getName());
				cellNode.setAttribute("value", cell.getType() == Cell.TYPE_LABEL? cell.getStrValue() : "");
				cellNode.setAttribute("comboOption", cell.getComboxOptionsStr());
				cellNode.setAttribute("id", String.valueOf(cell.getId()));
				cellNode.setAttribute("margin.top", String.valueOf(cell.getMargin().top));
				cellNode.setAttribute("margin.left", String.valueOf(cell.getMargin().left));
				cellNode.setAttribute("margin.bottom", String.valueOf(cell.getMargin().bottom));
				cellNode.setAttribute("margin.right", String.valueOf(cell.getMargin().right));
			}
		}
		StreamResult sr = new StreamResult(new File(fileName));
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.transform(new DOMSource(doc), sr);
	}
	
	public static void loadReportFromXml(String fileName, FJReport report) throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc;
		URI uri = null;
		try {
			uri = new URI(fileName);
		} catch (Exception e) {
			uri = null;
		};
		if (uri == null)
			doc = db.parse(new File(fileName));
		else
			doc = db.parse(new File(uri));
		createReportFromDoc(doc, report);
	}

	public static void loadReportFromXml(InputStream is, FJReport report) throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc;
		doc = db.parse(is);
		createReportFromDoc(doc, report);
	}
	
	public static void createReportFromDoc(Document doc, FJReport report) {
		Element root = doc.getDocumentElement();
		PageFormat pf = new PageFormat();
		Element node = (Element) root.getElementsByTagName("paper").item(0);
		Paper paper = new Paper();
		pf.setOrientation(Integer.parseInt(node.getAttribute("orientation")));
		if (pf.getOrientation() == 1) {
			paper.setSize(Double.parseDouble(node.getAttribute("width")), 
					Double.parseDouble(node.getAttribute("height")));
			paper.setImageableArea(
					Double.parseDouble(node.getAttribute("imageableX")),
					Double.parseDouble(node.getAttribute("imageableY")),
					Double.parseDouble(node.getAttribute("imageableWidth")),
					Double.parseDouble(node.getAttribute("imageableHeight"))
					);
		} else {
			paper.setSize(Double.parseDouble(node.getAttribute("height")), 
					Double.parseDouble(node.getAttribute("width")));
			paper.setImageableArea(
					Double.parseDouble(node.getAttribute("imageableY")),
					Double.parseDouble(node.getAttribute("imageableX")),
					Double.parseDouble(node.getAttribute("imageableHeight")),
					Double.parseDouble(node.getAttribute("imageableWidth"))
					);
		}
		pf.setPaper(paper);
		report.setPageFormat(pf);
		
		List pages = new ArrayList();
		FJReportPage page;
		Element pageNode;
		Line line;
		Cell cell;
		NodeList nodes;
		Element lineNode, cellNode;
		int top, left, bottom, right;

		NodeList pagesNode = ((Element) root.getElementsByTagName("pages").item(0)).getElementsByTagName("page");
		for(int i = 0; i < pagesNode.getLength(); i++) {
			pageNode = (Element) pagesNode.item(i);
			page = new FJReportPage();
			nodes = pageNode.getElementsByTagName("vLines").item(0).getChildNodes();
			for(int j = 0; j < nodes.getLength(); j++) {
				lineNode = (Element) nodes.item(j);
				line = new Line(report);
				page.vLines.add(line);
				line.setStartPoint(new Point(Integer.parseInt(lineNode.getAttribute("start.x")),
						Integer.parseInt(lineNode.getAttribute("start.y"))));
				line.setLength(Integer.parseInt(lineNode.getAttribute("length")));
				line.setOrientation(Line.VERTICAL_ORIENTATION);
				line.setBorderType(Integer.parseInt(lineNode.getAttribute("borderType")));
				line.setInVisible(Boolean.parseBoolean(lineNode.getAttribute("invisible")));
				line.setLineType(Integer.parseInt(lineNode.getAttribute("lineType")));
				line.setLineWidth(Integer.parseInt(lineNode.getAttribute("lineWidth")));
			}
			nodes = pageNode.getElementsByTagName("hLines").item(0).getChildNodes();
			for(int j = 0; j < nodes.getLength(); j++) {
				lineNode = (Element) nodes.item(j);
				line = new Line(report);
				page.hLines.add(line);
				line.setStartPoint(new Point(Integer.parseInt(lineNode.getAttribute("start.x")),
						Integer.parseInt(lineNode.getAttribute("start.y"))));
				line.setLength(Integer.parseInt(lineNode.getAttribute("length")));
				line.setOrientation(Line.HORIZONTAL_ORIENTATION);
				line.setBorderType(Integer.parseInt(lineNode.getAttribute("borderType")));
				line.setInVisible(Boolean.parseBoolean(lineNode.getAttribute("invisible")));
				line.setLineType(Integer.parseInt(lineNode.getAttribute("lineType")));
				line.setLineWidth(Integer.parseInt(lineNode.getAttribute("lineWidth")));
			}
			nodes = pageNode.getElementsByTagName("cells").item(0).getChildNodes();
			for(int j = 0; j < nodes.getLength(); j++) {
				cellNode = (Element) nodes.item(j);
				cell = new Cell(Integer.parseInt(cellNode.getAttribute("bound.x")),
						Integer.parseInt(cellNode.getAttribute("bound.y")),
						Integer.parseInt(cellNode.getAttribute("bound.width")),
						Integer.parseInt(cellNode.getAttribute("bound.height")));
				cell.setReport(report);
				page.cells.add(cell);
				if (cellNode.getAttribute("id") != "")
					cell.setId(Long.parseLong(cellNode.getAttribute("id")));
				cell.setName(cellNode.getAttribute("name"));
				cell.setValue(cellNode.getAttribute("value"));
				cell.setType(Integer.parseInt(cellNode.getAttribute("type")));
				cell.setComboxOptionsStr(cellNode.getAttribute("comboOption"));
				cell.setFont(new Font(cellNode.getAttribute("font.name"),
						Integer.parseInt(cellNode.getAttribute("font.style")),
						Integer.parseInt(cellNode.getAttribute("font.size"))));
				cell.setAlignment(Integer.parseInt(cellNode.getAttribute("alignment")));
				top = cellNode.getAttribute("margin.top") == ""? 0: Integer.parseInt(cellNode.getAttribute("margin.top"));
				left = cellNode.getAttribute("margin.left") == ""? 0: Integer.parseInt(cellNode.getAttribute("margin.left"));
				bottom = cellNode.getAttribute("margin.bottom") == ""? 0: Integer.parseInt(cellNode.getAttribute("margin.bottom"));
				right = cellNode.getAttribute("margin.right") == ""? 0: Integer.parseInt(cellNode.getAttribute("margin.right"));
				if (top != 0 || left != 0 || bottom != 0 || right != 0)
					cell.setMargin(new Insets(top, left, bottom, right));
				cell.createEditor();
			}
			pages.add(page);
		}
		report.setPages(pages);
		report.firstPage();
		report.setState(FJReport.EDIT_STATE);
		report.buildMap();
	}
	
}
