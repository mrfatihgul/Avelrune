import { randomUUID } from "node:crypto";
import { expect, test as base, type APIRequestContext, type Locator, type Page } from "@playwright/test";

type Account = {
  email: string;
  password: string;
  fullName: string;
  accessToken: string;
  id: number;
};

type Inquiry = {
  id: number;
  ownerUserId: number;
  senderUserId: number | null;
  member: boolean;
};

const listingData = {
  description: "Bu ilan, yerel Playwright senaryosu için hazırlanmış bir test ilanıdır.",
  price: 8750000,
  listingType: "SALE",
  propertyType: "APARTMENT",
  city: "İstanbul",
  district: "Şişli",
  neighborhood: "Nişantaşı",
  grossArea: 140,
  netArea: 125,
  roomCount: "3+1",
  buildingAge: 8,
  floor: 4,
  totalFloors: 9,
  heatingType: "COMBI",
  latitude: 41.05,
  longitude: 28.99,
  imageUrls: ["/images/properties/p02-1.jpg"],
};

function bearer(account: Account) {
  return { Authorization: `Bearer ${account.accessToken}` };
}

async function register(request: APIRequestContext): Promise<Account> {
  const email = `e2e.${randomUUID()}@avelrune.test`;
  const password = "AvelruneE2e2026!";
  const fullName = "E2E Kullanıcısı";
  const response = await request.post("/api/auth/register", { data: { email, password, fullName } });
  await expect(response).toBeOK();
  const { accessToken, user } = await response.json();
  return { email, password, fullName, accessToken, id: user.id };
}

const test = base.extend<{ listing: { id: number; slug: string; title: string; owner: Account } }>({
  listing: async ({ request }, use) => {
    const owner = await register(request);
    const response = await request.post("/api/properties", {
      headers: bearer(owner),
      data: { ...listingData, title: `E2E Nişantaşı ${randomUUID()}` },
    });
    expect(response.status()).toBe(201);
    const { id, slug, title } = await response.json();
    try {
      await use({ id, slug, title, owner });
    } finally {
      const deletion = await request.delete(`/api/properties/${id}`, { headers: bearer(owner) });
      expect([204, 404], "Test ilanı kaldırılmış olmalı").toContain(deletion.status());
    }
  },
});

async function login(page: Page, account: Pick<Account, "email" | "password">, admin = false) {
  await page.goto(admin ? "/admin/login" : "/login");
  await page.getByLabel("E-posta").fill(account.email);
  await page.getByLabel("Şifre").fill(account.password);
  await page.getByRole("button", { name: admin ? "Giriş" : "Giriş yap", exact: true }).click();
  await expect(page).toHaveURL(admin ? /\/admin\/?$/ : /\/my-listings\/?$/);
}

async function logout(page: Page) {
  await page.getByRole("button", { name: "Çıkış", exact: true }).click();
  await expect(page).toHaveURL(/\/$/);
}

async function sendInquiry(
  page: Page,
  slug: string,
  sender: Pick<Account, "email" | "fullName">,
  message: string,
): Promise<Inquiry> {
  await page.goto(`/listing/${slug}`);
  const form = page.locator("form.owner-message-form");
  await form.getByLabel("Ad soyad").fill(sender.fullName);
  await form.getByLabel("E-posta").fill(sender.email);
  await form.getByLabel("Telefon").fill("05321234567");
  await form.getByRole("textbox", { name: /^Mesaj/ }).fill(message);
  const sent = page.waitForResponse(
    (response) =>
      new URL(response.url()).pathname === "/api/inquiries" && response.request().method() === "POST",
  );
  await form.getByRole("button", { name: "Mesaj gönder" }).click();
  const response = await sent;
  expect(response.status()).toBe(201);
  await expect(page.getByText("İlan sahibine iletildi.")).toBeVisible();
  return response.json();
}

async function updateStatus(message: Locator, status: "SOLVED" | "UNSOLVED") {
  const select = message.getByLabel("Talep durumu");
  await expect(select).toBeEnabled();
  await select.selectOption(status);
  await expect(message.getByRole("status")).toHaveText("Talep durumu kaydedildi.");
  await expect(select).toHaveValue(status);
}

test("üye başka ilana mesaj gönderir ve ilan sahibi talebin durumunu değiştirir", async ({ page, request, listing }) => {
  const sender = await register(request);
  const message = `Üye olarak ilan hakkında bilgi almak istiyorum. ${randomUUID()}`;
  await login(page, sender);
  const inquiry = await sendInquiry(page, listing.slug, sender, message);
  expect(inquiry).toMatchObject({ senderUserId: sender.id, ownerUserId: listing.owner.id, member: true });

  const forbidden = await request.patch(`/api/inquiries/${inquiry.id}/update`, {
    headers: bearer(sender),
    data: { status: "SOLVED" },
  });
  expect(forbidden.status()).toBe(403);

  await logout(page);
  await login(page, listing.owner);
  await page.goto(`/my-listings/${listing.id}`);
  const item = page.getByRole("listitem").filter({ hasText: message });
  await expect(item.getByText("Üye", { exact: true })).toBeVisible();
  await expect(item.getByLabel("Talep durumu")).toHaveValue("UNDERREVIEW");
  await updateStatus(item, "SOLVED");
  await page.reload();
  await expect(item.getByLabel("Talep durumu")).toHaveValue("SOLVED");
  await updateStatus(item, "UNSOLVED");
  await page.reload();
  await expect(item.getByLabel("Talep durumu")).toHaveValue("UNSOLVED");
});

