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

export function getCurrentUsername() 
{
  return getUsernameFromToken(getToken());
}

function getUsernameFromToken(token: string | null) 
{
  // Split the token into parts
  if (token !== null) {
    const parts = token.split(".");

    if (parts.length !== 3) {
      throw new Error("Invalid JWT token format");
    }

    // Get the payload (middle part)
    const payload = parts[1];

    // Base64 decode the payload
    // Note: Need to add padding and handle URL-safe base64
    const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );

    // Parse the JSON
    const data = JSON.parse(jsonPayload);

    // Look for common username fields
    return data.username || data.sub || data.email || null;
  }
  return "";
}