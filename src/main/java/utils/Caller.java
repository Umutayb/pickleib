package utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.simple.JSONObject;
import org.junit.Assert;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@SuppressWarnings("unused")
public abstract class Caller {

    static ObjectMapper objectMapper = new ObjectMapper();
    static Printer log = new Printer(Caller.class);

    public Caller(){
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    protected static <Model> Model perform(Call<Model> call, Boolean strict, Boolean printBody){
        String serviceName = getMethod();
        log.new Info("Performing " + call.request().method() + " call for '" + serviceName + "' service on url: " + call.request().url());
        try {
            Response<Model> response = call.execute();

            if (printBody) printBody(response);

            if (response.isSuccessful()){
                if (response.message().length()>0) log.new Info(response.message());
                log.new Success("The response code is: " + response.code());
            }
            else{
                if (response.message().length()>0)
                    log.new Warning(response.message());
                log.new Warning("The response code is: " + response.code());
                log.new Warning(response.raw());

                if (strict)
                    Assert.fail("The strict call performed for " + serviceName + " service returned response code " + response.code());
            }
            return response.body();
        }
        catch (IOException exception) {
            log.new Error(exception.getLocalizedMessage(),exception);
            Assert.fail("The call performed for " + serviceName + " failed for an unknown reason.");
        }
        return null;
    }
    protected static <Model> Response<Model> getResponse(Call<Model> call, Boolean strict, Boolean printBody){
        String serviceName = getMethod();
        log.new Info("Performing " + call.request().method() + " call for '" + serviceName + "' service on url: " + call.request().url());
        try {
            Response<Model> response = call.execute();

            if (printBody) printBody(response);

            if (response.isSuccessful()){
                if (response.message().length()>0)
                    log.new Info(response.message());
                log.new Success("The response code is: " + response.code());
            }
            else{
                if (response.message().length()>0)
                    log.new Warning(response.message());
                log.new Warning("The response code is: " + response.code());
                log.new Warning(response.raw());

                if (strict)
                    Assert.fail("The strict call performed for " + serviceName + " service returned response code " + response.code());
            }
            return response;
        }
        catch (IOException exception) {
            if (strict){
                log.new Error(exception.getLocalizedMessage(), exception);
                Assert.fail("The call performed for " + serviceName + " failed for an unknown reason.");
            }
        }
        return null;
    }

    @Deprecated
    protected static <Model> Model perform(Call<Model> call, Boolean strict, Boolean printBody, String serviceName) {
        log.new Info("Performing " + call.request().method() + " call for '" + serviceName + "' service on url: " + call.request().url());
        try {
            Response<Model> response = call.execute();

            if (printBody) printBody(response);

            if (response.isSuccessful()){
                if (response.message().length()>0) log.new Info(response.message());
                log.new Success("The response code is: " + response.code());
            }
            else{
                if (response.message().length()>0)
                    log.new Warning(response.message());
                log.new Warning("The response code is: " + response.code());
                log.new Warning(response.raw());

                if (strict)
                    Assert.fail("The strict call performed for " + serviceName + " service returned response code " + response.code());
            }
            return response.body();
        }
        catch (IOException exception) {
            log.new Error(exception.getLocalizedMessage(),exception);
            Assert.fail("The call performed for " + serviceName + " failed for an unknown reason.");
        }
        return null;
    }

    @Deprecated
    protected static <Model> Response<Model> getResponse(Call<Model> call, Boolean strict, Boolean printBody, String serviceName) {
        log.new Info("Performing " + call.request().method() + " call for '" + serviceName + "' service on url: " + call.request().url());
        try {
            Response<Model> response = call.execute();

            if (printBody) printBody(response);

            if (response.isSuccessful()){
                if (response.message().length()>0)
                    log.new Info(response.message());
                log.new Success("The response code is: " + response.code());
            }
            else{
                if (response.message().length()>0)
                    log.new Warning(response.message());
                log.new Warning("The response code is: " + response.code());
                log.new Warning(response.raw());

                if (strict)
                    Assert.fail("The strict call performed for " + serviceName + " service returned response code " + response.code());
            }
            return response;
        }
        catch (IOException exception) {
            if (strict){
                log.new Error(exception.getLocalizedMessage(), exception);
                Assert.fail("The call performed for " + serviceName + " failed for an unknown reason.");
            }
        }
        return null;
    }

    static <Model> void printBody(Response<Model> response) throws IOException {
        FileUtilities.Json convert = new FileUtilities.Json();
        String message = "The response body is: \n";
        try {
            if (response.body() != null) // Success response with a non-null body
                log.new Info(message + objectMapper.valueToTree(response.body()).toPrettyString());

            else if (response.errorBody() != null){ // Error response with a non-null body
                String errorMessage = response.errorBody().string();
                JSONObject responseJSON = convert.str2json(errorMessage);
                if (responseJSON!=null)
                    log.new Warning(message + objectMapper.valueToTree(responseJSON).toPrettyString());
                else // Success response with a non-null & non-json body
                    log.new Warning(message + errorMessage);
            }
            else log.new Info("The response body is empty."); // Success response with a null body
        }
        catch (IOException exception){log.new Warning(exception.getStackTrace());}
    }

    static String getMethod(){
        Throwable dummyException = new Throwable();
        StackTraceElement[] stackTrace = dummyException.getStackTrace();
        // LOGGING-132: use the provided logger name instead of the class name
        String method = stackTrace[0].getMethodName();
        // Caller will be the third element
        if( stackTrace.length > 2 ) {
            StackTraceElement caller = stackTrace[2];
            method = caller.getMethodName();
        }
        return method;
    }
}