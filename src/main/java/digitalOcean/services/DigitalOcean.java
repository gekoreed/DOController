package digitalOcean.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import digitalOcean.config.APIKey;
import digitalOcean.entity.Droplet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgen Shevchenko on 7/07/14.
 * curl -X GET "https://api.digitalocean.com/v2/droplets" \
 * -H "Authorization: ddcddd8a740cf1fc247ebefc82ac2c1e325f651d5834b129101b885bb11de660"
 * -d {"type": "reboot"}
 */


public class DigitalOcean {
    private ObjectMapper mapper = new ObjectMapper();
    private static DigitalOcean digitalOcean;

    private CloseableHttpClient httpclient = HttpClients.custom().build();

    public static DigitalOcean getInstance() {
        if (digitalOcean == null) {
            digitalOcean = new DigitalOcean();
        }
        return digitalOcean;
    }

    @Override
    public String toString() {
        return "DO{}";
    }

    public List<Droplet> getDroplets() throws IOException, JSONException {
        JsonNode jsonNode = makeGetRequest("https://api.digitalocean.com/v2/droplets");
        return formDropletsList(jsonNode);
    }


    public boolean createDroplet(String name, String regionString, String sizeString, String imageString, List<String> ssh,
                                 boolean backups, boolean ipv6) {
        HttpPost post = createPost("https://api.digitalocean.com/v2/droplets");
        ObjectNode request = mapper.createObjectNode();
        request.put("name", name);
        request.put("region", regionString);
        request.put("size", sizeString);
        request.put("image", imageString);
        if (ssh != null) {
            ArrayNode keys = request.putArray("ssh_keys");
            ssh.forEach(keys::add);
        } else {
            request.put("ssh_keys", (byte[]) null);
        }
        request.put("backups", backups);
        request.put("ipv6", ipv6);
        request.put("user_data", "null");
        request.put("private_networking", "null");

        String requestString = request.toString();
        post.setEntity(new StringEntity(requestString, "UTF-8"));

        int i = makePostRequest(post);
        return i >= 200 && i <= 202;
    }


    /**
     * Simple java method to reboot your selected droplet
     * It uses simple HttpURLConnection to reboot that droplet
     *
     * @param dropletID droplet ID to be rebooted
     * @return status
     * @throws JSONException standart
     */
    public String rebootDroplet(String dropletID) {
        int resCode = postRequestToServer(dropletID, "reboot");
        if (resCode == 200 || resCode == 201) {
            System.out.println("Server is rebooted");
            return "Server is rebooted. Response code: " + resCode;
        } else {
            System.out.println("Something went wrong!!!");
            return "Something went wrong!!!" + resCode;
        }
    }


    public String powerOff(String dropletID) {
        int resCode = postRequestToServer(dropletID, "power_off");
        if (resCode == 200 || resCode == 201) {
            System.out.println("Server is OFFLINE");
            return "Server is OFFLINE " + resCode;
        } else {
            System.out.println("Something went wrong!!!");
            return "Something went wrong!!! " + resCode;
        }
    }


    public String powerOn(String dropletID) {
        int resCode = postRequestToServer(dropletID, "power_on");
        if (resCode == 200 || resCode == 201) {
            System.out.println("Server is ONLINE");
            return "Server is ONLINE " + resCode;
        } else {
            System.out.println("Something went wrong!!!");
            return "Something went wrong!!! " + resCode;
        }
    }


    public int renameDroplet(String newName, String dropletID) throws IOException {

        HttpPost post = createPost(String.format("https://api.digitalocean.com/v2/droplets/%s/actions", dropletID));

        ObjectNode request = mapper.createObjectNode();
        request.put("type", "rename");
        request.put("name", newName);

        String requestString = request.toString();
        post.setEntity(new StringEntity(requestString, "UTF-8"));

        return makePostRequest(post);
    }


