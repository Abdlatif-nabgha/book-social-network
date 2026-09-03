import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Menu } from '../../../components/menu/menu';
import { ApiConfiguration } from '../../../services/api-configuration';
import { Token } from '../../../services/token/token';
import { BookResponse } from '../../../services/models/book-response';
import { findById } from '../../../services/fn/book/find-by-id';
import { borrowBook } from '../../../services/fn/book-transaction-history/borrow-book';
import { updateShareableStatus } from '../../../services/fn/book/update-shareable-status';
import { updateArchivedStatus } from '../../../services/fn/book/update-archived-status';
import { deleteBook } from '../../../services/fn/book/delete-book';
import { findAllFeedbacksByBook } from '../../../services/fn/feedback/find-all-feedbacks-by-book';
import { saveFeedback } from '../../../services/fn/feedback/save-feedback';
import { PageResponseFeedbackResponse } from '../../../services/models/page-response-feedback-response';

@Component({
  selector: 'app-book-details',
  standalone: true,
  imports: [
    Menu,
    CommonModule,
    FormsModule
  ],
  templateUrl: './book-details.html',
  styleUrl: './book-details.scss'
})
export class BookDetails implements OnInit {
  bookId: number | null = null;
  book = signal<BookResponse | null>(null);
  feedbackResponse = signal<PageResponseFeedbackResponse>({});
  
  loading = signal<boolean>(true);
  loadingFeedbacks = signal<boolean>(false);
  submittingReview = signal<boolean>(false);
  deleting = signal<boolean>(false);
  showDeleteModal = signal<boolean>(false);
  
  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  showToast = signal<boolean>(false);

  // Review form
  newRating = signal<number>(5);
  newComment = signal<string>('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration,
    public tokenService: Token
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.bookId = +id;
        this.fetchBookDetails(this.bookId);
        this.fetchFeedbacks(this.bookId);
      }
    });
  }

  fetchBookDetails(id: number) {
    this.loading.set(true);
    this.errorMessage.set('');
    findById(this.http, this.apiConfig.rootUrl, { bookId: id })
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          this.book.set(res.body?.data || null);
        },
        error: (err) => {
          this.loading.set(false);
          console.error(err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to load book details.');
        }
      });
  }

  fetchFeedbacks(id: number) {
    this.loadingFeedbacks.set(true);
    findAllFeedbacksByBook(this.http, this.apiConfig.rootUrl, { bookId: id, page: 0, size: 10 })
      .subscribe({
        next: (res) => {
          this.loadingFeedbacks.set(false);
          this.feedbackResponse.set(res.body || {});
        },
        error: (err) => {
          this.loadingFeedbacks.set(false);
          console.error('Failed to load feedbacks', err);
        }
      });
  }

  isOwner(): boolean {
    const currentBook = this.book();
    if (!currentBook) return false;
    if (this.tokenService.userId && currentBook.ownerId) {
      return this.tokenService.userId === currentBook.ownerId;
    }
    return !!(this.tokenService.userFullName && currentBook.ownerName === this.tokenService.userFullName);
  }

  borrow() {
    const currentBook = this.book();
    if (!currentBook?.id) return;
    this.errorMessage.set('');
    
    borrowBook(this.http, this.apiConfig.rootUrl, { bookId: currentBook.id })
      .subscribe({
        next: () => {
          this.successMessage.set(`You borrowed "${currentBook.title}" successfully!`);
          this.showToast.set(true);
          setTimeout(() => this.showToast.set(false), 3500);
        },
        error: (err) => {
          console.error(err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Could not borrow this book.');
        }
      });
  }

  toggleShareable() {
    const currentBook = this.book();
    if (!currentBook?.id) return;
    this.errorMessage.set('');

    updateShareableStatus(this.http, this.apiConfig.rootUrl, { bookId: currentBook.id })
      .subscribe({
        next: (res) => {
          if (res.body?.data) {
            this.book.set(res.body.data);
          } else {
            currentBook.shareable = !currentBook.shareable;
            this.book.set({ ...currentBook });
          }
          const updated = this.book();
          this.successMessage.set(updated?.shareable ? 'Book is now shareable in the network.' : 'Book is now private.');
          this.showToast.set(true);
          setTimeout(() => this.showToast.set(false), 3000);
        },
        error: (err) => {
          console.error(err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to update shareable status.');
        }
      });
  }

  toggleArchived() {
    const currentBook = this.book();
    if (!currentBook?.id) return;
    this.errorMessage.set('');

    updateArchivedStatus(this.http, this.apiConfig.rootUrl, { bookId: currentBook.id })
      .subscribe({
        next: (res) => {
          if (res.body?.data) {
            this.book.set(res.body.data);
          } else {
            currentBook.archived = !currentBook.archived;
            this.book.set({ ...currentBook });
          }
          const updated = this.book();
          this.successMessage.set(updated?.archived ? 'Book archived and made private.' : 'Book restored from archive.');
          this.showToast.set(true);
          setTimeout(() => this.showToast.set(false), 3000);
        },
        error: (err) => {
          console.error(err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to update archive status.');
        }
      });
  }

  editBook() {
    if (this.bookId) {
      this.router.navigate(['books/manage', this.bookId]).catch(err => console.error(err));
    }
  }

  openDeleteModal() {
    this.showDeleteModal.set(true);
  }

  closeDeleteModal() {
    this.showDeleteModal.set(false);
  }

  confirmDelete() {
    if (!this.bookId) return;
    this.deleting.set(true);
    this.errorMessage.set('');

    deleteBook(this.http, this.apiConfig.rootUrl, { bookId: this.bookId })
      .subscribe({
        next: () => {
          this.deleting.set(false);
          this.closeDeleteModal();
          this.router.navigate(['books/my-books']).catch(err => console.error(err));
        },
        error: (err) => {
          this.deleting.set(false);
          this.closeDeleteModal();
          console.error(err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to delete book. Please check if it is currently borrowed.');
        }
      });
  }

  submitReview() {
    if (!this.bookId || !this.newComment().trim()) return;
    this.submittingReview.set(true);
    this.errorMessage.set('');

    saveFeedback(this.http, this.apiConfig.rootUrl, {
      body: {
        bookId: this.bookId,
        note: this.newRating(),
        comment: this.newComment().trim()
      }
    }).subscribe({
      next: () => {
        this.submittingReview.set(false);
        this.newComment.set('');
        this.newRating.set(5);
        this.successMessage.set('Review submitted successfully!');
        this.showToast.set(true);
        if (this.bookId) {
          this.fetchFeedbacks(this.bookId);
          this.fetchBookDetails(this.bookId); // Refresh rating score
        }
        setTimeout(() => this.showToast.set(false), 3500);
      },
      error: (err) => {
        this.submittingReview.set(false);
        console.error(err);
        this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to submit review.');
      }
    });
  }

  goBack() {
    this.router.navigate(['/books']).catch(err => console.error(err));
  }
}
