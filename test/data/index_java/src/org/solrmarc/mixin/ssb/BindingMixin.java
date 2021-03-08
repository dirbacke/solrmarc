package org.solrmarc.mixin.ssb;

import org.solrmarc.index.SolrIndexerMixin;
import org.marc4j.marc.Record;
import org.marc4j.marc.DataField;
import org.marc4j.marc.VariableField;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BindingMixin extends SolrIndexerMixin {
    private final static List<String> VALID_BIND_VALUES = Arrays.asList(
            "pocket",
            "storpocket",
            "häftad",
            "spiralhäftad",
            "spiralbunden");

    /**
     * get the binding for a record in field 020q
     *
     * @param record MARC Record
     * @return <code>String</code> with binding if in valid array
     */
    public String getBinding(final Record record) {
        String binding = "", field = "020";
        char section = 'q';
        for (VariableField variableField : record.getVariableFields(field)) {
            if(((DataField) variableField).getSubfield(section) != null) {
                if(((DataField) variableField).getSubfield(section).getData() != null) {
                    binding += parseValidStrings(((DataField) variableField).getSubfield(section).getData().toLowerCase(Locale.forLanguageTag("sv-SE")));
                }
            }
        }
        return binding;
    }

    private String parseValidStrings(String data) {
        if (VALID_BIND_VALUES.contains(data.toLowerCase(Locale.forLanguageTag("sv-SE"))))
            return data;
        return "";
    }
}
