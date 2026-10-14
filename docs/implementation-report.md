# Implementation and test report

## Implemented scope

- OpenAPI 3.0.3 contract-first API with generated Spring interfaces for customers, products, orders, authentication, and categories.
- MySQL persistence, Faker-based sample data, transactional checkout, stock locking, and inventory restoration on permitted order cancellation.
- Customer storefront and administration dashboard, with the shopping cart stored in browser local storage.
- Business analysis, Postman/Newman contract flow, backend unit/integration tests, and a browser-driven checkout test.

## Verification results

| Check | Result |
|---|---|
| `mvnw.cmd clean package` | Passed; 5 backend tests (1 service unit test and 4 Spring Boot/MockMvc integration tests), 0 failures |
| Newman collection | Passed; all 9 requests and 12 assertions succeeded |
| Playwright storefront E2E | Passed; shopper registration, product selection, checkout, and persisted order verification |
| Prism mock + prototype UI | Passed; Prism loaded the OpenAPI contract and the browser prototype displayed mock catalog data |
| HTTP smoke checks | `/v3/api-docs`, Swagger UI, storefront, and admin page returned HTTP 200 |

The automated backend tests and browser/contract flows were run against the H2 test database. The normal application profile uses MySQL. The source contract at `src/main/resources/shop-api.yaml` is OpenAPI 3.0.3 and is configured as the Swagger UI definition; SpringDoc's generated `/v3/api-docs` endpoint currently reports OpenAPI 3.0.1.

## Demo limitations

The authentication response contains a classroom-only demo token that is not used to authorize API calls. The admin screen is also not access-controlled. These are intentionally documented limitations, not production-ready security.
