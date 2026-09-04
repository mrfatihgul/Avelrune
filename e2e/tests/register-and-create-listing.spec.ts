import { expect, test } from "@playwright/test";

test("üye olup yeni ilan yayınlar", async ({ page }) => {
  const stamp = Date.now();
  const email = `e2e.${stamp}@avelrune.test`;
  const title = `E2E Nişantaşı 3+1 ${stamp}`;

  await page.goto("/");
  await expect(page.getByRole("banner").getByRole("link", { name: "Avelrune Gayrimenkul" })).toBeVisible();

  await page.getByRole("navigation", { name: "Hesap" }).getByRole("link", { name: "Üye Ol" }).click();
  await expect(page.getByRole("heading", { name: "Üye ol" })).toBeVisible();

  await page.getByLabel("Ad soyad").fill("E2E Kullanıcı");
  await page.getByLabel("E-posta").fill(email);
  await page.getByLabel("Şifre").fill("AvelruneE2e2026!");
  await page.getByRole("button", { name: "Üye ol" }).click();

  await expect(page).toHaveURL(/\/my-listings\/?$/);
  await expect(page.getByRole("heading", { name: "Evlerim" })).toBeVisible();
  await expect(page.getByText("Henüz ilan yok.")).toBeVisible();

  await page.getByRole("link", { name: "Yeni ilan" }).click();
  await expect(page.getByRole("heading", { name: "Yeni ilan" })).toBeVisible();

  await page.getByRole("radio", { name: "Satılık" }).check();
  await page.getByLabel("Başlık").fill(title);
  await page.getByLabel("Açıklama").fill(
    "Bu ilan Playwright e2e ile tarayıcıdan oluşturuldu. Salon sokağa bakıyor ve mutfak ayrı duruyor."
  );
  await page.getByLabel("Fiyat").fill("8750000");
  await page.getByLabel("Tip").selectOption({ label: "Daire" });
  await page.getByLabel("Oda").fill("3+1");
  await page.getByLabel("Şehir").fill("İstanbul");
  await page.getByLabel("İlçe").fill("Şişli");
  await page.getByLabel("Mahalle").fill("Nişantaşı");
  await page.getByLabel("Brüt m²").fill("140");
  await page.getByLabel("Net m²").fill("125");
  await page.getByLabel("Bina yaşı").fill("8");
  await page.getByLabel("Kat", { exact: true }).fill("4");
  await page.getByLabel("Toplam kat").fill("9");
  await page.getByLabel("Isıtma").selectOption({ label: "Kombi" });
  await page.getByRole("button", { name: "İlanı yayınla" }).click();

  await expect(page).toHaveURL(/\/my-listings\/?$/);
  await expect(page.getByRole("heading", { name: title })).toBeVisible();
});
