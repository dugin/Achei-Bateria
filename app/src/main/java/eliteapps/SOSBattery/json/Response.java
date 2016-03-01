package eliteapps.SOSBattery.json;

/**
 * Created by Rodrigo on 26/02/2016.
 */
public class Response {
    private Result result;


    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }


    @Override
    public String toString() {
        return "ClassPojo [result = " + result + "]";
    }
}
