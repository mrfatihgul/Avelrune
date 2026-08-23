import { FilterMeta, PageResponse, PropertyDetail, PropertySummary, SearchParams } from "./types";
import { apiFetch } from "./api";

export function listingLabel(type: string) {
  return type === "RENT" ? "Kiralık" : "Satılık";
}

export function propertyTypeLabel(type: string) {
  const map: Record<string, string> = {
    APARTMENT: "Daire",
    RESIDENCE: "Rezidans",
    VILLA: "Villa",
    HOUSE: "Müstakil Ev",
    YALI: "Yalı",
    STONE_HOUSE: "Taş Ev",
  };
  return map[type] ?? type;
}

export function heatingLabel(type: string) {
  const map: Record<string, string> = {
    COMBI: "Kombi",
    CENTRAL: "Merkezi",
    FLOOR_HEATING: "Yerden ısıtma",
    AIR_CONDITIONING: "Klima",
    NONE: "Yok",
  };
  return map[type] ?? type;
}

export function formatPrice(price: number, listingType: string) {
  const formatted = new Intl.NumberFormat("tr-TR").format(price);
  if (listingType === "RENT") {
    return `${formatted} ₺ / Ay`;
  }
  return `${formatted} ₺`;
}

export function formatDate(value: string) {
  return new Intl.DateTimeFormat("tr-TR", {
    day: "numeric",
    month: "long",
    year: "numeric",
  }).format(new Date(value));
}

export function buildQuery(params: SearchParams) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value) query.set(key, value);
  });
  const text = query.toString();
  return text ? `?${text}` : "";
}

export async function getProperties(params: SearchParams = {}) {
  const query = buildQuery(params);
  return apiFetch<PageResponse<PropertySummary>>(`/api/properties${query}`);
}

export async function getFeaturedProperties() {
  return apiFetch<PropertySummary[]>("/api/properties/featured");
}

export async function getProperty(slug: string) {
  return apiFetch<PropertyDetail>(`/api/properties/${slug}`);
}

export async function getSimilar(slug: string) {
  return apiFetch<PropertySummary[]>(`/api/properties/${slug}/similar`);
}

export async function getFilterMeta() {
  return apiFetch<FilterMeta>("/api/properties/meta");
}

export async function getAgents() {
  return apiFetch<import("./types").Agent[]>("/api/agents");
}
