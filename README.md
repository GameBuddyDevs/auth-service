# GameBuddy Auth Service
GameBuddy Auth Service is a microservice in the GameBuddy project, responsible for handling authentication and authorization functionalities. This service is built using Spring Java and provides a set of RESTful APIs to manage user authentication, registration, and other related operations. The service also includes Admin APIs to manage user accounts and reported content.

## APIs
### Auth APIs

##### POST /auth/login

- Description: This API allows users to log in to the GameBuddy platform.
- Request Body: LoginRequest
- Response: LoginResponse
- HTTP Status: 200 (OK) if successful, 401 (Unauthorized) if login credentials are invalid.

##### POST /auth/register

- Description: This API allows users to register on the GameBuddy platform.
- Request Body: RegisterRequest
- Response: RegisterResponse
- HTTP Status: 201 (Created) if successful, 400 (Bad Request) if registration data is invalid.

##### POST /auth/verify

- Description: This API verifies the user's registration code sent during registration.
- Request Body: VerifyRequest
- Response: VerifyResponse
- HTTP Status: 200 (OK) if successful, 400 (Bad Request) if verification fails.

##### POST /auth/username

- Description: This API allows users to set their username after successful registration.
- Request Header: Authorization (Bearer Token)
- Request Body: UsernameRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if successful, 401 (Unauthorized) if the token is invalid.

##### POST /auth/details

- Description: This API allows users to update their account details (age, games, keywords, etc.).
- Request Header: Authorization (Bearer Token)
- Request Body: DetailsRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if successful, 401 (Unauthorized) if the token is invalid.

##### POST /auth/validateToken

- Description: This API validates the authenticity of the provided token.
- Request Header: Authorization (Bearer Token)
- Response: TokenResponse
- HTTP Status: 200 (OK) if the token is valid, 401 (Unauthorized) if the token is invalid.

##### POST /auth/sendCode

- Description: This API sends a verification code to the user's registered email for account verification.
- Request Body: SendCodeRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the code is sent successfully, 400 (Bad Request) if there's an issue with the request.

##### PUT /auth/change/pwd

- Description: This API allows users to change their account password.
- Request Header: Authorization (Bearer Token)
- Request Body: ChangePwdRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the password is changed successfully, 401 (Unauthorized) if the token is invalid.

##### PUT /auth/change/avatar

- Description: This API allows users to change their profile avatar.
- Request Header: Authorization (Bearer Token)
- Request Body: ChangeAvatarRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the avatar is changed successfully, 401 (Unauthorized) if the token is invalid.

##### PUT /auth/change/age

- Description: This API allows users to change their age in the account details.
- Request Header: Authorization (Bearer Token)
- Request Body: ChangeAgeRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the age is changed successfully, 401 (Unauthorized) if the token is invalid.

##### PUT /auth/change/games

- Description: This API allows users to update the games they play in the account details.
- Request Header: Authorization (Bearer Token)
- Request Body: ChangeDetailRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the games are updated successfully, 401 (Unauthorized) if the token is invalid.

##### PUT /auth/change/keywords

- Description: This API allows users to update their keywords (interests) in the account details.
- Request Header: Authorization (Bearer Token)
- Request Body: ChangeDetailRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the keywords are updated successfully, 401 (Unauthorized) if the token is invalid.


### Admin APIs

##### GET /admin/get/blocked/users

- Description: This API allows admins to get a list of blocked users.
- Request Header: Authorization (Bearer Token)
- Response: GamerResponse
- HTTP Status: 200 (OK) if successful, 401 (Unauthorized) if the token is invalid.

##### GET /admin/get/reported/messages

- Description: This API allows admins to get a list of reported messages/content.
- Request Header: Authorization (Bearer Token)
- Response: MessageResponse
- HTTP Status: 200 (OK) if successful, 401 (Unauthorized) if the token is invalid.

##### POST /admin/ban/user/{userId}

