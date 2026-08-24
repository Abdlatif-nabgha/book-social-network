import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { heroUsersSolid, heroBookOpenSolid, heroArrowRightSolid } from '@ng-icons/heroicons/solid';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, NgIcon],
  providers: [provideIcons({ heroUsersSolid, heroBookOpenSolid, heroArrowRightSolid })],
  templateUrl: './home.html'
})
export class HomeComponent {}
