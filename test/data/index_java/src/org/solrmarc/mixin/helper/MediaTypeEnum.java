package org.solrmarc.mixin.helper;

public enum MediaTypeEnum {
    AUDIO_BOOK,
    AUDIO_BOOK_CD,
    AUDIO_BOOK_DAISY,
    AUDIO_BOOK_DAISY_TEXT,
    AUDIO_BOOK_MP3,
    BOOK,
    BOOK_EASY_TO_READ,
    BRAILLE,
    CD,
    COMBINED,
    E_BOOK,
    E_BOOK_EPUB,
    E_BOOK_EPUB_EASY_TO_READ,
    E_BOOK_PDF,
    E_BOOK_PDF_EASY_TO_READ,
    E_AUDIO_BOOK,
    E_AUDIO_BOOK_EASY_TO_READ,
    FILM_BLURAY,
    FILM_DVD,
    FILM_VHS,
    GAME,
    GAMES_COMPUTER,
    GAMES_PS,
    GAMES_XBOX,
    JOURNAL,
    LARGE_SCALE,
    MAGAZINE,
    MAPS,
    MOVIE,
    MUSIC,
    MULTIMEDIA,
    NEWS_PAPER,
    NOTES,
    OBJECTS,
    OTHER,
    POCKET,
    SERIAL,
    STREAMING_MOVIE,
    THESIS,
    VINYL,
    YEARBOOK;

    public String value() {
        return "MediaTypeEnum." + name();
    }
}