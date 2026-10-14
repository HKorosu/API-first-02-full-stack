const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './e2e',
  use: {
    baseURL: process.env.E2E_BASE_URL || 'http://localhost:18081',
    headless: true
  },
  reporter: 'list'
});
