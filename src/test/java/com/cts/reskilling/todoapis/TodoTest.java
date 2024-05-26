package com.cts.reskilling.todoapis;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cts.reskilling.todoapis.Utility.ExcelUtility;

import static io.restassured.RestAssured.*;

import java.io.IOException;

@Test(dependsOnMethods = "com.cts.reskilling.todoapis.UserTest.testLoginUsingValidUserData")
public class TodoTest {

	private final String updatedTodo = "{\r\n" + "    \"completed\":true\r\n" + "}";

	private String jwtLoginToken;

	@DataProvider(name = "todoDataProvider")
	public Object[][] todoDataProvider() throws IOException {
		return ExcelUtility.readExcel("Todo");
	}

	@Test(dataProvider = "todoDataProvider")
	public void testCreateValidTodo(String title, String description, String priority, String endDate,
			String isCompleted) {
		System.out.println("-------------Create Valid Todo----------------------");
		given().header("Authorization", "Bearer " + jwtLoginToken)
				.body("{\r\n" + "    \"title\":\""+title+"\",\r\n"
						+ "    \"description\":\""+description+"\",\r\n"
						+ "    \"priority\":"+priority+",\r\n" + "    \"endDate\":\""+endDate+"\"\r\n" + "    \"completed\":\"" + isCompleted + "\",\r\n"+"}")
				.when().post("http://localhost:8080/todo/create").then().log().all();
	}

	@Test
	public void testShowAllTodo() {
		System.out.println("-------------Show All Todo----------------------");
		given().header("Authorization", "Bearer " + jwtLoginToken).header("Content-Type", "application/json").when()
				.get("http://localhost:8080/todo").then().log().all();
	}

	@Test
	public void testShowCompletedTodo() {
		System.out.println("-------------Show Completed Todo----------------------");
		given().queryParams("completed", "true").header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get("http://localhost:8080/todo").then().log().all();
	}

	@Test
	public void testShowIncompletedTodo() {
		System.out.println("-------------Show In-Completed Todo----------------------");
		given().queryParams("completed", "false").header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get("http://localhost:8080/todo").then().log().all();
	}

	@Test
	public void testGetTodosByPriority() {
		System.out.println("-------------Get Todos By Priority----------------------");
		given().header("Authorization", "Bearer " + jwtLoginToken).header("Content-Type", "application/json").when()
				.get("http://localhost:8080/todo?priority=10").then().log().all();
	}

	@Test
	public void testGetTodosByEndDate() {
		System.out.println("-------------Get Todos By End Date----------------------");
		given().queryParams("enddate", "2024-05-17").header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get("http://localhost:8080/todo").then().log().all();
	}

	@Test
	public void testGetTodosAlphabetically() {
		System.out.println("-------------Get Todos Alphabetically----------------------");
		given().queryParams("sort", "az").header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get("http://localhost:8080/todo").then().log().all();
	}

	@Test
	public void testGetTodosReverseAlphabetic() {
		System.out.println("-------------Get Todos Reverse Alphabetic----------------------");
		given().queryParams("sort", "za").header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get("http://localhost:8080/todo").then().log().all();
	}

	@Test
	public void testShowAllOverdueTodo() {
		System.out.println("-------------Show All Overdue Todo----------------------");
		given().header("Authorization", "Bearer " + jwtLoginToken).header("Content-Type", "application/json").when()
				.get("http://localhost:8080/todo/overdue").then().log().all();
	}

	@Test
	public void testShowTodaysTodo() {
		System.out.println("-------------Show All Overdue Todo----------------------");
		given().header("Authorization", "Bearer " + jwtLoginToken).header("Content-Type", "application/json").when()
				.get("http://localhost:8080/todo/today").then().log().all();
	}

	@Test
	public void testGetTodoById(int todoId) {
		System.out.println("-------------Get Todo By Id----------------------");
		given().header("Authorization", "Bearer " + jwtLoginToken).header("Content-Type", "application/json").when()
				.get("http://localhost:8080/todo/"+todoId).then().log().all();
	}

	@Test
	public void testUpdateTodoById(int todoId) {
		System.out.println("-------------Update Todo----------------------");
		given().header("Authorization", "Bearer " + jwtLoginToken).header("Content-Type", "application/json").body(updatedTodo)
				.when().put("http://localhost:8080/todo/update/"+todoId).then().log().all();
	}

	@Test
	public void testDeleteTodoById(int todoId) {
		System.out.println("-------------Delete Todo By Id----------------------");
		given().header("Authorization", "Bearer " + jwtLoginToken).header("Content-Type", "application/json").when()
				.delete("http://localhost:8080/todo/delete/"+todoId).then().log().all();
	}
}
