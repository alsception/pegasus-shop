/**
 * THIS FILE 
 * 1. Creates a writable store for auth state
 * 2. Persists the JWT token in localStorage
 * 3. Provides logout functionality
 * 4. Provides a helper to get the token directly
 */

import { writable } from 'svelte/store';

interface AuthState {
  token: string | null;
  isAuthenticated: boolean;
}

const storedToken = localStorage.getItem('token');

export const auth = writable<AuthState>({
  token: storedToken,
  isAuthenticated: !!storedToken,
});

export function setToken(token: string) {
  localStorage.setItem('token', token);
  auth.set({ token, isAuthenticated: true });
}

export function clearToken() {
  localStorage.removeItem('token');
  auth.set({ token: null, isAuthenticated: false });
}

export function getToken() {
  return localStorage.getItem('token');
}
