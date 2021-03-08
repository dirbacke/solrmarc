package org.solrmarc.mixin.ssb;

import org.solrmarc.index.SolrIndexerMixin;
import org.marc4j.marc.Record;
import org.marc4j.marc.DataField;
import org.marc4j.marc.VariableField;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BindingMixin extends SolrIndexerMixin {
    /**
     *
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
        List<String> allowedValues = Arrays.asList("pocket", "storpocket", "häftad", "spiralhäftad", "spiralbunden");
        return allowedValues.contains(data.toLowerCase(Locale.forLanguageTag("sv-SE"))) ? data : "";
    }

}
