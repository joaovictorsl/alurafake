test:
	./mvnw clean test

run:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=dev