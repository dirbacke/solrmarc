package org.solrmarc.mixin;

import java.util.List;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.solrmarc.index.SolrIndexerMixin;

import org.solrmarc.mixin.helper.MediaTypeInformation;

public class SSBCustomMixin extends SolrIndexerMixin {
    private enum SSBMediaType {
        AUDIO_BOOK,
        AUDIO_BOOK_DAISY,
        BOOK,
        BRAILLE,
        COMBINED,
        E_BOOK,
        E_AUDIO_BOOK,
        GAME,
        STREAMING_MOVIE,
        MAGAZINE,
        MAPS,
        MOVIE,
        MUSIC,
        MULTIMEDIA,
        NOTES,
        OBJECTS,
        OTHER;

        @Override
        public String toString() {
            return "SSBMediaType." + name();
        }
    }

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
                return SSBMediaType.NOTES.toString();
            case 'e':
            case 'f':
                return SSBMediaType.MAPS.toString();
            case 'i':
                return parseFieldI(mediaTypeInformation);
            case 'g':
                return parseFieldG(mediaTypeInformation);
            case 'j':
                return parseFieldJ(mediaTypeInformation);
            case 'm':
                return parseFieldM(mediaTypeInformation);
            case 'o':
                return SSBMediaType.COMBINED.toString();
            case 'r':
                return SSBMediaType.OBJECTS.toString();
            default:
                break;
        }
        return SSBMediaType.OTHER.toString();
    }

    private String parseFieldA(MediaTypeInformation mediaTypeInformation) {
        if (mediaTypeInformation.getLeader().charAt(7) == 's') {
            if (mediaTypeInformation.getField008().charAt(21) == 'n' || mediaTypeInformation.getField008().charAt(21) == 'p') {
                return SSBMediaType.MAGAZINE.toString();
            }
        } else if (mediaTypeInformation.getField007().charAt(0) == 'c' && mediaTypeInformation.getField007().charAt(1) == 'r') {
            return SSBMediaType.E_BOOK.toString();
        } else if (mediaTypeInformation.getField007().charAt(0) == 'f' || mediaTypeInformation.getField008().charAt(23) == 'f') {
            return SSBMediaType.BRAILLE.toString();
        }
        return SSBMediaType.BOOK.toString();
    }

    private String parseFieldI(MediaTypeInformation mediaTypeInformation) {
        if (mediaTypeInformation.getField007().charAt(0) == 'c' && mediaTypeInformation.getField007().charAt(1) == 'r') {
            return SSBMediaType.E_AUDIO_BOOK.toString();
        }
        if (mediaTypeInformation.getField007().charAt(0) == 's' && mediaTypeInformation.getField007().charAt(1) == 'd') {
            return SSBMediaType.AUDIO_BOOK.toString();
        }
        if (mediaTypeInformation.getField007().charAt(0) == 'c' &&
                (mediaTypeInformation.getField007().charAt(1) == 'o' || mediaTypeInformation.getField007().charAt(1) == 'd') &&
                isDaisy(mediaTypeInformation.getRecord().getVariableFields("500"))) {
            return SSBMediaType.AUDIO_BOOK_DAISY.toString();
        }
        return SSBMediaType.OTHER.toString();
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
            return SSBMediaType.STREAMING_MOVIE.toString();
        } else if (mediaTypeInformation.getField007().charAt(0) == 'v' &&
                (mediaTypeInformation.getField007().charAt(4) == 'v' || mediaTypeInformation.getField007().charAt(4) == 'b' || mediaTypeInformation.getField007().charAt(4) == 's')) {
            return SSBMediaType.MOVIE.toString();
        }
        return SSBMediaType.OTHER.toString();
    }

    private String parseFieldJ(MediaTypeInformation mediaTypeInformation) {
        if (mediaTypeInformation.getField007().charAt(0) == 's' && mediaTypeInformation.getField007().charAt(1) == 'd') {
            return SSBMediaType.MUSIC.toString();
        }
        return SSBMediaType.OTHER.toString();
    }

    private String parseFieldM(MediaTypeInformation mediaTypeInformation) {
        if (mediaTypeInformation.getField007().charAt(0) == 'c' &&
                mediaTypeInformation.getField007().charAt(1) == 'o' &&
                isDaisy(mediaTypeInformation.getRecord().getVariableFields("500"))) {
            return SSBMediaType.AUDIO_BOOK_DAISY.toString();
        } else if (mediaTypeInformation.getField007().charAt(0) == 'c' && mediaTypeInformation.getField007().charAt(1) == 'o') {
            return SSBMediaType.MULTIMEDIA.toString();
        } else if (mediaTypeInformation.getField008().charAt(26) == 'g') {
            return SSBMediaType.GAME.toString();
        }
        return SSBMediaType.OTHER.toString();
    }
}