test("misafir mesaj gönderebilir ama ilan sahibi kendi ilanına yazamaz", async ({ page, request, listing }) => {
  const message = `Misafir olarak ilanı görmek istiyorum. ${randomUUID()}`;
  const inquiry = await sendInquiry(
    page,
    listing.slug,
    { email: "misafir@avelrune.test", fullName: "E2E Misafiri" },
    message,
  );
  expect(inquiry).toMatchObject({ senderUserId: null, ownerUserId: listing.owner.id, member: false });

  await login(page, listing.owner);
  await page.goto(`/listing/${listing.slug}`);
  await expect(page.getByText("Bu ilan sizin. Gelen mesajları Evlerim’den okuyabilirsiniz.")).toBeVisible();
  await expect(page.getByRole("button", { name: "Mesaj gönder" })).toHaveCount(0);
  const rejected = await request.post("/api/inquiries", {
    headers: bearer(listing.owner),
    data: {
      fullName: listing.owner.fullName,
      email: listing.owner.email,
      phone: "05321234567",
      message: "Kendi ilanıma mesaj göndermeye çalışıyorum.",
      propertyId: listing.id,
    },
  });
  expect(rejected.status()).toBe(400);

  await page.goto(`/my-listings/${listing.id}`);
  const item = page.getByRole("listitem").filter({ hasText: message });
  await expect(item.getByText("Ziyaretçi", { exact: true })).toBeVisible();
});

test("kullanıcı çıkış yapıp admin olarak girer ve inquiry durumunu değiştirir", async ({ page, request, listing, baseURL }) => {
  const configuredAdmin = process.env.E2E_ADMIN_EMAIL && process.env.E2E_ADMIN_PASSWORD
    ? { email: process.env.E2E_ADMIN_EMAIL, password: process.env.E2E_ADMIN_PASSWORD }
    : undefined;
  const local = baseURL && ["localhost", "127.0.0.1", "[::1]"].includes(new URL(baseURL).hostname);
  test.skip(!configuredAdmin && !local, "Yerel ortam dışında E2E_ADMIN_EMAIL ve E2E_ADMIN_PASSWORD gerekli.");
  const admin = configuredAdmin ?? { email: "admin@avelrune.com", password: "AvelruneAdmin2026!" };

  const sender = await register(request);
  const message = `Admin durum değişikliği için test mesajı. ${randomUUID()}`;
  await login(page, sender);
  await sendInquiry(page, listing.slug, sender, message);
  await page.goto("/admin/messages");
  await expect(page).toHaveURL(/\/my-listings\/?$/);

  await logout(page);
  await login(page, admin, true);
  await page.goto("/admin/messages");
  const row = page.getByRole("row").filter({ hasText: message });
  await expect(row.getByLabel("Talep durumu")).toHaveValue("UNDERREVIEW");
  await updateStatus(row, "SOLVED");
  await page.reload();
  await expect(row.getByLabel("Talep durumu")).toHaveValue("SOLVED");
  await updateStatus(row, "UNSOLVED");
  await page.reload();
  await expect(row.getByLabel("Talep durumu")).toHaveValue("UNSOLVED");
});

test("ilan sahibi ilanını düzenler ve kaldırır", async ({ page, request, listing }) => {
  const updatedTitle = `${listing.title} Güncellendi`;
  await login(page, listing.owner);
  await page.goto(`/my-listings/${listing.id}`);
  await page.getByRole("link", { name: "Düzenle" }).click();
  await page.getByLabel("Başlık").fill(updatedTitle);
  await page.getByRole("button", { name: "İlanı güncelle" }).click();
  await expect(page).toHaveURL(/\/my-listings\/?$/);
  await expect(page.getByRole("heading", { name: updatedTitle, exact: true })).toBeVisible();

  await page.goto(`/my-listings/${listing.id}`);
  await expect(page.getByRole("heading", { name: updatedTitle, exact: true })).toBeVisible();
  page.once("dialog", (dialog) => dialog.accept());
  await page.getByRole("button", { name: "Kaldır", exact: true }).click();
  await expect(page).toHaveURL(/\/my-listings\/?$/);
  await expect(page.getByText("Henüz ilan yok.")).toBeVisible();
  expect((await request.get(`/api/properties/id/${listing.id}`)).status()).toBe(404);
});

test("başka bir kullanıcı ilanı düzenleyemez veya kaldıramaz", async ({ page, request, listing }) => {
  const outsider = await register(request);
  await login(page, outsider);
  await page.goto(`/my-listings/${listing.id}/edit`);
  await expect(page.getByRole("button", { name: "İlanı güncelle" })).toHaveCount(0);
  await expect(page.locator(".form-error")).toBeVisible();

  const update = await request.put(`/api/properties/${listing.id}`, {
    headers: bearer(outsider),
    data: { ...listingData, title: "Yetkisiz değişiklik" },
  });
  expect(update.status()).toBe(403);
  const deletion = await request.delete(`/api/properties/${listing.id}`, { headers: bearer(outsider) });
  expect(deletion.status()).toBe(403);
  const current = await request.get(`/api/properties/id/${listing.id}`);
  await expect(current).toBeOK();
  expect(await current.json()).toMatchObject({ title: listing.title });
});
