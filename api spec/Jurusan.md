# Jurusan API Spec

## Get Jurusan

Method : GET

URL : /api/v1/jurusan


Response Success(2xx):

```json
{
  "data": [
    {
      "id": "j0001",
      "name": "title example"
    }
  ]
}
```

Response Failed(4xx):

```json
{
  "error": "example some error massage..."
}
```

## Create Jurusan

Method : POST

URL : /api/v1/jurusan

Request Header :

- X-API-TOKEN: "admin-token-example" (Mandatory)

Request Body :

```json
{
  "id": "j0001",
  "name": "title example"
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

## Update Jurusan

Method : PATCH

URL : /api/v1/jurusan/{jurusanId}

Request Header :

- X-API-TOKEN: "admin-token-example" (Mandatory)

Request Body :

```json
{
  "name": "new title example"
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

## Delete Jurusan

Method : DELETE

URL : /api/v1/jurusan/{jurusanId}

Request Header :

- X-API-TOKEN: "admin-token-example" (Mandatory)

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