import { Component } from '@angular/core';
import {AuthenticationRequest} from '../../services/models/authentication-request';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {authenticate} from '../../services/fn/authentication/authenticate';
import {HttpClient} from '@angular/common/http';
import {ApiConfiguration} from '../../services/api-configuration';
import { Token } from '../../services/token/token';

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
  errorMessage: Array<string> = [];
  showPassword = false;

  constructor(
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration,
    private tokenService: Token
  ) {}

  protected login() {
    this.errorMessage = [];
    
    if (!this.authRequest.email) {
      this.errorMessage.push('Email is mandatory');
    } else {
      const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      if (!emailRegex.test(this.authRequest.email)) {
        this.errorMessage.push('Please enter a valid email format');
      }
    }
    
    if (!this.authRequest.password) {
      this.errorMessage.push('Password is mandatory');
    }
    
    if (this.errorMessage.length > 0) {
      return;
    }
    
    authenticate(this.http, this.apiConfig.rootUrl, { body: this.authRequest })
      .subscribe({
        next: (response) => {
          const authResponse = response.body;
          if (authResponse && authResponse.data?.token) {
            this.tokenService.token = authResponse.data.token as string;
          }
          this.router.navigate(['books']).catch(() => {
            console.error('Navigation to books failed');
          });
        },
        error: (err) => {
          if (err.error?.validationErrors) {
            this.errorMessage = err.error.validationErrors;
          } else if (err.error?.errorMsg) {
            this.errorMessage = [err.error.errorMsg];
          } else if (err.error?.error) {
            if (err.error.error.includes('User is disabled') || err.error.error.includes('DisabledException')) {
              this.errorMessage = ['Your account is not activated yet. Please check your email for the activation code.'];
            } else {
              this.errorMessage = [err.error.error];
            }
          } else if (err.error?.message) {
            if (err.error.message.includes('User is disabled') || err.error.message.includes('DisabledException')) {
              this.errorMessage = ['Your account is not activated yet. Please check your email for the activation code.'];
            } else {
              this.errorMessage = [err.error.message];
            }
          } else {
            this.errorMessage = ['An error occurred'];
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
