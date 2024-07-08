package ru.andreycherenkov.statements;

public enum AuthorStatement {

    AUTHOR_ID("author_id"),
    FIRST_NAME("first_name"),
    LAST_NAME("last_name"),

    SELECT_JOIN_AUTHOR("""
                    SELECT a.author_id, a.first_name, a.last_name, 
                    b.book_id, b.title, b.isbn, b.publication_year
                    FROM authors a
                    JOIN author_book ab ON a.author_id = ab.author_id
                    JOIN books b ON ab.book_id = b.book_id
                    WHERE a.author_id = ?
                    """),
    SELECT_AUTHOR("""
                    SELECT *
                    FROM authors
                    WHERE author_id = ?
                    """),
    DELETE_BOOK("""
                    DELETE FROM author_book WHERE author_id = ?
                    """),
    DELETE_AUTHOR("""
                    DELETE FROM authors WHERE author_id = ?
                    """),
    FIND_ALL("""
                    SELECT DISTINCT a.author_id, a.first_name, a.last_name, b.book_id, b.title, b.isbn, b.publication_year
                    FROM authors a
                    LEFT JOIN author_book ab ON a.author_id = ab.author_id
                    LEFT JOIN books b ON ab.book_id = b.book_id
                    """),
    INSERT_INTO_AUTHORS("""
                    INSERT INTO authors (first_name, last_name) VALUES (?, ?)
                    """),
    INSERT_INTO_AUTHOR_BOOK("""
                    INSERT INTO author_book (book_id, author_id) VALUES (?, ?)
                    """),
    UPDATE_AUTHOR("""
                    UPDATE authors SET first_name = ?, last_name = ? WHERE author_id = ?
                    """);

    private String value;

    AuthorStatement(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
