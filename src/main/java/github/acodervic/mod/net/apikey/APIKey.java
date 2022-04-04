package github.acodervic.mod.net.apikey;

import github.acodervic.mod.data.str;

/**
 * apikey的抽象
 */
public class APIKey {
    String name;// 名称
    String value;// 值
    String location = "url";// url body header
    Boolean available = true;

    /**
     * @return the available
     */
    public Boolean getAvailable() {
        return available;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    public boolean intUrl() {
        return new str(this.location).eqAllIgnoreCase("url");
    }

    public boolean intBody() {
        return new str(this.location).eqAllIgnoreCase("body");
    }

    public boolean intHeader() {
        return new str(this.location).eqAllIgnoreCase("header");
    }

}
