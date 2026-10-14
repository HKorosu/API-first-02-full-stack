const { test, expect } = require('@playwright/test');

test('shopper registers, adds a product, checks out, and sees the saved order', async ({ page, request, baseURL }) => {
  await page.goto('/storefront.html');

  const firstProduct = page.locator('.card').first();
  await expect(firstProduct).toBeVisible();
  await firstProduct.getByRole('button', { name: 'Thêm vào giỏ' }).click();
  await expect(page.locator('#cart-count')).toHaveText('1');

  const email = `browser-${Date.now()}@example.test`;
  await page.locator('#auth-open').click();
  await page.locator('#auth-name').fill('Browser E2E Shopper');
  await page.locator('#auth-email').fill(email);
  await page.locator('#auth-password').fill('browser-test-123');
  await page.locator('#register-button').click();
  await expect(page.locator('#account-label')).toHaveText('Browser E2E Shopper');

  await page.locator('#checkout').click();
  const result = page.locator('#checkout-result');
  await expect(result).toContainText('Đặt hàng thành công!');
  const text = await result.textContent();
  const orderId = text.match(/Mã đơn:\s*(\d+)/)?.[1];
  expect(orderId).toBeTruthy();

  const response = await request.get(`${baseURL}/api/v1/orders/${orderId}`);
  expect(response.ok()).toBeTruthy();
  const order = await response.json();
  expect(order.id).toBe(Number(orderId));
  expect(order.items).toHaveLength(1);
  expect(order.items[0].quantity).toBe(1);
});
