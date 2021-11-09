package Services.GPU;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Manage the different type of notification the program can send
 */
public class GPUInfoService extends Service {


    public GPUInfoService(Properties properties) {
        super(properties);
    }

    public List<GPUInfo> getListInfoGPU(Locale locale) throws InterruptedException {

        List<GPUInfo> gpuInfos = new ArrayList<>();

        Map<String, GPUName> GPUNameToId = new HashMap<>();

        for (GPUName gpuName : GPUName.values()) {

            GPUNameToId.put(GPUInfoService.getClassDivGPU(gpuName), gpuName);
        }

        JSONArray lineProducts = readJsonFromUrl(String.format(getProperties().getProperty("NVIDIA_API_LINK"),
                locale.toString().toUpperCase(),
                locale.toString().toUpperCase()))
                .getJSONArray("listMap");

        for (Object o : lineProducts) {

            JSONObject jsonLineItem = (JSONObject) o;

            if (jsonLineItem.getString("fe_sku").startsWith("NVGFT")) {

                GPUInfo gpuInfo = new GPUInfo();

                gpuInfo.setIdGPU(jsonLineItem.getString("fe_sku").replace("_".concat(locale.toString().toUpperCase()), ""));
                gpuInfo.setGpuName(GPUNameToId.get(gpuInfo.getIdGPU()));
                gpuInfo.setActive(Boolean.parseBoolean(jsonLineItem.getString("is_active")));
                gpuInfo.setProductUrl(jsonLineItem.getString("product_url"));
                gpuInfo.setPrice(Double.parseDouble(jsonLineItem.getString("price")));

                gpuInfos.add(gpuInfo);
            }
        }

        return gpuInfos;
    }

    /**
     * Return the class name of the div who contains supply information of the gpu by giving the Services.GPU name
     * @param gpu Nvidia FE Services.GPU
     * @return Div class of supply information, example : NVGFT060T if for an 3060ti
     */
    public static String getClassDivGPU(GPUName gpu) {

        StringBuilder result = new StringBuilder("NVGFT");

        result.append(gpu.toString(), 1, 4);

        if(gpu.toString().contains("ti")) {

            result.append('T');
        }

        return result.toString();
    }

    /**
     * Parse JSON from an URL
     * @param url URL
     * @return JSONObjetct parsed
     */
    private JSONObject readJsonFromUrl(String url)  {

        JSONObject json = null;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = null;

        try {

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
        }

        if (response != null) {

            json = new JSONObject(response.body());
        }

        return json;
    }
}
