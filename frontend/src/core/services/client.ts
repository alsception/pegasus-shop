/*
 *   This file handles:
 *
 *   Sending a login request (POST /login)
 *   Storing the token using setToken() from the store
 *   Sending authenticated requests (through the api() function)
 *   Optional logout logic
**/


import { showSuccessToast } from '../utils/toaster';
import { authenticate, clearToken, getToken } from './SessionStore';

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
 
  if (!response.ok) 
  {
    let message = await processErrMsg(response, '');
    throw new Error(`${message}`);
  }

  const data = await response.json();

  authenticate(data.token);
}

export function logout() {
  clearToken();
  // Optionally: call backend logout endpoint if you have one, i dont
}

export async function register(username: string, password: string) 
{
  const response = await fetch(`${API_BASE_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password }),
    credentials: 'include' // if backend uses cookies
  });

  console.log(response)
  
  if (!response.ok) 
  {
    let message = await processErrMsg(response, '');
    throw new Error(`${message}`);
  }

  const data = await response.text();  
  
  showSuccessToast(data);
  
  /*setToken(data.token);*///in future we should send token
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
    throw new Error('You are not logged in or your session has expired. Please log in to continue.');
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {...options, headers});

  if (!response.ok) 
  {
    let message = await processErrMsg(response, '');
    throw new Error(`${message}`);
  }
  
  // Check if response has content before parsing JSON
  const contentLength = response.headers.get('content-length');
  const contentType = response.headers.get('content-type');
  
  if (contentLength === '0' || response.status === 204 || !contentType?.includes('application/json')) 
  {
    return null as T; // or return {} as T
  }
  
  let x = await response.json(); // Add await here too!

  return x;
}

async function processErrMsg(response: any, path: any)
{
  let message = await response.text();
  if (!message) {
    // Provide default messages for common statuses
    message = response.status + ' - ';
    switch (response.status) {
    case 400:
      message += 'Bad request';
      break;
    case 401:
      message += 'Unauthorized';
      break;
    case 403:
      message += 'Forbidden';
      break;
    case 404:
      message += `NOT FOUND: ${path}`;
      break;
    case 500:
      message += 'Internal server error';
      break;
    default:
      message += `Request failed with status ${response.status}`;
    }
  }
  return message;
}

export default client;