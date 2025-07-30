import { test, expect } from '@playwright/test';

test.describe('VisuAgent Dashboard', () => {
  test('should display dashboard and components', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByText('VisuAgent Dashboard')).toBeVisible();
    await expect(page.getByText('Video Stream')).toBeVisible();
    await expect(page.getByText('Settings')).toBeVisible();
    await expect(page.getByText('Preview')).toBeVisible();
  });

  test('should show ROI fields after selection', async ({ page }) => {
    await page.goto('/');
    // Simuliere ROI-Auswahl (hier nur UI-Test, keine echte Canvas-Interaktion)
    // Erwartet, dass die Koordinatenfelder angezeigt werden
    await expect(page.getByLabel('X')).toBeVisible();
    await expect(page.getByLabel('Y')).toBeVisible();
    await expect(page.getByLabel('Width')).toBeVisible();
    await expect(page.getByLabel('Height')).toBeVisible();
  });

  test('should trigger AI and show result in preview', async ({ page }) => {
    await page.goto('/');
    // Simuliere Klick auf "Wert erkennen"
    await page.getByRole('button', { name: 'Wert erkennen' }).click();
    // Erwartet, dass ein Messwert im Preview angezeigt wird (Demo: 23.5 Newton)
    await expect(page.getByText(/23\.5|Newton|Volt|bar/)).toBeVisible();
  });
});
