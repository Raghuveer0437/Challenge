# challenge

Application will able to create a account, retrieves the account details if already exists and able to transfer the funds between any two accounts.

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Gradle 7.6.1](https://gradle.org/install/)

## Running the application locally

1. Please clone the codebase  from the github using below URL.
```bash
git clone https://github.com/Raghuveer0437/Challenge.git
```
2. Your default branch is main you need to checkout master branch
```bash
git checkout master
```
3.There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.dws.challenge.ChallengeApplication` class from your IDE.

4.After the application is up on default port 8080, Please check the below CURL from postman
```bash
curl --location --request POST 'http://localhost:8080/v1/accounts/fundTransfer' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id":1,
    "fromAccountId":1234,
    "toAccountId":5678,
    "transferAmount": 1
}'
```




