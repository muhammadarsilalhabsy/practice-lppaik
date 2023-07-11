# Activity API Spec

## Search Activity

Method : GET

URL : /api/v1/activity

Request Header :

- X-API-TOKEN: "token-example" (Mandatory)

Request Param:

- title : `String`, judul kegiatan/ activity title, `using like query` (optional)
- jurusan : `String`, jurusan user, `using where query` (optional)
- page : `Integer` start from 0, default 0
- size : `Integer` default 10

Response Success(2xx):

```json
{
  "data": [
    {
      "id": "1",
      "title": "title example",
      "image": "image-example.png",
      "location": "university class example",
      "description": "description about activity example",
      "link": "video, materi",
      "time": "09:30 - 10:30"
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

## Create Activity

Method : POST

URL : /api/v1/activity

Request Header :

- X-API-TOKEN: "admin-token-example" (Mandatory)

Request Body :

```json
{
  "title": "title example",
  "image": "image-example.png",
  "location": "university class example",
  "description": "description about activity example",
  "link": "video, materi",
  "time": "09:30 - 10:30"
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

## Add activity for mahasiswa with id

Method : POST

URL : /api/v1/activity/{activityId}/for/{mahasiswaId}

Request Header :

- X-API-TOKEN: "admin-or-ketua-token-example" (Mandatory)

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

## Update Activity

Method : PATCH

URL : /api/v1/activity/{activityId}

Request Header :

- X-API-TOKEN: "admin-token-example" (Mandatory)

Request Body :

```json
{
  "title": "new title example",
  "image": "new image-example.png",
  "location": "new university class example",
  "description": "new description about activity example",
  "link": "video, materi",
  "time": "09:30 - 10:30"
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

## Delete Activity

Method : Delete

URL : /api/v1/activity/{activityId}

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