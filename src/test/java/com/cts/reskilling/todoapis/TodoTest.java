package com.cts.reskilling.todoapis;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import com.cts.reskilling.todoapis.Utility.ExcelUtility;
import com.cts.reskilling.todoapis.model.Todo;

import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

@Test(dependsOnMethods = "com.cts.reskilling.todoapis.UserTest.testLoginUsingValidUserData")
public class TodoTest {

//	private String updatedTodo = "{\r\n" + "    \"completed\":true\r\n" + "}";
	private String jwtLoginToken;
	private String baseUrl;
	private Properties properties;
	private Todo updatedTodo;
	private String todoCreateEndpoint;
	private String todoDeleteEndpoint;
	private String todoUpdateEndpoint;
	private String todoDeleteAllEndpoint;
	private String todoRetrieveEndpoint;
	private String todoOverduesEndpoint;
	private String todoTodayEndpoint;
	private String userLoginEndpoint;
	private String userLogoutEndpoint;

	@DataProvider(name = "validTodoDataProvider")
	public Object[][] validTodoDataProvider() throws IOException {
		return ExcelUtility.readExcel("ValidTodos");
	}

	@DataProvider(name = "invalidTodoDataProvider")
	public Object[][] invalidTodoDataProvider() throws IOException {
		return ExcelUtility.readExcel("InvalidTodos");
	}

	@BeforeClass
	public void pre() {
		File pFile;
		try {
			pFile = new File("src/test/resources/application.properties");
			properties.load(new FileInputStream(pFile));
		} catch (Exception e) {
			Reporter.log("Error while loading properties file");
			Assert.fail("Error while loading properties file");
		}
		baseUrl = properties.getProperty("api.base.url");
		todoCreateEndpoint=properties.getProperty("todo.create.endpoint");
		todoDeleteEndpoint=properties.getProperty("todo.delete.endpoint");
		todoUpdateEndpoint=properties.getProperty("todo.update.endpoint");
		todoDeleteAllEndpoint=properties.getProperty("todo.deleteall.endpoint");
		todoRetrieveEndpoint=properties.getProperty("todo.retrieve.endpoint");
		todoOverduesEndpoint=properties.getProperty("todo.overdues.endpoint");
		todoTodayEndpoint=properties.getProperty("todo.today.endpoint");
		userLoginEndpoint=properties.getProperty("user.login.endpoint");
		userLogoutEndpoint=properties.getProperty("user.logout.endpoint");
		
		jwtLoginToken = given().header("Content-Type", "application/json")
				.body("{\r\n" + "    \"username\":\"" + properties.getProperty("username") + "\",\r\n"
						+ "    \"password\":\"" + properties.getProperty("password") + "\"\r\n" + "}")
				.when().post(baseUrl+userLoginEndpoint).then().extract().response().toString();
		updatedTodo = new Todo("", properties.getProperty("updated.todo.title"),
				properties.getProperty("updated.todo.description"),
				LocalDate.parse(properties.getProperty("updated.todo.endDate"), DateTimeFormatter.ISO_DATE),
				Boolean.valueOf(properties.getProperty("updated.todo.isCompleted")),
				Long.valueOf(properties.getProperty("updated.todo.priority")));

	}

	@Test(dataProvider = "validTodoDataProvider")
	public void testCreateValidTodo(String testId, String title, String description, String priority, String endDate,
			String isCompleted) {
		Reporter.log("Test 'create valid to-do' for testId: " + testId, true);
//		Todo todo=new Todo(null, title, description, LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE), Boolean.valueOf(isCompleted), Long.valueOf(priority));
		Response response = given().header("Authorization", "Bearer " + jwtLoginToken)
				.body("{\r\n" + "    \"title\":\"" + title + "\",\r\n" + "    \"description\":\"" + description
						+ "\",\r\n" + "    \"priority\":" + priority + ",\r\n" + "    \"endDate\":\"" + endDate
						+ "\"\r\n" + "    \"completed\":\"" + isCompleted + "\",\r\n" + "}")
				.when().post(baseUrl+todoCreateEndpoint).then().extract().response();
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		Assert.assertEquals(response.getStatusCode(), 201, response.asString());
	}

