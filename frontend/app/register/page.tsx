"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { apiFetch } from "@/lib/api";
import { saveSession } from "@/lib/auth";
import { AuthUser } from "@/lib/types";
import { useGuestOnly } from "@/lib/useGuestOnly";

export default function RegisterPage() {
  const router = useRouter();
  const ready = useGuestOnly();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    setLoading(true);
    setError("");
    try {
      const response = await apiFetch<{
        accessToken: string;
        user: AuthUser;
      }>("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({
          fullName: form.get("fullName"),
          email: form.get("email"),
          password: form.get("password"),
        }),
      });
      saveSession(response.accessToken, response.user);
      router.replace("/my-listings");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Kayıt tamamlanamadı.");
    } finally {
      setLoading(false);
    }
  }

  if (!ready) return null;

  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">Hesap</span>
        <h1 className="display">Üye ol</h1>
      </section>
      <section className="wrap" style={{ maxWidth: 520, paddingBottom: 80 }}>
        <form className="form" onSubmit={onSubmit}>
          <label className="field">
            <span>Ad soyad</span>
            <input name="fullName" autoComplete="name" required />
          </label>
          <label className="field">
            <span>E-posta</span>
            <input name="email" type="email" autoComplete="email" required />
          </label>
          <label className="field">
            <span>Şifre</span>
            <input name="password" type="password" autoComplete="new-password" minLength={8} required />
          </label>
          {error ? <p className="form-error">{error}</p> : null}
          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Kaydediliyor…" : "Üye ol"}
          </button>
        </form>
        <p style={{ marginTop: 20, color: "var(--muted)" }}>
          Zaten hesabınız var mı? <Link href="/login">Giriş yapın</Link>
        </p>
      </section>
    </SiteShell>
  );
}
