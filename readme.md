# java-challenge
java-challenge for AXA

# Tasks done
1. Controller syntax update
2. Bug fixing
3. Central logger
4. Entity update
5. UT and IT included
6. Cache feature implemented
7. Improve documents and comments
8. JWT authentication implemented
9. Devtools implemented

# Steps to use
1. Run the application
2. Create a user by using the curl:

curl --location 'localhost:8080/api/users' \
--header 'Content-Type: application/json' \
--data '{
    "userName":"alinaser",
    "password":"123456"
}'

3. Login using folowing curl:
curl --location 'localhost:8080/api/authenticate' \
--header 'Content-Type: application/json' \
--data '{
    "userName": "alinaser",
    "password":"123456"
}'

Result: 
{
    "id_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGluYXNlciIsImV4cCI6MTY4MzY4MzQ4MiwiaWF0IjoxNjgzNjQ3NDgyfQ.P5FqkY0VBB0WLhHtaWddnQzMR0WCxOgTwFFeNMz-cVI"
}

4. Copy the id_token and set in the Authorization header of the following curl:
curl --location 'localhost:8080/api/v1/employees' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGluYXNlciIsImV4cCI6MTY4MzY4MzQ4MiwiaWF0IjoxNjgzNjQ3NDgyfQ.P5FqkY0VBB0WLhHtaWddnQzMR0WCxOgTwFFeNMz-cVI' \
--header 'Content-Type: application/json' \
--data '{
    "name":"Kamal",
    "department":"CSE",
    "salary":123
}'

5. Get employee by ID. Run following curl
curl --location --request GET 'localhost:8080/api/v1/employees/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGluYXNlciIsImV4cCI6MTY4MzY4MzQ4MiwiaWF0IjoxNjgzNjQ3NDgyfQ.P5FqkY0VBB0WLhHtaWddnQzMR0WCxOgTwFFeNMz-cVI' \
--header 'Content-Type: application/json' \

6. Get all employee. Run following curl
curl --location --request GET 'localhost:8080/api/v1/employees' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGluYXNlciIsImV4cCI6MTY4MzY4MzQ4MiwiaWF0IjoxNjgzNjQ3NDgyfQ.P5FqkY0VBB0WLhHtaWddnQzMR0WCxOgTwFFeNMz-cVI' \
--header 'Content-Type: application/json' \

7. Update employee. Run following curl
curl --location --request POST 'localhost:8080/api/v1/employees/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGluYXNlciIsImV4cCI6MTY4MzY4MzQ4MiwiaWF0IjoxNjgzNjQ3NDgyfQ.P5FqkY0VBB0WLhHtaWddnQzMR0WCxOgTwFFeNMz-cVI' \
--header 'Content-Type: application/json' \
--data '{
    "name":"Ali",
    "department":"EEE",
    "salary":123
}'

8. Delete employee. Run following curl
curl --location --request DELETE 'localhost:8080/api/v1/employees/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGluYXNlciIsImV4cCI6MTY4MzY4MzQ4MiwiaWF0IjoxNjgzNjQ3NDgyfQ.P5FqkY0VBB0WLhHtaWddnQzMR0WCxOgTwFFeNMz-cVI' \
--header 'Content-Type: application/json' \
