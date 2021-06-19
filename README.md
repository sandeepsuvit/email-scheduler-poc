# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.1/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.5.1/reference/htmlsingle/#using-boot-devtools)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.5.1/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Validation](https://docs.spring.io/spring-boot/docs/2.5.1/reference/htmlsingle/#boot-features-validation)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.5.1/reference/htmlsingle/#production-ready)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.5.1/reference/htmlsingle/#boot-features-developing-web-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

### Quartz Resources

* https://docs.spring.io/spring-boot/docs/2.5.1/reference/html/features.html#features.quartz
* https://www.callicoder.com/spring-boot-quartz-scheduler-email-scheduling-example/
* https://www.tutorialsbuddy.com/quartz-with-spring-boot-and-mysql-example
* https://www.baeldung.com/spring-quartz-schedule
* https://medium.com/@ChamithKodikara/spring-boot-2-quartz-2-scheduler-integration-a8eaaf850805

### Usage

- To schedule an email job trigger a POST to `http://localhost:8080/emails/schedule` with the following payload

```json
{
    "email": "jhon@mail.com",
    "subject": "Testing email schedule",
    "body": "Hi Jhon this is a test message",
    "deliverOn": "2021-06-19T21:21:00",
    "timeZone": "Asia/Kolkata"
}
```

**Note:** The field `deliverOn` denotes when the email needs to be triggered.

- To schedule a message job trigger a POST to `http://localhost:8080/messsags/schedule` with he following payload

```json
{
    "content": "This is a test message",
    "makeVisibleAt": "2021-06-19T21:24:00"
}
```

- At any point in time if you need to unschedule a scheduled message trigger a DELETE to `http://localhost:8080/messages/{messageId}/unschedule` where `messageId` is the primary key of the message stored in the db.