package ru.andreycherenkov.statements;

public enum GenreStatement {

    GENRE_ID("genre_id"),
    NAME("name"),

    SELECT_JOIN_GENRE("""
                        SELECT g.genre_id, g.name, b.book_id
                        FROM genres g
                        JOIN books b ON g.genre_id = b.genre_id
                        WHERE g.genre_id = ?
                        """),
    SELECT_GENRE("""
                        SELECT * FROM genres WHERE genre_id = ?
                        """),
    SELECT_BOOK_ID_FROM_BOOKS("""
                        SELECT book_id FROM books WHERE genre_id = ?
                        """),
    DELETE_BOOK("""
                        DELETE FROM genres WHERE genre_id = ?
                        """),
    FIND_ALL("""
                        SELECT g.genre_id, g.name, b.book_id
                        FROM genres g
                        LEFT JOIN books b ON g.genre_id = b.genre_id
                        """),
    INSERT_GENRE("""
                        INSERT INTO genres (name) VALUES (?)
                        """),
    UPDATE_GENRE("""
                        UPDATE genres SET name = ? WHERE genre_id = ?
                        """);

    private String value;

    GenreStatement(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
