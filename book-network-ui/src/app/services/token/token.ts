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

  get userFullName(): string {
    return this.decodedToken?.fullName || '';
  }
}
