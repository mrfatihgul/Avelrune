"use client";

import Image from "next/image";
import { useEffect, useState } from "react";
import { PropertyImage } from "@/lib/types";

export function PropertyGallery({ images, title }: { images: PropertyImage[]; title: string }) {
  const [open, setOpen] = useState(false);
  const [index, setIndex] = useState(0);

  useEffect(() => {
    if (!open) return;
    document.body.style.overflow = "hidden";
    const onKey = (event: KeyboardEvent) => {
      if (event.key === "Escape") setOpen(false);
      if (event.key === "ArrowRight") setIndex((value) => (value + 1) % images.length);
      if (event.key === "ArrowLeft") setIndex((value) => (value - 1 + images.length) % images.length);
    };
    window.addEventListener("keydown", onKey);
    return () => {
      document.body.style.overflow = "";
      window.removeEventListener("keydown", onKey);
    };
  }, [open, images.length]);

  if (images.length === 0) return null;

  function openAt(i: number) {
    setIndex(i);
    setOpen(true);
  }

  return (
    <>
      <div className="detail-gallery wrap">
        {images.slice(0, 3).map((image, i) => (
          <button key={image.id} type="button" onClick={() => openAt(i)} aria-label="Galeriyi aç">
            <Image src={image.url} alt={image.altText || title} fill sizes="50vw" />
          </button>
        ))}
      </div>
      {open ? (
        <div className="lightbox" role="dialog" aria-modal="true">
          <div className="lightbox-bar">
            <span>
              {index + 1} / {images.length}
            </span>
            <button className="btn btn-light" type="button" onClick={() => setOpen(false)}>
              Kapat
            </button>
          </div>
          <Image
            src={images[index].url}
            alt={images[index].altText || title}
            width={1600}
            height={1066}
          />
          <div className="lightbox-nav">
            <button
              className="btn btn-light"
              type="button"
              onClick={() => setIndex((value) => (value - 1 + images.length) % images.length)}
            >
              Geri
            </button>
            <button
              className="btn btn-light"
              type="button"
              onClick={() => setIndex((value) => (value + 1) % images.length)}
            >
              İleri
            </button>
          </div>
        </div>
      ) : null}
    </>
  );
}
