import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Menu } from '../../../components/menu/menu';
import { ApiConfiguration } from '../../../services/api-configuration';
import { BorrowedBookResponse } from '../../../services/models/borrowed-book-response';
import { PageResponseBorrowedBookResponse } from '../../../services/models/page-response-borrowed-book-response';
import { findAllReturnedBooks } from '../../../services/fn/book-transaction-history/find-all-returned-books';
import { approveReturnBorrowedBook } from '../../../services/fn/book-transaction-history/approve-return-borrowed-book';

@Component({
  selector: 'app-returned-books',
  standalone: true,
  imports: [
    Menu,
    CommonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './returned-books.html',
  styleUrl: './returned-books.scss'
})
export class ReturnedBooks implements OnInit {
  returnedBooksResponse: PageResponseBorrowedBookResponse = {};
  page = 0;
  size = 10;
  loading = signal<boolean>(true);
  approving = signal<boolean>(false);

  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  showToast = signal<boolean>(false);

  // Approval Modal State
  selectedBookToApprove = signal<BorrowedBookResponse | null>(null);
  showApproveModal = signal<boolean>(false);

  constructor(
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration
  ) {}

  ngOnInit(): void {
    this.fetchReturnedBooks();
  }

  fetchReturnedBooks() {
    this.loading.set(true);
    this.errorMessage.set('');
    findAllReturnedBooks(this.http, this.apiConfig.rootUrl, { page: this.page, size: this.size })
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          this.returnedBooksResponse = res.body || {};
        },
        error: (err) => {
          this.loading.set(false);
          console.error('Failed to load returned books', err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Could not load returned books.');
        }
      });
  }

  goToPage(page: number) {
    this.page = page;
    this.fetchReturnedBooks();
  }

  openApproveModal(book: BorrowedBookResponse) {
    this.selectedBookToApprove.set(book);
    this.showApproveModal.set(true);
  }

  closeApproveModal() {
    this.showApproveModal.set(false);
    this.selectedBookToApprove.set(null);
  }

  confirmApprove() {
    const book = this.selectedBookToApprove();
    if (!book || !book.bookId) return;

    this.approving.set(true);
    this.errorMessage.set('');

    approveReturnBorrowedBook(this.http, this.apiConfig.rootUrl, { bookId: book.bookId })
      .subscribe({
        next: () => {
          this.approving.set(false);
          this.closeApproveModal();
          this.successMessage.set(`Return of "${book.title}" approved successfully! The book is now available in your library again.`);
          this.showToast.set(true);
          this.fetchReturnedBooks();
          setTimeout(() => this.showToast.set(false), 4000);
        },
        error: (err) => {
          this.approving.set(false);
          this.closeApproveModal();
          console.error(err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to approve return.');
        }
      });
  }

  viewDetails(bookId?: number) {
    if (bookId) {
      this.router.navigate(['books', bookId]).catch(err => console.error(err));
    }
  }
}
