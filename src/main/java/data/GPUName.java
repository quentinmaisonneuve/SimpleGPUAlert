package data;

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
     * Convert a string name of Data.GPU to an enum Data.GPU
     * @param gpuName Name of Data.GPU
     * @return Enum of corresponding Data.GPU
     */
    public static GPUName StringToGPU(String gpuName) {

        GPUName gpu = null;

        if(StringUtils.isNotBlank(gpuName)) {

            for (GPUName g : GPUName.values()) {

                if (gpuName.toLowerCase().equals(g.toString())) {

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
