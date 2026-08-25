import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Token } from '../../services/token/token';
import { NgIcon } from '@ng-icons/core';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    NgIcon
  ],
  templateUrl: './menu.html',
  styleUrl: './menu.scss'
})
export class Menu implements OnInit {
  userInitial = 'U';

  constructor(
    private router: Router,
    private tokenService: Token
  ) {}

  ngOnInit() {
    const fullName = this.tokenService.userFullName;
    if (fullName) {
      const parts = fullName.trim().split(/\s+/);
      if (parts.length >= 2) {
        this.userInitial = (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
      } else if (parts.length === 1 && parts[0]) {
        this.userInitial = parts[0].substring(0, 2).toUpperCase();
      }
    } else {
      this.userInitial = 'U';
    }
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['login']).catch(err => {
      console.error('Logout navigation failed', err);
    });
  }
}
