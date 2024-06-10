package ru.andreycherenkov.servlet.dto;

import java.util.List;

public record IncomingAuthorDto(String firstName,
                                String lastName,
                                List<Long> bookIds) {
}
