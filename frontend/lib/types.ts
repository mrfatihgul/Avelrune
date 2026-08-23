export type ListingType = "SALE" | "RENT";
export type PropertyType =
  | "APARTMENT"
  | "RESIDENCE"
  | "VILLA"
  | "HOUSE"
  | "YALI"
  | "STONE_HOUSE";
export type HeatingType =
  | "COMBI"
  | "CENTRAL"
  | "FLOOR_HEATING"
  | "AIR_CONDITIONING"
  | "NONE";

export interface PropertySummary {
  id: number;
  title: string;
  slug: string;
  price: number;
  listingType: ListingType;
  propertyType: PropertyType;
  city: string;
  district: string;
  neighborhood: string;
  grossArea: number;
  roomCount: string;
  featured: boolean;
  active: boolean;
  coverImage: string | null;
  createdAt: string;
  ownerUserId?: number | null;
}

export interface PropertyImage {
  id: number;
  url: string;
  altText: string;
  sortOrder: number;
}

export interface Agent {
  id: number;
  fullName: string;
  title: string;
  bio: string;
  phone: string;
  email: string;
  photoUrl: string;
  expertise: string;
}

export interface PropertyDetail extends Omit<PropertySummary, "coverImage"> {
  description: string;
  netArea: number;
  buildingAge: number;
  floor: number;
  totalFloors: number;
  heatingType: HeatingType;
  furnished: boolean;
  latitude: number;
  longitude: number;
  features: string[];
  images: PropertyImage[];
  agent: Agent | null;
  assignedUserId?: number | null;
  ownerUserId?: number | null;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface FilterMeta {
  cities: { name: string; districts: string[] }[];
  propertyTypes: { value: string; label: string }[];
  listingTypes: { value: string; label: string }[];
}

export interface PropertyStats {
  total: number;
  active: number;
  inactive: number;
  sale: number;
  rent: number;
  featured: number;
}

export interface ContactMessage {
  id: number;
  fullName: string;
  phone: string;
  email: string;
  message: string;
  propertySlug?: string;
  propertyId?: number | null;
  ownerUserId?: number | null;
  senderUserId?: number | null;
  member?: boolean;
  read: boolean;
  createdAt: string;
}

export interface AuthUser {
  id: number;
  email: string;
  fullName: string;
  role: string;
}

export interface SearchParams {
  listingType?: string;
  city?: string;
  district?: string;
  propertyType?: string;
  minPrice?: string;
  maxPrice?: string;
  rooms?: string;
  minArea?: string;
  maxArea?: string;
  buildingAge?: string;
  sort?: string;
  page?: string;
}
