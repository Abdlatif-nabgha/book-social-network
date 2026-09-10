import { Component, signal } from '@angular/core';
import { RegisterRequest } from '../../services/models/register-request';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { register } from '../../services/fn/authentication/register';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration } from '../../services/api-configuration';
import { timeout } from 'rxjs/operators';
import { ValidationUtils } from '../../services/utils/validation-utils';
import { ToastrService } from 'ngx-toastr';

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
  showPassword = signal<boolean>(false);
  loading = signal<boolean>(false);

  constructor(
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration,
    private toastr: ToastrService
  ) {}

  protected submitRegister() {
    this.errorMessage.set([]);

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
      this.toastr.error(errors[0], 'Validation Error');
      return;
    }

    this.loading.set(true);

    register(this.http, this.apiConfig.rootUrl, { body: this.registerRequest })
      .pipe(timeout(5000))
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          this.toastr.success('Registration successful! Please check your email for the activation code.', 'Success');
          this.router.navigate(['activate-account']).catch(() => {
            console.error('Navigation to activate-account failed');
          });
        },
        error: (err) => {
          this.loading.set(false);
          console.error('Registration failed:', err);
          if (err && err.name === 'TimeoutError') {
            const msg = 'The server is taking too long to respond. Please try again.';
            this.errorMessage.set([msg]);
            this.toastr.error(msg, 'Timeout');
            return;
          }

          let errorMsgs: string[] = [];
          if (err.error?.validationErrors) {
            errorMsgs = Array.isArray(err.error.validationErrors) ? err.error.validationErrors : [String(err.error.validationErrors)];
          } else if (err.error?.errorMsg) {
            errorMsgs = [err.error.errorMsg];
          } else if (err.error?.error) {
            errorMsgs = [err.error.error];
          } else if (err.error?.message) {
            errorMsgs = [err.error.message];
          } else if (err.message) {
            errorMsgs = [err.message];
          } else {
            if (err.status === 0) {
              errorMsgs = ['Please check your internet connection'];
            } else {
              errorMsgs = ['An error occurred during registration'];
            }
          }

          this.errorMessage.set(errorMsgs);
          this.toastr.error(errorMsgs[0], 'Registration Failed');
        }
      });
  }

  protected login() {
    this.router.navigate(['login']).catch(() => {
      console.error('Navigation to login failed');
    });
  }
}
