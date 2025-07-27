# Ryanair Interconnecting Flights API

## Overview
The Ryanair Interconnecting Flights API is a Spring Boot application designed to find direct and interconnected flights (with a maximum of one stop) using Ryanair's external APIs. This API allows users to search for flights based on departure and arrival locations, as well as specific time constraints.

## Features
- **Direct Flights**: Retrieve flights with no stops.
- **Interconnected Flights**: Retrieve flights with exactly one stop, adhering to layover rules. Asynchronous processing is used to handle multiple requests efficiently.
- **Time Constraints**: Specify departure and arrival time windows.
- **Caching**: Efficiently manage route data with caching.
- **Error Handling**: Comprehensive error handling for various scenarios.
- **Testing**: Includes unit and integration tests to ensure reliability.

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.12+

### Build and Run
1. Clone the repository:
   ```
   git clone https://github.com/PelinAkpinar/ryanair-interconnecting-flights.git
   ```
2. Navigate to the project directory:
   ```
   cd ryanair-interconnecting-flights
   ```
3. Build the project:
   ```
   mvn clean package
   ```
4. Run the application:
   ```
   java -jar target/ryanair-flights-1.0.0.jar
   ```
   Or using Maven:
   ```
   mvn spring-boot:run
   ```

## API Documentation
### Endpoint
- **GET** `/ryanair/interconnections`
    - **Query Parameters**:
        - `departure`: Departure airport code
        - `arrival`: Arrival airport code
        - `departureDateTime`: Departure date and time in ISO 8601 format (e.g., `2025-10-01T12:00`)
        - `arrivalDateTime`: Arrival date and time in ISO 8601 format (e.g., `2025-10-02T12:00`)

### Example Request
```
GET /ryanair/interconnections?departure=BCN&arrival=DUB&departureDateTime=2025-10-01T07:00&arrivalDateTime=2025-10-01T16:00
```

### Example Response
```json
[
  {
    "stops": 0,
    "legs": [
      {
        "flightDepartureTime": "2025-10-01T11:20:00",
        "flightArrivalTime": "2025-10-01T13:05:00",
        "flightNumber": "3976",
        "flightFrom": "BCN",
        "flightTo": "DUB"
      }
    ]
  },
  {
    "stops": 0,
    "legs": [
      {
        "flightDepartureTime": "2025-10-01T13:45:00",
        "flightArrivalTime": "2025-10-01T15:30:00",
        "flightNumber": "6874",
        "flightFrom": "BCN",
        "flightTo": "DUB"
      }
    ]
  },
  {
    "stops": 1,
    "legs": [
      {
        "flightDepartureTime": "2025-10-01T10:05:00",
        "flightArrivalTime": "2025-10-01T11:30:00",
        "flightNumber": "3800",
        "flightFrom": "BCN",
        "flightTo": "BHX"
      },
      {
        "flightDepartureTime": "2025-10-01T14:45:00",
        "flightArrivalTime": "2025-10-01T15:50:00",
        "flightNumber": "665",
        "flightFrom": "BHX",
        "flightTo": "DUB"
      }
    ]
  },
  {
    "stops": 1,
    "legs": [
      {
        "flightDepartureTime": "2025-10-01T08:40:00",
        "flightArrivalTime": "2025-10-01T10:35:00",
        "flightNumber": "3182",
        "flightFrom": "BCN",
        "flightTo": "AGP"
      },
      {
        "flightDepartureTime": "2025-10-01T13:15:00",
        "flightArrivalTime": "2025-10-01T15:20:00",
        "flightNumber": "7047",
        "flightFrom": "AGP",
        "flightTo": "DUB"
      }
    ]
  }
]
```

## Testing
To run tests:
```
mvn test
```

## Acknowledgments
- Thanks to Ryanair for providing the external APIs used in this project.
- Special thanks to the contributors and the open-source community for their support.
