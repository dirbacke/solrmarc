package org.solrmarc.mixin;

import java.util.List;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.solrmarc.index.SolrIndexerMixin;

import org.solrmarc.mixin.helper.MediaTypeEnum;
import org.solrmarc.mixin.helper.MediaTypeInformation;

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
     * Parse out media type from record
     *
     * @param record MARC Record
     * @return <code>String</code> of media types
     */

    public String getSSBMediaType(final Record record) {
        MediaTypeInformation mediaTypeInformation = new MediaTypeInformation();
        mediaTypeInformation.setRecord(record);
        mediaTypeInformation.parseFields();
        return parseOutMediaType(mediaTypeInformation);
    }

    private String parseOutMediaType(MediaTypeInformation mediaTypeInformation) {
        System.out.println("Leader field is " + mediaTypeInformation.getLeader());
        switch (mediaTypeInformation.getLeader().charAt(6)) {
            case 'a':
                return parseFieldA(mediaTypeInformation);
            case 'c':
            case 'd':
                return MediaTypeEnum.NOTES.value();
            case 'e':
            case 'f':
                return MediaTypeEnum.MAPS.value();
            case 'i':
                return parseFieldI(mediaTypeInformation);
            case 'g':
                return parseFieldG(mediaTypeInformation);
            case 'j':
                return parseFieldJ(mediaTypeInformation);
            case 'm':
                return parseFieldM(mediaTypeInformation);
            case 'o':
                return MediaTypeEnum.COMBINED.value();
            case 'r':
                return MediaTypeEnum.OBJECTS.value();
            default:
                break;
        }
        return MediaTypeEnum.OTHER.value();
    }

    private String parseFieldA(MediaTypeInformation mediaTypeInformation) {
        if (mediaTypeInformation.getLeader().charAt(7) == 's') {
            if (mediaTypeInformation.getField008().charAt(21) == 'n' || mediaTypeInformation.getField008().charAt(21) == 'p') {
                return MediaTypeEnum.MAGAZINE.value();
            }
        } else if (mediaTypeInformation.getField007().charAt(0) == 'c' && mediaTypeInformation.getField007().charAt(1) == 'r') {
            return MediaTypeEnum.E_BOOK.value();
        } else if (mediaTypeInformation.getField007().charAt(0) == 'f' || mediaTypeInformation.getField008().charAt(23) == 'f') {
            return MediaTypeEnum.BRAILLE.value();
        }
        return MediaTypeEnum.BOOK.value();
    }

    private String parseFieldI(MediaTypeInformation mediaTypeInformation) {
        if (mediaTypeInformation.getField007().charAt(0) == 'c' && mediaTypeInformation.getField007().charAt(1) == 'r') {
            return MediaTypeEnum.E_AUDIO_BOOK.value();
        }
        if (mediaTypeInformation.getField007().charAt(0) == 's' && mediaTypeInformation.getField007().charAt(1) == 'd') {
            return MediaTypeEnum.AUDIO_BOOK.value();
        }
        if (mediaTypeInformation.getField007().charAt(0) == 'c' &&
                (mediaTypeInformation.getField007().charAt(1) == 'o' || mediaTypeInformation.getField007().charAt(1) == 'd') &&
                isDaisy(mediaTypeInformation.getRecord().getVariableFields("500"))) {
            return MediaTypeEnum.AUDIO_BOOK_DAISY.value();
        }
        return MediaTypeEnum.OTHER.value();
    }

    private boolean isDaisy(List<VariableField> fields500) {
        for (VariableField field : fields500) {
            String data = ((DataField) field).getSubfield('a') != null ?
                    ((DataField) field).getSubfield('a').getData() : "";
            if (data.toUpperCase().contains("DAISY"))
                return true;
        }
        return false;
    }

    private String parseFieldG(MediaTypeInformation mediaTypeInformation) {
        if (mediaTypeInformation.getField007().charAt(0) == 'c' && mediaTypeInformation.getField007().charAt(1) == 'r') {
            return MediaTypeEnum.STREAMING_MOVIE.value();
        } else if (mediaTypeInformation.getField007().charAt(0) == 'v' &&
                (mediaTypeInformation.getField007().charAt(4) == 'v' || mediaTypeInformation.getField007().charAt(4) == 'b' || mediaTypeInformation.getField007().charAt(4) == 's')) {
            return MediaTypeEnum.MOVIE.value();
        }
        return MediaTypeEnum.OTHER.value();
    }

    private String parseFieldJ(MediaTypeInformation mediaTypeInformation) {
        if (mediaTypeInformation.getField007().charAt(0) == 's' && mediaTypeInformation.getField007().charAt(1) == 'd') {
            return MediaTypeEnum.MUSIC.value();
        }
        return MediaTypeEnum.OTHER.value();
    }

    private String parseFieldM(MediaTypeInformation mediaTypeInformation) {
        if (mediaTypeInformation.getField007().charAt(0) == 'c' &&
                mediaTypeInformation.getField007().charAt(1) == 'o' &&
                isDaisy(mediaTypeInformation.getRecord().getVariableFields("500"))) {
            return MediaTypeEnum.AUDIO_BOOK_DAISY_TEXT.value();
        } else if (mediaTypeInformation.getField007().charAt(0) == 'c' && mediaTypeInformation.getField007().charAt(1) == 'o') {
            return MediaTypeEnum.MULTIMEDIA.value();
        } else if (mediaTypeInformation.getField008().charAt(26) == 'g') {
            return MediaTypeEnum.GAME.value();
        }
        return MediaTypeEnum.OTHER.value();
    }
}
