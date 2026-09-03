import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Menu } from '../../components/menu/menu';
import { ApiConfiguration } from '../../services/api-configuration';
import { Token } from '../../services/token/token';
import { getStats } from '../../services/fn/admin/get-stats';
import { AdminStatsResponse } from '../../services/models/admin-stats-response';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    Menu,
    CommonModule
  ],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.scss'
})
export class AdminDashboard implements OnInit {
  stats = signal<AdminStatsResponse | null>(null);
  loading = signal<boolean>(true);
  errorMessage = signal<string>('');
  lastUpdated = signal<Date>(new Date());

  constructor(
    private router: Router,
    private http: HttpClient,
    private apiConfig: ApiConfiguration,
    public tokenService: Token
  ) {}

  ngOnInit(): void {
    if (!this.tokenService.isAdmin) {
      this.errorMessage.set('Access Restricted: You need an ADMIN role to access the Admin Control Center.');
    }
    this.fetchStats();
  }

  fetchStats() {
    this.loading.set(true);
    this.errorMessage.set('');

    getStats(this.http, this.apiConfig.rootUrl)
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          this.stats.set(res.body?.data || null);
          this.lastUpdated.set(new Date());
        },
        error: (err) => {
          this.loading.set(false);
          console.error('Failed to load admin stats', err);
          if (err.status === 403) {
            this.errorMessage.set('Forbidden (403): You do not have the ADMIN role required to view admin stats.');
          } else {
            this.errorMessage.set(err.error?.error || err.error?.message || 'Could not load admin stats.');
          }
        }
      });
  }

  refresh() {
    this.fetchStats();
  }
}
