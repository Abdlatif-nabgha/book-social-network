import { Component, signal, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { confirm } from '../../services/fn/authentication/confirm';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration } from '../../services/api-configuration';
import { FormsModule } from '@angular/forms';
import { CodeInputModule } from 'angular-code-input';
import { ToastrService } from 'ngx-toastr';

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
  loading = signal<boolean>(false);

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private http: HttpClient,
    private apiConfig: ApiConfiguration,
    private toastr: ToastrService
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

    const token = this.tokenCode.trim();
    if (!token) {
      const msg = 'Activation code is mandatory';
      this.errorMessage.set([msg]);
      this.toastr.error(msg, 'Validation Error');
      return;
    }
    if (token.length !== 6) {
      const msg = 'Activation code must be exactly 6 characters long';
      this.errorMessage.set([msg]);
      this.toastr.error(msg, 'Validation Error');
      return;
    }

    this.loading.set(true);

    confirm(this.http, this.apiConfig.rootUrl, { token })
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          this.toastr.success('Your account has been successfully activated!', 'Success');
          this.router.navigate(['login']).catch((err) => {
            console.error('Navigation to login failed', err);
          });
        },
        error: (err) => {
          this.loading.set(false);
          console.error('Activation failed:', err);
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
              errorMsgs = ['An error occurred during activation. Please make sure the code is correct.'];
            }
          }

          this.errorMessage.set(errorMsgs);
          this.toastr.error(errorMsgs[0], 'Activation Failed');
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
