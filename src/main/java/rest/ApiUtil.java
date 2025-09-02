package rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonSerializable.Base;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiUtil {
	private static final Set<Integer> usedNumbers = new HashSet<>();
	private static final Random random = new Random();
	private static String BASE_URL;
	Properties prop;

	/**
	 * Retrieves the base URL from the configuration properties file.
	 *
	 * <p>
	 * This method loads the properties from the file located at
	 * <code>{user.dir}/src/main/resources/config.properties</code> and extracts the
	 * value associated with the key <code>base.url</code>. The value is stored in
	 * the static variable <code>BASE_URL</code> and returned.
	 *
	 * @return the base URL string if successfully read from the properties file;
	 *         {@code null} if an I/O error occurs while reading the file.
	 */
	public String getBaseUrl() {
		prop = new Properties();
		try (FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties")) {
			prop.load(fis);
			BASE_URL = prop.getProperty("base.url");
			return BASE_URL;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the username from the configuration properties file.
	 *
	 * <p>
	 * This method reads the properties from the file located at
	 * <code>{user.dir}/src/main/resources/config.properties</code> and returns the
	 * value associated with the key <code>username</code>.
	 *
	 * @return the username as a {@code String} if found in the properties file;
	 *         {@code null} if an I/O error occurs while reading the file.
	 */
	public String getUsername() {
		prop = new Properties();
		try (FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties")) {
			prop.load(fis);
			return prop.getProperty("username");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the password from the configuration properties file.
	 *
	 * <p>
	 * This method reads the <code>config.properties</code> file located in the
	 * <code>src/main/resources</code> directory relative to the project's root. It
	 * loads the properties from the file and returns the value associated with the
	 * <code>password</code> key.
	 *
	 * <p>
	 * If an <code>IOException</code> occurs during file access or loading, the
	 * stack trace is printed and the method returns <code>null</code>.
	 *
	 * @return the password value as a <code>String</code> from the properties file,
	 *         or <code>null</code> if an exception occurs
	 */
	public String getPassword() {
		prop = new Properties();
		try (FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties")) {
			prop.load(fis);
			return prop.getProperty("password");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sends a GET request to the specified endpoint with a cookie and optional
	 * request body.
	 *
	 * <p>
	 * This method uses RestAssured to construct and send a GET request to the given
	 * endpoint. It attaches a cookie named <code>orangehrm</code> with the provided
	 * value and sets the <code>Content-Type</code> header to
	 * <code>application/json</code>. If a request body is provided, it is included
	 * in the request.
	 *
	 * @param endpoint    the API endpoint to send the request to (relative to the
	 *                    base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param body        a map containing key-value pairs to be sent as the JSON
	 *                    request body (can be null)
	 * @return the response received from the GET request
	 */
	public CustomResponse GetHolidayData(String endpoint, String cookieValue, Map<String, String> body) {

		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		List<Object> ids = new ArrayList<>();
		List<Object> names = new ArrayList<>();
		List<Object> dates = new ArrayList<>();
		List<Object> recurrings = new ArrayList<>();
		List<Object> lengths = new ArrayList<>();
		List<Object> lengthNames = new ArrayList<>();

		JsonPath jsonPath = response.jsonPath();
		List<Map<String, Object>> data = jsonPath.getList("data");

		if (data != null) {
			for (Map<String, Object> holiday : data) {
				ids.add(holiday.get("id"));
				names.add(holiday.get("name"));
				dates.add(holiday.get("date"));
				recurrings.add(holiday.get("recurring"));
				lengths.add(holiday.get("length"));
				lengthNames.add(holiday.get("lengthName"));
			}
		} else {
			System.out.println("⚠️ 'data' field is null in response. Status code: " + statusCode);
		}

		return new CustomResponse(response, statusCode, status, ids, names, dates, recurrings, lengths, lengthNames);

	}

	/**
	 * Sends a GET request to the specified endpoint with a cookie and optional
	 * request body.
	 *
	 * <p>
	 * This method constructs a GET request using RestAssured, attaching a cookie
	 * named <code>orangehrm</code> with the specified value and setting the
	 * <code>Content-Type</code> header to <code>application/json</code>. If a body
	 * is provided, it is included in the request.
	 *
	 * @param endpoint    the relative URL endpoint to send the GET request to
	 * @param cookieValue the value for the <code>orangehrm</code> cookie
	 * @param body        a map of key-value pairs representing the request body
	 *                    (optional, may be null)
	 * @return the response received from the GET request
	 */
	public CustomResponse GetLeaveData(String endpoint, String cookieValue, Map<String, String> body) {

		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		List<Object> ids = new ArrayList<>();
		List<Object> names = new ArrayList<>();
		List<Object> dates = new ArrayList<>();
		List<Object> recurrings = new ArrayList<>();
		List<Object> lengths = new ArrayList<>();
		List<Object> lengthNames = new ArrayList<>();

		JsonPath jsonPath = response.jsonPath();
		List<Map<String, Object>> data = jsonPath.getList("data");

		if (data != null) {
			for (Map<String, Object> Leave : data) {
				ids.add(Leave.get("id"));
				names.add(Leave.get("name"));
				dates.add(Leave.get("date"));
				recurrings.add(Leave.get("recurring"));
				lengths.add(Leave.get("length"));
				lengthNames.add(Leave.get("lengthName"));
			}
		} else {
			System.out.println("⚠️ 'data' field is null in response. Status code: " + statusCode);
		}

		return new CustomResponse(response, statusCode, status, ids, names, dates, recurrings, lengths, lengthNames);
	}

	/**
	 * Executes a GET request to the given endpoint using a specified cookie and
	 * optional request body.
	 *
	 * <p>
	 * This method prepares a GET request with RestAssured, attaching a cookie named
	 * <code>orangehrm</code> with the provided value and setting the request header
	 * <code>Content-Type</code> to <code>application/json</code>. If the request
	 * body is not null, it is included in the request payload.
	 *
	 * @param endpoint    the API endpoint to target, appended to the base URL
	 * @param cookieValue the value for the <code>orangehrm</code> cookie
	 * @param body        a map containing key-value pairs to be used as the request
	 *                    body (optional)
	 * @return the response object resulting from the GET request
	 */

	public CustomResponse GetEmpCount(String endpoint, String cookieValue, Map<String, String> body) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		System.out.println("Raw response:");
		response.prettyPrint(); // ✅ Debug print

		JsonPath jsonPath = response.jsonPath();

		// ✅ Get the "count" directly from the "data" object
		Map<String, Object> data = jsonPath.getMap("data");
		int count = 0;

		count = (int) data.get("count");
		System.out.println("⚠️ 'count' key is missing inside 'data'.");

		return new CustomResponse(response, statusCode, status, count); // ⬅️ return single count
	}

	/**
	 * Sends a GET request to the specified API endpoint with an authentication
	 * cookie and optional body.
	 *
	 * <p>
	 * This method utilizes RestAssured to perform a GET request to the provided
	 * endpoint. It includes a cookie named <code>orangehrm</code> with the
	 * specified value and sets the <code>Content-Type</code> to
	 * <code>application/json</code>. If a request body is provided, it is attached
	 * to the request.
	 *
	 * @param endpoint    the relative endpoint path to be appended to the base URL
	 * @param cookieValue the value of the <code>orangehrm</code> cookie for session
	 *                    handling or authentication
	 * @param body        a map containing request body parameters (optional; may be
	 *                    null)
	 * @return the {@code Response} object containing the results of the GET request
	 */

	public CustomResponse GetLeaveType(String endpoint, String cookieValue, Map<String, String> body) {

		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (body != null) {
			request.body(body);
		}
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		List<Object> ids = new ArrayList<>();
		List<Object> names = new ArrayList<>();
		List<Object> situationals = new ArrayList<>();
		List<Object> Deletes = new ArrayList<>();

		JsonPath jsonPath = response.jsonPath();
		List<Map<String, Object>> data = jsonPath.getList("data");

		if (data != null) {
			for (Map<String, Object> Leave : data) {
				ids.add(Leave.get("id"));
				names.add(Leave.get("name"));
				situationals.add(Leave.get("situational"));
				Deletes.add(Leave.get("deleted"));

			}
		} else {
			System.out.println("⚠️ 'data' field is null in response. Status code: " + statusCode);
		}

		return new CustomResponse(response, statusCode, status, ids, names, situationals, Deletes);
	}

	/**
	 * Performs a GET request to the specified endpoint with a provided cookie and
	 * optional request body.
	 *
	 * <p>
	 * This method constructs a GET request using RestAssured, sets the
	 * <code>Content-Type</code> header to <code>application/json</code>, and
	 * includes a cookie named <code>orangehrm</code>. If the provided body map is
	 * not null, it is added to the request as the JSON payload.
	 *
	 * @param endpoint    the API endpoint to which the GET request will be sent,
	 *                    appended to the base URL
	 * @param cookieValue the value of the <code>orangehrm</code> cookie used for
	 *                    session or auth
	 * @param body        a map of key-value pairs representing the request body
	 *                    (optional; may be null)
	 * @return the {@link io.restassured.response.Response} object containing the
	 *         server's response
	 */
	public CustomResponse GetUsageReport(String endpoint, String cookieValue, Map<String, String> body) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		List<Object> names = new ArrayList<>();
		List<Object> props = new ArrayList<>();
		List<Object> sizes = new ArrayList<>();
		List<Object> pins = new ArrayList<>();
		List<Object> cellProperties = new ArrayList<>(); // Keep it List<Object> for flexibility in CustomResponse

		JsonPath jsonPath = response.jsonPath();
		List<Map<String, Object>> headers = jsonPath.getList("data.headers");

		if (headers != null) {
			for (Map<String, Object> header : headers) {
				names.add(header.get("name"));
				props.add(header.get("prop"));
				sizes.add(header.get("size"));
				pins.add(header.get("pin"));

				Object cellProp = header.get("cellProperties");
				if (cellProp instanceof Map || cellProp == null) {
					cellProperties.add(cellProp); // add map or null as-is
				} else {
					System.out.println("⚠️ Unexpected type for cellProperties: " + cellProp.getClass().getSimpleName());
					cellProperties.add(null);
				}
			}
		} else {
			System.out.println("❌ 'data.headers' is missing or empty in the response. Status code: " + statusCode);
		}

		return new CustomResponse(response, statusCode, status, props, names, sizes, pins, cellProperties);
	}

	/**
	 * Sends a PUT request to the specified endpoint with a cookie and optional
	 * request body.
	 *
	 * <p>
	 * This method constructs a PUT request using RestAssured, sets the
	 * <code>Content-Type</code> header to <code>application/json</code>, and
	 * includes a cookie named <code>orangehrm</code>. If a request body is
	 * provided, it is attached to the request.
	 *
	 * @param endpoint    the relative endpoint to which the PUT request is sent,
	 *                    appended to the base URL
	 * @param cookieValue the value of the <code>orangehrm</code> cookie for session
	 *                    or authorization purposes
	 * @param requestBody an object representing the request body (can be null)
	 * @return the {@link io.restassured.response.Response} returned from the PUT
	 *         request
	 */
	public CustomResponse PutTerminationReason(String endpoint, String cookieValue, String requestBody) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (requestBody != null) {
			request.body(requestBody);
		}

		Response response = request.put(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();
		Map<String, Object> data = jsonPath.getMap("data");

		Object id = null;
		Object name = null;

		if (data != null) {
			id = data.get("id");
			name = data.get("name");
		} else {
			System.out.println("❌ 'data' object is missing or empty in the response. Status code: " + statusCode);
		}

		return new CustomResponse(response, statusCode, status, id, name);
	}

	/**
	 * Sends a GET request to the specified API endpoint with an authentication
	 * cookie and optional body.
	 *
	 * <p>
	 * This method utilizes RestAssured to perform a GET request to the provided
	 * endpoint. It includes a cookie named <code>orangehrm</code> with the
	 * specified value and sets the <code>Content-Type</code> to
	 * <code>application/json</code>. If a request body is provided, it is attached
	 * to the request.
	 *
	 * @param endpoint    the relative endpoint path to be appended to the base URL
	 * @param cookieValue the value of the <code>orangehrm</code> cookie for session
	 *                    handling or authentication
	 * @param body        a map containing request body parameters (optional; may be
	 *                    null)
	 * @return the {@code Response} object containing the results of the GET request
	 */
	public CustomResponse DeletePim(String deleteEndPoint, String cookieValue, String requestBody) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (requestBody != null) {
			request.body(requestBody);
		}

		Response response = request.delete(BASE_URL + deleteEndPoint).then().extract().response();
		System.out.println(response);

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();

		// Extract single data value (e.g., "3" from ["3"])
		List<Object> dataList = jsonPath.getList("data");
		Object dataValue = null;

		if (dataList != null && !dataList.isEmpty()) {
			dataValue = dataList.get(0); // only one element expected
		} else {
			System.out.println("❌ 'data' array is missing or empty in the response. Status code: " + statusCode);
		}

		return new CustomResponse(response, statusCode, status, dataValue);
	}

	/**
	 * Sends a POST request to the specified endpoint with a cookie and JSON string
	 * payload.
	 *
	 * <p>
	 * This method uses RestAssured to perform a POST request to the given endpoint.
	 * It sets the <code>Content-Type</code> header to
	 * <code>application/json</code>, includes a cookie named
	 * <code>orangehrm</code>, and attaches the provided JSON string as the request
	 * body.
	 *
	 * @param endpoint    the relative URL endpoint for the POST request, appended
	 *                    to the base URL
	 * @param cookieValue the value of the <code>orangehrm</code> cookie used for
	 *                    authentication or session handling
	 * @param body        a JSON-formatted string representing the request payload
	 * @return the {@link io.restassured.response.Response} object containing the
	 *         server's response
	 */
	public CustomResponse PostPimEmp(String endpoint, String cookieValue, String requestBody) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (requestBody != null) {
			request.body(requestBody);
		}

		Response response = request.post(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();
		Map<String, Object> data = jsonPath.getMap("data");

		Object firstName = null;
		Object employeeId = null;

		if (data != null) {
			firstName = data.get("firstName");
			employeeId = data.get("employeeId");
		} else {
			System.out.println("❌ 'data' object is missing or empty in the response. Status code: " + statusCode);
		}

		return new CustomResponse(response, statusCode, status, employeeId, firstName);
	}

	/**
	 * Sends a PUT request to the specified endpoint with a cookie and JSON string
	 * payload to update employee information.
	 *
	 * <p>
	 * This method uses RestAssured to perform a PUT request to the given endpoint.
	 * It sets the <code>Content-Type</code> header to
	 * <code>application/json</code>, includes a cookie named <code>orangehrm</code>
	 * for authentication or session handling, and attaches the provided JSON string
	 * as the request body if it is not null.
	 *
	 * <p>
	 * After receiving the response, the method extracts the <code>empNumber</code>,
	 * <code>firstName</code>, and <code>lastName</code> from the <code>data</code>
	 * object in the JSON response. If the <code>data</code> object is missing or
	 * null, it logs an error message with the status code.
	 *
	 * @param endpoint    the relative URL endpoint for the PUT request, appended to
	 *                    the base URL
	 * @param cookieValue the value of the <code>orangehrm</code> cookie used for
	 *                    authentication or session handling
	 * @param requestBody a JSON-formatted string representing the request payload
	 *                    to update the employee
	 * @return the {@link CustomResponse} object containing the server's response,
	 *         status code, status line, employee number, first name, and last name
	 */

	public CustomResponse PutVimEmp(String endpoint, String cookieValue, String requestBody) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (requestBody != null) {
			request.body(requestBody);
		}

		Response response = request.put(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();
		Map<String, Object> data = jsonPath.getMap("data");

		Object id = null;
		Object name = null;
		Object lastName = null;

		if (data != null) {
			id = data.get("empNumber");
			name = data.get("firstName");
			lastName = data.get("lastName");
		} else {
			System.out.println("❌ 'data' object is missing or empty in the response. Status code: " + statusCode);
		}

		return new CustomResponse(response, statusCode, status, id, name, lastName);
	}

	/**
	 * Sends a DELETE request to the specified endpoint with a cookie and JSON
	 * string payload to delete employee information.
	 *
	 * <p>
	 * This method uses RestAssured to perform a DELETE request to the given
	 * endpoint. It sets the <code>Content-Type</code> header to
	 * <code>application/json</code>, includes a cookie named <code>orangehrm</code>
	 * for authentication or session handling, and attaches the provided JSON string
	 * as the request body if it is not null.
	 *
	 * <p>
	 * After receiving the response, the method extracts the <code>data</code> list
	 * from the JSON response. If the list is present and not empty, it retrieves
	 * the first element as the employee ID. If the <code>data</code> list is
	 * missing or empty, it logs an error message with the status code.
	 *
	 * @param endpoint    the relative URL endpoint for the DELETE request, appended
	 *                    to the base URL
	 * @param cookieValue the value of the <code>orangehrm</code> cookie used for
	 *                    authentication or session handling
	 * @param requestBody a JSON-formatted string representing the request payload
	 *                    for the deletion request
	 * @return the {@link CustomResponse} object containing the server's response,
	 *         status code, status line, and the deleted employee ID (duplicated in
	 *         both ID and name fields of the response)
	 */
	public CustomResponse DeletePimEmp(String endpoint, String cookieValue, String requestBody) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (requestBody != null) {
			request.body(requestBody);
		}

		Response response = request.delete(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String status = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();
		List<Object> dataList = jsonPath.getList("data");

		Object employeeId = null;

		if (dataList != null && !dataList.isEmpty()) {
			employeeId = dataList.get(0);
		} else {
			System.out.println("❌ 'data' object is missing or empty in the response. Status code: " + statusCode);
		}

		return new CustomResponse(response, statusCode, status, employeeId, employeeId);
	}

	/**
	 * Retrieves the password from the configuration properties file.
	 *
	 * <p>
	 * This method loads the properties from the file located at
	 * <code>{user.dir}/src/main/resources/config.properties</code> and returns the
	 * value associated with the key <code>password</code>.
	 *
	 * @return the password as a {@code String} if found in the properties file;
	 *         {@code null} if an I/O error occurs while reading the file.
	 */
	public static String generateUniqueName(String base) {
		int uniqueNumber;
		do {
			uniqueNumber = 1000 + random.nextInt(9000);
		} while (usedNumbers.contains(uniqueNumber));

		usedNumbers.add(uniqueNumber);
		return base + uniqueNumber;
	}

	/**
	 * Generates a unique numeric ID by adding a randomly generated 4-digit number
	 * to the provided base value.
	 *
	 * <p>
	 * This method generates a random number between 1000 and 9999 and ensures it is
	 * unique by checking against a maintained list of previously generated numbers
	 * (<code>usedNumbers</code>). Once a unique number is found, it is added to the
	 * <code>usedNumbers</code> set to prevent future duplication.
	 *
	 * @param base an integer value to which the generated unique 4-digit number
	 *             will be added
	 * @return the resulting unique ID as an integer, formed by adding the base to
	 *         the unique number
	 */
	public static int generateUniqueID(int base) {
		int uniqueNumber;
		do {
			uniqueNumber = 1000 + random.nextInt(9000);
		} while (usedNumbers.contains(uniqueNumber));

		usedNumbers.add(uniqueNumber);
		return base + uniqueNumber;
	}

	public CustomResponse GetVacancies(String endpoint, String cookieValue, Map<String, Object> queryParams) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		if (queryParams != null) {
			request.queryParams(queryParams);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();
		List<Object> dataList = jsonPath.getList("data");

		List<Object> id = jsonPath.getList("data.id");
		List<Object> name = jsonPath.getList("data.name");
		List<Object> description = jsonPath.getList("data.description");
		List<Object> numOfPositions = jsonPath.getList("data.numOfPositions");
		List<Object> statusList = jsonPath.getList("data.status");
		List<Object> isPublished = jsonPath.getList("data.isPublished");
		List<Object> jobTitle = jsonPath.getList("data.jobTitle");
		
		return new CustomResponse(response, statusCode, statusLine, id, name, description, numOfPositions, statusList, isPublished, jobTitle);
	}
	
	
	
	/**
	 * Sends a GET request to fetch job titles from the specified endpoint.
	 *
	 * <p>
	 * This method uses RestAssured to perform a GET request to the given endpoint.
	 * It sets the "Content-Type" header to "application/json", includes a cookie
	 * named "orangehrm" for authentication/session handling, and optionally adds
	 * query parameters if provided.
	 *
	 * <p>
	 * After receiving the response, it extracts the "data" array from the JSON
	 * response. From each item, it retrieves "id" and "title" fields and stores
	 * them in lists. These lists are wrapped inside a {@link CustomResponse} object
	 * along with the raw response, status code, and status line.
	 *
	 * @param endpoint    the relative URL endpoint for the GET request
	 * @param cookieValue the value of the "orangehrm" cookie used for authentication/session
	 * @param queryParams optional map of query parameters to include in the request
	 * @return CustomResponse containing the response, status code, status line, and extracted lists
	 */
	
	

	public CustomResponse GetJobTitles(String endpoint, String cookieValue, Map<String, Object> queryParams) {
    RequestSpecification request = RestAssured.given()
            .cookie("orangehrm", cookieValue)
            .header("Content-Type", "application/json");

    if (queryParams != null) {
        request.queryParams(queryParams);
    }

    Response response = request.get(BASE_URL + endpoint).then().extract().response();

    int statusCode = response.getStatusCode();
    String status = response.getStatusLine();

    JsonPath jsonPath = response.jsonPath();

    System.out.println("Response Body: " + response.asString());

    // ✅ Extract with correct types
    List<Integer> ids = jsonPath.getList("data.id", Integer.class);
    List<String> titles = jsonPath.getList("data.title", String.class);

    // ✅ Convert to Object lists for flexibility in CustomResponse
    List<Object> idsAsObjects = new ArrayList<>(ids);
    List<Object> titlesAsObjects = new ArrayList<>(titles);

    // ✅ Create response object
    CustomResponse customResponse = new CustomResponse(response, statusCode, status, idsAsObjects, titlesAsObjects);

    // ✅ Set lists explicitly
    customResponse.setIds(idsAsObjects);
    customResponse.setTitle(titlesAsObjects);

    return customResponse;
}
	
	
	
	
	
	/**
	 * Sends a GET request to fetch personal details of employees from the specified endpoint.
	 *
	 * <p>
	 * This method uses RestAssured to perform a GET request. It sets the "Content-Type" header to
	 * "application/json" and includes a cookie named "orangehrm" for authentication/session handling.
	 * Optional query parameters can be provided via the queryParams map.
	 *
	 * <p>
	 * After receiving the response, the method extracts key fields from the "data" section of the JSON.
	 * It handles both cases where "data" is a single object or an array:
	 * <ul>
	 *     <li>empNumber</li>
	 *     <li>firstName</li>
	 *     <li>lastName</li>
	 *     <li>nationality.name</li>
	 * </ul>
	 *
	 * <p>
	 * The extracted values, along with the raw response, status code, and status line, are wrapped
	 * inside a {@link CustomResponse} object for further use.
	 *
	 * @param endpoint    the relative URL endpoint for the GET request
	 * @param cookieValue the value of the "orangehrm" cookie used for authentication/session
	 * @param queryParams optional map of query parameters to include in the request
	 * @return CustomResponse containing the response, status code, status line, and extracted personal details
	 */
	
	
	

	public CustomResponse GetEmpPersonalDetails(String endpoint, String cookieValue, Map<String, Object> queryParams) {
	    RequestSpecification request = RestAssured.given()
	            .cookie("orangehrm", cookieValue)
	            .header("Content-Type", "application/json");

	    if (queryParams != null) {
	        request.queryParams(queryParams);
	    }

	    Response response = request.get(BASE_URL + endpoint).then().extract().response();

	    Integer statusCode = response.getStatusCode();
	    String status = response.getStatusLine();

	    JsonPath jsonPath = response.jsonPath();

	    System.out.println("Response Body: " + response.asString());

	    // ✅ Safe extraction for empNumber (works if "data" is object or array)
	    Integer empNumber = null;
	    String lastName = null;
	    String firstName = null;
	    String nationalityName = null;

	    if (jsonPath.get("data.empNumber") != null) {  
	        // Case 1: "data" is an object
	        empNumber = jsonPath.getInt("data.empNumber");
	        lastName = jsonPath.getString("data.lastName");
	        firstName = jsonPath.getString("data.firstName");
	        nationalityName = jsonPath.getString("data.nationality.name");
	    } else if (jsonPath.get("data[0].empNumber") != null) {  
	        empNumber = jsonPath.getInt("data[0].empNumber");
	        lastName = jsonPath.getString("data[0].lastName");
	        firstName = jsonPath.getString("data[0].firstName");
	        nationalityName = jsonPath.getString("data[0].nationality.name");
	    }

	    return new CustomResponse(
	            response,
	            statusCode,
	            status,
	            empNumber,
	            firstName,
	            lastName,
	            nationalityName
	    );
	}



	
	/**
	 * Sends a GET request to fetch employee data from the given endpoint.
	 *
	 * <p>
	 * Uses RestAssured with "Content-Type: application/json" and the "orangehrm" cookie for authentication.
	 *
	 * <p>
	 * Extracts key employee fields from the "data" section of the response. Handles both:
	 * <ul>
	 *     <li>Single object</li>
	 *     <li>Array of objects</li>
	 * </ul>
	 * Fields extracted: empNumber, firstName, lastName, employeeId.
	 *
	 * <p>
	 * Stores extracted values in lists and wraps them, along with status code, status line, and raw response,
	 * in a {@link CustomResponse}.
	 *
	 * @param endpoint    API endpoint
	 * @param cookieValue "orangehrm" cookie value
	 * @param body        optional body (not used in GET)
	 * @return CustomResponse with response details and employee data lists
	 */

	
	

public CustomResponse GetEmpData(String endpoint, String cookieValue, String body) {
    Response response = RestAssured.given()
            .header("Content-Type", "application/json")
            .cookie("orangehrm", cookieValue)
            .get(BASE_URL + endpoint)
            .then()
            .extract()
            .response();

    Integer statusCode = response.getStatusCode();
    String statusLine = response.getStatusLine();

    JsonPath jsonPath = response.jsonPath();

    // Initialize lists
    List<Integer> empNumbers = new ArrayList<>();
    List<String> firstNames = new ArrayList<>();
    List<String> lastNames = new ArrayList<>();
    List<String> employeeNumbers = new ArrayList<>();

    // Handle "data" being array or single object
    Object dataNode = jsonPath.get("data");

    if (dataNode instanceof List) {
        // ✅ data is an array
        empNumbers = jsonPath.getList("data.empNumber");
        firstNames = jsonPath.getList("data.firstName");
        lastNames = jsonPath.getList("data.lastName");
        employeeNumbers = jsonPath.getList("data.employeeId");
    } else if (dataNode instanceof Map) {
        // ✅ data is a single object
        empNumbers.add(jsonPath.getInt("data.empNumber"));
        firstNames.add(jsonPath.getString("data.firstName"));
        lastNames.add(jsonPath.getString("data.lastName"));
        employeeNumbers.add(jsonPath.getString("data.employeeId"));
    }
	CustomResponse customResponse = new CustomResponse(
			response,
			statusCode,
			statusLine,
			empNumbers,
			firstNames,
			lastNames,
			employeeNumbers,
			response.asString()
	);

    customResponse.setEmpNumbers(empNumbers);
    customResponse.setFirstNames(firstNames);
    customResponse.setLastNames(lastNames);
    customResponse.setEmployeeNumbers(employeeNumbers);
    customResponse.setResponseBody(response.asString());

    return customResponse;
}



/**
 * Sends a GET request to fetch the workweek schedule of an employee.
 *
 * <p>
 * Uses RestAssured with the "orangehrm" cookie for authentication.
 *
 * <p>
 * Extracts the "data" section, which maps day numbers to work hours, and stores it in a Map.
 *
 * <p>
 * Wraps the response, status code, status line, raw response, and workweek map in a {@link CustomResponse}.
 *
 * @param endpoint    API endpoint to fetch workweek
 * @param cookieValue "orangehrm" cookie value
 * @return CustomResponse containing workweek data and response details
 */




public CustomResponse GetLeaveWorkWeek(String endpoint, String cookieValue) {
    Response response = RestAssured
            .given()
            .cookie("orangehrm", cookieValue)
            .when()
            .get(BASE_URL + endpoint)
            .then()
            .extract()
            .response();

    int statusCode = response.getStatusCode();
    String statusLine = response.getStatusLine();

    JsonPath jsonPath = response.jsonPath();

    // Extract "data" (workweek schedule per day, where keys are day numbers)
    Map<String, Integer> workweekData = jsonPath.getMap("data", String.class, Integer.class);

	CustomResponse cr = new CustomResponse(
			response,
			statusCode,
			statusLine,
			workweekData,
			response.asString()
	);

	cr.setWorkweekData(workweekData);
    // Return in CustomResponse
    return cr;
}




/**
 * Sends a POST request to create a new employee.
 *
 * <p>
 * Uses RestAssured with "orangehrm" cookie for authentication and sets the
 * "Content-Type" to "application/json". The request body is passed as a JSON string.
 *
 * <p>
 * Extracts the response, status code, and status line, and wraps them in a
 * {@link CustomResponse} object.
 *
 * <p>
 * Lists for employee IDs or names are not needed for this request, so they are set to null.
 *
 * @param endpoint    API endpoint for creating an employee
 * @param cookieValue "orangehrm" cookie value for authentication
 * @param body        JSON string containing employee data
 * @return CustomResponse containing response, status code, and status line
 */




public CustomResponse PostEmployee(String endpoint, String cookieValue, String body) {
    Response response = RestAssured
        .given()
            .relaxedHTTPSValidation()
            .header("Content-Type", "application/json")
            .cookie("orangehrm", cookieValue)
            .body(body)
        .when()
            .post(BASE_URL + endpoint)   // baseUrl should already be defined in apiUtil
        .then()
            .extract()
            .response();

    // Wrap response inside CustomResponse (basic version)
    return new CustomResponse(
        response,
        response.getStatusCode(),
        response.getStatusLine(),
        null,   // empStatusIdList not needed here
        null    // empStatusNameList not needed here
    );
}
public CustomResponse createReport(String endpoint, String cookieValue, String body) {
    Response response = RestAssured
        .given()
            .header("Content-Type", "application/json")
            .cookie("orangehrm", cookieValue)
            .body(body)
        .when()
            .post(BASE_URL + endpoint)
        .then()
            .extract()
            .response();

    JsonPath jsonPath = response.jsonPath();

    // Extract data fields into lists
    List<Integer> ids = new ArrayList<>();
    List<String> names = new ArrayList<>();

    Object dataNode = jsonPath.get("data");

    if (dataNode instanceof Map) {
        Map<String, Object> dataMap = (Map<String, Object>) dataNode;
        Object idObj = dataMap.get("id");
        Object nameObj = dataMap.get("name");

        if (idObj != null) ids.add((Integer) idObj);
        if (nameObj != null) names.add(nameObj.toString());
    }

    // Return as CustomResponse
    return new CustomResponse(
        response,
        response.getStatusCode(),
        response.getStatusLine(),
        ids,
        names
    );
}


/**
 * Sends a POST request to add a new candidate to the system.
 *
 * <p>
 * Uses RestAssured with relaxed HTTPS validation, sets the "orangehrm" cookie
 * for authentication, and sets the "Content-Type" header to "application/json".
 * The requestBody contains the candidate data in JSON format.
 *
 * <p>
 * Parses the "data" section of the response. If "data" is a Map, it extracts
 * the "id" and "name" of the newly created candidate and stores them in lists:
 * <ul>
 *     <li>empStatusIdList</li>
 *     <li>empStatusNameList</li>
 * </ul>
 *
 * <p>
 * Wraps the extracted values along with the raw response, status code, and
 * status line in a {@link CustomResponse} object.
 *
 * @param endpoint    API endpoint for creating a candidate
 * @param cookieValue "orangehrm" cookie value for authentication
 * @param requestBody JSON string containing candidate data
 * @return CustomResponse containing response, status code, status line, and extracted candidate ID/name
 */





	public CustomResponse PostCandidate(String endpoint, String cookieValue, String requestBody) {
		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).when().post(BASE_URL + endpoint).then()
				.extract().response();

		JsonPath jsonPath = response.jsonPath();

		List<Integer> empStatusIdList = new ArrayList<>();
		List<String> empStatusNameList = new ArrayList<>();

		Object dataObj = jsonPath.get("data");
		if (dataObj instanceof Map) {
			Map<String, Object> status = (Map<String, Object>) dataObj;
			empStatusIdList.add(((Number) status.get("id")).intValue());
			empStatusNameList.add((String) status.get("name"));
//        extractEmploymentStatusData(status, empStatusIdList, empStatusNameList);
		}

		return new CustomResponse(response, response.getStatusCode(), response.getStatusLine(), empStatusIdList,
				empStatusNameList);
	}
	
	
	
	/**
	 * Sends a POST request to create a new job category at the specified endpoint.
	 *
	 * <p>
	 * Uses RestAssured with "orangehrm" cookie for authentication and sets
	 * "Content-Type" to "application/json". The requestBody contains the JSON
	 * payload for the new job category.
	 *
	 * <p>
	 * Extracts the response and parses the "data" object. If "data" is a map,
	 * the method retrieves the "id" and "name" fields and stores them in lists:
	 * <ul>
	 *     <li>empStatusIdList</li>
	 *     <li>empStatusNameList</li>
	 * </ul>
	 *
	 * <p>
	 * The lists along with the response, status code, and status line are wrapped
	 * in a {@link CustomResponse} object.
	 *
	 * @param endpoint    API endpoint for creating a job category
	 * @param cookieValue "orangehrm" cookie value for authentication
	 * @param requestBody JSON string containing job category data
	 * @return CustomResponse containing response, status code, status line, and extracted job category ID/name
	 */

	
	
	
	public CustomResponse PostJobCategoriesTest(String endpoint, String cookieValue, String requestBody) {
		Response response = RestAssured.given().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).when().post(BASE_URL + endpoint).then()
				.extract().response();

		JsonPath jsonPath = response.jsonPath();

		List<Integer> empStatusIdList = new ArrayList<>();
		List<String> empStatusNameList = new ArrayList<>();

		Object dataObj = jsonPath.get("data");
		if (dataObj instanceof Map) {
			Map<String, Object> status = (Map<String, Object>) dataObj;
			empStatusIdList.add(((Number) status.get("id")).intValue());
			empStatusNameList.add((String) status.get("name"));
//        extractEmploymentStatusData(status, empStatusIdList, empStatusNameList);
		}

		return new CustomResponse(response, response.getStatusCode(), response.getStatusLine(), empStatusIdList,
				empStatusNameList);
	}


	
		/*
	 Sends a PUT request to update an Employment Status in OrangeHRM and parses the response.

	 This method performs the following steps:
	 1. Builds and sends a PUT request to the given API endpoint using RestAssured.
	 2. Includes the authentication cookie and the JSON request body containing updated employment status details.
	 3. Retrieves the "data" field from the JSON response, which may be returned as:
	    - A single object (Map) representing one updated employment status.
	    - A list of objects (List<Map>) representing multiple updated employment statuses.
	 4. Calls extractEmploymentStatusData() to populate lists of employment status IDs and names.
	 5. Wraps the raw API response, status info, and parsed lists in a CustomResponse object for further validation in tests.

	 Parameters:
	 - endpoint: The API endpoint path (relative to the base URL) for updating employment statuses.
	 - cookieValue: The authentication cookie value for the OrangeHRM session.
	 - requestBody: The JSON request body containing updated employment status information.

	 Returns:
	 A CustomResponse object containing:
	 - The raw API response.
	 - HTTP status code and status line.
	 - A list of updated employment status IDs.
	 - A list of updated employment status names.

	 Notes:
	 - The method dynamically handles both single-object and list responses for "data".
	 - If the API response format changes, parsing logic may require updates.
	*/

		private void extractEmploymentStatusData(Map<String, Object> status, List<Integer> empStatusIdList,
			List<String> empStatusNameList) {
		if (status.get("id") != null) {
			empStatusIdList.add(((Number) status.get("id")).intValue());
		}
		if (status.get("name") != null) {
			empStatusNameList.add((String) status.get("name"));
		}
	}
		
		
		
		/**
		 * Sends a PUT request to update employee details at the given endpoint.
		 *
		 * <p>
		 * Uses RestAssured with "orangehrm" cookie for authentication and sets
		 * "Content-Type" to "application/json". The requestBody is passed as a JSON string.
		 *
		 * <p>
		 * Extracts the response, status code, and status line. Handles "data" in the
		 * response as a single object and adds only non-null/non-empty fields to lists:
		 * <ul>
		 *     <li>empNumber</li>
		 *     <li>firstName</li>
		 *     <li>lastName</li>
		 *     <li>employeeId</li>
		 * </ul>
		 *
		 * <p>
		 * These lists along with the raw response are wrapped in a {@link CustomResponse} object.
		 *
		 * @param endpoint    API endpoint for updating an employee
		 * @param cookieValue "orangehrm" cookie value for authentication
		 * @param requestBody JSON string containing employee data to update
		 * @return CustomResponse containing response, status code, status line, and non-null fields
		 */

		
		
		

		public CustomResponse putEmployeeDetails(String endpoint, String cookieValue, String requestBody) {
		    // Send PUT request
			Response response = RestAssured.given()
			        .header("Content-Type", "application/json")
			        .cookie("orangehrm", cookieValue)
			        .body(requestBody)
			        .put(BASE_URL + endpoint)
			        .then()
			        .extract()
			        .response();

		    Integer statusCode = response.getStatusCode();
		    String statusLine = response.getStatusLine();

		    JsonPath jsonPath = response.jsonPath();
		    Object dataNode = jsonPath.get("data");

		    // Initialize lists
		    List<Integer> empNumbers = new ArrayList<>();
		    List<String> firstNames = new ArrayList<>();
		    List<String> lastNames = new ArrayList<>();
		    List<String> employeeIds = new ArrayList<>();

		    if (dataNode instanceof Map) {
		        Map<String, Object> dataMap = (Map<String, Object>) dataNode;

		        // Only add non-null/non-empty values
		        if (dataMap.get("empNumber") != null) {
		            empNumbers.add((Integer) dataMap.get("empNumber"));
		        }
		        if (dataMap.get("firstName") != null && !dataMap.get("firstName").toString().isEmpty()) {
		            firstNames.add(dataMap.get("firstName").toString());
		        }
		        if (dataMap.get("lastName") != null && !dataMap.get("lastName").toString().isEmpty()) {
		            lastNames.add(dataMap.get("lastName").toString());
		        }
		        if (dataMap.get("employeeId") != null && !dataMap.get("employeeId").toString().isEmpty()) {
		            employeeIds.add(dataMap.get("employeeId").toString());
		        }
		    }

		    // Create CustomResponse object with lists
		    CustomResponse customResponse = new CustomResponse(
		            response,
		            statusCode,
		            statusLine,
		            empNumbers,
		            firstNames,
		            lastNames,
		            employeeIds,
		            response.asString()
		    );

		    return customResponse;
		}


		
		/**
		 * Sends a DELETE request to remove a recruitment candidate.
		 *
		 * <p>
		 * Uses RestAssured with relaxed HTTPS validation, sets the "orangehrm" cookie
		 * for authentication, and sets the "Content-Type" header to "application/json".
		 * The requestBody contains the candidate information to delete in JSON format.
		 *
		 * <p>
		 * Extracts the list of deleted candidate IDs from the "data" array in the response.
		 * Since DELETE responses usually don't include names, the name list is left empty.
		 *
		 * <p>
		 * Wraps the extracted ID list, raw response, status code, and status line in
		 * a {@link CustomResponse} object for further use.
		 *
		 * @param endpoint    API endpoint for deleting a candidate
		 * @param cookieValue "orangehrm" cookie value for authentication
		 * @param requestBody JSON string containing candidate deletion data
		 * @return CustomResponse containing response, status code, status line, and deleted candidate IDs
		 */

		
		
		
		public CustomResponse DeleterecruitmentCad(String endpoint, String cookieValue, String requestBody) {
	    Response response = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .cookie("orangehrm", cookieValue)
	            .header("Content-Type", "application/json")
	            .body(requestBody)
	            .when()
	            .delete(BASE_URL + endpoint)
	            .then()
	            .extract()
	            .response();

	    JsonPath jsonPath = response.jsonPath();

	    // Extract [id] from: { "data": [19], ... }
	    List<Integer> empStatusIdList = jsonPath.getList("data", Integer.class);

	    // For DELETE, name list is usually not present — leave empty
	    List<String> empStatusNameList = new ArrayList<>();

	    return new CustomResponse(
	            response,
	            response.getStatusCode(),
	            response.getStatusLine(),
	            empStatusIdList,
	            empStatusNameList
	    );
	}
}