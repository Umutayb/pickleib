package utils;

import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import static resources.Colors.*;
import resources.Database;
import org.junit.Assert;
import java.io.File;

public class ApiUtilities {

    JsonUtilities jsonUtilities = new JsonUtilities();
    Printer log = new Printer(ApiUtilities.class);

    String url;
    String uri;

    public ApiUtilities(String url, String uri){
        this.url = url;
        this.uri = uri;
    }

    public Response performApiCall(String requestType, Object input, Boolean inputRequired){

        Response response = null;

        RequestSpecification request;

        String requestUrl = "https://"+url+uri;

        log.new info("Performing "+BLUE+requestType+GRAY+" request at: \""+BLUE+requestUrl+GRAY+"\""+RESET);

        try{
            switch (requestType.toLowerCase()){
                case "post":
                    request = RestAssured.given().header("Content-Type","application/json").header("Accept","application/json");

                    if (input!=null && input!="")
                        request.body(input);
                    else if(inputRequired)
                        Assert.fail(YELLOW+"The input cannot be null"+RESET);

                    response = request.post(requestUrl);

                    printStatusCode(response);

                    Database.serverResponse = jsonUtilities.str2json(response.asString());

                    return response;

                case "get":
                    response = RestAssured.get(requestUrl);

                    printStatusCode(response);

                    if(response.getStatusCode()==200){
                        Database.contextJSON = jsonUtilities.str2json(response.asString());
                        Database.serverResponse = jsonUtilities.str2json(response.asString());

                    }

                    return response;

                case "put":
                    request = RestAssured.given().header("Content-Type","application/json").header("Accept","application/json");

                    if (input!=null && input!="")
                        request.body(input);
                    else if(inputRequired)
                        Assert.fail(YELLOW+"The input cannot be null"+RESET);

                    response = request.put(requestUrl);

                    printStatusCode(response);

                    Database.serverResponse = jsonUtilities.str2json(response.asString());

                    return response;

                case "delete":
                    request = RestAssured.given().header("Accept","application/json");

                    response = request.delete(requestUrl);

                    printStatusCode(response);

                    Database.serverResponse = jsonUtilities.str2json(response.asString());

                    return response;

                default:
                    Assert.fail("Undefined request type: "+requestType);
                    return null;

            }
        }catch (Exception gamma){
            Assert.fail(YELLOW+"Could not perform requests for the reason: "+gamma.fillInStackTrace()+RESET);
        }

        return response;
    }

    public Response uploadFile(String url, String uri, Object file, String fileUrl){

        Response response = null;

        RequestSpecification request;

        String requestUrl = "https://"+url+uri;

        log.new info("Performing "+BLUE+"post"+GRAY+" request at: \""+BLUE+requestUrl+GRAY+"\""+RESET);

        request = RestAssured.given().multiPart("file", new File(fileUrl), "image/jpeg");

        if (file!=null && file!="")
            request.body(file);
        else
            Assert.fail(YELLOW+"The input cannot be null"+RESET);

        response = request.post(requestUrl);

        printStatusCode(response);

        Database.serverResponse = jsonUtilities.str2json(response.asString());

        return response;

    }

    public void printStatusCode(Response response){
        if (response.getStatusCode()==200)
            log.new info("Server response: "+GREEN+response.getStatusCode()+RESET);
        else if (response.getStatusCode()==500)
            log.new info("Server response: "+RED+response.getStatusCode()+RESET);
        else if (response.getStatusCode()==404)
            log.new info("Server response: "+YELLOW+response.getStatusCode()+RESET);
        else
            log.new info("Server response: "+BLUE_BACKGROUND+response.getStatusCode()+RESET);
    }

}