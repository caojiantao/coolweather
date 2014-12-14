package model;

public class Province {
	private int id;
	private String provinceName;
	private String provinceCode;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProvinceName() {
		return this.provinceName;
	}

	public void setProvinceName(String name) {
		this.provinceName = name;
	}

	public String getProvinceCode() {
		return this.provinceCode;
	}

	public void setProvinceCode(String code) {
		this.provinceCode = code;
	}
}
