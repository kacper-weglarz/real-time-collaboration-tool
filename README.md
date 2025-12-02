# Real-time Collaboration Tool

<video width="600" controls>
  <source src="Real-Time-Collaboration-Tool.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>

---

A **web-based document editor** with user authentication, document sharing, and **real-time editing**. Think of it as a simple Google Docs clone built with **Spring Boot** and plain **JS/HTML/CSS**.

---

## What does it do?

- **Users can register and log in** using JWT tokens for authentication.
- **Create documents** with titles and content.
- Each document has an **owner and roles**: OWNER (full access), EDITOR (edit and view), VIEWER (read-only).
- **Share documents** with other users and assign roles.
- Real-time editing powered by **WebSockets** (STOMP + SockJS) — changes sync instantly across users.
- **Dashboard** for listing documents, searching, and creating new ones.

---

## Backend Details

- Built with **Spring Boot**, **Spring Security**, **JPA/Hibernate**, and **PostgreSQL**.
- JWT tokens for authentication and authorization. Each API call requires a valid token.
- WebSockets configured for real-time communication between clients.
- REST controllers handle user registration, login, document CRUD operations, and sharing.
- Permissions managed in a separate table, so roles can be assigned per document.

---

## Frontend Details

- Plain **HTML, CSS, and JavaScript**, no frameworks.
- Views: login/register, user dashboard, document editor.
- STOMP + SockJS for connecting to the WebSocket server and subscribing to document updates.
- Edits sent via WebSocket and broadcasted to all connected users.
- JWT tokens stored in the browser’s **localStorage**.

---

## Step-by-step Features

### Authentication and User Profile

- Register with username, email, and password. Passwords are securely hashed.
- Login to receive a JWT token.
- All API calls include the token for authentication.
- Profile endpoint returns user details.

### Documents

- Create a document (you become the OWNER).
- List all documents you have access to.
- View, edit, or delete documents based on your role.
- Only the OWNER can delete a document.

### Permissions and Sharing

- Each document has a separate permissions table.
- OWNER can assign any role.
- EDITOR can only share as VIEWER.
- Permissions are checked before every action.

### Real-time Editing

- When you open a document, the frontend connects to the WebSocket server.
- Changes to the title or content are sent via WebSocket.
- The server broadcasts updates to all users viewing the document.
- A debounce mechanism prevents spamming the server.

---

## Running Locally

1. Set up PostgreSQL and create a database.
2. Configure the database credentials in `application.properties`.
3. Run the backend with `mvn spring-boot:run`.
4. Access the frontend in your browser at the login page (e.g., `http://localhost:8080/loginsignup.html`).

---

## What’s Next?

- Document version history.
- Online user indicators.
- Colored cursors for multiple users.
- Better error handling.

---

This project covers the full workflow from authentication and CRUD to permissions and real-time collaboration. It’s my first big portfolio piece, so there’s plenty of room for improvement, but it already demonstrates a lot of core concepts in web development.
