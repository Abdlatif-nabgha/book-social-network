import { Component, signal } from '@angular/core';
import { RegisterRequest } from '../../services/models/register-request';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { register } from '../../services/fn/authentication/register';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration } from '../../services/api-configuration';
import { timeout } from 'rxjs/operators';
import { ValidationUtils } from '../../services/utils/validation-utils';

@Component({
  selector: 'app-register',
  imports: [
    FormsModule
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  registerRequest: RegisterRequest = {email: '', firstName: '', lastName: '', password: ''};
  errorMessage = signal<Array<string>>([]);
  successMessage = signal<string>('');
  showPassword = signal<boolean>(false);
  loading = signal<boolean>(false);
  showToast = signal<boolean>(false);

  constructor(
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration
  ) {}

  protected submitRegister() {
    this.errorMessage.set([]);
    this.successMessage.set('');

    // Client side validation
    const errors: Array<string> = [];
    if (!this.registerRequest.firstName) {
      errors.push('First name is mandatory');
    }
    if (!this.registerRequest.lastName) {
      errors.push('Last name is mandatory');
    }
    if (!this.registerRequest.email) {
      errors.push('Email is mandatory');
    } else if (!ValidationUtils.isValidEmail(this.registerRequest.email)) {
      errors.push('Please enter a valid email format');
    }
    if (!this.registerRequest.password) {
      errors.push('Password is mandatory');
    } else if (this.registerRequest.password.length < 8) {
      errors.push('Password must be at least 8 characters long');
    }

    if (errors.length > 0) {
      this.errorMessage.set(errors);
      return;
    }

    this.loading.set(true);

    register(this.http, this.apiConfig.rootUrl, { body: this.registerRequest })
      .pipe(timeout(5000))
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          console.log('Registration successful:', res);
          this.successMessage.set('Registration successful — we sent you a code to verify your account.');
          this.showToast.set(true);
          // Auto-redirect after a short delay so the user sees the success message
          setTimeout(() => {
            this.showToast.set(false);
            this.router.navigate(['activate-account']).catch(() => {
              console.error('Navigation to activate-account failed');
            });
          }, 3500);
        },
        error: (err) => {
          this.loading.set(false);
          console.error('Registration failed:', err);
          if (err && err.name === 'TimeoutError') {
            this.errorMessage.set(['The server is taking too long to respond. Please try again.']);
            return;
          }

          if (err.error?.validationErrors) {
            this.errorMessage.set(err.error.validationErrors);
          } else if (err.error?.errorMsg) {
            this.errorMessage.set([err.error.errorMsg]);
          } else if (err.error?.error) {
            this.errorMessage.set([err.error.error]);
          } else if (err.error?.message) {
            this.errorMessage.set([err.error.message]);
          } else if (err.message) {
            this.errorMessage.set([err.message]);
          } else {
            if (err.status === 0) {
              this.errorMessage.set(['Please check your internet connection']);
            } else {
              this.errorMessage.set(['An error occurred during registration']);
            }
          }
        }
      });
  }

  protected login() {
    this.router.navigate(['login']).catch(() => {
      console.error('Navigation to login failed');
    });
  }
}

