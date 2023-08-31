package hsy.pipe;

import org.json.JSONObject;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.util.ConversionHelper;
import fi.nls.oskari.util.JSONHelper;

public class TagPipeConfiguration {
	
	protected final static String PARAM_TAG_ID = "tag_id";
    protected final static String PARAM_TAG_TYPE = "tag_type";
    protected final static String PARAM_TAG_ADDRESS = "tag_address";
    protected final static String PARAM_TAG_PIPE_SIZE = "tag_pipe_size";
    protected final static String PARAM_TAG_LOW_PRESSURE_LEVEL = "tag_low_pressure_level";
    protected final static String PARAM_TAG_MAX_PRESSURE_LEVEL = "tag_max_pressure_level";
    protected final static String PARAM_TAG_MAX_WATER_TAKE = "tag_max_water_take";
    protected final static String PARAM_TAG_MIN_PRESSURE_LEVEL = "tag_min_pressure_level";
    protected final static String PARAM_TAG_BOTTOM_HEIGHT = "tag_bottom_height";
    protected final static String PARAM_TAG_LOW_TAG_HEIGHT = "tag_low_tag_height";
    protected final static String PARAM_TAG_BARRAGE_HEIGHT = "tag_barrage_height";
    protected final static String PARAM_TAG_GROUND_HEIGHT = "tag_ground_height";
    protected final static String PARAM_TAG_OTHER_ISSUE = "tag_other_issue";
    protected final static String PARAM_TAG_GEOJSON = "tag_geojson";
	//Separate Finnish kiinteistötunnus (real estate ID) into 4 different fields
	protected final static String PARAM_TAG_MUNICIPALITY = "tag_municipality";
	protected final static String PARAM_TAG_NEIGHBORHOOD = "tag_neighborhood";
	protected final static String PARAM_TAG_BLOCK = "tag_block";
	protected final static String PARAM_TAG_PLOT = "tag_plot";
	
	private int tagId = -1;
	private String tagType;
	private String tagAddress;
	private Double tagPipeSize;
	private Double tagLowPressureLevel;
	private Double tagMaxPressureLevel;
	private Double tagMaxWaterTake;
	private Double tagMinPressureLevel;
	private Double tagBottomHeight;
	private Double tagLowTagHeight;
	private Double tagBarrageHeight;
	private Double tagGroundHeight;
	private String tagOtherIssue;
	private JSONObject tagGeoJson;
	private String tagMunicipality;
	private String tagNeighborhood;
	private String tagBlock;
	private String tagPlot;
	
    /**
     * Constructor from action parameters
     * NOTE: Doesn't set tagType or tagId!
     * This is because insert/update logic might differ
     */
    public TagPipeConfiguration(ActionParameters params) throws Exception {
        setTagAddress(ConversionHelper.getString(params.getRequiredParam(PARAM_TAG_ADDRESS),""));
        setTagPipeSize(ConversionHelper.getDouble(params.getHttpParam(PARAM_TAG_PIPE_SIZE), 0));
        setTagLowPressureLevel(ConversionHelper.getDouble(params.getHttpParam(PARAM_TAG_LOW_PRESSURE_LEVEL), 0));
        setTagMaxPressureLevel(ConversionHelper.getDouble(params.getHttpParam(PARAM_TAG_MAX_PRESSURE_LEVEL), 0));
        setTagMaxWaterTake(ConversionHelper.getDouble(params.getHttpParam(PARAM_TAG_MAX_WATER_TAKE), 0));
        setTagMinPressureLevel(ConversionHelper.getDouble(params.getHttpParam(PARAM_TAG_MIN_PRESSURE_LEVEL), 0));
        setTagBottomHeight(ConversionHelper.getDouble(params.getHttpParam(PARAM_TAG_BOTTOM_HEIGHT), 0));
        setTagLowTagHeight(ConversionHelper.getDouble(params.getHttpParam(PARAM_TAG_LOW_TAG_HEIGHT), 0));
        setTagBarrageHeight(ConversionHelper.getDouble(params.getHttpParam(PARAM_TAG_BARRAGE_HEIGHT), 0));
        setTagGroundHeight(ConversionHelper.getDouble(params.getHttpParam(PARAM_TAG_GROUND_HEIGHT), 0));
        setTagOtherIssue(ConversionHelper.getString(params.getHttpParam(PARAM_TAG_OTHER_ISSUE),""));
        setTagGeoJson(new JSONObject(ConversionHelper.getString(params.getHttpParam(PARAM_TAG_GEOJSON),"")));
        setTagMunicipality(ConversionHelper.getString(params.getRequiredParam(PARAM_TAG_MUNICIPALITY),""));
        setTagNeighborhood(ConversionHelper.getString(params.getRequiredParam(PARAM_TAG_NEIGHBORHOOD),""));
        setTagBlock(ConversionHelper.getString(params.getRequiredParam(PARAM_TAG_BLOCK),""));
        setTagPlot(ConversionHelper.getString(params.getRequiredParam(PARAM_TAG_PLOT),""));
    }

