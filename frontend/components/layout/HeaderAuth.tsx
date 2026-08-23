"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { clearSession, getUser } from "@/lib/auth";
import { AuthUser } from "@/lib/types";

export function HeaderAuth() {
  const pathname = usePathname();
  const router = useRouter();
  const [user, setUser] = useState<AuthUser | null>(null);

  useEffect(() => {
    setUser(getUser());
  }, [pathname]);

  if (user) {
    return (
      <>
        {user.role === "ADMIN" ? (
          <Link href="/admin">Yönetim</Link>
        ) : (
          <Link href="/evlerim">Evlerim</Link>
        )}
        <button
          type="button"
          onClick={() => {
            clearSession();
            setUser(null);
            router.push("/");
          }}
        >
          Çıkış
        </button>
      </>
    );
  }

  return (
    <>
      <Link href="/giris">Giriş</Link>
      <Link href="/uye-ol">Üye Ol</Link>
    </>
  );
}
