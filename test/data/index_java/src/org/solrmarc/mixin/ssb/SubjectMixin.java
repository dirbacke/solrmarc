package org.solrmarc.mixin.ssb;

import org.solrmarc.index.SolrIndexerMixin;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;


import java.util.*;
import java.util.stream.Collectors;

public class SubjectMixin extends SolrIndexerMixin {
    private static final char[] SECTIONS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] LOCAL_SECTIONS = "cefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Get all subjects that doesn't have a fictional character in them.
     * @param record the full Marc record
     * @return a list of subject from specified fields
     */
    public List<String> getSubjects(final Record record) {
        List<String> subjects = new ArrayList<>();
        List<String> fields = Arrays.asList("600", "610", "630", "648", "650", "651");
        return fields.stream()
                .map(field->
                        parseRecordForSubject(record, field))
                .flatMap(keys-> keys.stream())
                .collect(Collectors.toList());
    }

    /**
     * Get a list of all keywords connected to fields. If keyword is missing, it will be replaced with "unknown"
     * @param record The full Marc record
     * @return a list of keywords
     */
    public List<String> getKeywordsForSubjects(final Record record) {
        List<String> fields = Arrays.asList("600", "610", "630", "648", "650", "651");
        return fields.stream()
                .map(field->
                    parseRecordForKeywords(record, field))
                .flatMap(keys-> keys.stream())
                .collect(Collectors.toList());
    }

    /**
     * Get local subjects
     * @param record Marc record
     * @return <code>Set</code> of local subject string
     */
    public Set<String> getLocalSubjects(final Record record) {
        Set<String> localSubjects = new HashSet<>();
        for (VariableField variableField : record.getVariableFields("697")) {
            if (!isFictionalPerson((DataField) variableField)) {
                for (char section : LOCAL_SECTIONS) {
                    localSubjects.addAll(getLocalSubjects((DataField) variableField, section));
                }
            }
        }
        return localSubjects;
    }

    private List<String> parseRecordForSubject(Record record, String field) {
        return record.getVariableFields(field).stream()
                .filter(variableField -> !isFictionalPerson((DataField) variableField))
                .map(variableField -> getSubjectsAsList((DataField) variableField))
                .flatMap(subjects -> subjects.stream())
                .collect(Collectors.toList());
    }

    private List<String> getSubjectsAsList(DataField dataField) {
        List<String> subjects = new ArrayList<>();
        for (char section : SECTIONS) {
            String subject = getSubjectFromSection(dataField, section);
            if (!subject.isEmpty()) {
                subjects.add(subject);
            }
        }
        return subjects;
    }

    private List<String> parseRecordForKeywords(Record record, String field) {
        List<String> keywords = new ArrayList<>();
        for (VariableField variableField : record.getVariableFields(field)) {
            if (!isFictionalPerson((DataField) variableField)) {
                for (char section : SECTIONS) {
                    String keyword = getKeyWord((DataField) variableField, section);
                    if (!keyword.isEmpty()) {
                        keywords.add(keyword);
                    }
                }
            }
        }
        return keywords;
    }

    private String getSubjectFromSection(DataField dataField, char section) {
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield(section);
            if (subfield != null && subfield.getData() != null) {
                return subfield.getData();
            }
        }
        return "";
    }

    private String getKeyWord(DataField dataField, char section) {
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield(section);
            if (subfield != null && subfield.getData() != null) {
                if (dataField.getSubfield('2') != null && dataField.getSubfield('2').getData() != null) {
                    return dataField.getSubfield('2').getData();
                }
                return "unknown";
            }
        }
        return "";
    }

    private boolean isFictionalPerson(DataField dataField) {
        if (dataField == null) {
            return false;
        }
        return dataField.getSubfields().stream()
                .map(Subfield::getData)
                .anyMatch(data->data.contains("fiktiv"));
    }

    private Set<String> getLocalSubjects(DataField dataField, char section) {
        Set<String> subjects = new HashSet<>();
        if (dataField != null) {
            subjects.addAll(dataField.getSubfields(section).stream()
                    .map(Subfield::getData)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        return subjects;
    }
}
