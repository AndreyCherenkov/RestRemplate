-- Создание таблицы "жанры"
CREATE TABLE genres (
                        genre_id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
);

-- Создание таблицы "авторы"
CREATE TABLE authors (
                         author_id INT AUTO_INCREMENT PRIMARY KEY,
                         first_name VARCHAR(255) NOT NULL,
                         last_name VARCHAR(255) NOT NULL
);

-- Создание таблицы "книги"
CREATE TABLE books (
                       book_id INT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       isbn VARCHAR(20) NOT NULL,
                       genre_id INT,
                       FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

-- Создание связывающей таблицы "авторы_книги"
CREATE TABLE author_book (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               author_id INT,
                               book_id INT,
                               FOREIGN KEY (author_id) REFERENCES authors(author_id),
                               FOREIGN KEY (book_id) REFERENCES books(book_id)
);

-- Вставка тестовых данных в таблицу "жанры"
INSERT INTO genres (name) VALUES
                              ('фантастика'),
                              ('детектив'),
                              ('роман'),
                              ('исторический роман'),
                              ('научная фантастика');

-- Вставка тестовых данных в таблицу "авторы"
INSERT INTO authors (first_name, last_name) VALUES
                                                ('Джордж', 'Мартин'),
                                                ('Агата', 'Кристи'),
                                                ('Лев', 'Толстой'),
                                                ('Артур', 'Конан Дойл'),
                                                ('Роберт', 'Хайнлайн');

-- Вставка тестовых данных в таблицу "книги"
INSERT INTO books (title, isbn, genre_id) VALUES
                                                     ('Игра престолов', '123', 1),
                                                     ('Убийство в Восточном экспрессе', '321', 2),
                                                     ('Война и мир', '1213-12', 3),
                                                     ('Собака Баскервилей', '13-1123-1', 2),
                                                     ('Чужак в чужой стране', '0931-133', 5);

-- Вставка тестовых данных в таблицу "авторы_книги"
INSERT INTO author_book (author_id, book_id) VALUES
                                                   (1, 1),
                                                   (2, 2),
                                                   (3, 3),
                                                   (4, 4),
                                                   (5, 5);
