@TemplateRegistrations({
    @TemplateRegistration(folder = "Office", content = "XlsxTemplate.xlsx", displayName = "#LBL_Xlsx_FORMAT", iconBase = "org/joeffice/spreadsheet/spreadsheet-16.png", position = 200, description = "XlsxDescription.html", requireProject = false),
    @TemplateRegistration(folder = "Office", content = "XlsTemplate.xls", displayName = "#LBL_Xls_FORMAT", iconBase = "org/joeffice/spreadsheet/spreadsheet-16.png", position = 220, description = "XlsDescription.html", requireProject = false)
})
@NbBundle.Messages({
    "LBL_Xlsx_FORMAT=Excel Spreadsheet",
    "LBL_Xls_FORMAT=Excel 97-2003 Spreadsheet"
})
package org.joeffice.spreadsheet;

import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.openide.util.NbBundle;
