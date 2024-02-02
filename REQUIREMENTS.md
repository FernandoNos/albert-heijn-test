## Assignment

The assignment is to implement Kotlin microservice using Spring boot. The expected REST endpoints schema is described
below. \
The assignment is rather open-ended and expects you to think of code structure and implementation yourself. \
Follow the requirements below and be ready to support decisions made in implementing this service.

### Endpoints

| Endpoint                             | Description                                                                                                                                                                                                                                                                                                                                                                                                                                      | Request body example                                                                                                                                                                     | Response Example                                                                                                                                                                                                                                                                                                     |
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **POST** /deliveries                 | Creates a new delivery.<br>Status is only allowed to be `"IN_PROGRESS"` or `"DELIVERED"`                                                                                                                                                                                                                                                                                                                                                         | <pre lang="json">{<br>  "vehicleId": "AHV-589",<br>  "startedAt": "2023-10-09T12:45:34.678Z",<br>  "status": "IN_PROGRESS"<br>}</pre>                                                    | <pre lang="json">{<br>  "id": "69201507-0ae4-4c56-ac2d-75fbe27efad8",<br>  "vehicleId": "AHV-589"<br>  "startedAt": "2023-10-09T12:45:34.678Z",<br>  "finishedAt": null,<br>  "status": "IN_PROGRESS" <br/>}</pre>                                                                                                   |
| **PATCH** /deliveries/{id}           | Update a delivery.<br>Only the `finishedAt` and `status` fields are allowed to be updated. `finishedAt` must be provided for status DELIVERED, it shouldn't be allowed otherwise.                                                                                                                                                                                                                                                                | <pre lang="json">{<br>  "finishedAt": "2023-10-11T13:20:55.000Z",<br>  "status": "DELIVERED"<br>}</pre>                                                                                  | <pre lang="json">{<br>  "id": "69201507-0ae4-4c56-ac2d-75fbe27efad8",<br>  "vehicleId": "AHV-589"<br>  "startedAt": "2023-10-09T12:45:34.678Z",<br>  "finishedAt": "2023-10-11T13:20:55.000Z",<br>  "status": "DELIVERED" <br/>}</pre>                                                                               |
| **PATCH** /deliveries/bulk-update    | Update multiple deliveries in the same endpoint. For the implementation you are not allowed to use bulk operations that the database might provide, implement this functionality in code.                                                                                                                                                                                                                                                        | <pre lang="json">[<br>  {<br>    "id": "69201507-0ae4-4c56-ac2d-75fbe27efad8",<br>    "finishedAt": "2023-10-11T13:20:55.000Z",<br>    "status": "DELIVERED"<br>  },<br>  ...<br>]</pre> | <pre lang="json">{<br>  "deliveries": [<br>    {<br>      "id": "69201507-0ae4-4c56-ac2d-75fbe27efad8",<br>      "vehicleId": "AHV-589"<br>      "startedAt": "2023-10-09T12:45:34.678Z",<br>      "finishedAt": "2023-10-11T13:20:55.000Z",<br>      "status": "DELIVERED" <br/>    },<br>    ...<br>  ]<br>}</pre> |
| **GET** /deliveries/business-summary | The business wants a summary of yesterday's deliveries (Amsterdam time).<br>The summary must include how many deliveries were **started**. The summary should also include the average time between delivery start. This means if there are 3 deliveries that started at `01:00`, `03:00` and `09:00` the time between starting deliveries is `2 hours` (01:00-03:00) and `6 hours` (03:00 - 09:00) so the average is `4 hours` or `240 minutes` |                                                                                                                                                                                          | <pre lang="json">{<br>  "deliveries": 3,<br>  "averageMinutesBetweenDeliveryStart": 240<br/>}</pre>                                                                                                                                                                                                                  |

## Requirements

- Please do not spend more than 3 hours on assignment.
- Write the assignment in **Kotlin**.
- Use **Git** and commit often, so we can see the iterations made on the code.
- Assume this is **production code** that will run in production at Albert Heijn and will be maintained/modified for a
  long time. Not all requirements for "production ready" are listed here, we expect you to decide what is necessary and
  to support your reasoning.
- The above REST endpoints are implemented (also following the requirements in the description).
- The data is stored in a `database`, you can choose what type.
- This is a customer facing application, which means a website will use this data to display it to the user.

## Sending in the assignment

- We expect a docker compose file that we can run with `docker-compose up` which should spin up a functional application
  at port 8080.
- Create a `.zip` or `.tar.gz` of the entire project **including the .git folder**.
- Send in only the zip file.

Thank you for your interest and time invested into making this assignment.



