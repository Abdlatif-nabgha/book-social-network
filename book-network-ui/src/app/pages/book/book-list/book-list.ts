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
import { Token } from '../../../services/token/token';
import { ToastrService } from 'ngx-toastr';

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
  size = 8;
  errorMessage = signal<string>('');
  loading = signal<boolean>(true);

  constructor(
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration,
    public tokenService: Token,
    private toastr: ToastrService
  ) {}

  isOwner(book: BookResponse): boolean {
    if (this.tokenService.userId && book.ownerId) {
      return this.tokenService.userId === book.ownerId;
    }
    return !!(this.tokenService.userFullName && book.ownerName === this.tokenService.userFullName);
  }

  manageBook(book: BookResponse, event?: Event) {
    if (event) event.stopPropagation();
    this.router.navigate(['books/manage', book.id]).catch(err => console.error(err));
  }

  viewDetails(book: BookResponse) {
    if (book.id) {
      this.router.navigate(['books', book.id]).catch(err => console.error(err));
    }
  }

  ngOnInit() {
    this.fetchDisplayableBooks();
  }

  fetchDisplayableBooks() {
    this.loading.set(true);
    this.errorMessage.set('');
    findAllDisplayable(this.http, this.apiConfig.rootUrl, { page: this.page, size: this.size })
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          this.bookResponse = res.body || {};
        },
        error: (err) => {
          this.loading.set(false);
          console.error(err);
          const msg = err.error?.error || err.error?.message || 'Failed to load books. Please check your connection.';
          this.errorMessage.set(msg);
        }
      });
  }

  goToPage(page: number) {
    this.page = page;
    this.fetchDisplayableBooks();
  }

  borrow(book: BookResponse, event?: Event) {
    if (event) event.stopPropagation();
    if (!book.id) return;
    this.errorMessage.set('');
    
    borrowBook(this.http, this.apiConfig.rootUrl, { bookId: book.id })
      .subscribe({
        next: () => {
          this.toastr.success(`Successfully borrowed "${book.title}"!`, 'Success');
          this.fetchDisplayableBooks(); // Refresh list
        },
        error: (err) => {
          console.error(err);
          const errorMsg = err.error?.error || err.error?.message || `Could not borrow "${book.title}". It might be already borrowed.`;
          this.errorMessage.set(errorMsg);
          this.toastr.error(errorMsg, 'Borrow Failed');
        }
      });
  }
}
