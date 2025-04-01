# URL Shortener Project

![License](https://img.shields.io/badge/license-MIT-blue.svg) ![Version](https://img.shields.io/badge/version-1.0.0-green.svg)

A simple yet powerful **URL Shortener** application built with Java Spring Boot and PostgreSQL. This project provides the ability to shorten long URLs into compact, shareable links. Future updates will include advanced features such as analytics, sharding, and URL activation/deactivation.

---

## Table of Contents

- [Features](#features)
- [Planned Features](#planned-features)
- [Getting Started](#getting-started)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgements](#acknowledgements)

---

## Features

### Current Features
- **URL Shortening**: Convert long URLs into short, user-friendly links.
- **Redirects**: Automatically redirect users from the short URL to the original long URL.
- **Persistence**: Store shortened URLs in a PostgreSQL database for quick retrieval.

---

## Planned Features

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

1. Clone the Repository  
   `git clone https://github.com/yourusername/url-shortener.git && cd url-shortener`  

2. Configure PostgreSQL Database  
   Update `src/main/resources/application.properties` with your database credentials:  
   `spring.datasource.url=jdbc:postgresql://localhost:5432/your_database`  
   `spring.datasource.username=your_username`  
   `spring.datasource.password=your_password`  
   `spring.jpa.hibernate.ddl-auto=update`  

3. Build the Project  
   `mvn clean install`  

4. Run the Application  
   `mvn spring-boot:run`  

---

## Usage

- **Shortening a URL**: Use `POST /api/shorten` with the long URL in the request body.  
- **Redirecting**: Access `http://localhost:8080/{shortCode}` to be redirected to the original long URL.  

Refer to the API documentation for detailed instructions.

---

## Configuration

To change the default server port, update `application.properties`:  
`server.port=8080`  

Other Spring Boot and PostgreSQL settings can be adjusted as needed.

---

## Contributing

Contributions are welcome!  
1. Fork the repository.  
2. Create a new branch for your feature or bug fix.  
3. Commit your changes with clear messages.  
4. Open a pull request detailing your changes.  

Follow the [Contributor Covenant](https://www.contributor-covenant.org/) as our code of conduct.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Acknowledgements

- [Java Spring Boot](https://spring.io/projects/spring-boot)  
- [PostgreSQL](https://www.postgresql.org/)  
- Open-source community for continuous support and improvement.

---

Happy coding!
