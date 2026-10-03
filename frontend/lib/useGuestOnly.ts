"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getToken, getUser } from "@/lib/auth";

export function useGuestOnly() {
  const router = useRouter();
  const [ready, setReady] = useState(false);

  useEffect(() => {
    const user = getToken() ? getUser() : null;
    if (user) {
      router.replace(user.role === "ADMIN" ? "/admin" : "/my-listings");
      return;
    }
    setReady(true);
  }, [router]);

  return ready;
}
