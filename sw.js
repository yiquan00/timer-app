const CACHE_NAME = "timer-app-v1";
const ASSETS = [
  "/",
  "/index.html",
  "/manifest.json",
  "/icon/timer-icon.svg"
];

self.addEventListener("install", (e) => {
  e.waitUntil(
    caches.open(CACHE_NAME).then((c) => c.addAll(ASSETS))
  );
  self.skipWaiting();
});

self.addEventListener("activate", (e) => {
  e.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(
        keys
          .filter((k) => k !== CACHE_NAME)
          .map((k) => caches.delete(k))
      )
    )
  );
  self.clients.claim();
});

self.addEventListener("fetch", (e) => {
  if (e.request.mode === "navigate") {
    e.respondWith(
      fetch(e.request).catch(() => caches.match("/index.html"))
    );
    return;
  }
  e.respondWith(
    caches.match(e.request).then((cached) => cached || fetch(e.request))
  );
});

self.addEventListener("message", (e) => {
  if (e.data && e.data.type === "TIMER_FINISHED") {
    self.registration.showNotification("计时器 Timer", {
      body: "⏰ 时间到！",
      icon: "/icon/timer-icon.svg",
      tag: "timer-done",
      requireInteraction: true,
      vibrate: [200, 100, 200, 100, 200]
    });
  }
});
