This module is for the spreadsheet
The supported formats are Microsoft Excel (Xls and Xslx) and Comma Separated Values (CSV)

Main classes:
* XslxDataObject : The NetBeans DataObject for the xslx files. Mostly responsible for opening and saving the file.
* SpreadsheetTopComponent : The Swing component (NetBeans TopComponent). Mostly responsible for rendering the document in a tab.
** This class uses a SpreadsheetCompenent for rendering. 
* SpreadsheetComponent is responsible for rendering the spreadsheet.
** It's a JTabbedPane where each tab contains a SheetComponent.
* SheetComponent is responsible for rendering one sheet of the spreadsheet
** It contains mainly a SheetTable
* SheetTable is an advanced Swing JTable component
* TableTransferHandler for clipboard handling
* XlslxTemplate.xlsx is an empty document that could be used for creating a new document (XslxDescription.html is used for its documentation)

Packages:
* actions: Actions related to the spreadsheet only
* cell: cell rendering and editing components
* csv: CSV related classes like parsing file
* rows: Component used to add support for row headers to the JTable
* sequence: Set of sequences. A sequence allow to complete empty cells based on the first few cell value. Like 1,2 has the sequence 1,2,3,4,5,...
* sheet: Classes use on a single sheet

