package com.avelrune.property.dto;

import java.util.List;

public class FilterMetaResponse {

    private List<CityOption> cities;
    private List<LabelValue> propertyTypes;
    private List<LabelValue> listingTypes;

    public FilterMetaResponse(List<CityOption> cities, List<LabelValue> propertyTypes, List<LabelValue> listingTypes) {
        this.cities = cities;
        this.propertyTypes = propertyTypes;
        this.listingTypes = listingTypes;
    }

    public List<CityOption> getCities() {
        return cities;
    }

    public List<LabelValue> getPropertyTypes() {
        return propertyTypes;
    }

    public List<LabelValue> getListingTypes() {
        return listingTypes;
    }

    public static class CityOption {
        private String name;
        private List<String> districts;

        public CityOption(String name, List<String> districts) {
            this.name = name;
            this.districts = districts;
        }

        public String getName() {
            return name;
        }

        public List<String> getDistricts() {
            return districts;
        }
    }

    public static class LabelValue {
        private String value;
        private String label;

        public LabelValue(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }
}