    public JSONObject getAsJSONObject() {
		final JSONObject root = new JSONObject();
		JSONHelper.putValue(root, PARAM_TAG_ID, this.getTagId());
		JSONHelper.putValue(root, PARAM_TAG_TYPE, this.getTagType());
		JSONHelper.putValue(root, PARAM_TAG_ADDRESS, this.getTagAddress());
		JSONHelper.putValue(root, PARAM_TAG_PIPE_SIZE, this.getTagPipeSize());
		JSONHelper.putValue(root, PARAM_TAG_LOW_PRESSURE_LEVEL, this.getTagLowPressureLevel());
		JSONHelper.putValue(root, PARAM_TAG_MAX_PRESSURE_LEVEL, this.getTagMaxPressureLevel());
		JSONHelper.putValue(root, PARAM_TAG_MAX_WATER_TAKE, this.getTagMaxWaterTake());
		JSONHelper.putValue(root, PARAM_TAG_MIN_PRESSURE_LEVEL, this.getTagMinPressureLevel());
		JSONHelper.putValue(root, PARAM_TAG_BOTTOM_HEIGHT, this.getTagBottomHeight());
		JSONHelper.putValue(root, PARAM_TAG_LOW_TAG_HEIGHT, this.getTagLowTagHeight());
		JSONHelper.putValue(root, PARAM_TAG_BARRAGE_HEIGHT, this.getTagBarrageHeight());
		JSONHelper.putValue(root, PARAM_TAG_GROUND_HEIGHT, this.getTagGroundHeight());
		JSONHelper.putValue(root, PARAM_TAG_OTHER_ISSUE, this.getTagOtherIssue());
		JSONHelper.putValue(root, PARAM_TAG_GEOJSON, this.getTagGeoJson());
		JSONHelper.putValue(root, PARAM_TAG_MUNICIPALITY, this.getTagMunicipality());
		JSONHelper.putValue(root, PARAM_TAG_NEIGHBORHOOD, this.getTagNeighborhood());
		JSONHelper.putValue(root, PARAM_TAG_BLOCK, this.getTagBlock());
		JSONHelper.putValue(root, PARAM_TAG_PLOT, this.getTagPlot());
		return root;
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public String getTagAddress() {
		return tagAddress;
	}

	public void setTagAddress(String tagAddress) {
		this.tagAddress = tagAddress;
	}

	public Double getTagPipeSize() {
		return tagPipeSize;
	}

	public void setTagPipeSize(Double tagPipeSize) {
		this.tagPipeSize = tagPipeSize;
	}

	public String getTagOtherIssue() {
		return tagOtherIssue;
	}

	public void setTagOtherIssue(String tagOtherIssue) {
		this.tagOtherIssue = tagOtherIssue;
	}

	public Double getTagLowPressureLevel() {
		return tagLowPressureLevel;
	}

	public void setTagLowPressureLevel(Double tagLowPressureLevel) {
		this.tagLowPressureLevel = tagLowPressureLevel;
	}

	public Double getTagMaxPressureLevel() {
		return tagMaxPressureLevel;
	}

	public void setTagMaxPressureLevel(Double tagMaxPressureLevel) {
		this.tagMaxPressureLevel = tagMaxPressureLevel;
	}

	public Double getTagMaxWaterTake() {
		return tagMaxWaterTake;
	}

	public void setTagMaxWaterTake(Double tagMaxWaterTake) {
		this.tagMaxWaterTake = tagMaxWaterTake;
	}

	public Double getTagMinPressureLevel() {
		return tagMinPressureLevel;
	}

	public void setTagMinPressureLevel(Double tagMinPressureLevel) {
		this.tagMinPressureLevel = tagMinPressureLevel;
	}

	public Double getTagBottomHeight() {
		return tagBottomHeight;
	}

	public void setTagBottomHeight(Double tagBottomHeight) {
		this.tagBottomHeight = tagBottomHeight;
	}

	public Double getTagLowTagHeight() {
		return tagLowTagHeight;
	}

	public void setTagLowTagHeight(Double tagLowTagHeight) {
		this.tagLowTagHeight = tagLowTagHeight;
	}

	public Double getTagBarrageHeight() {
		return tagBarrageHeight;
	}

	public void setTagBarrageHeight(Double tagBarrageHeight) {
		this.tagBarrageHeight = tagBarrageHeight;
	}

	public Double getTagGroundHeight() {
		return tagGroundHeight;
	}

	public void setTagGroundHeight(Double tagGroundHeight) {
		this.tagGroundHeight = tagGroundHeight;
	}

	public JSONObject getTagGeoJson() {
		return tagGeoJson;
	}

	public void setTagGeoJson(JSONObject tagGeoJson) {
		this.tagGeoJson = tagGeoJson;
	}

	public String getTagMunicipality() {
		return tagMunicipality;
	}

	public void setTagMunicipality(String tagMunicipality) {
		this.tagMunicipality = tagMunicipality;
	}

	public String getTagNeighborhood() {
		return tagNeighborhood;
	}

	public void setTagNeighborhood(String tagNeighborhood) {
		this.tagNeighborhood = tagNeighborhood;
	}

	public String getTagBlock() {
		return tagBlock;
	}

	public void setTagBlock(String tagBlock) {
		this.tagBlock = tagBlock;
	}

	public String getTagPlot() {
		return tagPlot;
	}

	public void setTagPlot(String tagPlot) {
		this.tagPlot = tagPlot;
	}

	
}
