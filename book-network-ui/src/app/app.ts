import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink, Router, NavigationEnd } from '@angular/router';
import { NgIcon } from '@ng-icons/core';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, NgIcon, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  title = signal('book-network-ui');
  isLanding = signal(true);

  constructor(private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const url = event.urlAfterRedirects;
      const isPublic = url === '/' || url === '' || url.startsWith('/login') || url.startsWith('/register') || url.startsWith('/activate-account') || url.startsWith('/token');
      this.isLanding.set(isPublic);
    });
  }
}
