package org.solrmarc.mixin;

import java.util.*;
import java.util.stream.Collectors;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.solrmarc.index.SolrIndexerMixin;

import org.solrmarc.mixin.helper.MediaTypeEnum;
import org.solrmarc.mixin.helper.MediaTypeInformation;
import org.solrmarc.mixin.helper.MediaTypeParser;
import org.solrmarc.mixin.helper.MediaSubTypeParser;

public class SSBCustomMixin extends SolrIndexerMixin {
    /**
     * format name in field 100 (Author) to Swedish standard
     *
     * @param record MARC Record
     * @return <code>String</code> name of author
     */
    public String formatSSBAuthorName(final Record record) {
        String fullname = "";
        if (record != null) {
            DataField field100 = ((DataField) record.getVariableField("100"));
            if (field100 != null) {
                if (field100.getSubfield('a') != null) {
                    fullname += field100.getSubfield('a').getData();
                }
                if (field100.getSubfield('b') != null) {
                    fullname += field100.getSubfield('b').getData();
                }
                if (field100.getSubfield('c') != null) {
                    fullname += field100.getSubfield('c').getData();
                }
            }
        }
        if (fullname.length() > 0 && fullname.indexOf(',') != -1) {
            String firstname = fullname.substring(fullname.indexOf(',') + 1).trim();
            String surname = fullname.substring(0, fullname.indexOf(',')).trim();
            return firstname + " " + surname;
        }
        return fullname;
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

    public String getSSBPublisherName(final Record record) {
        MediaTypeInformation mediaInformation = new MediaTypeInformation(record);
        String value = mediaInformation.getFieldContent("260", 'b');
        if (null == value || "".equals(value)){
            return mediaInformation.getFieldContent("264", 'b');
        }
        return value;
    }

    public Set<String> getSSBSubject(final Record record) {
        MediaTypeInformation mediaInformation = new MediaTypeInformation(record);
        List<String> fields = Arrays.asList("600", "610", "630", "648", "650", "651", "697");
        Set<String> values = new HashSet<>();
        char[] sections = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (int i=0; i < fields.size(); i++) {
            Set<String> content = mediaInformation.getFieldContentForSubject(fields.get(i), sections);
            if (!content.isEmpty()) {
                values.addAll(content);
            }
        }
        return parseOutFictionalPersons(values);
    }

    public Set<String> getSSBSubjectWithType(final Record record) {
        MediaTypeInformation mediaInformation = new MediaTypeInformation(record);
        List<String> fields = Arrays.asList("600", "610", "630", "648", "650", "651", "697");
        Set<String> values = new HashSet<>();
        char[] sections = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (int i=0; i < fields.size(); i++) {
            Set<String> content = mediaInformation.getFieldContentWithType(fields.get(i), sections);
            if (!content.isEmpty()) {
                values.addAll(content);
            }
        }

        return parseOutFictionalPersons(values);
    }

    public Set<String> getSSBFictionalPerson(final Record record) {
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

    public Set<String> getSSBCoAuthors(final Record record) {
        MediaTypeInformation mediaTypeInformation = new MediaTypeInformation(record);
        String fieldNumber = "700";
        char[] sections = "abcdeq".toCharArray();
        return mediaTypeInformation.getFieldContentWithCoAuthors(fieldNumber, sections);
    }

    public Set<String> getSSBIllustrators(final Record record) {
        MediaTypeInformation mediaTypeInformation = new MediaTypeInformation(record);
        String fieldNumber = "700";
        char[] sections = "abcdeq".toCharArray();
        return mediaTypeInformation.getFieldContentWithIllustrators(fieldNumber, sections);
    }

    public Set<String> getSSBTranslators(final Record record) {
        MediaTypeInformation mediaTypeInformation = new MediaTypeInformation(record);
        String fieldNumber = "700";
        char[] sections = "abcdeq".toCharArray();
        return mediaTypeInformation.getFieldContentWithTranslators(fieldNumber, sections);
    }

    public Set<String> getSSBOtherAuthors(final Record record) {
        MediaTypeInformation mediaTypeInformation = new MediaTypeInformation(record);
        String fieldNumber = "700";
        char[] sections = "abcdeq".toCharArray();
        return mediaTypeInformation.getFieldContentWithOtherAuthors(fieldNumber, sections);
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

    private Set<String> parseOnlyFictionalPersons(Set<String> values) {
        Set<String> subjects = new HashSet<>();
        subjects.addAll(values.stream().filter(Objects::nonNull).map(value-> {
            if (value.contains("fiktiv")) {
                return value;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList()));
        return subjects;
    }
}
