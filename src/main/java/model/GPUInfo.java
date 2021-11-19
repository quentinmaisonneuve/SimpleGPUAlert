package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
public class GPUInfo {

    private String idGPU;
    private GPUName gpuName;
    private boolean isActive;
    private String productUrl;
    private double price;
    private Locale locale;
}
