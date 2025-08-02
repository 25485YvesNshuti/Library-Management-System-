# 📚 Library Management System (LMS)

### 🗓️ Date: 13/07/2025

## 📌 Project Overview

This Library Management System (LMS) is developed to automate and streamline library operations. It helps manage books, users, borrowing, reservations, and fines efficiently. The system ensures proper tracking of books, user activities, and sends timely email/SMS notifications. It minimizes manual workload and improves the overall experience for both library staff and students.

---

## 🎯 System Goals

| Goal ID | Description                                                  |
|---------|--------------------------------------------------------------|
| G1      | Make library operations faster and easier                    |
| G2      | Track book availability and borrowing                        |
| G3      | Allow students to borrow, return, and reserve books          |
| G4      | Prevent book loss and late returns through fines and alerts |
| G5      | Provide role-based access for Admin, Librarian, and Students|

---

## 👥 System Users and Roles

| Role      | Responsibilities                                                                 |
|-----------|----------------------------------------------------------------------------------|
| Admin     | Manage users, set borrowing rules, view reports, and manage categories/settings |
| Librarian | Manage books, handle issue/return/reservation, fines, lost books, and reports   |
| Student   | Search/view books, borrow/return/reserve, report lost books, and pay fines      |

---

## ⚙️ Functional Requirements

### 🔐 Login & Security
- Role-based login: Admin, Librarian, Student
- Secure password storage
- Optional password reset

### 📖 Book Management
- Add, edit, delete book details
- Track available and borrowed copies
- Organize books by categories

### 📚 Borrowing
- Max of 3 books per student
- Borrow period: 14 days
- Record issue and due dates

### 🔄 Returning
- Processed by librarian
- Late returns generate fines
- Updates book status as "available"

### 🔁 Renewal
- One-time renewal allowed
- Extends loan by 7 days
- Requires librarian approval

### 📌 Reservation
- Allowed when book is unavailable
- Queue system: first-come, first-served
- Notification sent when available
- Held for 3 days

### ❗ Lost Book Handling
- Student reports through system or librarian
- Librarian confirms and marks as lost
- Fine applied
- Borrowing re-enabled after payment

### 💰 Fine Management
- Applied for lost/late returns
- Must be cleared to continue borrowing
- Admin/Librarian can waive fines

### 🔍 Search and Filter
- Search by title, author, or category
- Filter by status (available/borrowed)

### ✉️ Notifications (via Email/SMS)
- Due date reminders
- Overdue notices
- Reservation availability
- Lost book fines
- Renewal status updates

### 📊 Reports
- Book inventory
- Issued/Overdue books
- Fine collections
- User activity logs

---

## 🧩 Use Case Diagram

*Use case diagram goes here (describes interactions between users and the system).*
<img width="1024" height="1536" alt="generated-image-cf6338ed-3a0e-4980-b550-6e5568a84e29" src="https://github.com/user-attachments/assets/48e2e891-a43e-4946-98e9-6c507af61381" />

---

## 🔗 ER Diagram

*ER diagram goes here (shows entities like User, Book, Borrowing, Fine, etc. and their relationships).*
<img width="1354" height="778" alt="ERD" src="https://github.com/user-attachments/assets/017cfab5-a039-4f7d-94ec-ea30fd829153" />

---

## 🧠 Summary

This project is ideal for improving efficiency in library environments by minimizing human error, ensuring accountability, and giving clear insights into library operations through detailed reports and notifications. It is designed to benefit students and library staff alike with an intuitive interface and powerful backend logic.

---

