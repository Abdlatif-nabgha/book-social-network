import { Routes } from '@angular/router';
import { BookList } from './book-list/book-list';
import { BookDetails } from './book-details/book-details';
import { MyBooks } from './my-books/my-books';
import { ManageBook } from './manage-book/manage-book';
import { BorrowedBooks } from './borrowed-books/borrowed-books';
import { ReturnedBooks } from './returned-books/returned-books';

export const BOOK_ROUTES: Routes = [
  { path: '', component: BookList },
  { path: 'my-books', component: MyBooks },
  { path: 'borrowed', component: BorrowedBooks },
  { path: 'returned', component: ReturnedBooks },
  { path: 'manage', component: ManageBook },
  { path: 'manage/:id', component: ManageBook },
  { path: 'admin', loadComponent: () => import('../admin/admin-dashboard').then(m => m.AdminDashboard) },
  { path: ':id', component: BookDetails }
];

