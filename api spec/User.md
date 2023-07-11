# User API spec

## User Register (test success)

Method : POST

URL : /api/v1/users/register

Request Body :

```json
{
  "username": "2191xxx",
  "password": "password123",
  "name" : "jhone doe",
  "email" : "example@gmail.com",
  "jurusanId" : "j001",
  "gender": "MALE"
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

## User Login

Method : POST

URL : /api/v1/auth/login

Request Body :

```json
{
  "username": "2191xxx",
  "password": "password123"
}
```

Response Success(2xx):
```json
{
  "data": {
    "token": "token-example",
    "expiredAt": 1000000000
  }
}
```

Response Failed(4xx):
```json
{
  "error": "example some error massage..."
}
```

## User Logout

Method : DELETE

URL : /api/v1/auth/logout

Request Header : 

- X-API-TOKEN: "token-example" (Mandatory) 

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

## Get Current User

Method : GET

URL : /api/v1/users/current

Request Header :

- X-API-TOKEN: "token-example" (Mandatory)

Response Success(2xx):
```json
{
  "data": {
      "username": "2191xxx",
      "password": "password123",
      "name" : "jhone doe",
      "email" : "example@gmail.com",
      "jurusan" : "PAUD",
      "avatar": "avatar.png",
      "role": "MAHASISWA",
      "gender": "MALE"
  }
}
```

Response Failed(4xx):
```json
{
  "error": "example some error massage..."
}
```

## Get BTQ Details Current User

Method : GET

URL : /api/v1/users/current/btq/details

Request Header :

- X-API-TOKEN: "token-example" (Mandatory)

Response Success(2xx):

```json
{
  "data": [
    {
      "id": 1,
      "activity": "example activity",
      "day": "sunday",
      "tutor": "219191"
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

## Search User

Method : GET

URL : /api/v1/users/current

Request Header :

- X-API-TOKEN: "token-example" (Mandatory)

Request Param:

- identity : `String`, user username or name, `using where query and like` (optional)
- jurusan : `String`, jurusan user, `using where query` (optional)
- page : `Integer` start from 0, default 0
- size : `Integer` default 10


Response Success(2xx):
```json
{
  "data": [
        {
        "username": "2191xxx",
        "password": "password123",
        "name" : "jhone doe",
        "email" : "example@gmail.com",
        "jurusan" : "PAUD",
        "avatar": "avatar.png",
        "role": "MAHASISWA",
        "gender": "MALE"
      }
  ],
  "paging": {
    "currentPage": 0,
    "totalPage": 1,
    "size": 10
  }
}
```

Response Failed(4xx):
```json
{
  "error": "example some error massage..."
}
```

## Update current user

Method : PATCH

URL : /api/v1/users/current

Request Header :

- X-API-TOKEN: "token-example" (Mandatory)

Response Success(2xx):

```json
{
  "password": "new pass example",
  "name":"new name example",
  "avatar": "new-img.png",
  "email":"new@gmail.com",
  "gender": "FEMALE"
}
```

Response Failed(4xx):
```json
{
  "error": "example some error massage..."
}
```