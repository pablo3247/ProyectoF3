test('Firma contrato como cliente', async ({ page }) => {
    await page.goto('/firmar');
    await page.fill('#dni', '12345678Z');
    await page.click('#firmar');
    await expect(page).toHaveURL(/contrato_firmado/);
});