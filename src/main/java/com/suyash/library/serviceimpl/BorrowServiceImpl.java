package com.suyash.library.serviceimpl;

import com.suyash.library.dto.BorrowResponse;
import com.suyash.library.exception.BookNotAvailableException;
import com.suyash.library.exception.ResourceNotFoundException;
import com.suyash.library.model.*;
import com.suyash.library.repository.*;
import com.suyash.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Value("${library.max.borrow.limit:3}")
    private int maxBorrowLimit;

    @Value("${library.borrow.duration.days:14}")
    private int borrowDurationDays;

    // Fine per overdue day in rupees
    private static final double FINE_PER_DAY = 5.0;

    @Override
    @Transactional
    public BorrowResponse borrowBook(Long bookId, String memberEmail) {
        Member member = getMemberOrThrow(memberEmail);

        // Check member borrow limit
        int activeBorrows = borrowRecordRepository.countActiveBorrowsByMember(member);
        if (activeBorrows >= maxBorrowLimit) {
            throw new IllegalArgumentException(
                "Borrow limit reached. You can borrow at most " + maxBorrowLimit + " books at a time.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies available for: " + book.getTitle());
        }

        // Check if member already has this book borrowed
        borrowRecordRepository.findByMemberAndBookIdAndStatus(member, bookId, BorrowStatus.BORROWED)
                .ifPresent(r -> { throw new IllegalArgumentException("You already have this book borrowed"); });

        // Decrement available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        BorrowRecord record = BorrowRecord.builder()
                .member(member)
                .book(book)
                .borrowDate(today)
                .dueDate(today.plusDays(borrowDurationDays))
                .status(BorrowStatus.BORROWED)
                .fine(0.0)
                .build();

        return mapToResponse(borrowRecordRepository.save(record));
    }

    @Override
    @Transactional
    public BorrowResponse returnBook(Long borrowRecordId, String memberEmail) {
        Member member = getMemberOrThrow(memberEmail);

        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found: " + borrowRecordId));

        if (!record.getMember().getEmail().equals(memberEmail)) {
            throw new IllegalArgumentException("This borrow record does not belong to you");
        }

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new IllegalArgumentException("Book has already been returned");
        }

        LocalDate today = LocalDate.now();
        double fine = 0.0;

        // Calculate fine if overdue
        if (today.isAfter(record.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), today);
            fine = overdueDays * FINE_PER_DAY;
        }

        // Return the copy
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        record.setReturnDate(today);
        record.setStatus(BorrowStatus.RETURNED);
        record.setFine(fine);

        return mapToResponse(borrowRecordRepository.save(record));
    }

    @Override
    public List<BorrowResponse> getMyBorrowHistory(String memberEmail) {
        Member member = getMemberOrThrow(memberEmail);
        return borrowRecordRepository.findByMember(member)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BorrowResponse> getMyActiveborrows(String memberEmail) {
        Member member = getMemberOrThrow(memberEmail);
        return borrowRecordRepository.findByMemberAndStatus(member, BorrowStatus.BORROWED)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BorrowResponse> getAllBorrowRecords() {
        return borrowRecordRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BorrowResponse> getOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markOverdueRecords() {
        List<BorrowRecord> overdue = borrowRecordRepository.findOverdueRecords(LocalDate.now());
        overdue.forEach(r -> {
            r.setStatus(BorrowStatus.OVERDUE);
            long overdueDays = ChronoUnit.DAYS.between(r.getDueDate(), LocalDate.now());
            r.setFine(overdueDays * FINE_PER_DAY);
        });
        borrowRecordRepository.saveAll(overdue);
    }

    // ── Helpers ──────────────────────────────────────────

    private Member getMemberOrThrow(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + email));
    }

    private BorrowResponse mapToResponse(BorrowRecord r) {
        return BorrowResponse.builder()
                .id(r.getId())
                .bookTitle(r.getBook().getTitle())
                .bookAuthor(r.getBook().getAuthor())
                .bookIsbn(r.getBook().getIsbn())
                .memberName(r.getMember().getName())
                .memberEmail(r.getMember().getEmail())
                .borrowDate(r.getBorrowDate())
                .dueDate(r.getDueDate())
                .returnDate(r.getReturnDate())
                .status(r.getStatus())
                .fine(r.getFine())
                .build();
    }
}
