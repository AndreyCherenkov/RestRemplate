package ru.andreycherenkov.servlet.dto;

import java.util.List;

public record OutgoingGenreDto(Long id,
                               String name,
                               List<Long> bookIds) {
}
