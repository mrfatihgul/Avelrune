"use client";

import { useEffect, useRef, useState } from "react";
import { FieldLabel } from "@/components/form/FieldLabel";
import { loadLeaflet } from "@/lib/maps";

const DEFAULT_ZOOM = 16;

export function LocationPickerField({
  latitude,
  longitude,
  onChange,
}: {
  latitude: number;
  longitude: number;
  onChange: (latitude: number, longitude: number) => void;
}) {
  return (
    <div className="field">
      <FieldLabel>Konum</FieldLabel>
      <input type="hidden" name="latitude" value={Number.isFinite(latitude) ? String(latitude) : ""} />
      <input type="hidden" name="longitude" value={Number.isFinite(longitude) ? String(longitude) : ""} />
      <LocationMap latitude={latitude} longitude={longitude} interactive onChange={onChange} />
      <p className="location-coords">
        {latitude.toFixed(6)}, {longitude.toFixed(6)}
      </p>
    </div>
  );
}

export function LocationMap({
  latitude,
  longitude,
  interactive = false,
  onChange,
}: {
  latitude: number;
  longitude: number;
  interactive?: boolean;
  onChange?: (latitude: number, longitude: number) => void;
}) {
  const canvasRef = useRef<HTMLDivElement>(null);
  const onChangeRef = useRef(onChange);
  const leafletMapRef = useRef<{ setView: (pos: [number, number], zoom?: number) => void } | null>(null);
  const skipNextMoveRef = useRef(false);
  const [error, setError] = useState("");
  const [ready, setReady] = useState(false);

  onChangeRef.current = onChange;

  useEffect(() => {
    if (!canvasRef.current) return;
    let cancelled = false;
    let cleanup = () => {};

    async function init() {
      try {
        await loadLeaflet();
        if (cancelled || !canvasRef.current || !window.L) return;
        const map = window.L.map(canvasRef.current, {
          center: [latitude, longitude],
          zoom: DEFAULT_ZOOM,
          zoomControl: true,
        });
        window.L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
          attribution: "&copy; OpenStreetMap",
        }).addTo(map);
        leafletMapRef.current = map;
        if (!interactive) {
          const icon = window.L.icon({
            iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
            iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
            shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
            iconSize: [25, 41],
            iconAnchor: [12, 41],
          });
          window.L.marker([latitude, longitude], { icon }).addTo(map);
        } else {
          const onMove = () => {
            if (skipNextMoveRef.current) {
              skipNextMoveRef.current = false;
              return;
            }
            const center = map.getCenter();
            onChangeRef.current?.(center.lat, center.lng);
          };
          map.on("moveend", onMove);
        }
        cleanup = () => map.remove();
        setTimeout(() => map.invalidateSize(), 80);
        setReady(true);
      } catch (err) {
        if (!cancelled) setError(err instanceof Error ? err.message : "Harita yüklenemedi.");
      }
    }

    void init();
    return () => {
      cancelled = true;
      cleanup();
      leafletMapRef.current = null;
    };
    // Map is created once per mount; parent updates go through pan effects.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [interactive]);

  useEffect(() => {
    skipNextMoveRef.current = true;
    leafletMapRef.current?.setView([latitude, longitude]);
  }, [latitude, longitude]);

  function useMyLocation() {
    if (!navigator.geolocation) {
      setError("Tarayıcı konum iznini desteklemiyor.");
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setError("");
        onChangeRef.current?.(position.coords.latitude, position.coords.longitude);
      },
      () => setError("Konum alınamadı. Tarayıcı iznini kontrol edin.")
    );
  }

  return (
    <div className={`location-map${interactive ? " is-picker" : ""}`}>
      <div className="location-map-stage">
        <div ref={canvasRef} className="location-map-canvas" role="presentation" />
        {interactive ? <div className="location-map-pin" aria-hidden="true" /> : null}
        {!ready && !error ? <p className="location-map-status">Harita yükleniyor…</p> : null}
      </div>
      {error ? <p className="form-error" style={{ padding: "0 14px 12px" }}>{error}</p> : null}
      {interactive ? (
        <div className="location-map-toolbar">
          <p>Haritayı kaydırarak iğneyi evin üzerine getirin.</p>
          <button className="btn btn-ghost" type="button" onClick={useMyLocation}>
            Konumumu kullan
          </button>
        </div>
      ) : null}
    </div>
  );
}
