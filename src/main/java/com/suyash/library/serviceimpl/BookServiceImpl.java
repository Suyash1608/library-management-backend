package com.suyash.library.serviceimpl;

import com.suyash.library.dto.BookRequest;
import com.suyash.library.dto.BookResponse;
import com.suyash.library.exception.ResourceNotFoundException;
import com.suyash.library.model.Book;
import com.suyash.library.repository.BookRepository;
import com.suyash.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookResponse addBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN already exists: " + request.getIsbn());
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .genre(request.getGenre())
                .publisher(request.getPublisher())
                .publishedYear(request.getPublishedYear())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .build();

        return mapToResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findOrThrow(id);
        int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();

        if (request.getTotalCopies() < borrowedCopies) {
            throw new IllegalArgumentException(
                "Cannot reduce total copies below number of currently borrowed copies: " + borrowedCopies);
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setGenre(request.getGenre());
        book.setPublisher(request.getPublisher());
        book.setPublishedYear(request.getPublishedYear());
        int diff = request.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + diff);

        return mapToResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = findOrThrow(id);
        if (!book.getTotalCopies().equals(book.getAvailableCopies())) {
            throw new IllegalArgumentException("Cannot delete book — some copies are currently borrowed");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public BookResponse getBookById(Long id) {
        return mapToResponse(findOrThrow(id));
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        return mapToResponse(bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn)));
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> getBooksByGenre(String genre) {
        return bookRepository.findByGenre(genre)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private Book findOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    public BookResponse mapToResponse(Book b) {
        return BookResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .author(b.getAuthor())
                .isbn(b.getIsbn())
                .genre(b.getGenre())
                .publisher(b.getPublisher())
                .publishedYear(b.getPublishedYear())
                .totalCopies(b.getTotalCopies())
                .availableCopies(b.getAvailableCopies())
                .build();
    }
}
