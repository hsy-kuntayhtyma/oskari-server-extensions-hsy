package hsy.helpers;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONArray;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "croppingMode",
    "transport",
    "layer",
    "bbox",
    "specialConditionsLink",
    "isSpecialConditions",
    "isAcceptSpecialConditions",
    "croppingUrl",
    "croppingLayer",
    "wmsUrl",
    "sessionKeys"
})
/**
 * Download details.
 * @author Marko Kuosmanen
 * @copyright Dimenteq Oy
 *
 */
public class DownloadDetail {

    @JsonProperty("croppingMode")
    private String croppingMode;
    @JsonProperty("transport")
    private String transport;
    @JsonProperty("layer")
    private String layer;
    @JsonProperty("bbox")
    private Bbox bbox;
    @JsonProperty("specialConditionsLink")
    private String specialConditionsLink;
    @JsonProperty("isSpecialConditions")
    private Boolean isSpecialConditions;
    @JsonProperty("isAcceptSpecialConditions")
    private Boolean isAcceptSpecialConditions;
    @JsonProperty("croppingUrl")
    private String croppingUrl;
    @JsonProperty("croppingLayer")
    private String croppingLayer;
    @JsonProperty("wmsUrl")
    private String wmsUrl;
    @JsonProperty("sessionKeys")
    private String sessionKeys;
    
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    @JsonIgnore
    @JsonIgnoreProperties
    private JSONArray identifiers;
    
    

    @JsonProperty("croppingMode")
    public String getCroppingMode() {
        return croppingMode;
    }

    @JsonProperty("croppingMode")
    public void setCroppingMode(String croppingMode) {
        this.croppingMode = croppingMode;
    }

    @JsonProperty("transport")
    public String getTransport() {
        return transport;
    }

    @JsonProperty("transport")
    public void setTransport(String transport) {
        this.transport = transport;
    }

    @JsonProperty("layer")
    public String getLayer() {
        return layer;
    }

    @JsonProperty("layer")
    public void setLayer(String layer) {
        this.layer = layer;
    }

    @JsonProperty("bbox")
    public Bbox getBbox() {
        return bbox;
    }
    
    public String getBboxString(){
    	StringWriter s = new StringWriter();
    	Bbox bb = this.getBbox();
    	s.append(bb.getLeft() + "," + bb.getBottom() + "," +bb.getRight() + "," + bb.getTop());
    	return s.toString();
    }

    @JsonProperty("bbox")
    public void setBbox(Bbox bbox) {
        this.bbox = bbox;
    }

    @JsonProperty("specialConditionsLink")
    public String getSpecialConditionsLink() {
        return specialConditionsLink;
    }

    @JsonProperty("specialConditionsLink")
    public void setSpecialConditionsLink(String specialConditionsLink) {
        this.specialConditionsLink = specialConditionsLink;
    }

    @JsonProperty("isSpecialConditions")
    public Boolean getIsSpecialConditions() {
        return isSpecialConditions;
    }

    @JsonProperty("isSpecialConditions")
    public void setIsSpecialConditions(Boolean isSpecialConditions) {
        this.isSpecialConditions = isSpecialConditions;
    }

    @JsonProperty("isAcceptSpecialConditions")
    public Boolean getIsAcceptSpecialConditions() {
        return isAcceptSpecialConditions;
    }

    @JsonProperty("isAcceptSpecialConditions")
    public void setIsAcceptSpecialConditions(Boolean isAcceptSpecialConditions) {
        this.isAcceptSpecialConditions = isAcceptSpecialConditions;
    }

    @JsonProperty("croppingUrl")
    public String getCroppingUrl() {
        return croppingUrl;
    }

    @JsonProperty("croppingUrl")
    public void setCroppingUrl(String croppingUrl) {
        this.croppingUrl = croppingUrl;
    }

    @JsonProperty("croppingLayer")
    public String getCroppingLayer() {
        return croppingLayer;
    }

    @JsonProperty("croppingLayer")
    public void setCroppingLayer(String croppingLayer) {
        this.croppingLayer = croppingLayer;
    }

    @JsonProperty("wmsUrl")
    public String getWmsUrl() {
        return wmsUrl;
    }

    @JsonProperty("wmsUrl")
    public void setWmsUrl(String wmsUrl) {
        this.wmsUrl = wmsUrl;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    @JsonProperty("sessionKeys")
    public String getSessionKeys() {
        return sessionKeys;
    }

    @JsonProperty("sessionKeys")
    public void setSessionKeys(String sessionKeys) {
        this.sessionKeys = sessionKeys;
    }
    
    @JsonProperty("identifiers")
    public JSONArray getIdentifiers() {
        return identifiers;
    }

    @JsonProperty("identifiers")
    public void setIdentifiers(JSONArray identifiers) {
        this.identifiers = identifiers;
    }

}
