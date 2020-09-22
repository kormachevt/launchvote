# Requirements
Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) **without frontend**.

The task is:

Build a voting system for deciding where to have lunch.

 * 2 types of users: admin and regular users
 * Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
 * Menu changes each day (admins do the updates)
 * Users can vote on which restaurant they want to have lunch at
 * Only one vote counted per user
 * If user votes again the same day:
    - If it is before 11:00 we asume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

Each restaurant provides new menu each day.

As a result, provide a link to github repository. It should contain the code, README.md with API documentation and couple curl commands to test it.

-----------------------------
P.S.: Make sure everything works with latest version that is on github :)

P.P.S.: Asume that your API will be used by a frontend developer to build frontend on top of that.


## curl samples (application deployed on localhost with no application context).
> For windows use `Git Bash`

#### get All Users
`curl --location --request GET 'localhost:8080/admin/users' --header 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

#### get Users 100001
`curl --location --request GET 'localhost:8080/admin/users/100001' --header 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

#### register Users
`curl --location --request POST 'localhost:8080/profile/register' --header 'Content-Type: application/json' --data-raw '{
     "login": "user2",
     "password": "user2",
     "email": "user@gmail2.com",
     "roles": [
         "USER","ADMIN"
     ]
 }'`

#### get Profile
`curl --location --request GET 'localhost:8080/profile' --header 'Authorization: Basic dXNlckBnbWFpbC5jb206cGFzc3dvcmQ='`

#### get All Restaurants
`curl --location --request GET 'localhost:8080/restaurants' --header 'Authorization: Basic dXNlckBnbWFpbC5jb206cGFzc3dvcmQ='`

#### get All Restaurants with dishes
`curl --location --request GET 'localhost:8080/restaurants/with-dishes' --header 'Authorization: Basic dXNlckBnbWFpbC5jb206cGFzc3dvcmQ='`

#### update Restaurants dishes
`curl --location --request PUT 'localhost:8080/restaurants/100002/dishes' --header 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu' --header 'Content-Type: application/json'--data-raw '[
     {
         "description": "New_Alfa_1",
         "price": 300
     },
     {
         "description": "New_Alfa_1",
         "price": 500
     }
 ]'`

#### make a Vote
`curl --location --request POST 'localhost:8080/votes?restaurantId=100003' --header 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

#### get voting Results
`curl --location --request GET 'localhost:8080/votes' --header 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`