import { expect, test } from "@playwright/test";

test("giriş yapmayan kullanıcı ilan oluşturma sayfasından girişe yönlendirilir", async ({ page }) => {
  await page.goto("/my-listings/new");

  await expect(page).toHaveURL(/\/login\/?$/);
  await expect(page.getByRole("heading", { name: "Giriş" })).toBeVisible();
});

test("hatalı giriş bilgileri kullanıcıya gösterilir", async ({ page }) => {
  await page.goto("/login");

  await page.getByLabel("E-posta").fill(`olmayan.${Date.now()}@avelrune.test`);
  await page.getByLabel("Şifre").fill("YanlisSifre2026!");
  await page.getByRole("button", { name: "Giriş yap" }).click();

  await expect(page.getByText("E-posta veya şifre hatalı.")).toBeVisible();
  await expect(page).toHaveURL(/\/login\/?$/);
});

test("kayıtlı kullanıcı çıkış yapıp yeniden giriş yapar", async ({ page }) => {
  const email = `e2e.login.${Date.now()}@avelrune.test`;
  const password = "AvelruneE2e2026!";

  await page.goto("/register");
  await page.getByLabel("Ad soyad").fill("E2E Giriş Kullanıcısı");
  await page.getByLabel("E-posta").fill(email);
  await page.getByLabel("Şifre").fill(password);
  await page.getByRole("button", { name: "Üye ol" }).click();
  await expect(page).toHaveURL(/\/my-listings\/?$/);

  await page.getByRole("button", { name: "Çıkış" }).click();
  await expect(page).toHaveURL(/\/$/);
  await page.getByRole("navigation", { name: "Hesap" }).getByRole("link", { name: "Giriş Yap" }).click();

  await page.getByLabel("E-posta").fill(email);
  await page.getByLabel("Şifre").fill(password);
  await page.getByRole("button", { name: "Giriş yap" }).click();

  await expect(page).toHaveURL(/\/my-listings\/?$/);
  await expect(page.getByRole("heading", { name: "Evlerim" })).toBeVisible();
});
