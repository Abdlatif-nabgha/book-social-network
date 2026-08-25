import { Component, signal, Signal } from '@angular/core';
import {AuthenticationRequest} from '../../services/models/authentication-request';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {authenticate} from '../../services/fn/authentication/authenticate';
import {HttpClient} from '@angular/common/http';
import {ApiConfiguration} from '../../services/api-configuration';
import { Token } from '../../services/token/token';
import { timeout } from 'rxjs';
import { ValidationUtils } from '../../services/utils/validation-utils';

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
    private tokenService: Token
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
      return;
    }
    
    this.loading.set(true); // start loading here

    authenticate(this.http, this.apiConfig.rootUrl, { body: this.authRequest })
      .pipe(timeout(5000))
      .subscribe({
        next: (response) => {
          this.loading.set(false);
          const authResponse = response.body;
          if (authResponse && authResponse.data?.token) {
            this.tokenService.token = authResponse.data.token as string;
          }
          this.router.navigate(['books']).catch(() => {
            console.error('Navigation to books failed');
          });
        },
        error: (err) => {
          this.loading.set(false);

          if (err && err.name === 'TimeoutError') {
            this.errorMessage.set(['The server is taking too long to respond. Please try your backend connection.']);
            return;
          }

          if (err.error?.validationErrors) {
            this.errorMessage.set(err.error.validationErrors);
          } else if (err.error?.errorMsg) {
            this.errorMessage.set([err.error.errorMsg]);
          } else if (err.error?.error) {
            if (err.error.error.includes('User is disabled') || err.error.error.includes('DisabledException')) {
              this.errorMessage.set(['Your account is not activated yet. Please check your email for the activation code.']);
            } else {
              this.errorMessage.set([err.error.error]);
            }
          } else if (err.error?.message) {
            if (err.error.message.includes('User is disabled') || err.error.message.includes('DisabledException')) {
              this.errorMessage.set(['Your account is not activated yet. Please check your email for the activation code.']);
            } else {
              this.errorMessage.set([err.error.message]);
            }
          } else {
            this.errorMessage.set(['An error occurred']);
          }
        }
      });
  }

  protected register() {
    this.router.navigate(['register']).catch(() => {
      console.error('Navigation to register failed');
    });
  }
}
