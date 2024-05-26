package com.cts.reskilling.todoapis;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cts.reskilling.todoapis.Utility.ExcelUtility;

import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Test
public class UserLoginLogoutTest {

	private String baseUrl;
	private Properties properties;
	private String userLoginEndpoint;
	private String userLogoutEndpoint;
	
	@DataProvider(name = "validLoginDataProvider")
	public Object[][] validSignupDataProvider() throws IOException {
		return ExcelUtility.readExcel("ValidLogins");
	}

	@DataProvider(name = "invalidLoginDataProvider")
	public Object[][] invalidSignupDataProvider() throws IOException {
		return ExcelUtility.readExcel("InvalidLogins");
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
		userLoginEndpoint=properties.getProperty("user.login.endpoint");
		userLogoutEndpoint=properties.getProperty("user.logout.endpoint");
	}

	@Test(dataProvider = "validLoginDataProvider")
	public void testLoginLogoutValidUser(String testId, String username, String email, String password) {
		Reporter.log("Test 'Login a Valid User' for testId: " + testId, true);
		Response response = given()
				.header("Content-Type", "application/json").body("{\r\n" + "    \"username\":\"" + username + "\",\r\n"
						+ "    \"password\":\"" + password + "\"\r\n" + "}")
				.when().post(baseUrl+userLoginEndpoint).then().extract().response();
		String jwtLoginToken = response.asString();
		Reporter.log("Status code: " + response.getStatusCode() + ",Response body: " + jwtLoginToken, true);
		Assert.assertTrue(response.getStatusCode() == 200 && !jwtLoginToken.isBlank() && !jwtLoginToken.contains(" "),
				jwtLoginToken);
		logoutTest(testId, jwtLoginToken);
	}

	@Test(dataProvider = "invalidLoginDataProvider")
	public void testLoginInvalidUser(String testId, String username, String email, String password) {
		Reporter.log("Test 'Login an In-valid User' for testId: " + testId, true);
		Response response = given()
				.header("Content-Type", "application/json").body("{\r\n" + "    \"username\":\"" + username + "\",\r\n"
						+ "    \"password\":\"" + password + "\"\r\n" + "}")
				.when().post(baseUrl+userLoginEndpoint).then().extract().response();
		Reporter.log("Status code: " + response.getStatusCode() + ", body: " + response.asString(), true);
		Assert.assertFalse(response.getStatusCode() == 200, response.asString());
	}

	public void logoutTest(String testId, String jwtToken) {
		Reporter.log("Test 'Logout of logged-in user' for testId: " + testId, true);
		Response response = given().header("Authorization", "Bearer " + jwtToken).when()
				.post(baseUrl+userLogoutEndpoint).then().extract().response();
		Reporter.log("Status code: " + response.getStatusCode() + ", Response body: " + response.asString(), true);
		Assert.assertEquals(response.getStatusCode(), 200, response.asString());
	}

}
