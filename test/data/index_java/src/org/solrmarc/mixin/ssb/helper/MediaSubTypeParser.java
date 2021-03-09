package org.solrmarc.mixin.ssb.helper;

import org.marc4j.marc.DataField;
import org.marc4j.marc.VariableField;

import java.util.List;

import org.solrmarc.mixin.ssb.helper.MediaTypeInformation;
import org.solrmarc.mixin.ssb.helper.MediaTypeEnum;

public class MediaSubTypeParser {
    public String parseFieldA(MediaTypeInformation mediaInformation) {
        if (mediaInformation.getLeader().charAt(7) == 's') {
            if (mediaInformation.isCharInPosField008('n', 21) ) {
                return MediaTypeEnum.NEWS_PAPER.value();
            }
            if (mediaInformation.isCharInPosField008('p', 21)) {
                return MediaTypeEnum.JOURNAL.value();
            }
        }
        if ((mediaInformation.getLeader().charAt(7) == 's' || mediaInformation.getLeader().charAt(7) == 'm') && mediaInformation.isCharInPosField008('y', 24)) {
            return MediaTypeEnum.YEARBOOK.value();
        }
        if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('r', 1)) {
            return parseEBook(mediaInformation);
        }
        if (mediaInformation.isCharInPosField007('f', 0) || mediaInformation.isCharInPosField008('f', 23)) {
            return MediaTypeEnum.BRAILLE.value();
        }
        if (mediaInformation.isCharInPosField008('m', 21) && (mediaInformation.getLeader().charAt(7) == 'b' || mediaInformation.getLeader().charAt(7) == 'i' || mediaInformation.getLeader().charAt(7) == 's') ) {
            return MediaTypeEnum.SERIAL.value();
        }
        if (mediaInformation.getLeader().charAt(7) == 'm' && mediaInformation.isLargeScale()) {
            return MediaTypeEnum.LARGE_SCALE.value();
        }
        if (mediaInformation.isCharInPosField008('m', 24)) {
            return MediaTypeEnum.THESIS.value();
        }
        if (mediaInformation.isPocket()) {
            return MediaTypeEnum.POCKET.value();
        }
        if (mediaInformation.isEasyToRead()) {
            return MediaTypeEnum.BOOK_EASY_TO_READ.value();
        }
        return MediaTypeEnum.BOOK.value();
    }

    public String parseFieldI(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('r', 1) && mediaInformation.isEasyToRead()) {
            return MediaTypeEnum.E_AUDIO_BOOK_EASY_TO_READ.value();
        }
        boolean isEAudioBook = mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('r', 1);
        boolean isEAudioBookAndDaisy = (mediaInformation.isCharInPosField007('s', 0) && mediaInformation.isCharInPosField007('d', 1));
        if (isEAudioBook) {
            return MediaTypeEnum.E_AUDIO_BOOK.value();
        }
        if (isDaisy(mediaInformation.getField500()) && (isEAudioBook || isEAudioBookAndDaisy)) {
            return MediaTypeEnum.AUDIO_BOOK_DAISY.value();
        }
        if (mediaInformation.isCharInPosField007('s', 0) && mediaInformation.isCharInPosField007('d', 1)) {
            if (mediaInformation.isMp3()) {
                return MediaTypeEnum.AUDIO_BOOK_MP3.value();
            }
            return MediaTypeEnum.AUDIO_BOOK_CD.value();
        }
        if (mediaInformation.isCharInPosField007('c', 0) &&
                (mediaInformation.isCharInPosField007('o', 1) || mediaInformation.isCharInPosField007('d', 1)) &&
                isDaisy(mediaInformation.getField500())) {
            return MediaTypeEnum.AUDIO_BOOK_DAISY.value();
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
        } else if (mediaInformation.isCharInPosField007('v', 0)) {
            if(mediaInformation.isCharInPosField007('v', 4)) {
                return MediaTypeEnum.FILM_DVD.value();
            }
            if(mediaInformation.isCharInPosField007('b', 4)) {
                return MediaTypeEnum.FILM_VHS.value();
            }
            if(mediaInformation.isCharInPosField007('s', 4)) {
                return MediaTypeEnum.FILM_BLURAY.value();
            }
        }
        return MediaTypeEnum.OTHER.value();
    }

    public String parseFieldJ(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('s', 0)
                && mediaInformation.isCharInPosField007('d', 1)
                && ( mediaInformation.isCharInPosField007('p', 10)
                ||   mediaInformation.isCharInPosField007('r', 10) ) ) {
            return MediaTypeEnum.VINYL.value();
        }
        if (mediaInformation.isCharInPosField007('s', 0)
                && mediaInformation.isCharInPosField007('d', 1)) {
            return MediaTypeEnum.CD.value();
        }
        return MediaTypeEnum.OTHER.value();
    }

    public String parseFieldM(MediaTypeInformation mediaInformation) {
        if (mediaInformation.isCharInPosField007('c', 0) &&
                mediaInformation.isCharInPosField007('o', 1) &&
                isDaisy(mediaInformation.getField500())) {
            return MediaTypeEnum.AUDIO_BOOK_DAISY_TEXT.value();
        }
        if (mediaInformation.isCharInPosField008('g', 26)) {
            if (mediaInformation.isAnnouncedGame("Systemkrav: Playstation")) {
                return MediaTypeEnum.GAMES_PS.value();
            }
            if (mediaInformation.isAnnouncedGame("Systemkrav: XBox")) {
                return MediaTypeEnum.GAMES_XBOX.value();
            }
            return MediaTypeEnum.GAMES_COMPUTER.value();
        } else if (mediaInformation.isCharInPosField007('c', 0) && mediaInformation.isCharInPosField007('o', 1)) {
            return MediaTypeEnum.MULTIMEDIA.value();
        }
        return MediaTypeEnum.OTHER.value();
    }

    private String parseEBook(MediaTypeInformation mediaInformation) {
        String pdf = "online pdf med adobe-kryptering";
        String epub = "online epub";
        if (mediaInformation.isAnnouncedMedia(pdf) && mediaInformation.isEasyToRead()) {
            return MediaTypeEnum.E_BOOK_PDF_EASY_TO_READ.value();
        }
        if (mediaInformation.isAnnouncedMedia(pdf)) {
            return MediaTypeEnum.E_BOOK_PDF.value();
        }
        if (mediaInformation.isAnnouncedMedia(epub) && mediaInformation.isEasyToRead()) {
            return MediaTypeEnum.E_BOOK_EPUB_EASY_TO_READ.value();
        }
        if (mediaInformation.isAnnouncedMedia(epub)) {
            return MediaTypeEnum.E_BOOK_EPUB.value();
        }
        return MediaTypeEnum.E_BOOK.value();
    }
}
