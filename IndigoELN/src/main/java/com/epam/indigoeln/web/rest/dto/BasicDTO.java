package com.epam.indigoeln.web.rest.dto;

import com.epam.indigoeln.core.model.BasicModelObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BasicDTO implements Serializable {

    private static final long serialVersionUID = -2304109712431441621L;

    @JsonProperty("id")
    private Long sequenceId;

    @NotEmpty
    private String name;
    private Set<UserPermissionDTO> accessList = new HashSet<>();

    //Read-Only auto-generated audit parameters
    private UserDTO   author;
    private UserDTO   lastModifiedBy;

    private ZonedDateTime lastEditDate;

    private ZonedDateTime creationDate;

    public BasicDTO() {
    }

    protected BasicDTO(BasicModelObject modelObject) {
        this.sequenceId = modelObject.getSequenceId();
        this.name = modelObject.getName();
        this.author = modelObject.getAuthor() != null ? new UserDTO(modelObject.getAuthor()) : null;
        this.lastModifiedBy = modelObject.getLastModifiedBy() != null ? new UserDTO(modelObject.getLastModifiedBy()) : null;
        this.lastEditDate = modelObject.getLastEditDate();
        this.creationDate = modelObject.getCreationDate();
        if (modelObject.getAccessList() != null) {
            this.accessList.addAll(modelObject.getAccessList().stream()
                    .map(UserPermissionDTO::new).collect(Collectors.toList()));
        }
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public String getName() {
        return name;
    }

    public Set<UserPermissionDTO> getAccessList() {
        return accessList;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public UserDTO getLastModifiedBy() {
        return lastModifiedBy;
    }

    public ZonedDateTime getLastEditDate() {
        return lastEditDate;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccessList(Set<UserPermissionDTO> accessList) {
        this.accessList = accessList;
    }

    @Override
    public String toString() {
        return "BasicDTO{" +
                "sequenceId=" + sequenceId +
                ", name='" + name + '\'' +
                '}';
    }
}
