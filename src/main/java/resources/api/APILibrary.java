package resources.api;

import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.Assert;
import resources.Database;
import utils.ApiUtilities;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static resources.Colors.*;

public class APILibrary {

    public String baseUrl;
    public String baseUri;

    Database database = new Database();
    ApiUtilities apiUtils = new ApiUtilities();

    public void setUpTestEnv(String url, String uri){
        baseUrl = url;
        baseUri = uri;
        database.sessionType = "api";
    }

    public void updateUri(String variableType, String newValue){
        switch (variableType.toLowerCase()){
            case "uri":
                baseUri = newValue;
                return;

            case "url":
                baseUrl = newValue;
                return;

            default:
                Assert.fail(RED+"No such environment variable was defined in ApiSteps.java @updateUri()");
                break;

        }
    }

    public void deletePet(String name, String id){

        String uri = baseUri + "/" + id;

        Response response = apiUtils.performApiCall("delete", baseUrl, uri, null, true);

        if (response.getStatusCode() == 200){
            System.out.println(GRAY+"The pet with id: "+BLUE+id+GRAY+" was successfully deleted."+RESET);
            database.contextJSON.put(name,database.serverResponse);
        }
        else Assert.fail(GRAY+"The server response was unexpectedly "+RED+response.getStatusCode()+RESET);
    }

    public void updatePetInfo(DataTable table){
        String id = null;
        String categoryID = null;
        String categoryName = null;
        String name = null;
        String photoUrl = null;
        String tagID = null;
        String tagName = null;
        String status = null;

        List<Map<String, String>> signForms = table.asMaps();

        for (Map<String, String> form : signForms) {
            id = form.get("ID");
            categoryID = form.get("Category ID");
            categoryName = form.get("Category Name");
            name = form.get("Name");
            photoUrl = form.get("Photo Url");
            tagID = form.get("Tag ID");
            tagName = form.get("Tag Name");
            status = form.get("Status");
        }

        JSONObject body = new JSONObject();

        JSONObject category = new JSONObject();

        category.put("id",categoryID);

        category.put("name",categoryName);

        List<String > photoUrls = new ArrayList<>();

        photoUrls.add(photoUrl);

        List<JSONObject > tags = new ArrayList<>();

        JSONObject tag = new JSONObject();

        tag.put("id",tagID);
        tag.put("name",tagName);

        tags.add(tag);

        body.put("id",id);

        body.put("category",category);

        body.put("name",name);

        body.put("photoUrls",photoUrls);

        body.put("tags",tags);

        body.put("status",status);

        Response response = apiUtils.performApiCall("put", baseUrl, baseUri, body, true);

        if (response.getStatusCode() == 200){
            System.out.println(BLUE+name+GRAY+" was successfully updated."+RESET);
            database.contextJSON.put(name, database.serverResponse);
        }
        else Assert.fail(GRAY+"The server response was unexpectedly "+RED+response.getStatusCode()+RESET);

    }

    public JSONObject getPet(String petName){return (JSONObject) database.contextJSON.get(petName);}

    public void uploadPetPic(String petName, String fileName){

        String petID = getPet(petName).get("id").toString();

        String uri = baseUri+"/"+petID+"/uploadImage";

        try {
            FileReader file = new FileReader("src/test/java/resources/files/"+fileName);

            apiUtils.uploadFile(baseUrl,uri,file,"src/test/java/resources/files/"+fileName);

            database.contextJSON.put(petName+"Picture", database.serverResponse);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void printServerResponseNamed(String requestName){
        System.out.println(GRAY+database.contextJSON.get(requestName)+ RESET);
    }

    public void addPet(DataTable table){

        String id = null;
        String categoryID = null;
        String categoryName = null;
        String name = null;
        String photoUrl = null;
        String tagID = null;
        String tagName = null;
        String status = null;

        List<Map<String, String>> signForms = table.asMaps();

        for (Map<String, String> form : signForms) {
            id = form.get("ID");
            categoryID = form.get("Category ID");
            categoryName = form.get("Category Name");
            name = form.get("Name");
            photoUrl = form.get("Photo Url");
            tagID = form.get("Tag ID");
            tagName = form.get("Tag Name");
            status = form.get("Status");
        }

        JSONObject body = new JSONObject();

        JSONObject category = new JSONObject();

        category.put("id",categoryID);

        category.put("name",categoryName);

        List<String > photoUrls = new ArrayList<>();

        photoUrls.add(photoUrl);

        List<JSONObject > tags = new ArrayList<>();

        JSONObject tag = new JSONObject();

        tag.put("id",tagID);
        tag.put("name",tagName);

        tags.add(tag);

        body.put("id",id);

        body.put("category",category);

        body.put("name",name);

        body.put("photoUrls",photoUrls);

        body.put("tags",tags);

        body.put("status",status);

        Response response = apiUtils.performApiCall("post", baseUrl, baseUri, body, true);

        if (response.getStatusCode() == 200){
            System.out.println(BLUE+name+GRAY+" was successfully added."+RESET);
            database.contextJSON.put(name, database.serverResponse);
        }
        else Assert.fail(GRAY+"The server response was unexpectedly "+RED+response.getStatusCode()+RESET);

    }

}
