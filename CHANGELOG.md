# Changelog

## 1.0 alpha 2

### General
- Migrated from Bitbucket to GitHub
- Dark mode using FlafLaf 1.0
- Added Applet that allows to start Joeffice embedded in any software accepting applets

### Word Processor
- Created component not dependant of NetBeans Platform that can be embedded in other Java application
- Use paragraph style for attributes not defined on text
- Use a default font (instead of previously used font) when no font is defined
- Scale fonts to 96/72 for Windows

### Spreadsheet
- Fixed missing background color for spreadsheets
- Fixed copy values of cells when the cell has a formula
- Added option to limit the size of the sheet up to the filled in cell
- Made SheetComponent independant of SpreadsheetComponent
- Scale fonts to 96/72 for Windows

### Presentation
- Fixed position and size of images in slide

### libraries
- Upgraded to NetBeans 12.3 (from NetBeans 8.1)
-- Many improvements including the migration to the Apache license: https://github.com/apache/netbeans
- Upgraded to Apache POI 5.0 (from Apache POI 3.11)
-- https://poi.apache.org/changes.html
- Upgraded to  H2 Database 1.4.200 (from 1.3.170)
-- https://h2database.com/html/changelog.html
- Use Batik version that is included in Apache POI
- Added JavaHelp 2.0
- Fixed missing JUniversalCharDet 2.4.0

## 1.0 alpha 1

- Everything was done: https://www.youtube.com/playlist?list=PLsezR6w8oWsJAKvFuv3JI34PLFFMIxLVA
