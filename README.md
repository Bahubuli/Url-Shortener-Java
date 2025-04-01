# URL Shortener Project

![License](https://img.shields.io/badge/license-MIT-blue.svg) ![Version](https://img.shields.io/badge/version-1.0.0-green.svg)

A simple yet powerful **URL Shortener** application built with Java Spring Boot and PostgreSQL. This project provides the ability to shorten long URLs into compact, shareable links. Future updates will include advanced features such as analytics, sharding, and URL activation/deactivation.

---

## Table of Contents

1. [Features](#features)
2. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Installation](#installation)
3. [Usage](#usage)
4. [API Endpoints](#api-endpoints)
5. [Future Enhancements](#future-enhancements)
6. [Contributing](#contributing)
7. [License](#license)
8. [Contact](#contact)

---

## Features

### Current Features
- **URL Shortening**: Convert long URLs into short, user-friendly links.
- **Redirects**: Redirect users from the short URL to the original long URL.
- **Persistence**: Store shortened URLs in a PostgreSQL database for quick retrieval.

### Planned Features (To Be Implemented)
- **Analytics**: Track click counts, geographic locations, and referral sources for each short URL.
- **Sharding**: Implement database sharding to handle high traffic and scale horizontally.
- **URL Activation/Deactivation**: Allow users to activate or deactivate short URLs.
- **Custom URLs**: Enable users to create custom short URLs.
- **Expiration**: Set expiration dates for short URLs.
- **Rate Limiting**: Protect against abuse by limiting the number of requests per user.

---

## Getting Started

### Prerequisites

Before running the application, ensure you have the following installed:
- Java 17 or later
- Maven (for building the project)
- PostgreSQL (database)
- Git (optional, for cloning the repository)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/url-shortener.git
   cd url-shortener
