const SERVER_API = process.env.API_INTERNAL_URL || "http://localhost";

export function apiPath(path: string) {
  if (typeof window === "undefined") {
    return `${SERVER_API}${path}`;
  }
  return path;
}

export async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const headers = new Headers(init?.headers);
  if (!headers.has("Content-Type") && init?.body) {
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
    const error = new Error(message) as Error & { status: number };
    error.status = response.status;
    throw error;
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}

export function authHeaders(token: string): HeadersInit {
  return { Authorization: `Bearer ${token}` };
}