    public boolean deleteDroplet(Long dropletID) {
        boolean done = false;
        try {
            HttpDelete request = new HttpDelete(String.format("https://api.digitalocean.com/v2/droplets/%s", dropletID));
            request.addHeader("Authorization", APIKey.APIKey);
            request.addHeader("Content-Type", "application/json");

            CloseableHttpResponse execute = httpclient.execute(request);
            int status = execute.getStatusLine().getStatusCode();
            done = status >= 200 && status <= 202;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return done;
    }

    private int postRequestToServer(String dropletID, String command) {
        HttpPost post = createPost(String.format("https://api.digitalocean.com/v2/droplets/%s/actions", dropletID));

        ObjectNode request = mapper.createObjectNode();
        request.put("type", command);

        String requestString = request.toString();
        post.setEntity(new StringEntity(requestString, "UTF-8"));

        return makePostRequest(post);
    }


    public boolean checkProperKey(String apiKey) throws IOException {
        HttpURLConnection httpcon = (HttpURLConnection) (new URL("https://api.digitalocean.com/v2/droplets").openConnection());
        httpcon.setDoOutput(true);
        httpcon.setRequestProperty("Authorization", apiKey);
        httpcon.setRequestMethod("GET");
        httpcon.connect();
        int responKey = httpcon.getResponseCode();
        System.out.println("ResponseCode  " + responKey);
        return responKey == 200 || responKey == 201;
    }

    /**
     * The response from the digitalOcean API is a complicated JSON
     * file. So in order to work with data comfortably in future
     * we create list of droplets.
     * It uses native JSON parser and pull needed data from response
     *
     * @param response String from DO in JSON format.
     * @return List of Droplets as standart Java ArrayList
     */
    private List<Droplet> formDropletsList(JsonNode response) {
        JsonNode droplets = response.get("droplets");
        List<Droplet> list = new ArrayList<>();
        for (int i = 0; i < droplets.size(); i++) {
            JsonNode dropletJ = droplets.get(i);
            JsonNode image = dropletJ.get("image");
            JsonNode networks = dropletJ.get("networks");
            JsonNode v4 = networks.get("v4");
            JsonNode ip = v4.get(0);
            JsonNode kernel = dropletJ.get("kernel");
            list.add(new Droplet.Builder()
                    .dropletID(dropletJ.get("id").asLong())
                    .dropletName(dropletJ.get("name").asText())
                    .setCreated(dropletJ.get("created_at").asText())
                    .status(dropletJ.get("status").asText())
                    .OS(image.get("name").asText())
                    .IPAddress(ip.get("ip_address").asText())
                    .gateway(ip.get("gateway").asText())
                    .kernelID(kernel != null && kernel.has("id") ? kernel.get("id").asText() : "")
                    .kernelName(kernel != null && kernel.has("name") ? kernel.get("name").asText() : "")
                    .kernelVersion(kernel != null && kernel.has("version") ? kernel.get("version").asText() : "")
                    .build());
        }
        return list;
    }


    private HttpPost createPost(String uri) {
        HttpPost request = new HttpPost(uri);

        request.addHeader("Authorization", APIKey.APIKey);
        request.addHeader("Content-Type", "application/json");

        return request;
    }


    private int makePostRequest(HttpPost req) {
        int statusCode = 0;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            statusCode = response.getStatusLine().getStatusCode();
            JsonNode jsonResponse = mapper.readTree(response.getEntity().getContent());
        } catch (IOException ignored) {
        }
        return statusCode;
    }


    private JsonNode makeGetRequest(String uri) {
        HttpGet request = new HttpGet(uri);

        request.addHeader("Authorization", APIKey.APIKey);
        request.addHeader("Content-Type", "application/json");

        JsonNode jsonResponse = null;

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            jsonResponse = mapper.readTree(response.getEntity().getContent());
        } catch (IOException ignored) {
        }
        return jsonResponse;
    }
}