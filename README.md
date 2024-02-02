# Albert Heijn Backend Technical Assignment

# Project

Developed using:

- Kotlin;
- Spring Boot 3.0.6;
- SQLite;

## Product decisions

- Vehicle id is an internal code that identifies a unique vehicle, for that reason, a validation was added to ensure
  that it follows the structure: 3 letters - 3 numbers;
- From the description, it was understood that multiple deliveries can be carried out by one vehicle;
- The user is allowed to enter deliveries in the past, but not in the future, given we do not have a status
  for `FUTURE_DELIVERY`;
- Authentication was kept out of this version, but in order to take the next steps, it would be mandatory to have it
  implemented, along with audit logs;
- It wasn't clear whether a user could move a delivery from `DELIVERED` to `IN_PROGRESS`. Given this is a system to be
  used on the go, mistakes can happen, hence why it was made possible to move between those states;

# Technicalities

## Details

- Ideally, we would have a specific endpoint that would allow us to register vehicles, but given we do not, as part of
  this MVP1, every time a new delivery is created, if there's no vehicle with the given `vehicleId`, a new entry is
  created, which is then associated with the newly created delivery;
- The endpoint `PATCH /deliveries/bulk-update` runs all the changes under the same transaction, meaning that if one
  update fails, all the already stored updates will be undone;
- Given the nature of the entities involved in this solution, it was decided to create 2 tables, named `Delivery`
  and `Vehicle`,
- Given Albert Heijn exists in different countries, meaning potentially different timezones, it was decided to store all
  the dates in `UTC`;
- Given that this microservice is expected to be interacted with by users, descriptive messages were included for error scenarios;

## Database Schema

The tables `Delivery` and `Vehicles` have a `nx1` relationship, meaning that many deliveries can be handled by the same
vehicle.

### Table Delivery

Represents the delivery entries.

- **id**: UUID that uniquely identifies this entry;
- **status**: status of the delivery (DELIVERED, IN_PROGRESS);
- **started_at**: timestamp represents the time when the delivery was started (UTC);
- **finished_at**: timestamp when the delivery was finished (UTC);
- **deliveredBy**: UUID identifying the vehicle which is making the delivery;

----

### Table Vehicle

Represents the vehicle.

- **id:**: UUID that uniquely identifies this vehicle;
- **vehicleId**: code associated with this vehicle;

## REST endpoints

### Create Delivery

#### Request

```
POST /deliveries HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "vehicleId": "AHV-124",
  "startedAt": "2024-01-31T14:00:00.000Z",
  "status": "IN_PROGRESS"
}
```

#### Response

```
{
    "id": "afbffa28-2f7e-484e-b19c-0cacdab316e7",
    "vehicleId": "AHV-124",
    "startedAt": "2024-01-31T14:00:00Z",
    "finishedAt": null,
    "status": "IN_PROGRESS"
}
```

### Update Delivery

#### Request

```
PATCH /deliveries/<UUID>
Host: localhost:8080
Content-Type: application/json

{
  "finishedAt": "2024-01-31T20:00:00.000Z",
  "status": "IN_PROGRESS"
}
```

#### Response

```
{
    "id": "15df1ca6-3860-4286-b3da-51ee128ddfa0",
    "vehicleId": "AHV-124",
    "startedAt": "2024-01-31T14:00:00Z",
    "finishedAt": null,
    "status": "IN_PROGRESS"
}
```

### Bulk Update Deliveries

#### Request

```
PATCH /deliveries/bulk-update
Host: localhost:8080
Content-Type: application/json

[
  {
    "id": "f1c45e99-4d17-40f8-9a9b-4821ca8984e1",
    "finishedAt": "2024-01-31T19:00:00.000Z",
    "status": "DELIVERED"
  },
    {
    "id": "15df1ca6-3860-4286-b3da-51ee128ddfa0",
    "finishedAt": "2024-01-31T19:00:00.000Z",
    "status": "DELIVERED"
  }
]
```

#### Response

```
{
    "deliveries": [
        {
            "id": "f1c45e99-4d17-40f8-9a9b-4821ca8984e1",
            "vehicleId": "AHV-124",
            "startedAt": "2024-01-31T14:00:00Z",
            "finishedAt": "2024-01-31T19:00:00Z",
            "status": "DELIVERED"
        },
        {
            "id": "15df1ca6-3860-4286-b3da-51ee128ddfa0",
            "vehicleId": "AHV-124",
            "startedAt": "2024-01-31T14:00:00Z",
            "finishedAt": "2024-01-31T19:00:00Z",
            "status": "DELIVERED"
        }
    ]
}
```

### Get Business Summary

#### Request

```
GET /deliveries/business-summary
Host: localhost:8080
```

#### Response

```
{
    "deliveries": 3,
    "averageMinutesBetweenDeliveryStart": 240
}
```

## How to run

Run the file name `run.sh` at the root level of the project, or

`docker build -t ah . && docker run -p 8080:8080 ah`

from your command line.

## Questions

- According to the documentation, it's not possible to modify the `started_at` attribute of a delivery. Wouldn't it be
  interesting for the users to be able to do so?

## Next steps

- Audit logs - which can then be used to keep track of all the changes done, and ensure illegal behaviors are
  identified;
- Events - given the information in this service is important, and can be used to drive multiple other internal flows,
  it would be interesting to have async events published into the environment, allowing other domains to react to
  delivery related updates;
- Replace the current solution, which relies on exceptions to inform the user of business validations
  with [Either](https://apidocs.arrow-kt.io/arrow-core/arrow.core/-either/index.html), so that exceptions can be used
  for exceptional scenarios only;
- As part of this project, it was decided to use SQLite as database. Given its rather limited features, it'd be
  interesting to move into a more robust database;
- Expand our test suite to include proper integration tests;
- Authentication - it was not included as part of this MVP1, which poses a great threat to the reliability of our
  system. For this version, it is advisable to make it only accessible internally, as a first step;
- As stated before, entries in the `Vehicle` table are created along with `Deliveries`. It would be interesting that a
  separate set of endpoints is created, so that the `create delivery` endpoint is not responsible for anything except
  creating deliveries;
- Include logs;
- Depending on the amount of users, it would be interesting to expand the stack so that it can better handle back pressure, for example, by using [WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html);