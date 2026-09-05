# ==========================================
# Stage 1: Build the Angular application
# ==========================================
FROM node:22-alpine AS build
WORKDIR /app

# 1. Copy package files and install dependencies
COPY package*.json ./
# Using npm ci for reliable builds
RUN npm ci

# 2. Copy source code and build the application
COPY . .
# You can customize the build command if needed
RUN npm run build -- --configuration production

# ==========================================
# Stage 2: Serve with NGINX
# ==========================================
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy the custom NGINX configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy the built Angular app from the build stage
# Note: Ensure this path matches the output path in angular.json.
# (For Angular 17+, it might be /app/dist/book-network-ui/browser)
COPY --from=build /app/dist/book-netwrok-ui/browser /usr/share/nginx/html

# Expose frontend port
EXPOSE 80

# Run nginx in foreground
ENTRYPOINT ["nginx", "-g", "daemon off;"]