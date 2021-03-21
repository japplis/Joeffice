// FIXME Templates fails due apparently that binary formats are not supported and the result file looks similar but a bit smaller and unzip fails
@TemplateRegistration(folder = "Office", content = "DocxTemplate.docx", displayName = "#LBL_Docx_FORMAT", iconBase = "org/joeffice/wordprocessor/wordprocessor-16.png", position = 100, description = "DocxDescription.html", requireProject = false)
@NbBundle.Messages("LBL_Docx_FORMAT=Word processor")
package org.joeffice.wordprocessor;

import org.netbeans.api.templates.TemplateRegistration;
import org.openide.util.NbBundle;
