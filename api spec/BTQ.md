# BTQ API Spec

## Add Details BTQ to Mahasiswa

Method : POST

URL : /api/v1/btq/tutor/{mahasiswaId}/details

Request Header :

- X-API-TOKEN: "example-tutor-toke" (Mandatory)

Request Body :

```json
{
  "id": 1,
  "activity": "example activity",
  "day": "sunday",
  "tutor": "219191"
}
```

Response Success(2xx):
```json
{
  "data": "OK"
}
```

Response Failed(4xx):
```json
{
  "error": "example some error massage..."
}
```

## Remove Details BTQ to Mahasiswa

Method : DELETE

URL : /api/v1/btq/tutor/{detailsId}/details

Request Header :

- X-API-TOKEN: "example-tutor-toke" (Mandatory)

Response Success(2xx):
```json
{
  "data": "OK"
}
```

Response Failed(4xx):
```json
{
  "error": "example some error massage..."
}
```

## Update Details BTQ to Mahasiswa

Method : DELETE

URL : /api/v1/btq/tutor/{detailsId}/details

Request Header :

- X-API-TOKEN: "example-tutor-toke" (Mandatory)

Request Body :

```json
{
  "activity": "example new update activity"
}
```

Response Success(2xx):
```json
{
  "data": "OK"
}
```

Response Failed(4xx):
```json
{
  "error": "example some error massage..."
}
```