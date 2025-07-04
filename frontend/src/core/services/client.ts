/*
    This file handles:

    Sending a login request (POST /login)
    Storing the token using setToken() from the store
    Sending authenticated requests (through the api() function)
    Optional logout logic
*/


import { setToken, clearToken, getToken } from './store';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function login(username: string, password: string) 
{
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password }),
    credentials: 'include' // if backend uses cookies
  });

  if (!response.ok) {
    throw new Error('Invalid credentials');
  }

  const data = await response.json();
  setToken(data.token);
}

export function logout() {
  clearToken();
  // Optionally: call backend logout endpoint if you have one, i dont
}

/**
 * This function executes call to the backend
 * Important: when calling it: await!
 */
async function client<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> 
{
  const token = getToken();
  const headers = new Headers(options.headers || {});

  headers.set('Content-Type', 'application/json');
  
  if (token) 
  {
    headers.set('Authorization', `Bearer ${token}`);
  }
  else
  {
    throw new Error('No auth token provided');
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {...options, headers});

  if (!response.ok) 
  {
    const message = await response.text();//do we need await here? yes
    
    throw new Error(`${message}`);  //<--- ln 78
  }
  
  // Check if response has content before parsing JSON
  const contentLength = response.headers.get('content-length');
  const contentType = response.headers.get('content-type');
  
  if (contentLength === '0' || response.status === 204 || !contentType?.includes('application/json')) {
      console.log(12)
      return null as T; // or return {} as T
  }
  
  let x = await response.json(); // Add await here too!

  return x;
}

export default client;