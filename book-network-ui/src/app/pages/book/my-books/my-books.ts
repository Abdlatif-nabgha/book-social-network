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
    updateShareableStatus(this.http, this.apiConfig.rootUrl, { bookId: book.id })
      .subscribe({
        next: () => {
          book.shareable = !book.shareable;
        },
        error: (err) => {
          console.error('Failed to update shareable status', err);
        }
      });
  }
}
