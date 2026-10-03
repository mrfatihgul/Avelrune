"use client";

import { apiFetch } from "@/lib/api";
import { saveSession } from "@/lib/auth";
import { AuthUser } from "@/lib/types";

export async function loginWithPassword(email: string, password: string, allowAdmin = true) {
  const response = await apiFetch<{
    accessToken: string;
    user: AuthUser;
  }>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
  if (!allowAdmin && response.user.role === "ADMIN") {
    throw new Error("E-posta veya şifre hatalı.");
  }
  saveSession(response.accessToken, response.user);
  return response.user;
}
