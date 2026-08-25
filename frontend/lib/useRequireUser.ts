"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getToken, getUser } from "@/lib/auth";
import { AuthUser } from "@/lib/types";

export function useRequireUser(options?: { admin?: boolean }) {
  const router = useRouter();
  const [user, setUser] = useState<AuthUser | null>(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    const token = getToken();
    const current = getUser();
    if (!token || !current) {
      router.replace(options?.admin ? "/admin/login" : "/login");
      return;
    }
    if (options?.admin && current.role !== "ADMIN") {
      router.replace("/my-listings");
      return;
    }
    if (!options?.admin && current.role === "ADMIN") {
      router.replace("/admin");
      return;
    }
    setUser(current);
    setReady(true);
  }, [router, options?.admin]);

  return { user, ready };
}
