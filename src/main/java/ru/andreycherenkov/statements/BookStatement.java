package ru.andreycherenkov.statements;

public enum BookStatement {
    BOOK_ID("book_id"),
    TITLE("title"),
    ISBN("isbn"),
    PUBLICATION_YEAR("publication_year"),

    SELECT_JOIN_BOOK("""
                            SELECT b.book_id, b.title, b.isbn, b.publication_year,
                            g.genre_id, g.name,
                            a.author_id, a.first_name, a.last_name
                            FROM books b
                            JOIN genres g ON b.genre_id = g.genre_id
                            JOIN author_book ba ON b.book_id = ba.book_id
                            JOIN authors a ON ba.author_id = a.author_id
                            WHERE b.book_id = ?
                            """),
    SELECT_BOOK("""
                            SELECT *
                            FROM books
                            WHERE book_id = ?
                            """),
    DELETE_AUTHOR_BOOK("""
                            DELETE FROM author_book WHERE book_id = ?
                            """),
    DELETE_BOOK("""
                            DELETE FROM books WHERE book_id = ?
                            """),
    FIND_ALL("""
                            SELECT b.book_id, b.title, b.isbn, b.publication_year,
                            g.genre_id, g.name,
                            a.author_id, a.first_name, a.last_name
                            FROM books b
                            LEFT JOIN genres g ON b.genre_id = g.genre_id
                            LEFT JOIN author_book ba ON b.book_id = ba.book_id
                            LEFT JOIN authors a ON ba.author_id = a.author_id
                            """),
    INSERT_INTO_BOOK("""
                            INSERT INTO books (title, isbn, publication_year, genre_id) VALUES (?, ?, ?, ?)
                            """),
    INSERT_INTO_AUTHOR_BOOK("""
                            INSERT INTO author_book (book_id, author_id) VALUES (?, ?)
                            """),
    UPDATE_BOOK("""
                            UPDATE books SET title = ?, isbn = ?, publication_year = ?, genre_id = ? WHERE book_id = ?
                            """)
    ;

    private String value;

    BookStatement(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
