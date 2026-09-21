const LEAFLET_SCRIPT_ID = "avelrune-leaflet";
const LEAFLET_CSS_ID = "avelrune-leaflet-css";

type LeafletNamespace = {
  map: (el: HTMLElement, opts: Record<string, unknown>) => LeafletMap;
  tileLayer: (url: string, opts: Record<string, unknown>) => { addTo: (map: LeafletMap) => void };
  marker: (pos: [number, number], opts?: Record<string, unknown>) => LeafletMarker;
  icon: (opts: Record<string, unknown>) => unknown;
};

type LeafletMap = {
  getCenter: () => { lat: number; lng: number };
  setView: (pos: [number, number], zoom?: number) => void;
  on: (event: string, handler: () => void) => void;
  off: (event: string, handler: () => void) => void;
  remove: () => void;
  invalidateSize: () => void;
};

type LeafletMarker = {
  addTo: (map: LeafletMap) => LeafletMarker;
};

declare global {
  interface Window {
    L?: LeafletNamespace;
  }
}

function loadScript(id: string, src: string) {
  const existing = document.getElementById(id) as HTMLScriptElement | null;
  if (existing) {
    if (existing.dataset.loaded === "true") return Promise.resolve();
    return new Promise<void>((resolve, reject) => {
      existing.addEventListener("load", () => resolve(), { once: true });
      existing.addEventListener("error", () => reject(new Error("Harita yüklenemedi.")), { once: true });
    });
  }
  return new Promise<void>((resolve, reject) => {
    const script = document.createElement("script");
    script.id = id;
    script.src = src;
    script.async = true;
    script.onload = () => {
      script.dataset.loaded = "true";
      resolve();
    };
    script.onerror = () => reject(new Error("Harita yüklenemedi."));
    document.head.appendChild(script);
  });
}

export async function loadLeaflet() {
  if (!document.getElementById(LEAFLET_CSS_ID)) {
    const link = document.createElement("link");
    link.id = LEAFLET_CSS_ID;
    link.rel = "stylesheet";
    link.href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
    document.head.appendChild(link);
  }
  if (window.L) return;
  await loadScript(LEAFLET_SCRIPT_ID, "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js");
}

export type { LeafletMap };
