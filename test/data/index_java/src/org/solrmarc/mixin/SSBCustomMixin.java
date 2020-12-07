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
        MediaTypeInformation mediaInformation = new MediaTypeInformation();
        mediaInformation.setRecord(record);
        mediaInformation.parseFields();
        return parseOutMediaType(mediaInformation);
    }

    private String parseOutMediaType(MediaTypeInformation mediaInformation) {
        switch (mediaInformation.getLeader().charAt(6)) {
            case 'a':
                return parseFieldA(mediaInformation);
            case 'c':
            case 'd':
                return MediaTypeEnum.NOTES.value();
            case 'e':
            case 'f':
                return MediaTypeEnum.MAPS.value();
            case 'i':
                return parseFieldI(mediaInformation);
            case 'g':
                return parseFieldG(mediaInformation);
            case 'j':
                return parseFieldJ(mediaInformation);
            case 'm':
                return parseFieldM(mediaInformation);
            case 'o':
                return MediaTypeEnum.COMBINED.value();
            case 'r':
                return MediaTypeEnum.OBJECTS.value();
            default:
                break;
        }
        return MediaTypeEnum.OTHER.value();
    }

    private String parseFieldA(MediaTypeInformation mediaInformation) {
        if (mediaInformation.getLeader().charAt(7) == 's') {
            if (mediaInformation.isCharInPosField008('n', 21) || mediaInformation.isCharInPosField008('p', 21)) {
                return MediaTypeEnum.MAGAZINE.value();
            }
        } else if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('r', 1)) {
            return MediaTypeEnum.E_BOOK.value();
        } else if (mediaInformation.isCharInPosField007('f', 0) || mediaInformation.isCharInPosField008('f', 23)) {
            return MediaTypeEnum.BRAILLE.value();
        }
        return MediaTypeEnum.BOOK.value();
    }

    private String parseFieldI(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('r', 1)) {
            return MediaTypeEnum.E_AUDIO_BOOK.value();
        }
        if ( isDaisy(mediaInformation.getField500()) && (
                (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('o', 1)) ||
                (mediaInformation.isCharInPosField007('s', 0) && mediaInformation.isCharInPosField007('d', 1)) ) ) {
            return MediaTypeEnum.AUDIO_BOOK_DAISY.value();
        }
        if (mediaInformation.isCharInPosField007('s', 0) && mediaInformation.isCharInPosField007('d', 1)) {
            return MediaTypeEnum.AUDIO_BOOK.value();
        }
        if (mediaInformation.isCharInPosField007('c', 0) &&
                (mediaInformation.isCharInPosField007('o', 1) || mediaInformation.isCharInPosField007('d', 1)) &&
                isDaisy(mediaInformation.getField500())) {
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

    private String parseFieldG(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('r', 1)) {
            return MediaTypeEnum.STREAMING_MOVIE.value();
        } else if (mediaInformation.isCharInPosField007('v', 0) &&
                (mediaInformation.isCharInPosField007('v', 4) || mediaInformation.isCharInPosField007('b', 4) || mediaInformation.isCharInPosField007('s', 4))) {
            return MediaTypeEnum.MOVIE.value();
        }
        return MediaTypeEnum.OTHER.value();
    }

    private String parseFieldJ(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('s', 0) && mediaInformation.isCharInPosField007('d', 1)) {
            return MediaTypeEnum.MUSIC.value();
        }
        return MediaTypeEnum.OTHER.value();
    }

    private String parseFieldM(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('c', 0) &&
                mediaInformation.isCharInPosField007('o', 1) &&
                isDaisy(mediaInformation.getField500())) {
            return MediaTypeEnum.AUDIO_BOOK_DAISY_TEXT.value();
        } else if (mediaInformation.isCharInPosField008('g', 26)) {
            return MediaTypeEnum.GAME.value();
        } else if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('o', 1)) {
            return MediaTypeEnum.MULTIMEDIA.value();
        }
        return MediaTypeEnum.OTHER.value();
    }
}
