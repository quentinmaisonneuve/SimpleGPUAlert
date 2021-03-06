package controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controller.Daemon;
import model.GPUInfo;
import model.GPUName;

/**
 * Manage the different type of notification the program can send
 */
public class GPUInfoService {

    // Constant
    public static final String NVIDIA_API_LINK = "NVIDIA_API_LINK";

    public List<GPUInfo> getListInfoGPU(Locale locale) {

        List<GPUInfo> gpuInfos = new ArrayList<>();
        Map<String, GPUName> GPUNameToId = new HashMap<>();
        String url = String.format(PropertyManager.getProperty(NVIDIA_API_LINK),
                locale.toString().toUpperCase(),
                locale.toString().toUpperCase());

        for (GPUName gpuName : GPUName.values()) {

            GPUNameToId.put(GPUInfoService.getClassDivGPU(gpuName), gpuName);
        }

        Daemon.logger.info("Request on : ".concat(url));
        JSONObject readJSON = JSONManager.readJsonFromUrl(url);

        try {

            JSONArray lineProducts = readJSON.getJSONArray("listMap");

            for (Object o : lineProducts) {

                JSONObject jsonLineItem = (JSONObject) o;

                if (jsonLineItem.getString("fe_sku").startsWith("NVGFT")) {

                    GPUInfo gpuInfo = new GPUInfo();

                    gpuInfo.setGpuId(jsonLineItem.getString("fe_sku").replace("_".concat(locale.toString().toUpperCase()), ""));
                    gpuInfo.setGpuName(GPUNameToId.get(gpuInfo.getGpuId()));
                    gpuInfo.setActive(Boolean.parseBoolean(jsonLineItem.getString("is_active")));
                    gpuInfo.setProductUrl(jsonLineItem.getString("product_url"));
                    gpuInfo.setPrice(Double.parseDouble(jsonLineItem.getString("price")));
                    gpuInfo.setLocale(locale);

                    gpuInfos.add(gpuInfo);
                }
            }

        } catch (JSONException e) {

            Daemon.logger.error("Error while parsing GPU information from JSON : ".concat(url));
            Daemon.logger.error(e);
            Daemon.logger.error(readJSON.toString());
        }

        return gpuInfos;
    }

    /**
     * Return the class name of the div who contains supply information of the gpu by giving the Data.GPU name
     * @param gpu Nvidia FE Data.GPU
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
}
