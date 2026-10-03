"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { clearSession, getToken, getUser, SESSION_CHANGED_EVENT } from "@/lib/auth";
import { AuthUser } from "@/lib/types";

export function HeaderAuth() {
  const pathname = usePathname();
  const [user, setUser] = useState<AuthUser | null | undefined>(undefined);

  useEffect(() => {
    const syncUser = () => setUser(getToken() ? getUser() : null);
    syncUser();
    window.addEventListener(SESSION_CHANGED_EVENT, syncUser);
    window.addEventListener("storage", syncUser);
    return () => {
      window.removeEventListener(SESSION_CHANGED_EVENT, syncUser);
      window.removeEventListener("storage", syncUser);
    };
  }, [pathname]);

  if (user === undefined) return null;

  if (user) {
    return (
      <>
        {user.role === "USER" ? (
          <Link className="auth-action auth-action--primary" href="/my-listings">Evlerim</Link>
        ) : null}
        <button
          className="auth-action"
          type="button"
          onClick={() => {
            clearSession();
            window.location.replace("/");
          }}
        >
          Çıkış
        </button>
      </>
    );
  }

  return (
    <>
      <Link className="auth-action" href="/login">Giriş Yap</Link>
      <Link className="auth-action auth-action--primary" href="/register">Üye Ol</Link>
    </>
  );
}
