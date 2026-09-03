import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookResponse } from '../../../services/models/book-response';
import { findAllByOwner } from '../../../services/fn/book/find-all-by-owner';
import { updateShareableStatus } from '../../../services/fn/book/update-shareable-status';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration } from '../../../services/api-configuration';
import { PageResponseBookResponse } from '../../../services/models/page-response-book-response';
import { Menu } from '../../../components/menu/menu';
import { CommonModule } from '@angular/common';

import { updateArchivedStatus } from '../../../services/fn/book/update-archived-status';
import { deleteBook } from '../../../services/fn/book/delete-book';

@Component({
  selector: 'app-my-books',
  standalone: true,
  imports: [
    Menu,
    CommonModule
  ],
  templateUrl: './my-books.html',
  styleUrl: './my-books.scss'
})
export class MyBooks implements OnInit {
  bookResponse: PageResponseBookResponse = {};
  page = 0;
  size = 6;
  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  showToast = signal<boolean>(false);
  loading = signal<boolean>(true);

  // Delete modal state
  showDeleteModal = signal<boolean>(false);
  bookToDelete = signal<BookResponse | null>(null);
  deleting = signal<boolean>(false);

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private http: HttpClient,
    private apiConfig: ApiConfiguration
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['saved']) {
        this.successMessage.set('Book saved successfully!');
        this.showToast.set(true);
        setTimeout(() => this.showToast.set(false), 4000);
      }
    });
    this.fetchUserBooks();
  }

  fetchUserBooks() {
    this.loading.set(true);
    this.errorMessage.set('');
    findAllByOwner(this.http, this.apiConfig.rootUrl, { page: this.page, size: this.size })
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          this.bookResponse = res.body || {};
        },
        error: (err) => {
          this.loading.set(false);
          console.error(err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Could not load your books.');
        }
      });
  }

  goToPage(page: number) {
    this.page = page;
    this.fetchUserBooks();
  }

  shareBook() {
    this.router.navigate(['books/manage']).catch(err => console.error(err));
  }

  editBook(book: BookResponse) {
    this.router.navigate(['books/manage', book.id]).catch(err => console.error(err));
  }

  toggleShareable(book: BookResponse) {
    if (!book.id) return;
    this.errorMessage.set('');
    updateShareableStatus(this.http, this.apiConfig.rootUrl, { bookId: book.id })
      .subscribe({
        next: () => {
          book.shareable = !book.shareable;
          this.successMessage.set(book.shareable ? `"${book.title}" is now shareable.` : `"${book.title}" is now private.`);
          this.showToast.set(true);
          setTimeout(() => this.showToast.set(false), 3000);
        },
        error: (err) => {
          console.error('Failed to update shareable status', err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to update shareable status.');
        }
      });
  }

  toggleArchived(book: BookResponse) {
    if (!book.id) return;
    this.errorMessage.set('');
    updateArchivedStatus(this.http, this.apiConfig.rootUrl, { bookId: book.id })
      .subscribe({
        next: () => {
          book.archived = !book.archived;
          this.successMessage.set(book.archived ? `"${book.title}" has been archived.` : `"${book.title}" restored from archive.`);
          this.showToast.set(true);
          setTimeout(() => this.showToast.set(false), 3000);
        },
        error: (err) => {
          console.error('Failed to update archive status', err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to update archive status.');
        }
      });
  }

  openDeleteModal(book: BookResponse) {
    this.bookToDelete.set(book);
    this.showDeleteModal.set(true);
  }

  closeDeleteModal() {
    this.showDeleteModal.set(false);
    this.bookToDelete.set(null);
  }

  confirmDelete() {
    const book = this.bookToDelete();
    if (!book?.id) return;
    this.deleting.set(true);
    this.errorMessage.set('');

    deleteBook(this.http, this.apiConfig.rootUrl, { bookId: book.id })
      .subscribe({
        next: () => {
          this.deleting.set(false);
          this.closeDeleteModal();
          this.successMessage.set(`"${book.title}" was permanently deleted.`);
          this.showToast.set(true);
          this.fetchUserBooks();
          setTimeout(() => this.showToast.set(false), 3500);
        },
        error: (err) => {
          this.deleting.set(false);
          this.closeDeleteModal();
          console.error('Failed to delete book', err);
          this.errorMessage.set(err.error?.error || err.error?.message || 'Failed to delete book. Please ensure it is not currently borrowed.');
        }
      });
  }
}
