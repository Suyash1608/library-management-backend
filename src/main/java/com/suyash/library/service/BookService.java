package com.suyash.library.service;

import com.suyash.library.dto.BookRequest;
import com.suyash.library.dto.BookResponse;

import java.util.List;

public interface BookService {
    BookResponse addBook(BookRequest request);
    BookResponse updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
    BookResponse getBookById(Long id);
    BookResponse getBookByIsbn(String isbn);
    List<BookResponse> getAllBooks();
    List<BookResponse> getAvailableBooks();
    List<BookResponse> searchBooks(String keyword);
    List<BookResponse> getBooksByGenre(String genre);
}
