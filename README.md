# Shop E-commerce Fullstack (API-First)

This separate implementation builds on the original Shop API and adds business analysis, authentication, categories, catalog search, storefront and admin pages, seeded data, and automated tests. The expanded assignment is in `doc2/`.

## Run against MySQL

1. Start MySQL in XAMPP. Apache is not needed.
2. Open PowerShell in this folder and run:
   ```powershell
   .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=18081"
   ```
3. Open `http://localhost:18081/swagger-ui.html`, `http://localhost:18081/storefront.html`, or `http://localhost:18081/admin.html`.
4. Faker seeds categories, sample products, and a demo shopper when missing. Demo login: `demo@shop.local` / `shop12345`.

The admin screen is a classroom prototype without access control. The returned `demo-...` token is not used to authorize API calls and is not production authentication.

## API-first mock stage

Run Prism from this directory:

```powershell
npx.cmd --yes @stoplight/prism-cli mock src\main\resources\shop-api.yaml --host 127.0.0.1 --port 4010
```

Open `http://localhost:18081/shop-prototype.html` and set its Mock Server URL to `http://127.0.0.1:4010`. For the full `storefront.html`, change API URL from `/api/v1` to `http://127.0.0.1:4010` to use Prism.

## Tests

Run the H2-backed backend unit and integration tests (no XAMPP database needed):

```powershell
.\mvnw.cmd test
```

The Postman collection at `postman\shop-api-contract-tests.postman_collection.json` tests registration/login, categories and products, checkout, order transitions, and stock restoration. With the backend running, execute:

```powershell
npx.cmd --yes newman run postman\shop-api-contract-tests.postman_collection.json --env-var "apiUrl=http://localhost:18081/api/v1"
```

Run the browser E2E flow after starting the backend. It registers a shopper, adds a product, checks out through the storefront, and verifies the saved order:

```powershell
npm.cmd install
npx.cmd playwright install chromium
npm.cmd run test:e2e
```

Set `E2E_BASE_URL` if the backend is not at `http://localhost:18081`. Test outcomes are recorded in `docs/implementation-report.md`.

## Project contents

- `docs/business-analysis.md`: user stories, rules, and workflow diagrams.
- `docs/implementation-report.md`: automated test and smoke-test results.
- `src/main/resources/shop-api.yaml`: OpenAPI 3.0.3 contract and code-generation source.
- `postman/shop-api-contract-tests.postman_collection.json`: contract/checkout flow checks.
- `e2e/storefront-checkout.spec.cjs`: browser-driven checkout E2E test.
- `src/main/resources/static/storefront.html`: customer search, cart, login/register, and checkout.
- `src/main/resources/static/admin.html`: category, inventory, and order management demo.
- `src/test/`: H2-backed Spring Boot integration tests.
