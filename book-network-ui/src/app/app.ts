import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { heroBookOpenSolid } from '@ng-icons/heroicons/solid';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, NgIcon],
  providers: [provideIcons({ heroBookOpenSolid })],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('book-network-ui');
}
