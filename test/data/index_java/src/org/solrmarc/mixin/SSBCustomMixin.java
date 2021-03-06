package org.solrmarc.mixin;

import java.util.*;
import java.util.stream.Collectors;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.solrmarc.index.SolrIndexerMixin;

import org.solrmarc.mixin.ssb.helper.MediaTypeEnum;
import org.solrmarc.mixin.ssb.helper.MediaTypeInformation;
import org.solrmarc.mixin.ssb.helper.MediaTypeParser;
import org.solrmarc.mixin.ssb.helper.MediaSubTypeParser;
import org.solrmarc.mixin.ssb.utils.Commons;

public class SSBCustomMixin extends SolrIndexerMixin {
    /**
     * format name in field 100 (Author) to Swedish standard
     *
     * @param record MARC Record
     * @return <code>String</code> name of author
     */
    public String formatSSBAuthorName(final Record record) {
        String name = "";
        if (record != null) {
            DataField field100 = ((DataField) record.getVariableField("100"));
            if (field100 != null) {
                if (field100.getSubfield('a') != null) {
                    name += field100.getSubfield('a').getData();
                }
                if (field100.getSubfield('b') != null) {
                    name += field100.getSubfield('b').getData();
                }
                if (field100.getSubfield('c') != null) {
                    name += field100.getSubfield('c').getData();
                }
            }
        }
        return Commons.swapName(name);
    }

    /**
     * Parse out media sub type from record
     *
     * @param record MARC Record
     * @return <code>String</code> of media types
     */
    public String getSSBMediaSubType(final Record record) {
        MediaTypeInformation mediaInformation = new MediaTypeInformation();
        mediaInformation.setRecord(record);
        mediaInformation.parseFields();
        return parseOutMediaSubType(mediaInformation);
    }

    /**
     * Parse out media type from record
     *
     * @param record MARC Record
     * @return <code>String</code> of media types
     */
    public String getSSBMediaType(final Record record) {
        MediaTypeInformation mediaInformation = new MediaTypeInformation();
        mediaInformation.setRecord(record);
        mediaInformation.parseFields();
        return parseOutMediaType(mediaInformation);
    }

    /**
     * Parse out publishers name from a record
     * @param record MARC record
     * @return <code>String</code> containing the publishers name
     */
    public String getSSBPublisherName(final Record record) {
        MediaTypeInformation mediaInformation = new MediaTypeInformation(record);
        String value = mediaInformation.getFieldContent("260", 'b');
        if (null == value || "".equals(value)){
            return mediaInformation.getFieldContent("264", 'b');
        }
        return value;
    }

    private String parseOutMediaSubType(MediaTypeInformation mediaInformation) {
        MediaSubTypeParser mediaTypeParser = new MediaSubTypeParser();
        switch (mediaInformation.getLeader().charAt(6)) {
            case 'a':
                return mediaTypeParser.parseFieldA(mediaInformation);
            case 'c':
            case 'd':
                return MediaTypeEnum.NOTES.value();
            case 'e':
            case 'f':
                return MediaTypeEnum.MAPS.value();
            case 'i':
                return mediaTypeParser.parseFieldI(mediaInformation);
            case 'g':
                return mediaTypeParser.parseFieldG(mediaInformation);
            case 'j':
                return mediaTypeParser.parseFieldJ(mediaInformation);
            case 'm':
                return mediaTypeParser.parseFieldM(mediaInformation);
            case 'o':
                return MediaTypeEnum.COMBINED.value();
            case 'r':
                return MediaTypeEnum.OBJECTS.value();
            default:
                break;
        }
        return MediaTypeEnum.OTHER.value();
    }

    private String parseOutMediaType(MediaTypeInformation mediaInformation) {
        MediaTypeParser mediaTypeParser = new MediaTypeParser();
        switch (mediaInformation.getLeader().charAt(6)) {
            case 'a':
                return mediaTypeParser.parseFieldA(mediaInformation);
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                return MediaTypeEnum.BOOK.value();
            case 'i':
                return mediaTypeParser.parseFieldI(mediaInformation);
            case 'g':
                return mediaTypeParser.parseFieldG(mediaInformation);
            case 'j':
                return mediaTypeParser.parseFieldJ(mediaInformation);
            case 'm':
                return mediaTypeParser.parseFieldM(mediaInformation);
            case 'o':
                return MediaTypeEnum.COMBINED.value();
            case 'r':
                return MediaTypeEnum.OBJECTS.value();
            default:
                break;
        }
        return MediaTypeEnum.OTHER.value();
    }

    private Set<String> parseOutFictionalPersons(Set<String> values) {
        Set<String> subjects = new HashSet<>();
        subjects.addAll(values.stream().filter(Objects::nonNull).map(value-> {
            if (value.contains("fiktiv")) {
                return null;
            }
            return value;
        }).filter(Objects::nonNull).collect(Collectors.toList()));
        return subjects;
    }
}
