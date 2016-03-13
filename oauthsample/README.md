# RestSecure

> A Simple implementation for securing Rest API's with FaceBook Signed Request


## Rest Securing

1. Run the Spring boot application
2. Go to <http://localhost:8080/index.html>
     * This page mocks any client or a webpage that will potentially consume the Rest API
3. Now access the secure end point <http://localhost:8080/user> with the bearer token displayed on the page 
in the Authorization header of your Rest calls.
