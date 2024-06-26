package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
	private static final String OK_METHOD = "GET";
	private static final String OK_PATH = "/hello";
	private static final int SC_OK = 200;
	private static final int SC_BAD_REQUEST = 400;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final Map<String, String> responseHeaders = Map.of("Content-Type", "application/json");

	@Override
	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("EVENT TYPE: " + event.getClass().toString());
		String path = event.getRequestContext().getHttp().getPath();
		String method = event.getRequestContext().getHttp().getMethod();
		logger.log("Request Method: " + method + " and Request path: " + path);

		Map<String, Object> body = new HashMap<>();
		int statusCode = SC_OK;
		if (OK_METHOD.equals(method) && OK_PATH.equals(path)) {
			body.put("statusCode", SC_OK);
			body.put("message", "Hello from Lambda");
		} else {
		String message = String.format(
				"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s", path, method);
			body.put("statusCode", SC_BAD_REQUEST);
			body.put("message", message);
			statusCode = SC_BAD_REQUEST;
		}
		return getResponse(statusCode, body);
	}

	private APIGatewayV2HTTPResponse getResponse(int statusCode,  Map<String, Object> body) {
		return APIGatewayV2HTTPResponse.builder()
		.withStatusCode(statusCode)
		.withHeaders(responseHeaders)
		.withBody(gson.toJson(body))
		.build();
	}
}
