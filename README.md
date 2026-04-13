# 📚 Library Management System

A full-featured REST API backend for managing a library — books, members, borrowing, and returns — with automatic fine calculation, overdue tracking, and role-based access control (MEMBER / LIBRARIAN).

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8 |
| Build Tool | Maven |

---

## 📁 Project Structure

```
src/main/java/com/suyash/library/
├── controller/
│   ├── AuthController.java        # Register & Login
│   ├── BookController.java        # Book CRUD + search
│   ├── BorrowController.java      # Borrow & return workflows
│   └── LibrarianController.java   # Member management (LIBRARIAN only)
├── service/                       # Interfaces
├── serviceimpl/                   # Business logic
│   ├── AuthServiceImpl.java
│   ├── BookServiceImpl.java
│   ├── BorrowServiceImpl.java     # Core: borrow limit, fine calc, overdue
│   └── MemberServiceImpl.java
├── repository/                    # JPA repositories with custom queries
├── model/
│   ├── Member.java
│   ├── MemberRole.java            # Enum: MEMBER, LIBRARIAN
│   ├── Book.java                  # With DB indexes on isbn, author, genre
│   ├── BorrowRecord.java
│   └── BorrowStatus.java          # Enum: BORROWED, RETURNED, OVERDUE
├── dto/                           # Request/Response DTOs
├── security/                      # JWT filter, UserDetailsService
├── config/                        # Security configuration
└── exception/                     # Custom exceptions + global handler
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- MySQL 8+
- Maven 3.6+

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Suyash1608/library-management-backend.git
   cd library-management-backend
   ```

2. **Configure MySQL** — update `application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   Server starts at: `http://localhost:8082`

---

## 🔐 Roles

| Role | Permissions |
|------|------------|
| `MEMBER` | Browse books, borrow/return books, view own history |
| `LIBRARIAN` | Full book management, view all borrows, manage members, mark overdue |

---

## 📡 API Endpoints

### Auth
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Register as a new member |
| POST | `/api/auth/login` | Public | Login and get JWT token |

### Books
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/books/available` | Public | List books with available copies |
| GET | `/api/books/search?keyword=` | Public | Search by title/author/genre/ISBN |
| GET | `/api/books/{id}` | Public | Get book details |
| GET | `/api/books` | MEMBER+ | All books |
| GET | `/api/books/genre/{genre}` | MEMBER+ | Filter by genre |
| GET | `/api/books/isbn/{isbn}` | MEMBER+ | Lookup by ISBN |
| POST | `/api/books` | LIBRARIAN | Add new book |
| PUT | `/api/books/{id}` | LIBRARIAN | Update book |
| DELETE | `/api/books/{id}` | LIBRARIAN | Delete book |

### Borrow & Return
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/borrow/{bookId}` | MEMBER+ | Borrow a book |
| PUT | `/api/borrow/return/{borrowRecordId}` | MEMBER+ | Return a book |
| GET | `/api/borrow/my/active` | MEMBER+ | My active borrows |
| GET | `/api/borrow/my/history` | MEMBER+ | My full borrow history |
| GET | `/api/borrow` | LIBRARIAN | All borrow records |
| GET | `/api/borrow/overdue` | LIBRARIAN | All overdue records |
| PUT | `/api/borrow/mark-overdue` | LIBRARIAN | Update overdue statuses + fines |

### Member Management (Librarian)
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/librarian/members` | LIBRARIAN | All members |
| GET | `/api/librarian/members/{id}` | LIBRARIAN | Member by ID |
| PUT | `/api/librarian/members/{id}/role?role=` | LIBRARIAN | Assign role |
| PUT | `/api/librarian/members/{id}/toggle-status` | LIBRARIAN | Activate/deactivate |
| DELETE | `/api/librarian/members/{id}` | LIBRARIAN | Delete member |

---

## 📋 Sample Requests

### Borrow a Book
```
POST /api/borrow/1
Authorization: Bearer <member_token>
```

### Return a Book
```
PUT /api/borrow/return/1
Authorization: Bearer <member_token>
```

Response includes fine amount if returned after due date:
```json
{
  "success": true,
  "message": "Book returned successfully",
  "data": {
    "id": 1,
    "bookTitle": "Clean Code",
    "status": "RETURNED",
    "fine": 25.0
  }
}
```

---

## ✅ Key Features

- **Borrow limit enforcement** — max 3 books at a time (configurable)
- **14-day borrow window** (configurable in application.properties)
- **Automatic fine calculation** — ₹5/day after due date
- **Overdue detection** — query finds and marks all overdue records in one call
- **Duplicate borrow prevention** — can't borrow the same book twice
- **Safe delete** — can't delete a member with active borrows or a book with borrowed copies
- **DB indexes** on `isbn`, `author`, `genre` for fast queries
- **Schema normalization** — normalized tables with FK constraints
- **Transactional workflows** — borrow and return are fully transactional

---

## 📬 Postman Collection

Import `Library-Management-API.postman_collection.json` into Postman. Login requests auto-save tokens.

---

## 👤 Author

**Suyash Gupta** — Java Backend Developer  
[LinkedIn](https://linkedin.com/in/suyash-16d08m/) | [GitHub](https://github.com/Suyash1608)
