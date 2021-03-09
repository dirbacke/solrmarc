package org.solrmarc.mixin.ssb.helper;

import org.solrmarc.mixin.ssb.utils.Commons;

import java.util.*;
import java.util.stream.Collectors;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

public class AuthorInformation {
	private Record record;
	
	public AuthorInformation() {}
	
	public AuthorInformation(Record record) {
		this.record = record;
	}
	
	public Set<String> getCoAuthors(String fieldNumber, char[] sections) {
		return getSubfieldWithAuthorAsSet(fieldNumber, sections, "aut");
	}
	
	public Set<String> getIllustrators(String fieldNumber, char[] sections) {
        return getSubfieldWithAuthorAsSet(fieldNumber, sections, "ill");
    }

    public Set<String> getTranslators(String fieldNumber, char[] sections) {
        return getSubfieldWithAuthorAsSet(fieldNumber, sections, "trl");
    }

    public Set<String> getOtherAuthors(String fieldNumber, char[] sections) {
		Set<String> knownAuthors = getCoAuthors(fieldNumber, sections);
		knownAuthors.addAll(getIllustrators(fieldNumber, sections));
		knownAuthors.addAll(getTranslators(fieldNumber, sections));
		Set<String> otherAuthors = getSubfieldWithAuthorAsSet(fieldNumber, sections, "");
		otherAuthors.removeAll(knownAuthors);
		return otherAuthors;
    }
    
	private Set<String> getSubfieldWithAuthorAsSet(String field, char[] sections, String key) {
        Set<String> data = new HashSet<>();
        
        for (VariableField variableField : record.getVariableFields(field)) {
            String fieldData = getFieldData(variableField, sections);
            String type = getTypeInSubField4(variableField);
            if (key.equalsIgnoreCase(type) || isOtherKey(key)) {
                data.add(fieldData);
            }
        }
        return data;
    }
	
	private boolean isOtherKey(String key) {
		return !"aut".equals(key) && !"trl".equals(key) && !"ill".equals(key);
	}
	
	private String getTypeInSubField4(VariableField variableField) {
		Subfield subField4 = ((DataField) variableField).getSubfield('4');
		if (subField4 != null) {
			return subField4.getData();
		}
		return "";
	}

	private String getFieldData(VariableField variableField, char[] sections) {
		List<String> fieldData = new ArrayList<>();
		String data;
		for (int i=0; i<sections.length; i++) {
			char section = sections[i];
			if(((DataField) variableField).getSubfield(section) != null) {
				if (((DataField) variableField).getSubfield(section).getData() != null) {
					data = ((DataField) variableField).getSubfield(section).getData();
					fieldData.add(Commons.swapName(data));
				}
			}
		}
		return fieldData.stream().collect(Collectors.joining(", "));
	}
}
