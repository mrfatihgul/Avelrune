"use client";

import { FormEvent, useEffect, useState } from "react";
import { apiFetch, authHeaders } from "@/lib/api";
import { getToken, getUser } from "@/lib/auth";

export function OwnerMessageForm({
  slug,
  propertyId,
  ownerUserId,
}: {
  slug: string;
  propertyId: number;
  ownerUserId?: number | null;
}) {
  const [user, setUser] = useState<ReturnType<typeof getUser>>(null);
  const [status, setStatus] = useState<"idle" | "loading" | "ok" | "error">("idle");
  const [message, setMessage] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    setUser(getUser());
  }, []);

  const isOwner = Boolean(user && ownerUserId && user.id === ownerUserId);

  if (!ownerUserId) {
    return null;
  }
  if (isOwner) {
    return (
      <div className="owner-note">
        Bu ilan sizin. Gelen mesajları Evlerim’den okuyabilirsiniz.
      </div>
    );
  }

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
      const token = getToken();
      await apiFetch("/api/requests", {
        method: "POST",
        headers: token ? authHeaders(token) : undefined,
        body: JSON.stringify({
          fullName: data.fullName,
          phone: data.phone,
          email: data.email,
          message: data.message,
          propertySlug: slug,
          propertyId,
        }),
      });
      setStatus("ok");
      setMessage("İlan sahibine iletildi.");
      form.reset();
    } catch (error) {
      setStatus("error");
      setMessage(error instanceof Error ? error.message : "Gönderilemedi.");
    }
  }

  if (status === "ok") {
    return (
      <div className="success-box">
        <h2 className="display" style={{ fontSize: "1.8rem", marginBottom: 8 }}>
          İletildi.
        </h2>
        <p>{message}</p>
      </div>
    );
  }

  return (
    <form className="form owner-message-form" onSubmit={onSubmit}>
      <p className="eyebrow">İlan sahibine yazın</p>
      <label className="field">
        <span>Ad soyad</span>
        <input name="fullName" defaultValue={user?.fullName ?? ""} autoComplete="name" />
        {errors.fullName ? <em className="form-error">{errors.fullName}</em> : null}
      </label>
      <label className="field">
        <span>Telefon</span>
        <input name="phone" autoComplete="tel" />
        {errors.phone ? <em className="form-error">{errors.phone}</em> : null}
      </label>
      <label className="field">
        <span>E-posta</span>
        <input name="email" type="email" defaultValue={user?.email ?? ""} autoComplete="email" />
        {errors.email ? <em className="form-error">{errors.email}</em> : null}
      </label>
      <label className="field">
        <span>Mesaj</span>
        <textarea name="message" placeholder="Bu ev hakkında görüşmek istiyorum." />
        {errors.message ? <em className="form-error">{errors.message}</em> : null}
      </label>
      {status === "error" ? <p className="form-error">{message}</p> : null}
      <button className="btn" type="submit" disabled={status === "loading"}>
        {status === "loading" ? "Gönderiliyor…" : "Mesaj gönder"}
      </button>
      {!user ? (
        <p className="form-hint">Üye olmadan da yazabilirsiniz. Üyeyseniz mesajınız hesabınızla bağlanır.</p>
      ) : null}
    </form>
  );
}
