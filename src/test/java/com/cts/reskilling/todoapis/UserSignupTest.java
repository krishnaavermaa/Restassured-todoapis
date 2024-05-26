package com.cts.reskilling.todoapis;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cts.reskilling.todoapis.Utility.ExcelUtility;
import com.cts.reskilling.todoapis.model.User;

import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Test
public class UserSignupTest {

	private String baseUrl;
	private Properties properties;
	private String userSignupEndpoint;
	
	@DataProvider(name = "validSignupDataProvider")
	public Object[][] validSignupDataProvider() throws IOException {
		return ExcelUtility.readExcel("ValidSignups");
	}
		
	@DataProvider(name = "invalidSignupDataProvider")
	public Object[][] invalidSignupDataProvider() throws IOException {
		return ExcelUtility.readExcel("InvalidSignups");
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
		userSignupEndpoint=properties.getProperty("user.signup.endpoint");
	}

	@Test(dataProvider = "validSignupDataProvider")
	public void testCreateValidUser(String testId, String username, String email, String password) {
		Reporter.log("Testing 'Create Valid User' for testId: "+testId,true);
		User newUser=new User(username, email, password);
		Response response=given().header("Content-Type", "application/json")
				.body(newUser)
				.when().post(baseUrl+userSignupEndpoint).then().extract().response();
		Reporter.log("Status code: "+response.getStatusCode()+", Response body: "+response.asString(),true);
		Assert.assertEquals(response.getStatusCode(), 201,response.asString());
	}
	
	@Test(dataProvider = "invalidSignupDataProvider")
	public void testCreateInvalidUser(String testId, String username, String email, String password) {
		Reporter.log("Testing 'Create In-valid User' for testId: "+testId,true);
		User newUser=new User(username, email, password);
		Response response=given().header("Content-Type", "application/json")
				.body(newUser)
				.when().post(baseUrl+userSignupEndpoint).then().extract().response();
		Reporter.log("Status code: "+response.getStatusCode()+", Response body: "+response.asString(),true);
		Assert.assertFalse(response.getStatusCode()==201);
	}

}
