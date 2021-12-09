package model;

import org.apache.maven.shared.utils.StringUtils;

/**
 * List of all Nvidia FE Data.GPU available
 */
public enum GPUName {

    _3060ti,
    _3070,
    _3070ti,
    _3080,
    _3080ti,
    _3090;

    /**
     * Convert a string name of GPU to an enum GPUName
     * @param gpuName Name of GPU
     * @return Enum of corresponding GPUName
     */
    public static GPUName StringToGPU(String gpuName) {

        GPUName gpu = null;

        if(StringUtils.isNotBlank(gpuName)) {

            for (GPUName g : GPUName.values()) {

                if (gpuName.trim().equalsIgnoreCase(g.toString())) {

                    gpu = g;
                    break;
                }
            }
        }

        return gpu;
    }

    @Override
    public String toString() {

        return super.toString().substring(1);
    }
}
