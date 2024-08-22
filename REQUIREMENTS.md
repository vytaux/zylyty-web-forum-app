Web Forum App
Welcome to the "Web Forum App" challenge! Your mission, should you choose to accept it, is to build a dynamic REST API for a bustling web forum. Utilize the power of Java to bring your code to life and manage your data with the robust MySQL (Version 8) database system. From user registration to lively discussions across various categories, your application will be the cornerstone of a thriving online community. Get ready to showcase your skills and create a space where ideas flourish!


Let's Start!
To start, just clone this Git repository:
git clone https://5e8rqfmxpl7kwdw4qw:HfvYUTawq1RxmhuuwSi1FROwhmcI1PaCYvMpExI1@git.zylyty.com/17239334339696dad4097-67f3-41e0-842d-029310e9bdb2.git

Delivery
Your challenge code should reside in the given Git repository.

Our evaluators will fetch the code from your Git repository and evaluate the latest commit in the main branch.

Please make sure that the repository is up to date before you press submit.

Also, it is required that you provide a Dockerfile in the root directory of the repository (no need for a docker-compose.yaml, although you can use one to test yourself locally if you like, but we just look for the Dockerfile).

This Dockerfile must build and run your project out of the box with no extra steps (only the server, we mount any required database automatically for you when testing).

In other words, we should be able to docker run <image> and have your challenge up and running.

Finally, do not forget to read from the environment variables we describe in this instructions, specially because our evaluators will provide your backend with a database, if needed, and communicate to your backend the connection details using those environment variables.

Good luck!

Environment Variables
Name	Description
ADMIN_API_KEY	The secret API key used to call the admin endpoints (the key that goes into the Token header of some requests)
API_LISTENING_PORT	The port on which the API is listening for HTTP requests
DB_HOST	The hostname of the database
DB_NAME	The name of the database
DB_PASSWORD	The password of the database
DB_PORT	The port of the database
DB_USERNAME	The username of the database
Attention:
No other environment variables will be injected when testing, so please make sure you don't expect any other environment variables besides the ones listed here.


User Registration Endpoint
The User Registration Endpoint allows new users to register to the web forum. Upon sending a POST request to this endpoint with a JSON body containing username, password, and email, the server will process the registration. A successful registration will return a 201 Created status code. If the provided data is invalid, such as an improperly formatted email or a weak password, the server will respond with a 400 Bad Request. In cases where the email is already associated with an existing account, the server will return a 418 I'm a teapot status code, indicating that the user cannot register again with the same email or username.

Example Request:
POST /user/register
Content-Type: application/json

{
"username": "newUser123",
"password": "userPassword123",
"email": "newuser123@example.com"
}
Response Codes:
201 Created: Registration was successful.
400 Bad Request: The provided data was invalid or incomplete.
418 I'm a teapot: The user is already registered with the provided email or username.
This endpoint is crucial for expanding the user base of the web forum and ensuring that user data starts off with a secure foundation.


Login Endpoint
The Login Endpoint is designed to authenticate users by verifying their credentials. When a POST request is sent to this endpoint with a JSON body containing username and password, the server checks these credentials against the stored user database. If the credentials are valid, the server responds with a 200 OK status, along with the user's username and email in the response body, and sets a session cookie in the response headers to maintain the user's logged-in state.

Example Request:
POST /user/login
Content-Type: application/json

{
"username": "existingUser",
"password": "userPassword123"
}
Successful Response:
Status Code: 200 OK
Response Body:
{
"username": "existingUser",
"email": "user@example.com"
}
Response Headers: Contains a Set-Cookie header with a session cookie in the form session=<session token of your choice>.
Error Handling:
401 Unauthorized: This status is returned if the username or password is incorrect, indicating that authentication has failed. The endpoint is also secure against SQL injection and large payload attacks, responding with 401 in such cases to protect the system's integrity.
This endpoint is vital for user interaction with the web forum, enabling secure access to user-specific features and maintaining session continuity.

List and Create Categories Endpoint
The List and Create Categories Endpoints serve dual purposes in managing forum categories. The "Create Categories" endpoint allows the creation of new categories when provided with a valid admin API key and a JSON body containing a list of category names. Upon successful creation, it returns a 201 Created status code. Conversely, the "List Categories" endpoint enables users to retrieve a list of all existing categories, including a default "Default" category that is always present, by sending a GET request. A successful request to list categories returns a 200 OK status with a JSON array of category names.

Example Create Categories Request:
POST /categories
Content-Type: application/json
Token: <admin_api_key>

