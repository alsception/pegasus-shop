/* /**
 * Ovaj fajl sluzi za pwa kofiguraciju
 *
const CACHE_NAME = 'pegasus-v1';

// Instalacija - keširaj samo index.html
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.add('/'))
  );
  self.skipWaiting();
});

// Aktivacija
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          if (cacheName !== CACHE_NAME) {
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
  self.clients.claim();
});

self.addEventListener('fetch', event => {
  // Ignoriši API pozive - neka padnu normalno
  if (event.request.url.includes('/api/')) {
    return; // Ne keširaj API
  }
  
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        if (response) {
          return response;
        }
        
        return fetch(event.request).then(response => {
          if (!response || response.status !== 200) {
            return response;
          }
          
          const responseToCache = response.clone();
          
          caches.open('pegasus-v1').then(cache => {
            cache.put(event.request, responseToCache);
          });
          
          return response;
        });
      })
      .catch(() => caches.match('/'))
  );
}); */