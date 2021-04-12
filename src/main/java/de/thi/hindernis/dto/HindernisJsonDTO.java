package de.thi.hindernis.dto;

import java.io.Serializable;
import java.util.Set;

public class HindernisJsonDTO implements Serializable {
    public Set<HindernisDTO> hindernisse;
    public HindernisConfigurationDTO config;
}
