import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Menu } from '../../../components/menu/menu';
import { ApiConfiguration } from '../../../services/api-configuration';
import { BorrowedBookResponse } from '../../../services/models/borrowed-book-response';
import { PageResponseBorrowedBookResponse } from '../../../services/models/page-response-borrowed-book-response';
import { findAllBorrowedBooks } from '../../../services/fn/book-transaction-history/find-all-borrowed-books';
import { returnBorrowedBook } from '../../../services/fn/book-transaction-history/return-borrowed-book';
import { saveFeedback } from '../../../services/fn/feedback/save-feedback';

@Component({
  selector: 'app-borrowed-books',
  standalone: true,
  imports: [
    Menu,
    CommonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './borrowed-books.html',
  styleUrl: './borrowed-books.scss'
})
export class BorrowedBooks implements OnInit {
  borrowedBooksResponse: PageResponseBorrowedBookResponse = {};
  page = 0;
  size = 10;
  loading = signal<boolean>(true);
  returning = signal<boolean>(false);

  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  showToast = signal<boolean>(false);

  // Return Book Confirmation Modal
  selectedBookToReturn = signal<BorrowedBookResponse | null>(null);
  showReturnModal = signal<boolean>(false);

  // Give Feedback Modal
  selectedBookForFeedback = signal<BorrowedBookResponse | null>(null);
  showFeedbackModal = signal<boolean>(false);
  submittingFeedback = signal<boolean>(false);
  feedbackRating = signal<number>(5);
  feedbackComment = signal<string>('');

  constructor(
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration
  ) {}

  ngOnInit(): void {
    this.fetchBorrowedBooks();
  }

  fetchBorrowedBooks() {
    this.loading.set(true);
    this.errorMessage.set('');
    findAllBorrowedBooks(this.http, this.apiConfig.rootUrl, { page: this.page, size: this.size })
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          this.borrowedBooksResponse = res.body || {};
        },
        error: (err) => {
          this.loading.set(false);
          console.error('Failed to load borrowed books', err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Could not load your borrowed books.');
        }
      });
  }

  goToPage(page: number) {
    this.page = page;
    this.fetchBorrowedBooks();
  }

  openReturnModal(book: BorrowedBookResponse) {
    this.selectedBookToReturn.set(book);
    this.showReturnModal.set(true);
  }

  closeReturnModal() {
    this.showReturnModal.set(false);
    this.selectedBookToReturn.set(null);
  }

  confirmReturn() {
    const book = this.selectedBookToReturn();
    if (!book || !book.bookId) return;

    this.returning.set(true);
    this.errorMessage.set('');

    returnBorrowedBook(this.http, this.apiConfig.rootUrl, { bookId: book.bookId })
      .subscribe({
        next: () => {
          this.returning.set(false);
          this.closeReturnModal();
          this.successMessage.set(`"${book.title}" marked as returned! Waiting for owner's approval.`);
          this.showToast.set(true);
          this.fetchBorrowedBooks();
          setTimeout(() => this.showToast.set(false), 4000);
        },
        error: (err) => {
          this.returning.set(false);
          this.closeReturnModal();
          console.error(err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to return the book.');
        }
      });
  }

  openFeedbackModal(book: BorrowedBookResponse) {
    this.selectedBookForFeedback.set(book);
    this.feedbackRating.set(5);
    this.feedbackComment.set('');
    this.showFeedbackModal.set(true);
  }

  closeFeedbackModal() {
    this.showFeedbackModal.set(false);
    this.selectedBookForFeedback.set(null);
  }

  submitFeedback() {
    const book = this.selectedBookForFeedback();
    if (!book || !book.bookId || !this.feedbackComment().trim()) return;

    this.submittingFeedback.set(true);
    this.errorMessage.set('');

    saveFeedback(this.http, this.apiConfig.rootUrl, {
      body: {
        bookId: book.bookId,
        note: this.feedbackRating(),
        comment: this.feedbackComment().trim()
      }
    }).subscribe({
      next: () => {
        this.submittingFeedback.set(false);
        this.closeFeedbackModal();
        this.successMessage.set('Feedback submitted successfully! Thank you.');
        this.showToast.set(true);
        setTimeout(() => this.showToast.set(false), 3500);
      },
      error: (err) => {
        this.submittingFeedback.set(false);
        console.error(err);
        this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to submit feedback.');
      }
    });
  }

  viewDetails(bookId?: number) {
    if (bookId) {
      this.router.navigate(['books', bookId]).catch(err => console.error(err));
    }
  }
}