{
"categories": ["Technology", "Health"]
}
(Please note that we are using the "Token" header, not the "Authorization" header, in this particular instance, also do not prefix it with "Bearer")

Successful Create Response:
Status Code: 201 Created
Example List Categories Request:
GET /categories
Cookie: session=<session_cookie>
Successful List Response:
Status Code: 200 OK
Response Body:
["Default", "Technology", "Health"]
Error Handling:
400 Bad Request: Returned if the create request contains no categories or attempts to create duplicate categories.
401 Unauthorized: Returned if the request lacks a valid session cookie or admin API key, ensuring security.
201 Created: Indicates successful category creation, even when handling a large number of categories, showcasing the system's robustness and stress tolerance.
These endpoints are essential for organizing forum content and enhancing user navigation through categorization.

PS. There's always the "Default" category which can never be deleted.

Filter and Create Threads Endpoint
These endpoints are crucial for the operation of the web forum, allowing users to initiate discussions and view them by category. The "Create Thread" endpoint enables authenticated users to create a new thread within a specified category by providing a thread title and an opening post. Upon successful creation, it returns a 201 Created status code. The "List Threads" endpoint, on the other hand, allows users to retrieve threads filtered by categories, authors, and sorting preferences, ensuring a personalized browsing experience.

Example Create Thread Request:
POST /thread
Content-Type: application/json
Cookie: session=<session_cookie>

{
"category": "Default",
"title": "Exciting Discussion",
"openingPost": {
"text": "Let's talk about something exciting!"
}
}
Successful Create Response:
Status Code: 201 Created
Example List Threads Request:
GET /thread?categories=Default&newest_first=true&page=0&page_size=10
Cookie: session=<session_cookie>
Or with multiple categories (Default and Funny):

GET /thread?categories=Default&categories=Funny&newest_first=true&page=0&page_size=10
Cookie: session=<session_cookie>
Successful List Response:
Status Code: 200 OK
Response Body:
{
"threads": [
{
"id": 1,
"category": "Default",
"title": "Exciting Discussion",
"author": "user123",
"createdAt": "2024-01-01T00:00:00Z",
"openingPost": {
"text": "Let's talk about something exciting!"
}
}
]
}
Error Handling:
401 Unauthorized: Returned if the request lacks a valid session cookie, ensuring that only authenticated users can create or list threads.
201 Created: Indicates successful thread creation.
400 Bad Request: Returned if the list request is missing required filter parameters like category or if a requested category does not exist.
These endpoints enhance the forum's interactivity and engagement by facilitating discussions and enabling efficient content discovery based on user interests.

Get and Create Thread Posts Endpoint
The Get and Create Thread Posts Endpoints enable users to contribute to discussions within threads and to retrieve the series of posts within a specific thread. The "Add Posts" endpoint allows authenticated users to add one or more posts to an existing thread by providing the thread ID and a list of posts in a JSON request. Each post submission returns a 201 Created status upon success. The "Get Posts" endpoint facilitates the retrieval of all posts within a given thread, ensuring users can follow the thread's conversation flow.

Example Add Posts Request:
POST /thread/post
Content-Type: application/json
Cookie: session=<session_cookie>

{
"threadId": 1,
"posts": [
{
"text": "I completely agree with this point!"
},
{
"text": "Here's an interesting fact that might add to the discussion..."
}
]
}
Successful Add Response:
Status Code: 201 Created
Example Get Posts Request:
Note: The Get Posts endpoint should only return the Posts which are replies to the thread, i.e. the openingPost is not listed by this endpoint, which means that when a new thread gets created, this endpoint returns zero posts.

GET /thread/post?thread_id=1
Cookie: session=<session_cookie>
Successful Get Response:
Status Code: 200 OK
Response Body:
{
"id": 1,
"category": "Default",
"title": "Super test thread",
"text": "Lorem ipsum dolor sit amet...",
"author": "user123",
"createdAt": "2024-01-01T00:00:00Z",
"posts": [
{
"author": "user123",
"text": "I completely agree with this point!",
"createdAt": "2024-01-01T01:00:00Z"
},
{
"author": "user456",
"text": "Here's an interesting fact that might add to the discussion...",
"createdAt": "2024-01-01T02:00:00Z"
}
]
}
Error Handling:
401 Unauthorized: Returned if the request lacks a valid session cookie, ensuring that only authenticated users can add or retrieve posts.
400 Bad Request: Returned if the specified thread does not exist, ensuring users interact only with valid content.
201 Created: Indicates successful addition of posts to a thread, enhancing the thread's value and engagement.
These endpoints are essential for fostering dynamic interactions within the forum, allowing users to build upon existing discussions and to easily access the evolving conversation within threads.


