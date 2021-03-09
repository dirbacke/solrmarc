package org.solrmarc.mixin.ssb;

import org.solrmarc.index.SolrIndexerMixin;

import org.marc4j.marc.Record;
import org.solrmarc.mixin.ssb.utils.Commons;
import org.solrmarc.mixin.ssb.helper.MediaTypeInformation;
import java.util.*;
import java.util.stream.Collectors;

public class FictionalMixin extends SolrIndexerMixin {
    private Commons commons = new Commons();
    public Set<String> getFictionalPerson(final Record record) {
        MediaTypeInformation mediaInformation = new MediaTypeInformation(record);
        List<String> fields = Arrays.asList("600", "697");
        Set<String> values = new HashSet<>();
        char[] sections = "ac".toCharArray();
        for (int i=0; i < fields.size(); i++) {
            Set<String> content = mediaInformation.getFieldContentForSubject(fields.get(i), sections);
            if (!content.isEmpty()) {
                values.addAll(content);
            }
        }
        return parseOnlyFictionalPersons(values);
    }

    private Set<String> parseOnlyFictionalPersons(Set<String> values) {
        Set<String> subjects = new HashSet<>();
        subjects.addAll(values.stream().filter(Objects::nonNull)
                .map(value-> {
                    if (value.contains("fiktiv")) {
                        return value;
                    }
                    return null;
                }).filter(Objects::nonNull)
                .map(fictional-> {
                    if (fictional.indexOf("(") != -1) {
                        return fictional.substring(0, fictional.indexOf("(") - 1);
                    }
                    return fictional;
                })
                .map(Commons::swapName)
                .collect(Collectors.toList()));
        return subjects;
    }
}
