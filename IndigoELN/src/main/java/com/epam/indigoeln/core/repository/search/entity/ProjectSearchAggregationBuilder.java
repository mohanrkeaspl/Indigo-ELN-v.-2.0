package com.epam.indigoeln.core.repository.search.entity;

import com.epam.indigoeln.core.model.Project;
import com.epam.indigoeln.core.repository.search.AbstractSearchAggregationBuilder;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

public class ProjectSearchAggregationBuilder extends AbstractSearchAggregationBuilder {

    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_NAME = "name";

    public static final String FIELD_KEYWORDS = "keywords";
    public static final String FIELD_REFERENCES = "references";
    public static final String FIELD_AUTHOR = "author";
    public static final String FIELD_ACCESS_LIST = "accessList";
    public static final String FIELD_CREATION_DATE = "creationDate";
    public static final String FIELD_KIND = "kind";

    public static final String FIELD_AUTHOR_ID = FIELD_AUTHOR + "._id";

    private static final List<String> SEARCH_QUERY_FIELDS = Arrays.asList(FIELD_DESCRIPTION, FIELD_NAME, FIELD_KEYWORDS, FIELD_REFERENCES);
    private static final List<String> AVAILABLE_FIELDS = Arrays.asList(FIELD_DESCRIPTION, FIELD_NAME, FIELD_KEYWORDS, FIELD_REFERENCES, FIELD_AUTHOR_ID, FIELD_KIND);

    private Collection<AggregationOperation> baseOperations = new ArrayList<>();

    private ProjectSearchAggregationBuilder() {
        setSearchQueryFields(SEARCH_QUERY_FIELDS);
        setAvailableFields(AVAILABLE_FIELDS);
        final int packageLength = Project.class.getPackage().getName().length() + 1;
        baseOperations.add(
                Aggregation.project(FIELD_DESCRIPTION, FIELD_NAME, FIELD_KEYWORDS, FIELD_REFERENCES, FIELD_AUTHOR, FIELD_ACCESS_LIST, FIELD_CREATION_DATE)
                        .andExpression("substr(_class, " + packageLength + ", -1)").as(FIELD_KIND)
        );
    }

    public static ProjectSearchAggregationBuilder getInstance() {
        return new ProjectSearchAggregationBuilder();
    }

    public Optional<Aggregation> build() {
        return Optional.ofNullable(aggregationOperations.isEmpty() ? null : aggregationOperations).map(ao -> {
            List<AggregationOperation> operations = new ArrayList<>(baseOperations);
            operations.addAll(ao);
            return Aggregation.newAggregation(operations);
        });
    }

}
