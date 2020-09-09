### curl samples (application deployed on localhost with no application context).
> For windows use `Git Bash`

#### get All Users
`curl --location --request GET 'localhost:8080/users' --header 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

#### get Users 100001
`curl --location --request GET 'localhost:8080/users/100001' --header 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

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