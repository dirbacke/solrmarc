package org.solrmarc.mixin.helper;

import java.util.HashSet;
import java.util.Set;

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
		return getSubfieldWithOtherAuthorAsSet(fieldNumber, sections, "aut");
	}
	
	public Set<String> getIllustrators(String fieldNumber, char[] sections) {
        return getSubfieldWithOtherAuthorAsSet(fieldNumber, sections, "ill");
    }

    public Set<String> getTranslators(String fieldNumber, char[] sections) {
        return getSubfieldWithOtherAuthorAsSet(fieldNumber, sections, "trl");
    }

    public Set<String> getOtherAuthors(String fieldNumber, char[] sections) {
        return getSubfieldWithOtherAuthorAsSet(fieldNumber, sections, "");
    }
    
	private Set<String> getSubfieldWithOtherAuthorAsSet(String field, char[] sections, String key) {
        Set<String> data = new HashSet<>();
        
        for (VariableField variableField : record.getVariableFields(field)) {
            String fieldData = getFieldData(variableField, sections);
            String type = getTypeInSubField4(variableField);
            if (key.equalsIgnoreCase(type) || isOtherKey(key)) {
                data.add(fieldData.trim());
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
		String fieldData = "";
		for (int i=0; i<sections.length; i++) {
			char section = sections[i];
			if(((DataField) variableField).getSubfield(section) != null) {
				if (((DataField) variableField).getSubfield(section).getData() != null) {
					fieldData += ((DataField) variableField).getSubfield(section).getData() + " ";
				}
			}
		}
		return fieldData.trim();
	}
}