Search Endpoint
The Search Endpoint is a powerful feature that enables users to search across the forum for threads containing specific text, enhancing discoverability and engagement within the forum. When a GET request is made to this endpoint with a search query, the server scans through the titles, posts, and content within threads to find matches to the query string. The response includes the IDs of threads containing the matching text along with snippets of the content where the match was found, providing context to the user. The search query is case-insensitive.

Example Search Request:
GET /search?text=but smith was actually a banana
Cookie: session=<session_cookie>
Successful Search Response:
Status Code: 200 OK
Response Body:
{
"searchResults": {
"31": [
"...astonishing happened – but Smith was ACTUALLY A BANANA. This surreal transformation..."
],
"72": [
"...one amazing day, but Smith was actually a banana. What the hell?...."
]
}
}
Error Handling:
401 Unauthorized: Returned if the request lacks a valid session cookie, ensuring that only authenticated users can perform searches.
Notice in the examples that the search query result string must contain 3 words before and 3 words after the matching query (consider that a word is any sequence of non-whitespace characters). If no matches are found, the searchResults object should be empty.



Delete Thread Endpoint
The Delete Thread Endpoint enables administrators to remove threads from the forum, ensuring the ability to moderate content and maintain the forum's quality. This endpoint requires an admin API key for authentication, ensuring that only authorized personnel can delete threads. When a DELETE request is sent to this endpoint with a specific thread ID, the server processes the request and, if successful, the thread is permanently removed from the forum.

Example Delete Thread Request:
DELETE /thread?id=2
Token: <admin_api_key>
(Please note that we are using the "Token" header, not the "Authorization" header, in this particular instance, also do not prefix it with "Bearer")

Successful Deletion Response:
Status Code: 204 No Content: Indicates the thread has been successfully deleted without returning any content.
Error Handling:
401 Unauthorized: Returned if the request lacks a valid admin API key, preventing unauthorized deletions.
400 Bad Request: Returned if the specified thread does not exist, ensuring only valid threads can be targeted for deletion.
This endpoint is crucial for forum administration, allowing for the removal of inappropriate or outdated content, thus maintaining a healthy and engaging community environment.


Delete Categories Endpoint
The Delete Categories Endpoint provides forum administrators with the capability to remove specific categories from the forum, except for the protected "Default" category which is integral to the forum's structure and cannot be deleted. This functionality is crucial for maintaining the relevancy and organization of the forum's content.

Example Delete Category Request:
DELETE /categories?category=Tech
Token: <admin_api_key>
(Please note that we are using the "Token" header, not the "Authorization" header, in this particular instance, also do not prefix it with "Bearer")

Successful Deletion Response:
Status Code: 200 OK: Indicates that the specified category has been successfully deleted.
Error Handling:
401 Unauthorized: Returned when the request is made without a valid admin API key, ensuring that only authorized users can delete categories.
400 Bad Request: Returned if the specified category does not exist or is the "Default" category, which is not deletable.
This endpoint is essential for forum administration, allowing for dynamic restructuring of the forum's categorization as topics become more or less relevant over time.

On deleting a category, all threads associated with that category are also deleted.


Import Users via CSV Endpoint
This endpoint facilitates the bulk registration of new users through CSV file uploads, streamlining the user onboarding process for administrators. This endpoint requires an admin API key for authentication, ensuring that only authorized individuals can import user data. Upon receiving a POST request with the CSV content, the server parses the CSV to register each user, adhering to the format of username,password,email.

Example Import Users CSV Request:
POST /csv
Content-Type: text/csv
Token: <admin_api_key>

username,password,e-mail
user1,password1,user1@example.com
user2,password2,user2@example.com
(Please note that we are using the "Token" header, not the "Authorization" header, in this particular instance, also do not prefix it with "Bearer")

Successful Import Response:
Status Code: 201 Created: Indicates that the users have been successfully registered.
Error Handling:
401 Unauthorized: Returned if the request is made without a valid admin API key, ensuring secure user data management.
400 Bad Request: Returned for various errors such as invalid CSV format, invalid user data (e.g., incorrect email format), or duplicate usernames or e-mails within the CSV, ensuring data integrity and preventing user conflicts.
200 OK: Returned on an empty CSV import, indicating the request was processed successfully without any user additions.
This endpoint is invaluable for forums expecting a large influx of users, allowing for efficient, bulk user setup while maintaining high data quality and security standards.