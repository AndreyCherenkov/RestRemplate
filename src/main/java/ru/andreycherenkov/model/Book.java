package ru.andreycherenkov.model;

import java.util.ArrayList;
import java.util.List;

public class Book {

        private Long id;
        private String title;
        private String isbn;
        private int publicationYear;
        private Genre genre;
        private List<Author> authors;

    public Book() {
        this.genre = new Genre();
        this.authors = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Genre getGenre() {
        return genre;
    }

    public Long getGenreId() {
        return genre.getId();
    }

    public void setGenreId(Long id) {
        this.genre.setId(id);
    }

    public void setGenreName(String name) {
        this.genre.setName(name);
    }

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthor(List<Author> author) {
        this.authors = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publicationYear=" + publicationYear +
                ", genre=" + genre +
                ", authors=" + authors +
                '}';
    }
}
