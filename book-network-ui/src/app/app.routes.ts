import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { HomeComponent } from './pages/home/home';
import { ActivateAccount } from './pages/activate-account/activate-account';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'activate-account', component: ActivateAccount },
  { path: 'token', redirectTo: 'activate-account', pathMatch: 'full' },
  { path: 'admin', loadComponent: () => import('./pages/admin/admin-dashboard').then(m => m.AdminDashboard) },
  { path: 'books', loadChildren: () => import('./pages/book/book.routes').then(m => m.BOOK_ROUTES) }
];
