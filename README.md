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
- [Contributing](#contributing)
- [License](#license)

## Features

- Fetch repositories of a user with their repositores and branches
- Support for pagination to handle large repositories
- Error handling for invalid requests or server errors

## Technologies Used

- Java
- Spring Boot
- Spring WebFlux
- WebClient for making HTTP requests
- Mockito for testing

## Getting Started

### Prerequisites

- Java 11 or higher installed on your system
- GitHub personal access token (PAT) with appropriate permissions

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your_username/github-repository-service.git