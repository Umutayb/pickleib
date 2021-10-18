package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import resources.Database;
import java.io.File;
import static resources.Colors.*;

public class ApiUtilities {

    JsonUtilities jsonUtilities = new JsonUtilities();

    public Response performApiCall(String requestType, String url, String uri, Object input, Boolean inputRequired){

        Response response = null;

        RequestSpecification request;

        String requestUrl = "https://"+url+uri;

        System.out.println(GRAY+"Performing "+BLUE+requestType+GRAY+" request at: \""+BLUE+requestUrl+GRAY+"\""+RESET);

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
                    System.out.println("Undefined request type: "+requestType);
                    Assert.fail();
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

        System.out.println(GRAY+"Performing "+BLUE+"post"+GRAY+" request at: \""+BLUE+requestUrl+GRAY+"\""+RESET);

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

    public static void printStatusCode(Response response){
        if (response.getStatusCode()==200)
            System.out.println(GRAY+"Server response: "+GREEN+response.getStatusCode()+RESET);
        else if (response.getStatusCode()==500)
            System.out.println(GRAY+"Server response: "+RED+response.getStatusCode()+RESET);
        else if (response.getStatusCode()==404)
            System.out.println(GRAY+"Server response: "+YELLOW+response.getStatusCode()+RESET);
        else
            System.out.println(GRAY+"Server response: "+BLUE_BACKGROUND+response.getStatusCode()+RESET);

        response.getStatusCode();
    }

}