
=========================================================
  FJReport - Free Java Report
   since 2006, by Frank Lewis
=========================================================


1. What is FJReport 

	JReport is named for Free Java Report.
	
	FJReport is originally designed for desktop application. 
	When I was writing an application something like Management 
	Information System (MIS), I found that my customers need a 
	"What You See Is What You Get" (WYSIWYG) editor to finish 
	grid-style report and submit. Also, there are many 
	circumstances that end users require a WYSIWYG editor to 
	fill report and programmers need a fast report tool to design 
	grid-style report. I can't find any free java package for 
	such requirements. So I have to write a package myself.
	
	FJReport is a light-weight WYSIWYG report package both for 
	form-style report designing and filling. Unlike most other 
	report designers which draw grids by cell melting, FJReport 
	auto calculates out cells from lines. User draws lines 
	directly on a page and let FJReport determine cells. 
	Light-weight means that FJReport is a small package with basic 
	functions provided. For easy deployment, it抯 not intent to 
	include other library.


2. Basic Functions 

	Swing based report with graphic report template desginer.
	Paged table printing facility.
	Some other useful swing components.


3. Four states of FJReport 

	The report has 4 states, which are line state, cell state, 
	edit state, readonly state.
	
	DRAWLINE_STATE. 
	Draw vertical or horizontal lines. 

	CELL_STATE.
	Set cell's properties. Left double click on a cell to activate 
	the cell edit dialog. There are currently 9 cell types, empty, 
	label, textfield, textarea, combobox, checkbox, datefiled, 
	image, custom When report state is switched from line to cell, 
	it will update cells' position and size according to the lines 
	change. The algorithm performance is about n*n, fast enough.

	EDIT_STATE.
	It is for end user of your application.
	User can finish form-style report in edit state. Edit components 
	are embedded into cells to accept users� input. The class of edit 
	component varies from JTextField to JPanel, according to cell抯 
	type. What they see on the screen is what they get on a paper 
	from printer. This is the main purpose of this package, to 
	provide form-style report editor to user, something like MS Word.
	
	READONLY_STATE
	It looks like edit state, but can't accept user input. You can 
	use it to show preview dialog.

4. How to use the report template designer? 

	In the east of the main content pane of template designer, there
	is a vertical toolbar containing some functional button.
	
	Click line, cell, edit button to pick report state.
	
	In line state, you can draw vertical or horizontal lines.
	 
	Right press mouse and drag, when right button is released, a new 
	line will appear. 
	
	Left click on a line to pick it. Left double click on it to set 
	line properties. When a line is picked (high lighted with red 
	color), left press mouse on it and drag can move the line to 
	the desired position. Left press mouse on its end point and drag 
	(to vertical line, bottom point is end; to horizontal line, 
	right point is end), the line length will change. Right click on 
	it will remove the line.
	
	Left double click on blank will create a horizontal line stretching 
	from left border to right border with line.startPoint.y = mouseEvent.y, 
	while right click on blank will create a vertical line stretching 
	from top to bottom.
	
	in cell state, you can set cell's properties, left double click on a 
	cell to activate the cell edit dialog.
	There are currently 9 cell types, which are empty, label, textfield, 
	textarea, combobox, checkbox, datefiled, image, custom.
	When state is switched from line to cell, fjreport will update cells' 
	position and size according to the lines change. the algorithm 
	performance is about n*n, fast enough.
	
	When designing complete, click save button to save your report template
	into a XML file.

5. How to save/load report by coding?
	use FJReport.saveReport and FJReport.loadReport.
	FJReport.saveDialog and FJReport.loadDialog will call 
	saveReport/loadReport	functions after show a fileChoose dialog.

	Each cell except type_null cell is identified by a string which 
	is the cell's name.
	Each null cell has value string which is shown to end user.
	The cell value can be access via FJReport.getValue(String name) 
	and FJReport.setValue(String name, String value).
	Use FJReport.getCellByName(String name) to access a specific cell.

6. Report events
	currently, 3 event are available
	valueChange, occurs when cell value is change.
	cellEnter, occurs when cell grabs focus.
	cellLeave, occurs when cell loose focus.
	See FJReport.addValueChangedActionListener, FJReport.addEditorEnterActionListener, FJReport.addEditorLeaveActionListener.

7. Sample codes
	See net.sf.fjreport.samples.piggy.xml for report template example.
	Use net.sf.fjreport.FJReportEditor to open that sample xml file.
	See net.sf.fjreport.samples.PiggyLetter for value setting guide.
	It's a what you see is what you get report editor.
	You can print it directly to fax. no preview dialog, no extra
	order filling UI input program.


8. Other useful components
	DatePicker
	  see net.sf.fjreport.datepicker.
	  there are two classes, FJDteField and FJDatePicker.
	StatusBar
	  see net.sf.fjreport.statusbar.
	Paged jtable print
	  see net.sf.fjreport.samples.TablePrintSample
	ModalDialog
	  see net.sf.util.ModalDialog

	All include main() functions to show a demo.

9. Notes
	Please feel free to contact me at fj_lewis@users.sourceforge.net. 
	All comments are welcome.
	


0.4.0 release	
	a. support image cell 
	b. support column visibility setting of pagedtable. 
	   double click the table on preview page to activate column 
	   visibility setting dialog. 
	c. complete comments of main class FJReport. 
	d. write a report sample, see samples directory. 

0.4.1 release
  some bugs fixed add French language translation 

0.4.2 release 
	a. fix landscape orientation page save/load error. 
	b. fix cell font setting error. 
	c. add numeric id to line and cell add Zoom class, 
	   zoom function is not yet completed 

0.4.3 release
	a. fix a bug which can't keep old cells when state switch from 
	   drawline_state to cell_state
	b. lines and cells automatic adjust to changed page format


Future features
 a.
    only 3 states for fjreport
 
	   public static final int STATE_DESIGN = 1;
	   public static final int STATE_EDIT = 2;
	   public static final int STATE_PREVIEW = 3;
	
    design state = drawling_state + cell_state
    in preview state, line position and cell bounds can be 
    adjusted by user
 
 b.
    support zoom
 c.
    support 1*n, m * n pages layout, for all 3 states
 d.
    support nested cell. very complicated report can be worked out.
 e.
    support dataset cells.
 f.
    an entity bean can be binded to a report
    
supposed a User bean like

  class User {
    String name;
    String phone;
    String duty;
    ...
    List<Address> addresses;
    
    class Address {
      String city;
      String street;
      String house;
      String zip;
    }
  }
 
A User object can be directly assigned to a designed report
     report.bindEntity(user);     
You will get an UI for inputting User, which is exactly what you 
get on your customer's printer or fax.

 g.
facilitate report designing.  
can open *.class(compiled entity bean) file in 
FJReportEditor, find properties of the bean via 
java reflect mechanism. Then user can set a cell's 
name by double click the property item of the bean.  
 