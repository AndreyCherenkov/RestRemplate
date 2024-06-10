package ru.andreycherenkov.servlet.dto;

import java.util.List;

public record OutgoingAuthorDto(Long id,
                                String firstName,
                                String lastName,
                                List<Long> bookIds){
}
