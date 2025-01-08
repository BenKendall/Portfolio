package com.nhsapp.database;

import com.nhsapp.model.Service;
import com.nhsapp.util.HaversineCalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DatabaseConnector {

	private static final String DB_URL = "jdbc:sqlite:resources/database/nhs_app.db";

	public static Connection connect() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(DB_URL);
		return conn;
	}

	public static void disconnect(Connection conn) throws SQLException {
		conn.close();
	}

	public List<Service> getServicesByPostcode(String postcode) {
		List<Service> services = new ArrayList<>();
		String sql = "SELECT * FROM Service WHERE postcode = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, postcode);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Service service = new Service(rs.getInt("service_id"), rs.getString("service_type"),
						rs.getString("name"), rs.getString("contact_number"), rs.getString("email_address"),
						rs.getString("line_address"), rs.getString("town/city"), rs.getString("county"),
						rs.getString("postcode"), rs.getBoolean("private"), 0.0);
				services.add(service);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return services;
	}

	public List<Service> getNearestServicesByType(String inputPostcode, String serviceType) {
		List<Service> services = new ArrayList<>();
		String sql = "SELECT s.*, " + "inputPC.latitude as input_lat, inputPC.longitude as input_long, "
				+ "servicePC.latitude as service_lat, servicePC.longitude as service_long " + "FROM Service s "
				+ "INNER JOIN Postcode servicePC ON s.postcode = servicePC.postcode " + "CROSS JOIN Postcode inputPC "
				+ "WHERE inputPC.postcode = ? AND s.service_type = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, inputPostcode);
			pstmt.setString(2, serviceType);
			ResultSet rs = pstmt.executeQuery();

			double inputLat = 0.0;
			double inputLong = 0.0;
			if (rs.next()) {
				inputLat = rs.getDouble("input_lat");
				inputLong = rs.getDouble("input_long");

			}

			while (rs.next()) {
				processServiceRow(services, rs, inputLat, inputLong);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		services.sort(Comparator.comparingDouble(Service::getDistance));
		return services;
	}

	private void processServiceRow(List<Service> services, ResultSet rs, double inputLat, double inputLong)
			throws SQLException {
		double serviceLat = rs.getDouble("service_lat");
		double serviceLong = rs.getDouble("service_long");
		double distance = HaversineCalculator.calculateDistance(inputLat, inputLong, serviceLat, serviceLong);

		Service service = new Service(rs.getInt("service_id"), rs.getString("service_type"), rs.getString("name"),
				rs.getString("contact_number"), rs.getString("email_address"), rs.getString("line_address"),
				rs.getString("town/city"), rs.getString("county"), rs.getString("postcode"), rs.getBoolean("private"),
				distance);
		services.add(service);
	}
}
