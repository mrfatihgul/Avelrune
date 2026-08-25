"use client";

import { FormEvent, useMemo, useState } from "react";
import { useSearchParams } from "next/navigation";
import { apiFetch } from "@/lib/api";

export function ContactForm() {
  const searchParams = useSearchParams();
  const listing = searchParams.get("ilan");
  const [status, setStatus] = useState<"idle" | "loading" | "ok" | "error">("idle");
  const [message, setMessage] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});

  const defaultMessage = useMemo(() => {
    return listing ? `${listing} ilanı hakkında görüşmek istiyorum.` : "";
  }, [listing]);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = event.currentTarget;
    const data = Object.fromEntries(new FormData(form).entries()) as Record<string, string>;
    const nextErrors: Record<string, string> = {};
    if (!data.fullName || data.fullName.trim().length < 2) nextErrors.fullName = "Ad soyad girin.";
    if (!data.phone || data.phone.replace(/\D/g, "").length < 10) nextErrors.phone = "Telefon girin.";
    if (!data.email || !data.email.includes("@")) nextErrors.email = "E-posta girin.";
    if (!data.message || data.message.trim().length < 10) nextErrors.message = "Mesajı biraz açın.";
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    setStatus("loading");
    try {
      await apiFetch("/api/inquiries", {
        method: "POST",
        body: JSON.stringify({
          fullName: data.fullName,
          phone: data.phone,
          email: data.email,
          message: data.message,
          propertySlug: listing || undefined,
        }),
      });
      setStatus("ok");
      setMessage("Notunuz bize ulaştı. Gün içinde dönüş yapılır.");
      form.reset();
    } catch (error) {
      setStatus("error");
      setMessage(error instanceof Error ? error.message : "Gönderilemedi.");
    }
  }

  if (status === "ok") {
    return (
      <div className="success-box">
        <h2 className="display" style={{ fontSize: "2.2rem", marginBottom: 12 }}>
          Alındı.
        </h2>
        <p>{message}</p>
      </div>
    );
  }

  return (
    <form className="form" onSubmit={onSubmit}>
      <label className="field">
        <span>Ad soyad</span>
        <input name="fullName" autoComplete="name" />
        {errors.fullName ? <em className="form-error">{errors.fullName}</em> : null}
      </label>
      <label className="field">
        <span>Telefon</span>
        <input name="phone" autoComplete="tel" />
        {errors.phone ? <em className="form-error">{errors.phone}</em> : null}
      </label>
      <label className="field">
        <span>E-posta</span>
        <input name="email" type="email" autoComplete="email" />
        {errors.email ? <em className="form-error">{errors.email}</em> : null}
      </label>
      <label className="field">
        <span>Mesaj</span>
        <textarea name="message" defaultValue={defaultMessage} />
        {errors.message ? <em className="form-error">{errors.message}</em> : null}
      </label>
      {status === "error" ? <p className="form-error">{message}</p> : null}
      <button className="btn" type="submit" disabled={status === "loading"}>
        {status === "loading" ? "Gönderiliyor…" : "Gönder"}
      </button>
    </form>
  );
}
