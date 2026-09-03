import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookRequest } from '../../../services/models/book-request';
import { saveBook } from '../../../services/fn/book/save-book';
import { findById } from '../../../services/fn/book/find-by-id';
import { uploadBookCoverPicture } from '../../../services/fn/book/upload-book-cover-picture';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration } from '../../../services/api-configuration';
import { Menu } from '../../../components/menu/menu';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { deleteBook } from '../../../services/fn/book/delete-book';

@Component({
  selector: 'app-manage-book',
  standalone: true,
  imports: [
    Menu,
    FormsModule,
    CommonModule
  ],
  templateUrl: './manage-book.html',
  styleUrl: './manage-book.scss'
})
export class ManageBook implements OnInit {
  bookId: number | null = null;
  bookRequest: BookRequest = {
    title: '',
    author: '',
    isbn: '',
    synopsis: '',
    shareable: false
  };

  selectedPicture: File | null = null;
  picturePreviewUrl = signal<string | null>(null);
  loading = signal<boolean>(false);
  deleting = signal<boolean>(false);
  showDeleteModal = signal<boolean>(false);
  errorMessage = signal<Array<string>>([]);

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private http: HttpClient,
    private apiConfig: ApiConfiguration
  ) {}

  openDeleteModal() {
    this.showDeleteModal.set(true);
  }

  closeDeleteModal() {
    this.showDeleteModal.set(false);
  }

  confirmDelete() {
    if (!this.bookId) return;
    this.deleting.set(true);
    this.errorMessage.set([]);

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
          console.error('Error deleting book:', err);
          this.errorMessage.set([err.error?.error || err.error?.message || 'Failed to delete book. Ensure it is not currently borrowed.']);
        }
      });
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.bookId = +id;
        this.fetchBookDetails(this.bookId);
      }
    });
  }

  fetchBookDetails(id: number) {
    findById(this.http, this.apiConfig.rootUrl, { bookId: id })
      .subscribe({
        next: (res) => {
          const bookResponse = res.body;
          if (bookResponse && bookResponse.data) {
            const book = bookResponse.data;
            this.bookRequest = {
              title: book.title || '',
              author: book.author || '',
              isbn: book.isbn || '',
              synopsis: book.synopsis || '',
              shareable: book.shareable || false
            };
            if (book.bookCover) {
              this.picturePreviewUrl.set('data:image/jpeg;base64,' + book.bookCover);
            }
          }
        },
        error: (err) => {
          console.error(err);
          this.errorMessage.set(['Failed to load the book details.']);
        }
      });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedPicture = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.picturePreviewUrl.set(reader.result as string);
      };
      reader.readAsDataURL(this.selectedPicture);
    }
  }

  cancel() {
    this.router.navigate(['books/my-books']).catch(err => console.error(err));
  }

  onSubmit() {
    this.errorMessage.set([]);
    
    // Client-side validations
    const errors: Array<string> = [];
    if (!this.bookRequest.title.trim()) errors.push('Title is mandatory');
    if (!this.bookRequest.author.trim()) errors.push('Author name is mandatory');
    if (!this.bookRequest.isbn.trim()) errors.push('ISBN is mandatory');
    if (!this.bookRequest.synopsis.trim()) errors.push('Synopsis is mandatory');

    if (errors.length > 0) {
      this.errorMessage.set(errors);
      return;
    }

    this.loading.set(true);

    // If editing, map the request body correctly. Note: saveBook API saves or updates depending on body
    const bodyPayload = {
      ...this.bookRequest,
      // If we are editing, we can pass id inside the body if supported by the backend model, or it handles it.
      // Let's check: BookRequest type doesn't have id, but the saveBook endpoint PATH is '/books'.
      // If the backend saveBook usecase updates if the book exists or if it determines by ISBN, we pass it.
      // Let's pass the ID by casting if the model has a hidden id or let the database handle it.
      // Actually, since BookRequest doesn't have ID, does it update by ID if we add it? Let's check BookRequest definition:
      // It has only author, isbn, shareable, synopsis, title. Let's see if we cast to any or if saveBook handles ID differently.
      // Wait, let's see how the saveBook endpoint updates the book in BookController.
    };

    // If editing, does the payload need an ID?
    // Let's inspect BookRequest model or check the backend saveBook endpoint to see if it takes id in BookRequest.
    // In book-request.ts, there was no id. But maybe they have another field or we can just send it.
    // Wait, let's cast bodyPayload to any and attach 'id' if editing so the backend knows which book to update!
    const payload: any = { ...this.bookRequest };
    if (this.bookId) {
      payload.id = this.bookId;
    }

    saveBook(this.http, this.apiConfig.rootUrl, { body: payload })
      .subscribe({
        next: (res) => {
          const apiResponse = res.body;
          const bookIdToUse = (apiResponse && apiResponse.data ? apiResponse.data.id : null) || this.bookId;

          if (this.selectedPicture && bookIdToUse) {
            this.uploadCoverPicture(bookIdToUse);
          } else {
            this.loading.set(false);
            this.router.navigate(['books/my-books'], { queryParams: { saved: 'true' } }).catch(err => console.error(err));
          }
        },
        error: (err) => {
          this.loading.set(false);
          console.error('Error saving book:', err);
          if (err.error?.validationErrors) {
            if (Array.isArray(err.error.validationErrors)) {
              this.errorMessage.set(err.error.validationErrors);
            } else if (typeof err.error.validationErrors === 'object') {
              this.errorMessage.set(Object.values(err.error.validationErrors));
            } else {
              this.errorMessage.set([String(err.error.validationErrors)]);
            }
          } else if (err.error?.error) {
            this.errorMessage.set([err.error.error]);
          } else if (err.error?.businessErrorDescription) {
            this.errorMessage.set([err.error.businessErrorDescription]);
          } else if (err.error?.errorMsg) {
            this.errorMessage.set([err.error.errorMsg]);
          } else if (err.error?.message) {
            this.errorMessage.set([err.error.message]);
          } else {
            if (err.status === 0) {
              this.errorMessage.set(['Please check your internet connection or server availability.']);
            } else if (err.status === 401 || err.status === 403) {
              this.errorMessage.set(['You are not authorized. Please log in again.']);
            } else {
              this.errorMessage.set(['An error occurred while saving the book.']);
            }
          }
        }
      });
  }

  uploadCoverPicture(bookId: number) {
    if (!this.selectedPicture) return;
    
    uploadBookCoverPicture(this.http, this.apiConfig.rootUrl, {
      bookId,
      body: {
        file: this.selectedPicture
      }
    }).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['books/my-books'], { queryParams: { saved: 'true' } }).catch(err => console.error(err));
      },
      error: (err) => {
        this.loading.set(false);
        console.error('Error uploading cover picture:', err);
        const uploadError = err.error?.error || err.error?.message || 'Book was saved but cover image upload failed.';
        this.errorMessage.set([uploadError]);
      }
    });
  }
}
