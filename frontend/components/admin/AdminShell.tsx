"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { Logo } from "@/components/layout/Logo";
import { clearSession, getToken, getUser } from "@/lib/auth";
import { AuthUser } from "@/lib/types";

const links = [
  { href: "/admin", label: "Özet" },
  { href: "/admin/properties", label: "İlanlar" },
  { href: "/admin/properties/new", label: "Yeni ilan" },
  { href: "/admin/messages", label: "Talepler" },
];

export function AdminShell({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathname = usePathname();
  const [user, setUser] = useState<AuthUser | null>(null);

  useEffect(() => {
    const token = getToken();
    const current = getUser();
    if (!token || !current) {
      router.replace("/admin/login");
      return;
    }
    if (current.role !== "ADMIN") {
      router.replace("/my-listings");
      return;
    }
    setUser(current);
  }, [router]);

  if (!user) {
    return <div className="login-page">Yükleniyor…</div>;
  }

  return (
    <div className="admin-shell">
      <aside className="admin-side">
        <Logo />
        <nav>
          {links.map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className={pathname === link.href ? "is-active" : undefined}
            >
              {link.label}
            </Link>
          ))}
        </nav>
        <div className="admin-account">
          <p>{user.fullName}</p>
          <button
            className="btn btn-light"
            type="button"
            onClick={() => {
              clearSession();
              router.replace("/admin/login");
            }}
          >
            Çıkış
          </button>
        </div>
      </aside>
      <div className="admin-main">{children}</div>
    </div>
  );
}
