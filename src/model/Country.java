package model;

public class Country {
	private int id;
	private String CountryName;
	private String CountryCode;
	private int cityId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCountryName() {
		return CountryName;
	}

	public void setCountryName(String name) {
		this.CountryName = name;
	}

	public String getCountryCode() {
		return CountryCode;
	}

	public void setCountryCode(String code) {
		this.CountryCode = code;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int id) {
		this.cityId = id;
	}
}
