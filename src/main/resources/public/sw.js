let ThemeCache = "hexo-boot-theme-pure-v1";

self.addEventListener('install', function(event) {
    event.waitUntil(
        caches.open(ThemeCache).then(function(cache) {
            return cache.addAll([]);
        })
    );
});

self.addEventListener('activate', function(event) {
    event.waitUntil(
        caches.keys().then(function(cacheNames) {
            return Promise.all(
                cacheNames.map(function(cacheName) {
                    if (cacheName !== ThemeCache) {
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
});

self.addEventListener('fetch', function(event) {
    event.respondWith(
        caches.match(event.request).then(function(resp) {
            return resp || fetch(event.request).then(function(response) {
                return caches.open(ThemeCache).then(function(cache) {
                    if (!response && response.status !== 200) {
                        return response;
                    }

                    let request = event.request;
                    if (request.destination === "" || request.url.indexOf("app.js") > -1 || request.url.indexOf("app.css") > -1) {
                        return response;
                    }

                    cache.put(request, response.clone());
                    return response;
                });
            });
        })
    );
});