package ru.andreycherenkov.servlet.dto;

import java.util.List;

public record OutgoingBookDto(Long id,
                              String title,
                              String isbn,
                              int publicationYear,
                              Long genre_id,
                              List<Long> authorIds) {
}
