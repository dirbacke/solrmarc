package org.solrmarc.mixin.ssb.helper;

import org.marc4j.marc.DataField;
import org.marc4j.marc.VariableField;

import java.util.List;

import org.solrmarc.mixin.ssb.helper.MediaTypeInformation;
import org.solrmarc.mixin.ssb.helper.MediaTypeEnum;

public class MediaTypeParser {
    public String parseFieldA(MediaTypeInformation mediaInformation) {
        if (mediaInformation.getLeader().charAt(7) == 's') {
            if (mediaInformation.isCharInPosField008('n', 21) || mediaInformation.isCharInPosField008('p', 21)) {
                return MediaTypeEnum.MAGAZINE.value();
            }
        } else if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('r', 1)) {
            return MediaTypeEnum.E_BOOK.value();
        }
        return MediaTypeEnum.BOOK.value();
    }

    public String parseFieldI(MediaTypeInformation mediaInformation) {
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
        return MediaTypeEnum.OTHER.value();
    }

    public boolean isDaisy(List<VariableField> fields500) {
        for (VariableField field : fields500) {
            String data = ((DataField) field).getSubfield('a') != null ?
                    ((DataField) field).getSubfield('a').getData() : "";
            if (data.toUpperCase().contains("DAISY"))
                return true;
        }
        return false;
    }

    public String parseFieldG(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('r', 1)) {
            return MediaTypeEnum.STREAMING_MOVIE.value();
        } else if (mediaInformation.isCharInPosField007('v', 0) &&
                (mediaInformation.isCharInPosField007('v', 4) || mediaInformation.isCharInPosField007('b', 4) || mediaInformation.isCharInPosField007('s', 4))) {
            return MediaTypeEnum.MOVIE.value();
        }
        return MediaTypeEnum.OTHER.value();
    }

    public String parseFieldJ(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('s', 0) && mediaInformation.isCharInPosField007('d', 1)) {
            return MediaTypeEnum.MUSIC.value();
        }
        return MediaTypeEnum.OTHER.value();
    }

    public String parseFieldM(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('c', 0) &&
                mediaInformation.isCharInPosField007('o', 1) &&
                isDaisy(mediaInformation.getField500())) {
            return MediaTypeEnum.AUDIO_BOOK_DAISY.value();
        } else if (mediaInformation.isCharInPosField008('g', 26)) {
            return MediaTypeEnum.GAME.value();
        } else if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('o', 1)) {
            return MediaTypeEnum.MULTIMEDIA.value();
        }
        return MediaTypeEnum.OTHER.value();
    }
}