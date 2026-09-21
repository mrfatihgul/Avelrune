import { clearSession, getToken } from "@/lib/auth";

const SERVER_API = process.env.API_INTERNAL_URL || "http://localhost";

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.status = status;
  }
}

export function apiPath(path: string) {
  if (typeof window === "undefined") {
    return `${SERVER_API}${path}`;
  }
  return path;
}

export async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const headers = new Headers(init?.headers);
  if (!headers.has("Content-Type") && init?.body && !(init.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }
  const response = await fetch(apiPath(path), {
    ...init,
    headers,
    cache: init?.cache ?? "no-store",
  });
  if (!response.ok) {
    let message = "İstek tamamlanamadı.";
    try {
      const text = await response.text();
      try {
        const data = JSON.parse(text) as { message?: string };
        if (data.message) message = data.message;
      } catch {
        if (text.trim()) message = text.trim().slice(0, 180);
      }
    } catch {
      /* ignore */
    }
    throw new ApiError(message, response.status);
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}

export function authHeaders(token: string): HeadersInit {
  return { Authorization: `Bearer ${token}` };
}

function loginPath() {
  if (typeof window === "undefined") {
    return "/login";
  }
  return window.location.pathname.startsWith("/admin") ? "/admin/login" : "/login";
}

function redirectToLogin() {
  if (typeof window === "undefined") {
    return;
  }
  const next = loginPath();
  if (window.location.pathname !== next) {
    window.location.replace(next);
  }
}

export async function apiAuthFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getToken();
  if (!token) {
    clearSession();
    redirectToLogin();
    throw new ApiError("Giriş yapmanız gerekiyor.", 401);
  }
  const headers = new Headers(init?.headers);
  if (!headers.has("Authorization")) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  try {
    return await apiFetch<T>(path, { ...init, headers });
  } catch (err) {
    if (err instanceof ApiError && err.status === 401) {
      clearSession();
      redirectToLogin();
    }
    throw err;
  }
}
