import { Component, signal } from '@angular/core';
import { AuthenticationRequest } from '../../services/models/authentication-request';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { authenticate } from '../../services/fn/authentication/authenticate';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration } from '../../services/api-configuration';
import { Token } from '../../services/token/token';
import { timeout } from 'rxjs';
import { ValidationUtils } from '../../services/utils/validation-utils';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {

  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMessage = signal<Array<string>>([]);
  showPassword = false;
  loading = signal<boolean>(false);

  constructor(
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration,
    private tokenService: Token,
    private toastr: ToastrService
  ) {}

  protected login() {
    this.errorMessage.set([]);

    const errors: Array<string> = [];
    
    if (!this.authRequest.email) {
      errors.push('Email is mandatory');
    } else if (!ValidationUtils.isValidEmail(this.authRequest.email)) {
      errors.push('Please enter a valid email format');
    }
    
    if (!this.authRequest.password) {
      errors.push('Password is mandatory');
    } else if (this.authRequest.password.length < 6) {
      errors.push('Password must be at least 6 characters long');
    }
    
    if (errors.length > 0) {
      this.errorMessage.set(errors);
      this.toastr.error(errors[0], 'Validation Error');
      return;
    }
    
    this.loading.set(true);

    authenticate(this.http, this.apiConfig.rootUrl, { body: this.authRequest })
      .pipe(timeout(5000))
      .subscribe({
        next: (response) => {
          this.loading.set(false);
          const authResponse = response.body;
          if (authResponse && authResponse.data?.token) {
            this.tokenService.token = authResponse.data.token as string;
          }
          this.toastr.success('Welcome back!', 'Login Successful');
          this.router.navigate(['books']).catch(() => {
            console.error('Navigation to books failed');
          });
        },
        error: (err) => {
          this.loading.set(false);

          if (err && err.name === 'TimeoutError') {
            const msg = 'The server is taking too long to respond. Please check your backend connection.';
            this.errorMessage.set([msg]);
            this.toastr.error(msg, 'Connection Timeout');
            return;
          }

          let errorMsg = 'An error occurred';
          if (err.error?.validationErrors) {
            const valErrors = Array.isArray(err.error.validationErrors) ? err.error.validationErrors : [String(err.error.validationErrors)];
            this.errorMessage.set(valErrors);
            errorMsg = valErrors[0];
          } else if (err.error?.errorMsg) {
            this.errorMessage.set([err.error.errorMsg]);
            errorMsg = err.error.errorMsg;
          } else if (err.error?.error) {
            if (err.error.error.includes('User is disabled') || err.error.error.includes('DisabledException')) {
              errorMsg = 'Your account is not activated yet. Please check your email for the activation code.';
            } else {
              errorMsg = err.error.error;
            }
            this.errorMessage.set([errorMsg]);
          } else if (err.error?.message) {
            if (err.error.message.includes('User is disabled') || err.error.message.includes('DisabledException')) {
              errorMsg = 'Your account is not activated yet. Please check your email for the activation code.';
            } else {
              errorMsg = err.error.message;
            }
            this.errorMessage.set([errorMsg]);
          } else {
            if (err.status === 0) {
              errorMsg = 'Please check your internet connection';
            }
            this.errorMessage.set([errorMsg]);
          }

          this.toastr.error(errorMsg, 'Login Failed');
        }
      });
  }

  protected register() {
    this.router.navigate(['register']).catch(() => {
      console.error('Navigation to register failed');
    });
  }
}
