import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { BookResponse } from '../../../services/models/book-response';
import { findAllDisplayable } from '../../../services/fn/book/find-all-displayable';
import { borrowBook } from '../../../services/fn/book-transaction-history/borrow-book';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration } from '../../../services/api-configuration';
import { PageResponseBookResponse } from '../../../services/models/page-response-book-response';
import { Menu } from '../../../components/menu/menu';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [
    Menu,
    CommonModule
  ],
  templateUrl: './book-list.html',
  styleUrl: './book-list.scss',
})
export class BookList implements OnInit {
  bookResponse: PageResponseBookResponse = {};
  page = 0;
  size = 6;
  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  showToast = signal<boolean>(false);

  constructor(
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration
  ) {}

  ngOnInit() {
    this.fetchDisplayableBooks();
  }

  fetchDisplayableBooks() {
    findAllDisplayable(this.http, this.apiConfig.rootUrl, { page: this.page, size: this.size })
      .subscribe({
        next: (res) => {
          this.bookResponse = res.body;
        },
        error: (err) => {
          console.error(err);
          this.errorMessage.set('Failed to load books. Please check your connection.');
        }
      });
  }

  goToPage(page: number) {
    this.page = page;
    this.fetchDisplayableBooks();
  }

  borrow(book: BookResponse) {
    if (!book.id) return;
    this.errorMessage.set('');
    this.successMessage.set('');
    
    borrowBook(this.http, this.apiConfig.rootUrl, { bookId: book.id })
      .subscribe({
        next: () => {
          this.successMessage.set(`Successfully borrowed "${book.title}"!`);
          this.showToast.set(true);
          this.fetchDisplayableBooks(); // Refresh list
          setTimeout(() => this.showToast.set(false), 3500);
        },
        error: (err) => {
          console.error(err);
          if (err.error?.error) {
            this.errorMessage.set(err.error.error);
          } else {
            this.errorMessage.set(`Could not borrow "${book.title}". It might be already borrowed.`);
          }
        }
      });
  }
}
