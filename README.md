Pegasus Shop

This project is intended as an excercise

It should have this basic functionalities:

I) Customer flow:
--------------------
1. Register
2. Login
3. Browse products
4. Add to cart
5. Edit cart
6. Buy (Add address and payment info (for now only invoice)
7. See order confimration
8. Leave a review
9. See past orders
10. Manage account and addresses
11. Wishlist

II) Admin / vendor flow:
-------------------------
1. Login
2. Manage users
3. Manage products (availability, stock...)
4. Manage orders (see all orders, edit status)
5. Manage wishlists



AI generated slop follows bellow (dont take it too seriously, it could contain errors):

alsception@proton.me 11/7/2025

============================================================================================

# Pegasus Shop

A comprehensive e-commerce web application built for modern online shopping experiences.

## Table of Contents
- [Features](#features)
- [User Types](#user-types)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## Features

### Customer Experience
- **User Authentication**: Secure registration and login system
- **Product Browsing**: Intuitive product catalog with search and filtering
- **Shopping Cart**: Add, edit, and manage items before purchase
- **Wishlist**: Save favorite items for future consideration
- **Checkout Process**: Streamlined purchasing with address and payment information
- **Order Management**: View order confirmations and order history
- **Review System**: Leave reviews and ratings for purchased products
- **Account Management**: Update personal information and manage delivery addresses

### Admin & Vendor Tools
- **User Management**: Comprehensive user administration
- **Product Management**: Add, edit, and organize product catalog
- **Order Processing**: Monitor all orders and update order status
- **Administrative Dashboard**: Centralized control panel for shop operations

## User Types

### 🛒 Customers
Regular shoppers who can browse, purchase, and review products.

**Customer Journey:**
1. Register for an account
2. Login to access personalized features
3. Browse the product catalog
4. Add desired items to shopping cart or wishlist
5. Edit cart contents as needed
6. Complete purchase with delivery address and payment info
7. Receive order confirmation
8. Leave product reviews after purchase
9. View past orders and order history
10. Manage wishlist items
11. Manage account settings and delivery addresses

### 👨‍💼 Admins/Vendors
Administrative users with elevated permissions to manage the shop.

**Admin Workflow:**
1. Login with admin credentials
2. Manage user accounts and permissions
3. Add, edit, or remove products from catalog
4. Monitor and process customer orders
5. Update order status and tracking information

## Tech Stack

- **Backend**: Spring Boot 3.3.10 REST API, Java 21
- **Database**: Supabase (PostgreSQL)
- **Frontend**: Svelte 5 compiled to static files
- **Styling**: Tailwind CSS with DaisyUI components
- **Images**: Unsplash API integration for product photos
- **Deployment**: Docker containers, hosted on Render.com (demo version)
- **Architecture**: Vertical slice with feature-based organization

## Architecture

Pegasus Shop follows a **vertical slice architecture** that promotes maintainability and scalability:

### Core Application Layer
- **Security**: Authentication and authorization components
- **Configuration**: Application settings and environment management
- **Common utilities**: Shared services and utilities

### Feature-Based Organization
Each business capability is organized as a self-contained feature:

- **User Feature**: Registration, authentication, profile management
- **Product Feature**: Catalog, inventory, product management
- **Cart Feature**: Shopping cart operations and session management
- **Order Feature**: Order processing, status management, history
- **Wishlist Feature**: Save and manage favorite items
- **Review Feature**: Product reviews and ratings

This architecture ensures that each feature contains its own:
- Controllers (REST endpoints)
- Services (business logic)
- Repositories (data access)
- Models/DTOs (data transfer objects)
- Tests (unit and integration)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/asception/pegasus-shop.git
   cd pegasus-shop
   ```

2. **Configure Supabase database**
   - Create a new project at [supabase.com](https://supabase.com)
   - Copy your database URL and API keys
   - Update `application.properties` with your Supabase credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://[YOUR-SUPABASE-URL]
   spring.datasource.username=[YOUR-DB-USERNAME]
   spring.datasource.password=[YOUR-DB-PASSWORD]
   supabase.url=[YOUR-SUPABASE-URL]
   supabase.key=[YOUR-SUPABASE-ANON-KEY]
   ```

3. **Configure Unsplash API**
   - Create a developer account at [unsplash.com/developers](https://unsplash.com/developers)
   - Create a new application to get your API key
   - Add your Unsplash API key to `application.properties`:
   ```properties
   unsplash.api.key=[YOUR-UNSPLASH-ACCESS-KEY]
   ```

4. **Build the application**
   ```bash
   # Install frontend dependencies
   npm install
   
   # Build frontend (Svelte + Tailwind CSS + DaisyUI)
   npm run build:frontend
   
   # Build Spring Boot application
   mvn clean package
   ```

5. **Run the application**
   
   **Option 1: Using Docker (Recommended)**
   ```bash
   # Build and run with Docker Compose
   docker-compose up --build
   
   # Or run in detached mode
   docker-compose up -d
   ```
   
   **Option 2: Local Development**
   ```bash
   # Using Maven
   mvn spring-boot:run
   
   # Or using the built JAR
   java -jar target/pegasus-shop-1.0.jar
   ```

6. **Access the application**
   - Main application: `http://localhost:8080`
   - Admin panel: `http://localhost:8080/admin`
   - API documentation: `http://localhost:8080/swagger-ui.html`

### Docker Deployment

The application is fully containerized for easy deployment:

```bash
# Build Docker image
docker build -t pegasus-shop .

# Run with Docker
docker run -p 8080:8080 pegasus-shop

# Or use Docker Compose for full stack
docker-compose up
```

**Docker Compose includes:**
- Spring Boot application container
- Environment-specific configurations
- Volume mounts for persistent data

### Deployment Scripts

The project includes convenient scripts for deployment:

```bash
# Build and deploy to production
./scripts/deploy.sh

# Start application in production mode
./scripts/start.sh

# Build frontend assets (Svelte + Tailwind + DaisyUI)
./scripts/build-frontend.sh

# Docker deployment
./scripts/docker-deploy.sh
```

## Frontend Development

The frontend is built with **Svelte** and styled using **Tailwind CSS** with **DaisyUI** components for a modern, responsive design.

### Styling Framework
- **Tailwind CSS**: Utility-first CSS framework for rapid UI development
- **DaisyUI**: Component library built on Tailwind CSS for consistent design
- **Responsive Design**: Mobile-first approach with breakpoint utilities
- **Dark Mode**: Built-in theme switching capabilities

### Development Workflow
```bash
# Install frontend dependencies
npm install

# Start development server with hot reload
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### Component Structure
```
src/
├── components/        # Reusable Svelte components
├── routes/           # Page components and routing
├── lib/              # Utilities and shared logic
├── stores/           # Svelte stores for state management
└── app.css          # Global styles and Tailwind imports
```

### For Customers
1. **Registration**: Create an account using email and password
2. **Shopping**: Browse products by category or use search functionality
3. **Wishlist**: Save interesting items to your wishlist for later
4. **Cart Management**: Review items before checkout
5. **Checkout**: Provide shipping address and select invoice payment method
6. **Order Tracking**: Monitor order status in your account dashboard

### For Admins
1. **Login**: Access admin panel with administrative credentials
2. **Dashboard**: Overview of shop statistics and recent activities
3. **Product Management**: Add new products, update existing ones, manage inventory
4. **Order Management**: Process orders, update shipping status, handle returns
5. **User Management**: Manage customer accounts and permissions

## Payment Methods

Currently supporting:
- **Invoice Payment**: Pay after receiving the order

*Additional payment methods (credit cards, PayPal, etc.) will be added in future updates.*

## API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### Product Endpoints
- `GET /api/products` - List all products
- `GET /api/products/:id` - Get specific product
- `POST /api/products` - Create product (admin only)
- `PUT /api/products/:id` - Update product (admin only)
- `DELETE /api/products/:id` - Delete product (admin only)

### Order Endpoints
- `POST /api/orders` - Create new order
- `GET /api/orders` - Get user orders
- `GET /api/orders/:id` - Get specific order
- `PUT /api/orders/:id/status` - Update order status (admin only)

### Cart Endpoints
- `GET /api/cart` - Get user's cart
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/update` - Update cart item quantity
- `DELETE /api/cart/remove` - Remove item from cart

### Wishlist Endpoints
- `GET /api/wishlist` - Get user's wishlist
- `POST /api/wishlist/add` - Add item to wishlist
- `DELETE /api/wishlist/remove` - Remove item from wishlist

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Development Roadmap

### Upcoming Features
- [ ] Multiple payment gateway integration
- [ ] Advanced search and filtering options
- [ ] Email notifications for order updates
- [ ] Multi-language support
- [ ] Mobile app development
- [ ] Advanced analytics dashboard
- [ ] Product recommendation engine

### Bug Reports & Feature Requests
Please use the GitHub Issues section to report bugs or request new features.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

For support and questions:
- Email: asception@proton.me
- Documentation: [Wiki](https://github.com/yourusername/pegasus-shop/wiki)
- Community: [Discussions](https://github.com/alsception/pegasus-shop/discussions)

---

**Pegasus Shop** - Soaring above the competition in e-commerce solutions.
