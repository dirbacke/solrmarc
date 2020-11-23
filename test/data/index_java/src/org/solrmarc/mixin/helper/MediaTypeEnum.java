package org.solrmarc.mixin.helper;

public enum MediaTypeEnum {
    AUDIO_BOOK,
    AUDIO_BOOK_DAISY,
    AUDIO_BOOK_DAISY_TEXT,
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

    public String value() {
        return "MediaTypeEnum." + name();
    }
}