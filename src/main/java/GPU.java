import io.opentelemetry.api.internal.StringUtils;

import java.util.Arrays;

/**
 * List of all Nvidia FE GPU available
 */
public enum GPU {

    _3060ti,
    _3070,
    _3070ti,
    _3080,
    _3080ti,
    _3090;

    /**
     * Convert a string name of GPU to an enum GPU
     * @param gpuName Name of GPU
     * @return Enum of corresponding GPU
     */
    public static GPU StringToGPU(String gpuName) {

        GPU gpu = null;

        if(!StringUtils.isNullOrEmpty(gpuName)) {

            for (GPU g : Arrays.asList(GPU.values())) {

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
