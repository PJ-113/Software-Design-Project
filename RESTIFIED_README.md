# RESTified E-Commerce API

Converted from original project at: /mnt/data/work/Software-Design-Project-main

## How to run (Eclipse)
1. Import as *Existing Maven Project*.
2. Ensure MySQL connection in `src/main/resources/application.properties` is valid.
3. Run `TestEcommerceApplication` main class.

## New REST Endpoints
- `GET /api/products` list all
- `GET /api/products/{id}`
- `POST /api/products` (JSON ProductDto)
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

- `GET /api/users`
- `GET /api/users/{id}`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

- `GET /api/cart/{userId}`
- `POST /api/cart/{userId}/add?productId=&qty=`
- `POST /api/cart/{userId}/update?productId=&qty=`
- `DELETE /api/cart/{userId}/remove?productId=`
- `DELETE /api/cart/{userId}/clear`

- `GET /api/orders/user/{userId}`
- `POST /api/orders/user/{userId}/checkout`
- `GET /api/orders/{orderId}`
- `GET /api/orders/all/checked-out`

Swagger UI: `/swagger-ui.html` (added via springdoc)
