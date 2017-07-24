package com.epam.indigoeln.core.service.print.itext2.sections.project;

import com.epam.indigoeln.core.service.print.itext2.model.experiment.ExperimentHeaderModel;
import com.epam.indigoeln.core.service.print.itext2.model.project.ProjectHeaderModel;
import com.epam.indigoeln.core.service.print.itext2.sections.BaseHeaderSectionWithLogo;
import com.epam.indigoeln.core.service.print.itext2.utils.DateTimeUtils;
import com.epam.indigoeln.core.service.print.itext2.utils.PdfPTableHelper;
import com.epam.indigoeln.core.service.print.itext2.utils.TableFactory;
import com.lowagie.text.pdf.PdfPTable;

public class ProjectHeaderSection
        extends BaseHeaderSectionWithLogo<ProjectHeaderModel> {

    public ProjectHeaderSection(ProjectHeaderModel model) {
        super(model);
    }

    @Override
    protected PdfPTable generateContentTable(float width) {
        PdfPTable table = TableFactory.createDefaultTable(4, width);

        PdfPTableHelper helper = new PdfPTableHelper(table);
        helper.addKeyValueCells("Author", model.getAuthor())
                .addKeyValueCells("Creation Date", DateTimeUtils.formatSafe(model.getCreationDate()))
                .addKeyValueCells("Project", model.getProjectName(), 3)
                .addKeyValueCells("Printed Page", model.getCurrentPage() + " of " + model.getTotalPages())
                .addKeyValueCells("Printed Date", DateTimeUtils.formatSafe(model.getPrintDate()));

        return table;
    }
}
