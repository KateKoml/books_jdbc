insert into public.authors (id, full_name, year_of_birth)
values  (1, 'Lewis Carroll', 1832),
        (2, 'Stephen King', 1947);


insert into public.books (id, name, year, author_id)
values  (1, 'Alice in Wonderland', 1865, 1),
        (2, 'Through the Looking-Glass, and What Alice Found There', 1871, 1),
        (3, 'Carrie', 1974, 2),
        (5, 'The Shining', 1977, 2);


insert into public.genres (id, type)
values  (1, 'fantasy'),
        (2, 'horror'),
        (3, 'romance');


insert into public.l_books_genres (id, book_id, genre_id)
values  (1, 1, 1),
        (2, 2, 1),
        (3, 3, 2),
        (4, 5, 2),
        (5, 5, 3);