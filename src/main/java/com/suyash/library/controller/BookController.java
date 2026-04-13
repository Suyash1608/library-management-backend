package com.suyash.library.controller;

import com.suyash.library.dto.*;
import com.suyash.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // Public endpoints
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAvailableBooks() {
        return ResponseEntity.ok(ApiResponse.success("Available books", bookService.getAvailableBooks()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchBooks(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success("Search results", bookService.searchBooks(keyword)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Book found", bookService.getBookById(id)));
    }

    // Authenticated endpoints
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        return ResponseEntity.ok(ApiResponse.success("All books", bookService.getAllBooks()));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(ApiResponse.success("Books by genre", bookService.getBooksByGenre(genre)));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<ApiResponse<BookResponse>> getByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(ApiResponse.success("Book found", bookService.getBookByIsbn(isbn)));
    }

    // Librarian-only endpoints
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponse>> addBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book added", bookService.addBook(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(@PathVariable Long id,
                                                                 @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Book updated", bookService.updateBook(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted", null));
    }
}
