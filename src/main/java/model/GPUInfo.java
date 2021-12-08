package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

/**
 * GPU Information
 */
@Getter
@Setter
public class GPUInfo {

    /** GPU id  (NVGFT070 = 3070, NVGFT070T = 3070ti, ...) */
    private String gpuId;

    /** GPU name */
    private GPUName gpuName;

    /** Is purchase link active */
    private boolean isActive;

    /** Purchase link */
    private String productUrl;

    /** Price */
    private double price;

    /** Language */
    private Locale locale;
}