	@Test(dataProvider = "invalidTodoDataProvider")
	public void testCreateInvalidTodo(String testId, String title, String description, String priority, String endDate,
			String isCompleted) {
		Reporter.log("Test 'create in-valid to-do' for testId: " + testId, true);
		Response response = given().header("Authorization", "Bearer " + jwtLoginToken)
				.body("{\r\n" + "    \"title\":\"" + title + "\",\r\n" + "    \"description\":\"" + description
						+ "\",\r\n" + "    \"priority\":" + priority + ",\r\n" + "    \"endDate\":\"" + endDate
						+ "\"\r\n" + "    \"completed\":\"" + isCompleted + "\",\r\n" + "}")
				.when().post(baseUrl+todoCreateEndpoint).then().extract().response();
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		Assert.assertFalse(response.getStatusCode() == 201, response.asString());
	}

	@Test
	public void testShowAllTodo() {
		Reporter.log("Test 'Show All Todo'");
		Response response = given().header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get(baseUrl+todoRetrieveEndpoint).then().extract()
				.response();
		List<Todo> todoList = null;
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		if (response.getStatusCode() == 200) {
			try {
				todoList = response.jsonPath().getList("", Todo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Assert.assertNotNull(todoList, response.toString());
		} else
			Assert.assertEquals(response.getStatusCode(), 204);
	}

	@Test
	public void testShowCompletedTodo() {
		Reporter.log("Test 'Show Completed Todo'");
		Response response = given().queryParams("completed", "true").header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get(baseUrl+todoRetrieveEndpoint).then().extract()
				.response();
		List<Todo> todoList = null;
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		if (response.getStatusCode() == 200) {
			try {
				todoList = response.jsonPath().getList("", Todo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Assert.assertNotNull(todoList, response.toString());
		} else
			Assert.assertEquals(response.getStatusCode(), 204);
	}

	@Test
	public void testShowIncompleteTodo() {
		Reporter.log("Test 'Show Incomplete Todo'");
		Response response = given().queryParams("completed", "false").header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get(baseUrl+todoRetrieveEndpoint).then().extract()
				.response();
		List<Todo> todoList = null;
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		if (response.getStatusCode() == 200) {
			try {
				todoList = response.jsonPath().getList("", Todo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Assert.assertNotNull(todoList, response.toString());
		} else
			Assert.assertEquals(response.getStatusCode(), 204);
	}

	@Test
	public void testGetTodosByPriority(Integer priority) {
		Reporter.log("Test 'Get Todos By Priority' for priority: " + priority);
		Response response = given().queryParams("priority", priority).header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get(baseUrl+todoRetrieveEndpoint).then().extract()
				.response();
		List<Todo> todoList = null;
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		if (response.getStatusCode() == 200) {
			try {
				todoList = response.jsonPath().getList("", Todo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Assert.assertNotNull(todoList, response.toString());
		} else
			Assert.assertEquals(response.getStatusCode(), 204);
	}

	@Test
	public void testGetTodosByEndDate(String endDate) {
		Reporter.log("Test 'Get Todos By End Date' for end date: " + endDate);
		Response response = given().queryParams("enddate", /* "2024-05-17" */endDate)
				.header("Authorization", "Bearer " + jwtLoginToken).header("Content-Type", "application/json").when()
				.get(baseUrl+todoRetrieveEndpoint).then().extract().response();
		List<Todo> todoList = null;
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		if (response.getStatusCode() == 200) {
			try {
				todoList = response.jsonPath().getList("", Todo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Assert.assertNotNull(todoList, response.toString());
		} else
			Assert.assertEquals(response.getStatusCode(), 204);
	}

	@Test
	public void testGetTodosAlphabetically() {
		Reporter.log("Test 'Get Todos Alphabetically'");
		Response response = given().queryParams("sort", "az").header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get(baseUrl+todoRetrieveEndpoint).then().extract()
				.response();
		List<Todo> todoList = null;
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		if (response.getStatusCode() == 200) {
			try {
				todoList = response.jsonPath().getList("", Todo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Assert.assertNotNull(todoList, response.toString());
		} else
			Assert.assertEquals(response.getStatusCode(), 204);
	}

	@Test
	public void testGetTodosReverseAlphabetic() {
		Reporter.log("Test 'Get Todos Reverse Alphabetic'");
		Response response = given().queryParams("sort", "za").header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get(baseUrl+todoRetrieveEndpoint).then().extract()
				.response();
		List<Todo> todoList = null;
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		if (response.getStatusCode() == 200) {
			try {
				todoList = response.jsonPath().getList("", Todo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Assert.assertNotNull(todoList, response.toString());
		} else
			Assert.assertEquals(response.getStatusCode(), 204);
	}

	@Test
	public void testShowAllOverdueTodo() {
		Reporter.log("Test 'Show All Overdue Todo'");
		Response response = given().header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get(baseUrl+todoOverduesEndpoint).then()
				.extract().response();
		List<Todo> todoList = null;
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		if (response.statusCode() == 200) {
			if (response.toString().startsWith("{")) {
				try {
					todoList = response.jsonPath().getList("", Todo.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Assert.assertNotNull(todoList, response.toString());
			} else
				Assert.assertTrue(true, response.toString());
		} else
			Assert.fail();
	}

	@Test
	public void testShowTodaysTodo() {
		Reporter.log("Test 'Show Today's Todo'");
		Response response = given().header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get(baseUrl+todoTodayEndpoint).then()
				.extract().response();
		List<Todo> todoList = null;
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		if (response.statusCode() == 200) {
			if (response.toString().startsWith("{")) {
				try {
					todoList = response.jsonPath().getList("", Todo.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Assert.assertNotNull(todoList, response.toString());
			} else
				Assert.assertTrue(true, response.toString());
		} else
			Assert.fail();
	}

	@Test
	public void testGetTodoById(Integer todoId) {
		Reporter.log("Test 'Get Todo By Id' for todo ID: " + todoId);
		Response response = given().header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().get(baseUrl+todoRetrieveEndpoint + todoId).then()
				.extract().response();
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		Assert.assertEquals(response.getStatusCode(), 200, response.asString());
	}

	@Test
	public void testUpdateTodoById(Integer todoId) {
		Reporter.log("Test 'Update Todo' for todo ID: " + todoId);
		Response response = given().header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").body(updatedTodo).when()
				.put(baseUrl+todoUpdateEndpoint + todoId).then().extract().response();
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		Assert.assertEquals(response.getStatusCode(), 200, response.asString());
	}

	@Test
	public void testDeleteTodoById(Integer todoId) {
		Reporter.log("Test 'Delete Todo By Id' for todo ID: " + todoId);
		Response response = given().header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().delete(baseUrl+todoDeleteEndpoint + todoId)
				.then().extract().response();
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		Assert.assertEquals(response.getStatusCode(), 204, response.asString());
	}

	@Test
	public void testDeleteAllTodo(@Optional(value = "false") Boolean isConfirm) {
		Reporter.log("Test 'Delete All Todos' with confirmation as: " + isConfirm);
		Response response = given().queryParams("confirm", isConfirm).header("Authorization", "Bearer " + jwtLoginToken)
				.header("Content-Type", "application/json").when().delete(baseUrl+todoDeleteAllEndpoint).then()
				.extract().response();
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		Assert.assertEquals(response.getStatusCode(), 204, response.asString());
	}
	
	@AfterClass
	public void post() {
		given().header("Authorization", "Bearer " + jwtLoginToken).when().post(baseUrl+userLogoutEndpoint);
	}
}
