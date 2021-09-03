package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonUtilities {

    public JSONObject urlsJson = new JSONObject();

    public JSONObject notificationJson = new JSONObject();

    FileUtilities fileUtil = new FileUtilities();

    public void saveJson(JSONObject inputJson, String jsonName){
        try {

            //JSONParser parser = new JSONParser();

            //JSONObject object = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream("src/test/java/resources/database/"+"Getir"+".JSON")));

            FileWriter file = new FileWriter("src/test/java/resources/database/"+jsonName+".JSON");

            ObjectMapper mapper = new ObjectMapper();

            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inputJson);

            if(file.toString().isEmpty())
                file.write(String.valueOf(json));
            else
                file.append(String.valueOf(json));

            file.close();

        }catch (Exception gamma){
            Assert.fail(String.valueOf(gamma));
        }

    }

    public void saveProjectJson(JSONObject inputJson,
                                       String jsonName, String pageName,
                                       String  elementsUrl, String pageTitle){
        try {
            JSONObject attributeJson = new JSONObject();

            ObjectMapper mapper = new ObjectMapper();

            JSONObject settingsJson = new JSONObject();

            settingsJson.put("send", "true");

            JSONObject senderJson = new JSONObject();

            senderJson.put("password", "Notifier1234");

            senderJson.put("address", "umutayb.notification@gmail.com");

            senderJson.put("id", "umutayb.notification@gmail.com");

            JSONObject recipientJson = new JSONObject();

            recipientJson.put("address","umutaybora@gmail.com");

            notificationJson.put("settings", settingsJson);

            notificationJson.put("sender", senderJson);

            notificationJson.put("recipient", recipientJson);

            JSONObject urlJson = new JSONObject();

            urlJson.put("title", pageTitle);

            urlJson.put("url", elementsUrl);

            urlsJson.put("landing", urlJson);

            attributeJson.put("Notification", notificationJson);

            attributeJson.put("Elements", inputJson);

            attributeJson.put("Urls", urlsJson);

            JSONObject projectJson = new JSONObject();

            if (fileUtil.verifyFilePresence("src/test/java/resources/database/"+jsonName+".JSON")){

                projectJson = parseJSONFile(jsonName);

                projectJson.put(pageName, attributeJson);

            }
            else {

                projectJson.put(pageName, attributeJson);

            }

            FileWriter file = new FileWriter("src/test/java/resources/database/"+jsonName+".JSON");

            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(projectJson);

            if(file.toString().isEmpty())
                file.write(String.valueOf(json));
            else
                file.append(String.valueOf(json));

            file.close();

        }catch (Exception gamma){
            Assert.fail(String.valueOf(gamma));
        }

    }

    public JsonObject parseJsonFile(String directory, String JsonName) throws FileNotFoundException {

        JsonParser jsonParser = new JsonParser();
        JsonElement object;

        FileReader fileReader = new FileReader(directory+"/"+JsonName+".JSON");

        object = jsonParser.parse(fileReader);
        JsonObject jsonObject = (JsonObject) object;

        assert jsonObject != null;

        return jsonObject;

    }

    public JSONObject parseJSONFile(String JsonName) throws IOException, ParseException {

        JSONParser jsonParser = new JSONParser();
        JSONObject object;

        FileReader fileReader = new FileReader("src/test/java/resources/database/"+JsonName+".JSON");

        object = (JSONObject) jsonParser.parse(fileReader);
        JSONObject jsonObject = object;

        assert jsonObject != null;

        return jsonObject;

    }

    public JsonObject getJsonObject(JsonObject json, String key){

        JsonObject jsonObject = json.get(key).getAsJsonObject();

        assert jsonObject != null;

        return jsonObject;
    }

    public String getElementAttribute(JsonObject attributes, String attributeType){

        return attributes.get(attributeType).getAsString();

    }

    public JSONObject str2json(String inputString){
        JSONObject object = null;
        try {

            JSONParser parser = new JSONParser();

            object = (JSONObject) parser.parse(inputString);

        }catch (Exception gamma){
            Assert.fail(String.valueOf(gamma));
        }
        return object;
    }

}