import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class Token {
  set token(token: string) {
    localStorage.setItem('token', token);
  }

  get token() {
    return localStorage.getItem('token') as string;
  }

  get decodedToken() {
    const t = this.token;
    if (!t) return null;
    try {
      const payloadBase64 = t.split('.')[1];
      const payloadJson = atob(payloadBase64);
      return JSON.parse(payloadJson);
    } catch (e) {
      console.error('Error decoding token', e);
      return null;
    }
  }

  get userId(): number | null {
    return this.decodedToken?.userId || null;
  }

  get userEmail(): string {
    return this.decodedToken?.sub || '';
  }

  get userFullName(): string {
    return this.decodedToken?.fullName || '';
  }

  get userInitials(): string {
    const fullName = this.userFullName;
    if (fullName) {
      const parts = fullName.trim().split(/\s+/);
      if (parts.length >= 2) {
        return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
      } else if (parts.length === 1 && parts[0]) {
        return parts[0].substring(0, 2).toUpperCase();
      }
    }
    return 'U';
  }

  get authorities(): Array<string> {
    const auth = this.decodedToken?.authorities;
    if (Array.isArray(auth)) {
      return auth;
    }
    return [];
  }

  get roles(): Array<string> {
    return this.authorities.map(a => a.replace(/^ROLE_/, ''));
  }

  get isAdmin(): boolean {
    return this.roles.includes('ADMIN') || this.authorities.includes('ROLE_ADMIN');
  }

  clear() {
    localStorage.removeItem('token');
  }
}
