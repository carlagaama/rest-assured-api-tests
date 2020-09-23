package tasks.apitest;

import groovy.json.JsonOutput;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;
import tasks.apitest.domain.Tasks;

public class APITest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8001/tasks-backend";
    }

    @Test
    public void shouldReturnTasks() {
        RestAssured
            .given()
            .when()
                .get("/todo")
            .then()
                .statusCode(HttpStatus.SC_OK)
            ;
    }

    @Test
    public void shouldSuccessSaveATask() {
        Tasks tasks = new Tasks();
        tasks.setTask("do this today!");
        tasks.setDueDate("2020-10-20");

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(JsonOutput.toJson(tasks))
                .when()
                    .post("/todo")
                .then()
                    .statusCode(HttpStatus.SC_CREATED)
                    .log().all()
                ;
    }

    @Test
    public void shouldNotSavePastDate() {
        Tasks task = new Tasks("the day cannot be in past :( ", "2010-01-01");
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(JsonOutput.toJson(task))
                .when()
                    .post("/todo")
                .then()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("message", CoreMatchers.is("Due date must not be in past"))
                ;
    }
}
