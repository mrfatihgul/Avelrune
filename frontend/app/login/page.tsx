"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { loginWithPassword } from "@/lib/login";
import { useGuestOnly } from "@/lib/useGuestOnly";

export default function LoginPage() {
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
      await loginWithPassword(String(form.get("email")), String(form.get("password")), false);
      router.replace("/my-listings");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Giriş yapılamadı.");
    } finally {
      setLoading(false);
    }
  }

  if (!ready) return null;

  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">Hesap</span>
        <h1 className="display">Giriş</h1>
      </section>
      <section className="wrap" style={{ maxWidth: 520, paddingBottom: 80 }}>
        <form className="form" onSubmit={onSubmit}>
          <label className="field">
            <span>E-posta</span>
            <input name="email" type="email" autoComplete="username" required />
          </label>
          <label className="field">
            <span>Şifre</span>
            <input name="password" type="password" autoComplete="current-password" required />
          </label>
          {error ? <p className="form-error">{error}</p> : null}
          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Kontrol ediliyor…" : "Giriş yap"}
          </button>
        </form>
        <p style={{ marginTop: 20, color: "var(--muted)" }}>
          Hesabınız yok mu? <Link href="/register">Üye olun</Link>
        </p>
      </section>
    </SiteShell>
  );
}
