package com.nhsapp.ui;

import com.nhsapp.database.DatabaseConnector;
import com.nhsapp.model.Service;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainApplicationWindow {
	private JFrame frame;
	private JTextField postcodeField;
	private JButton searchButton, exportButton, copyToClipboardButton;
	private JComboBox<Service> gpDropdown, dentistDropdown, schoolDropdown, opticianDropdown;
	private JCheckBox gpCheckBox, dentistCheckBox, schoolCheckBox, opticianCheckBox;
	private JPanel dropdownPanel, buttonPanel;

	public MainApplicationWindow() {
		initialize();
	}
	
	//Sets up GUI
	private void initialize() {
		frame = new JFrame("NHS Service Finder");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		JPanel inputPanel = new JPanel();
		postcodeField = new JTextField(10);
		inputPanel.add(postcodeField);

		gpCheckBox = new JCheckBox("GP");
		dentistCheckBox = new JCheckBox("Dentist");
		schoolCheckBox = new JCheckBox("School/Nursery");
		opticianCheckBox = new JCheckBox("Optician");
		inputPanel.add(gpCheckBox);
		inputPanel.add(dentistCheckBox);
		inputPanel.add(schoolCheckBox);
		inputPanel.add(opticianCheckBox);

		frame.add(inputPanel, BorderLayout.NORTH);

		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		searchButton = new JButton("Search");
		searchButton.addActionListener(this::searchActionPerformed);
		buttonPanel.add(searchButton);
		exportButton = new JButton("Export to CSV");
		exportButton.addActionListener(this::exportActionPerformed);
		exportButton.setVisible(false);
		buttonPanel.add(exportButton);

		copyToClipboardButton = new JButton("Copy to Clipboard");
		copyToClipboardButton.addActionListener(this::copyToClipboardActionPerformed);
		copyToClipboardButton.setVisible(false);
		buttonPanel.add(copyToClipboardButton);

		frame.add(buttonPanel, BorderLayout.SOUTH);

		dropdownPanel = new JPanel(new GridLayout(0, 1, 10, 5));
		frame.add(dropdownPanel, BorderLayout.CENTER);

		gpDropdown = new JComboBox<>();
		dentistDropdown = new JComboBox<>();
		schoolDropdown = new JComboBox<>();
		opticianDropdown = new JComboBox<>();

		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	//Search Button action
	private void searchActionPerformed(ActionEvent e) {
		String enteredPostcode = postcodeField.getText();
		DatabaseConnector dbConnector = new DatabaseConnector();

		dropdownPanel.removeAll();

		if (gpCheckBox.isSelected()) {
			dropdownPanel.add(new JLabel("GP:"));
			updateServiceDropdown(gpDropdown, dbConnector.getNearestServicesByType(enteredPostcode, "GP"));
			dropdownPanel.add(gpDropdown);
		}
		if (dentistCheckBox.isSelected()) {
			dropdownPanel.add(new JLabel("Dentists:"));
			updateServiceDropdown(dentistDropdown, dbConnector.getNearestServicesByType(enteredPostcode, "Dentist"));
			dropdownPanel.add(dentistDropdown);
		}
		if (schoolCheckBox.isSelected()) {
			dropdownPanel.add(new JLabel("Schools/Nurseries:"));
			updateServiceDropdown(schoolDropdown, dbConnector.getNearestServicesByType(enteredPostcode, "School"));
			dropdownPanel.add(schoolDropdown);
		}
		if (opticianCheckBox.isSelected()) {
			dropdownPanel.add(new JLabel("Opticians:"));
			updateServiceDropdown(opticianDropdown, dbConnector.getNearestServicesByType(enteredPostcode, "Optician"));
			dropdownPanel.add(opticianDropdown);
		}

		dropdownPanel.revalidate();
		dropdownPanel.repaint();

		exportButton.setVisible(true);
		copyToClipboardButton.setVisible(true);

		frame.pack();
	}
	
	//Exports selected options to CSV
	private void exportActionPerformed(ActionEvent e) {
		List<Service> selectedServices = getSelectedServices();
		if (selectedServices.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "No services selected for export.", "Export Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");
		fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

		int userSelection = fileChooser.showSaveDialog(frame);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			if (!filePath.endsWith(".csv")) {
				filePath += ".csv";
			}
			writeServicesToCSV(selectedServices, filePath);
		}
	}
	
	//Copies to clipboard
	private void copyToClipboardActionPerformed(ActionEvent e) {
		List<Service> selectedServices = getSelectedServices();
		if (selectedServices.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "No services selected to copy.", "Copy Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		String textToCopy = formatServicesForClipboard(selectedServices);
		StringSelection stringSelection = new StringSelection(textToCopy);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		JOptionPane.showMessageDialog(frame, "Results copied to clipboard.", "Copy to Clipboard",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	//Selected services from dropdowns
	private List<Service> getSelectedServices() {
		List<Service> services = new ArrayList<>();

		if (gpCheckBox.isSelected() && gpDropdown.getSelectedItem() != null) {
			services.add((Service) gpDropdown.getSelectedItem());
		}
		if (dentistCheckBox.isSelected() && dentistDropdown.getSelectedItem() != null) {
			services.add((Service) dentistDropdown.getSelectedItem());
		}
		if (schoolCheckBox.isSelected() && schoolDropdown.getSelectedItem() != null) {
			services.add((Service) schoolDropdown.getSelectedItem());
		}
		if (opticianCheckBox.isSelected() && opticianDropdown.getSelectedItem() != null) {
			services.add((Service) opticianDropdown.getSelectedItem());
		}

		return services;
	}
	
	//Writes the selected services to a CSV file
	private void writeServicesToCSV(List<Service> services, String filePath) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			writer.write(
					"Service ID,Service Type,Name,Contact Number,Email Address,Line Address,Town/City,County,Postcode,Is Private,Distance\n");
			for (Service service : services) {
				writer.write(service.toCSVFormat() + "\n");
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(frame, "Error writing to file: " + ex.getMessage(), "Export Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	//Formats the string for the clipboard
	private String formatServicesForClipboard(List<Service> services) {
		StringBuilder sb = new StringBuilder();
		for (Service service : services) {
			sb.append(service.toCSVFormat()).append("\n");
		}
		return sb.toString();
	}
	
	//Updates dropdown
	private void updateServiceDropdown(JComboBox<Service> dropdown, List<Service> services) {
		dropdown.removeAllItems();
		services.stream().limit(6).forEach(dropdown::addItem);
	}

	public void show() {
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			MainApplicationWindow window = new MainApplicationWindow();
			window.show();
		});
	}
}
