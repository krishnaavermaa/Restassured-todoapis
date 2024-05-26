package com.cts.reskilling.todoapis;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cts.reskilling.todoapis.Utility.ExcelUtility;

import static io.restassured.RestAssured.*;

import java.io.IOException;

@Test
public class UserTest {

	private String jwtLoginToken;

	@DataProvider(name = "userDataProvider")
	public Object[][] userDataProvider() throws IOException {
		return ExcelUtility.readExcel("User");
	}

	@Test(dataProvider = "userDataProvider", priority = 1)
	public void testCreateValidUser(String username, String email, String password) {
		System.out.println("-------------Create valid user----------------------");
		given().header("Content-Type", "application/json")
				.body("{\r\n" + "    \"username\":\"" + username + "\",\r\n" + "    \"email\":\"" + email + "\",\r\n"
						+ "    \"password\":\"" + password + "\"\r\n" + "}")
				.when().post("http://localhost:8080/user/signup").then().log().all();
		System.out.println("-----------------------------------");
	}

	@Test(dataProvider = "userDataProvider", dependsOnMethods = "testCreateValidUser", priority = 2)
	public void testLoginUsingValidUserData(String username, String email, String password) {
		System.out.println("-------------login Using Valid User Data----------------------");
		jwtLoginToken = given().header("Content-Type", "application/json")
				.body("{\r\n" + "    \"username\":\"" + username + "\",\r\n" + "    \"password\":\"" + password
						+ "\"\r\n" + "}")
				.when().post("http://localhost:8080/user/login").then().extract().response().asString();
		System.out.println(jwtLoginToken);
		System.out.println("-----------------------------------");
	}

	@Test(dependsOnMethods = "testLoginUsingValidUserData", priority = 3)
	public void testLogoutUsingValidData() {
		System.out.println("--------------logout Using Valid Data---------------------");
		given().header("Authorization", "Bearer " + jwtLoginToken).when().post("http://localhost:8080/user/logout").then().log()
				.all();
	}
}
