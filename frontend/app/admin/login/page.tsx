"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { Logo } from "@/components/layout/Logo";
import { loginWithPassword } from "@/lib/login";

export default function AdminLoginPage() {
  const router = useRouter();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    setLoading(true);
    setError("");
    try {
      const user = await loginWithPassword(String(form.get("email")), String(form.get("password")));
      if (user.role !== "ADMIN") {
        router.replace("/my-listings");
        return;
      }
      router.replace("/admin");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Giriş yapılamadı.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-box">
        <Logo />
        <h1 className="display" style={{ fontSize: "2.6rem", margin: "24px 0 8px" }}>
          Yönetim
        </h1>
        <p style={{ color: "var(--muted)", marginBottom: 28 }}>
          Yalnızca ofis kullanımı içindir.
        </p>
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
            {loading ? "Kontrol ediliyor…" : "Giriş"}
          </button>
        </form>
      </div>
    </div>
  );
}
