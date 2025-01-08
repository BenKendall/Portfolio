package com.nhsapp.model;

public class Service {
	private int serviceId;
	private String serviceType;
	private String name;
	private String contactNumber;
	private String emailAddress;
	private String lineAddress;
	private String townCity;
	private String county;
	private String postcode;
	private boolean isPrivate;
	private double distance;

	public Service(int serviceId, String serviceType, String name, String contactNumber, String emailAddress,
			String lineAddress, String townCity, String county, String postcode, boolean isPrivate, double distance) {
		this.serviceId = serviceId;
		this.serviceType = serviceType;
		this.name = name;
		this.contactNumber = contactNumber;
		this.emailAddress = emailAddress;
		this.lineAddress = lineAddress;
		this.townCity = townCity;
		this.county = county;
		this.postcode = postcode;
		this.isPrivate = isPrivate;
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getLineAddress() {
		return lineAddress;
	}

	public void setLineAddress(String lineAddress) {
		this.lineAddress = lineAddress;
	}

	public String getTownCity() {
		return townCity;
	}

	public void setTownCity(String townCity) {
		this.townCity = townCity;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	@Override
	public String toString() {
		return name;
	}

	public String toCSVFormat() {
		return serviceType + "," + name + "," + contactNumber + "," + emailAddress + "," + lineAddress + "," + townCity
				+ "," + county + "," + postcode;
	}
}
