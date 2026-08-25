import { Routes } from '@angular/router';
import { BookList } from './book-list/book-list';
import { BookDetails } from './book-details/book-details';
import { MyBooks } from './my-books/my-books';
import { ManageBook } from './manage-book/manage-book';

export const BOOK_ROUTES: Routes = [
  { path: '', component: BookList },
  { path: 'my-books', component: MyBooks },
  { path: 'manage', component: ManageBook },
  { path: 'manage/:id', component: ManageBook },
  { path: ':id', component: BookDetails }
];
