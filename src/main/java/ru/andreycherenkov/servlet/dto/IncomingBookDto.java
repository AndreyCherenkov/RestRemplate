package ru.andreycherenkov.servlet.dto;

import java.util.List;

public record IncomingBookDto(String title,
                              String isbn,
                              int publicationYear,
                              Long genreId,
                              List<Long> authorIds) {
}
