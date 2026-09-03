import { Component, OnInit, signal, HostListener, ElementRef } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Token } from '../../services/token/token';
import { ApiConfiguration } from '../../services/api-configuration';
import { getUserProfile, UserProfileResponse } from '../../services/fn/user/get-user-profile';
import { updateUserProfile } from '../../services/fn/user/update-user-profile';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    CommonModule,
    FormsModule
  ],
  templateUrl: './menu.html',
  styleUrl: './menu.scss'
})
export class Menu implements OnInit {
  showProfileDropdown = signal<boolean>(false);
  showMobileMenu = signal<boolean>(false);
  showProfileModal = signal<boolean>(false);

  loadingProfile = signal<boolean>(false);
  savingProfile = signal<boolean>(false);
  profileSuccess = signal<string>('');
  profileError = signal<string>('');

  userProfile = signal<UserProfileResponse | null>(null);
  editFirstName = signal<string>('');
  editLastName = signal<string>('');
  
  userFullName = signal<string>('');
  userInitials = signal<string>('U');
  userEmail = signal<string>('');

  constructor(
    private router: Router,
    public tokenService: Token,
    private http: HttpClient,
    private apiConfig: ApiConfiguration,
    private elementRef: ElementRef
  ) {}

  ngOnInit() {
    this.refreshLocalUserInfo();
  }

  refreshLocalUserInfo() {
    this.userFullName.set(this.tokenService.userFullName || 'User');
    this.userInitials.set(this.tokenService.userInitials);
    this.userEmail.set(this.tokenService.userEmail);
  }

  toggleProfileDropdown() {
    this.showProfileDropdown.update(v => !v);
  }

  closeProfileDropdown() {
    this.showProfileDropdown.set(false);
  }

  toggleMobileMenu() {
    this.showMobileMenu.update(v => !v);
  }

  closeMobileMenu() {
    this.showMobileMenu.set(false);
  }

  openProfileModal() {
    this.closeProfileDropdown();
    this.closeMobileMenu();
    this.showProfileModal.set(true);
    this.profileSuccess.set('');
    this.profileError.set('');

    this.loadingProfile.set(true);
    getUserProfile(this.http, this.apiConfig.rootUrl)
      .subscribe({
        next: (res) => {
          this.loadingProfile.set(false);
          const data = res.body?.data;
          if (data) {
            this.userProfile.set(data);
            this.editFirstName.set(data.firstName || '');
            this.editLastName.set(data.lastName || '');
            this.userFullName.set(data.fullName || `${data.firstName} ${data.lastName}`);
            this.userEmail.set(data.email || '');
          }
        },
        error: (err) => {
          this.loadingProfile.set(false);
          console.error('Failed to load profile', err);
          // Fallback to token values
          const parts = (this.tokenService.userFullName || '').split(' ');
          this.editFirstName.set(parts[0] || '');
          this.editLastName.set(parts.slice(1).join(' ') || '');
        }
      });
  }

  closeProfileModal() {
    this.showProfileModal.set(false);
  }

  saveProfile() {
    if (!this.editFirstName().trim() || !this.editLastName().trim()) {
      this.profileError.set('First name and Last name are required.');
      return;
    }

    this.savingProfile.set(true);
    this.profileError.set('');
    this.profileSuccess.set('');

    updateUserProfile(this.http, this.apiConfig.rootUrl, {
      body: {
        firstName: this.editFirstName().trim(),
        lastName: this.editLastName().trim()
      }
    }).subscribe({
      next: (res) => {
        this.savingProfile.set(false);
        const data = res.body?.data;
        if (data) {
          if (data.token) {
            this.tokenService.token = data.token;
          }
          this.userProfile.set(data);
          this.userFullName.set(data.fullName || `${data.firstName} ${data.lastName}`);
          this.refreshLocalUserInfo();
        }
        this.profileSuccess.set('Profile updated successfully!');
        setTimeout(() => {
          this.profileSuccess.set('');
        }, 3000);
      },
      error: (err) => {
        this.savingProfile.set(false);
        console.error('Failed to update profile', err);
        this.profileError.set(err.error?.error || err.error?.message || 'Failed to update profile.');
      }
    });
  }

  logout() {
    this.closeProfileDropdown();
    this.closeMobileMenu();
    this.closeProfileModal();
    this.tokenService.clear();
    this.router.navigate(['login']).catch(err => {
      console.error('Logout navigation failed', err);
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.closeProfileDropdown();
    }
  }
}
