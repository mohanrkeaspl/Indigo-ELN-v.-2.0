package com.epam.indigoeln.web.rest.dto;

import com.epam.indigoeln.core.model.Experiment;
import com.epam.indigoeln.core.model.ExperimentStatus;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for Experiment
 */
@JsonTypeName("Experiment")
public class ExperimentDTO extends BasicDTO {

    private static final long serialVersionUID = -305591958439648518L;

    private TemplateDTO template;
    private List<ComponentDTO> components = new ArrayList<>();
    private ExperimentStatus status;

    public ExperimentDTO() {
        super();
    }

    public ExperimentDTO(Experiment experiment) {
        super(experiment);
        if (experiment.getTemplate() != null) {
            this.template = new TemplateDTO(experiment.getTemplate());
        }
        this.components = experiment.getComponents() != null ?
                experiment.getComponents().stream().map(ComponentDTO::new).collect(Collectors.toList()) : new ArrayList<>();
        this.status = experiment.getStatus();
    }

    public List<ComponentDTO> getComponents() {
        return components;
    }

    public TemplateDTO getTemplate() {
        return template;
    }

    public void setTemplate(TemplateDTO template) {
        this.template = template;
    }

    public void setComponents(List<ComponentDTO> components) {
        this.components = components != null ? components : new ArrayList<>();
    }

    public ExperimentStatus getStatus() {
        return status;
    }

    public void setStatus(ExperimentStatus status) {
        this.status = status;
    }
}
