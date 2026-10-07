import { expect, test } from "@playwright/test";

test("ilan listesinden detay sayfasını açar", async ({ page }) => {
  await page.goto("/listings");

  const firstCard = page.locator(".listing-grid .property-card").first();
  await expect(firstCard).toBeVisible();
  const title = await firstCard.getByRole("heading").innerText();
  await firstCard.getByRole("link").click();

  await expect(page).toHaveURL(/\/listing\/[^/]+\/?$/);
  await expect(page.getByRole("heading", { name: title, exact: true })).toBeVisible();
});

test("ilan filtrelerini uygular ve temizler", async ({ page }) => {
  await page.goto("/listings");

  await page.getByLabel("Min. fiyat").fill("1000");
  await page.getByLabel("Max. fiyat").fill("1");
  await page.getByRole("button", { name: "Uygula" }).click();

  await expect(page).toHaveURL(/minPrice=1000/);
  await expect(page).toHaveURL(/maxPrice=1/);
  await expect(page.getByText("Bu ölçütlere uyan ilan yok. Filtreleri azaltmayı deneyin.")).toBeVisible();

  await page.getByRole("button", { name: "Temizle" }).click();

  await expect(page).toHaveURL(/\/listings\/?$/);
  await expect(page.locator(".chips")).toHaveCount(0);
});
