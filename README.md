# Restaurant orders (Java + React)

Junior hiring task: REST API for restaurant orders with a React frontend.

## Prerequisites

- Java 17+
- Node.js 20+ and npm

## Backend (Spring Boot)

```bash
./mvnw spring-boot:run
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:restaurant-orders`, user `sa`, empty password)

### Authentication

HTTP Basic auth for all API routes except Swagger and H2 console:

| Username | Password |
|----------|----------|
| `user`   | `password` |

### Orders API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/orders` | List orders (`?sort=totalPrice` or `?sort=-totalPrice`) |
| `GET` | `/orders/{orderNr}` | Get one order |
| `POST` | `/orders` | Create order |
| `PATCH` | `/orders/{orderNr}/status` | Update status |

Example:

```bash
curl -u user:password http://localhost:8080/orders
```

## Frontend (React + Vite + Tailwind)

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The dev server proxies `/orders` to the backend.

Production build:

```bash
cd frontend
npm run build
```

Output is in `frontend/dist/`.

## Development workflow

Run backend and frontend in two terminals. Create and manage orders in the UI; the API must be running first.

## Tests

```bash
./mvnw test
```
