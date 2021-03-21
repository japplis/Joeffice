This module is for word processing
Only supported format at the moment is Microsoft Word (Docx)

Main classes:
* DocxDataObject : The NetBeans DataObject for the docx file. Mostly responsible for opening and saving the file.
* WordProcessorTopComponent : The Swing component (NetBeans TopComponent). Mostly responsible for rendering the document in a tab.
** This class uses a Swing JTextPane for rendering. 
** The JTextPane uses DocxEditorKit as editor kit
* DocxEditorKit is responsible modify the Swing document
** DocxEditorKit uses DocxDocument (Swing DefaultStyleDocument) for the data of the document.
* DocxDocument is used to add/remove elements in the Swing document
* DocumentUpdater is a process that listen to the changed in DocxDocument and updates the Apache POI document with the same changes
so that you save the same data as you see in the JTextPane.
* DocumentUpdater2 is an attempt for a better updater synchronization between the Swing document and the POI document doesn't always work correctly.
* RichTextTransferHandler for clipboard handling
* TransferableRichText is used for copy / paste of RTF (text + attributes) if available or of plain text.
* DocxTemplate.docx is an empty document that could be used for creating a new document (DocxDescription.html is used for its documentation)

Packages:
* actions: Actions related to word processor only
** SimpleActionsFactory: A collection of actions, simpler to maintain than creating 1 file per action.
* app: Swing components used for actions that can modify the document
* reader: Parse the Apache POI document to a DocxDocument.
* view: Swing components used to render different parts of the document, like a table
