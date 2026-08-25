import { Component, signal, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { confirm } from '../../services/fn/authentication/confirm';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration } from '../../services/api-configuration';
import { FormsModule } from '@angular/forms';
import { CodeInputModule } from 'angular-code-input';

@Component({
  selector: 'app-activate-account',
  imports: [
    FormsModule,
    CodeInputModule
  ],
  templateUrl: './activate-account.html',
  styleUrl: './activate-account.scss',
})
export class ActivateAccount implements OnInit {
  tokenCode = '';
  errorMessage = signal<Array<string>>([]);
  successMessage = signal<string>('');
  loading = signal<boolean>(false);
  showToast = signal<boolean>(false);

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private http: HttpClient,
    private apiConfig: ApiConfiguration
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.tokenCode = token;
        if (this.tokenCode.length === 6) {
          this.activateAccount();
        }
      }
    });
  }

  protected activateAccount() {
    this.errorMessage.set([]);
    this.successMessage.set('');

    const token = this.tokenCode.trim();
    if (!token) {
      this.errorMessage.set(['Activation code is mandatory']);
      return;
    }
    if (token.length !== 6) {
      this.errorMessage.set(['Activation code must be exactly 6 characters long']);
      return;
    }

    this.loading.set(true);

    confirm(this.http, this.apiConfig.rootUrl, { token })
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          this.successMessage.set('Your account has been successfully activated!');
          this.showToast.set(true);
          
          setTimeout(() => {
            this.showToast.set(false);
            this.router.navigate(['login']).catch((err) => {
              console.error('Navigation to login failed', err);
            });
          }, 3500);
        },
        error: (err) => {
          this.loading.set(false);
          console.error('Activation failed:', err);
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
            this.errorMessage.set(['An error occurred during activation. Please make sure the code is correct.']);
          }
        }
      });
  }

  protected login() {
    this.router.navigate(['login']).catch((err) => {
      console.error('Navigation to login failed', err);
    });
  }

  onCodeChanged(code: string) {
    this.tokenCode = code;
  }

  onCodeCompleted(code: string) {
    this.tokenCode = code;
    this.activateAccount();
  }
}
