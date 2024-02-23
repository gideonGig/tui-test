# GitHub Repository Service

The GitHub Repository Service is a Spring Boot application that provides functionality to fetch repositories and their branches from GitHub using the GitHub GraphQL API.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Testing](#testing)


## Features

- Fetch repositories of a user with their repositores and branches
- Support for pagination to handle large repositories
- Error handling for invalid requests or server errors

## Technologies Used

- Java
- Spring Boot
- Spring WebFlux
- Maven
- WebClient for making HTTP requests
- Mockito for testing
- Docker

## Getting Started

### Prerequisites

- Java 20 or higher installed on your system
- GitHub personal access token (PAT) with appropriate permissions

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/gideonGig/tui-test.git


2. Build the project using Maven:

   ```bash
   ./mvn clean install

3. Start the application:

   ```bash
   docker compose up

4. Access the api using an HTTP client like POSTMAN, application runs on
- http://localhost:5280



## Configuration
Before running the application, make sure to set the following configuration properties:
. github.baseurl: Base URL for GitHub API.
You can set these properties in the application.properties file or as environment variables.

### Fetch Repositories and Branches

To fetch repositories and branches for a GitHub user, send a GET request to the following endpoint:
- GET /api/v1/github/{username}

Replace `{username}` with the GitHub username you want to fetch repositories for. You must also include the following headers in your request:

- `Authorization`: Bearer token for GitHub API authentication
- `Accept`: Must be set to `application/json`

The response will contain a JSON array of repositories, each containing information about the repository and its branches.

## Contributing

Contributions to the GitHub Repository Fetcher are welcome! If you have any ideas for improvements or new features, feel free to open an issue or submit a pull request.







