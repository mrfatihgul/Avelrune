"use client";

import { ChangeEvent, DragEvent, useRef, useState } from "react";
import { apiAuthFetch } from "@/lib/api";

const MAX_FILES = 12;
const MAX_BYTES = 8 * 1024 * 1024;
const ACCEPT = "image/jpeg,image/png,image/webp";

type Item = { url: string; preview: string };

export function ImageUploader({
  name,
  initialUrls,
  onBusyChange,
}: {
  name: string;
  initialUrls: string[];
  onBusyChange?: (busy: boolean) => void;
}) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [items, setItems] = useState<Item[]>(() =>
    initialUrls.filter(Boolean).map((url) => ({ url, preview: url }))
  );
  const [error, setError] = useState("");
  const [uploading, setUploading] = useState(false);
  const [drag, setDrag] = useState(false);

  async function addFiles(files: FileList | File[]) {
    const incoming = Array.from(files);
    if (incoming.length === 0) return;
    setError("");
    if (items.length + incoming.length > MAX_FILES) {
      setError(`En fazla ${MAX_FILES} görsel ekleyebilirsiniz.`);
      return;
    }
    setUploading(true);
    onBusyChange?.(true);
    try {
      const uploaded: Item[] = [];
      for (const file of incoming) {
        if (file.size > MAX_BYTES) {
          throw new Error(`${file.name} 8 MB sınırını aşıyor.`);
        }
        if (!/^image\/(jpeg|png|webp)$/.test(file.type) && !/\.(jpe?g|png|webp)$/i.test(file.name)) {
          throw new Error("Yalnızca JPEG, PNG veya WEBP yükleyebilirsiniz.");
        }
        const body = new FormData();
        body.append("file", file);
        const response = await apiAuthFetch<{ url: string }>("/api/properties/media", {
          method: "POST",
          body,
        });
        uploaded.push({ url: response.url, preview: URL.createObjectURL(file) });
      }
      setItems((current) => [...current, ...uploaded]);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Görsel yüklenemedi.");
    } finally {
      setUploading(false);
      onBusyChange?.(false);
      if (inputRef.current) inputRef.current.value = "";
    }
  }

  function onInput(event: ChangeEvent<HTMLInputElement>) {
    if (event.target.files) {
      void addFiles(event.target.files);
    }
  }

  function onDrop(event: DragEvent<HTMLDivElement>) {
    event.preventDefault();
    setDrag(false);
    if (event.dataTransfer.files.length > 0) {
      void addFiles(event.dataTransfer.files);
    }
  }

  function remove(url: string) {
    setItems((current) => {
      const next = current.filter((item) => item.url !== url);
      const removed = current.find((item) => item.url === url);
      if (removed?.preview.startsWith("blob:")) {
        URL.revokeObjectURL(removed.preview);
      }
      return next;
    });
  }

  return (
    <div className="image-upload">
      <input type="hidden" name={name} value={items.map((item) => item.url).join("\n")} />
      <input
        ref={inputRef}
        className="image-upload-input"
        type="file"
        accept={ACCEPT}
        multiple
        aria-label="Görseller"
        onChange={onInput}
      />
      <div
        className={`image-upload-drop${drag ? " is-drag" : ""}`}
        onDragEnter={(event) => {
          event.preventDefault();
          setDrag(true);
        }}
        onDragOver={(event) => {
          event.preventDefault();
          setDrag(true);
        }}
        onDragLeave={(event) => {
          event.preventDefault();
          if (event.currentTarget.contains(event.relatedTarget as Node)) return;
          setDrag(false);
        }}
        onDrop={onDrop}
      >
        <p>{uploading ? "Yükleniyor…" : "Fotoğrafları buraya sürükleyin"}</p>
        <button
          className="btn btn-ghost"
          type="button"
          disabled={uploading || items.length >= MAX_FILES}
          onClick={() => inputRef.current?.click()}
        >
          Bilgisayardan seç
        </button>
        <span>JPEG, PNG veya WEBP · en fazla 8 MB · {MAX_FILES} görsele kadar</span>
      </div>
      {items.length > 0 ? (
        <ul className="image-upload-grid">
          {items.map((item) => (
            <li key={item.url} className="image-upload-item">
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img src={item.preview} alt="" />
              <button type="button" onClick={() => remove(item.url)} aria-label="Görseli kaldır">
                Kaldır
              </button>
            </li>
          ))}
        </ul>
      ) : null}
      {error ? <em className="form-error">{error}</em> : null}
    </div>
  );
}
