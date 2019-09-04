# Uploader
Simple API for upload and shared files between users.

# How To Use
- Use Maven for package project
- After building the project, you can run it using the command: "java -jar uploader-0.0.1.jar --com.github.pavelvil.uploader.directory=/file/dir"

# Using
For testing API you can use PostmanCanary.
- /api/register (POST). Single public endpoint. Used to register a user. 
Accepts an object in the format:
```javascript
{
  "email" : "user_email@mail.ru",
  "password" : "12345userpas"
}
```
Return CREATED status.
- /api/file (GET). Returns all current user files.
- /api/file/{id} (GET). Return file by id. Returned NOT_FOUND status if file not found or OK status and JSON object in the format:
```javascript
{
  "id" : "fileId",
  "data" : "fileData",
  "name" : "fileName"
}
```
- /api/file (POST). Saves the file that came from the form-data body. Returns the ID of the saved file and CREATED status.
- /api/share (POST). Share file between users. Accepts an object in the format:
```javascript
{
  "email" : "who_to_allow_access_to_the_file",
  "fileId" : "shared_file_id"
}
```