- Description: This API allows admins to block a user by their user ID.
- Request Header: Authorization (Bearer Token)
- Path Variable: userId (The ID of the user to be blocked)
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the user is blocked successfully, 401 (Unauthorized) if the token is invalid.

##### POST /admin/unban/user/{userId}

- Description: This API allows admins to unblock a previously blocked user by their user ID.
- Request Header: Authorization (Bearer Token)
- Path Variable: userId (The ID of the user to be unblocked)
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the user is unblocked successfully, 401 (Unauthorized) if the token is invalid.

##### POST /admin/add/game

- Description: This API allows admins to add a new game to the platform.
- Request Header: Authorization (Bearer Token)
- Request Body: GameRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the game is added successfully, 401 (Unauthorized) if the token is invalid.

##### POST /admin/add/keyword

- Description: This API allows admins to add a new keyword (interest) to the platform.
- Request Header: Authorization (Bearer Token)
- Request Body: KeywordRequest
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the keyword is added successfully, 401 (Unauthorized) if the token is invalid.

##### DELETE /admin/delete/reported/message/{messageId}

- Description: This API allows admins to delete a reported message/content by its ID.
- Request Header: Authorization (Bearer Token)
- Path Variable: messageId (The ID of the reported message to be deleted)
- Response: DefaultMessageResponse
- HTTP Status: 200 (OK) if the message is deleted successfully, 401 (Unauthorized) if the token is invalid.

## Getting Started

1. Clone the GameBuddy Auth Service repository from GitHub.

2. Open the project with your preferred IDE. (Use Gradle.)

3. Configure the necessary database and messaging services (e.g., PostgreSQL, MongoDB).

4. Update the application.yml file with the database and messaging service credentials.

5. Run the application using Gradle or your preferred IDE. (Initial port is 4567. You can change it from application.yml)

## Gradle Commands
To build, test, and run the GameBuddy Auth Service, you can use the following Gradle commands:

### Clean And Build
To clean the build artifacts and build the project, run:

`./gradlew clean build`

> The built JAR file will be located in the build/libs/ directory.

### Test
To run the tests for the GameBuddy Auth Service, you can use the following Gradle command:

`./gradlew test`

> This command will execute all the unit tests in the project. The test results will be displayed in the console, indicating which tests passed and which ones failed.

Additionally, if you want to generate test reports, you can use the following command:

`./gradlew jacocoTestReport`

> This will generate test reports using the JaCoCo plugin. The test reports can be found in the build/reports/tests and build/reports/jacoco directories. The JaCoCo report will provide code coverage information to see how much of your code is covered by the tests.

### Spotless Code Formatter
This project has Spotless rules. If the code is unformatted, building the project will generate error. To format the code according to the configured Spotless rules, run:

`./gradlew spotlessApply`

### Sonarqube Analysis
To perform a SonarQube analysis of the project, first, ensure you have SonarQube configured and running. Then, run:

`./gradlew sonarqube`

### Run 
To run the GameBuddy Auth Service locally using Gradle, use the following command:

`./gradlew bootRun`

> This will start the service, and you can access the APIs at http://localhost:4567.

## Dockerizing the Project
To containerize the GameBuddy Auth Service using Docker, follow the steps below:

1. Make sure you have Docker installed on your system. You can download Docker from the official website: https://www.docker.com/get-started

2. Project already has a Dockerfile. Examine the Dockerfile in the root directory of the project. The Dockerfile define the container image configuration.

3. Build the Docker image using the Dockerfile. Open a terminal and navigate to the root directory of the project.

 `docker build -t gamebuddy-auth-service .`

 This will create a Docker image with the name **gamebuddy-auth-service**.

4. Run the Docker container from the image you just built.

 `docker run -d -p 4567:4567 --name gamebuddy-auth gamebuddy-auth-service`

 This will start the GameBuddy Auth Service container, and it will be accessible at http://localhost:4567.
