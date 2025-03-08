package com.swissre.EmployeeVerifications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeVerification {
	private static final String FILE_PATH = "emp.csv";

	public static void main(String[] args) throws IOException {
		List<Employee> employees = populateEmployeeData(FILE_PATH);
		Map<Integer, List<Employee>> managerEmployeeMap = employees.stream()
				.filter(e -> e.managerId != null)
				.collect(Collectors.groupingBy(e -> e.managerId));

		Map<Integer, Employee> employeeMap = employees.stream()
				.collect(Collectors.toMap(e -> e.id, e -> e));

		validateSalaries(managerEmployeeMap, employeeMap);
		validateHierarchyDepth(employeeMap);
	}

	// Read csv file and populated emplyee info as a list
	public static List<Employee> populateEmployeeData(String filePath)
			throws IOException {
		List<Employee> employees = new ArrayList<>();
		List<String> lines = Files.readAllLines(Path.of(filePath));

		for (String line : lines.subList(1, lines.size())) {
			String[] fields = line.split(",");

			try {
				int id = Integer.parseInt(fields[0].trim());
				String firstName = fields[1].trim();
				String lastName = fields[2].trim();
				double salary = Double.parseDouble(fields[3].trim());
				Integer managerId = (fields.length > 4
						&& !fields[4].trim().isEmpty())
								? Integer.parseInt(fields[4].trim())
								: null;

				employees.add(new Employee(id, firstName, lastName, salary,
						managerId));
			} catch (NumberFormatException e) {
				System.err.println(
						"Skipping invalid number format in line: " + line);
			}
		}
		return employees;
	}

	/*
	 * managerEmpoyeeMap holds manager id as key and respectice subordinate
	 * employees as value, calculate average salaries of subordinates and apply
	 * min max salary logic
	 */
	public static void validateSalaries(
			Map<Integer, List<Employee>> managerEmployeeMap,
			Map<Integer, Employee> employeeMap) {

		for (var entry : managerEmployeeMap.entrySet()) {
			Employee manager = employeeMap.get(entry.getKey());
			List<Employee> subordinates = entry.getValue();

			double avgSalary = subordinates.stream().mapToDouble(e -> e.salary)
					.average().orElse(0);
			double minSalAllowed = avgSalary * 1.2; // 20% more salary
			double maxSalAllowed = avgSalary * 1.5; // 50% more salary

			if (manager.salary < minSalAllowed) {
				System.out.printf(
						"Manager %s earns %.2f less than minimum salary allowed for a manager.\n",
						manager.firstName + " " + manager.lastName,
						minSalAllowed - manager.salary);
			} else if (manager.salary > maxSalAllowed) {
				System.out.printf(
						"Manager %s earns %.2f more than maximum salary allowed for a manager.\n",
						manager.firstName + " " + manager.lastName,
						manager.salary - maxSalAllowed);
			}
		}
	}

	/*
	 * employeeMap holds the key as emp id and employee object as value. from
	 * employee object fetch manager id and using that manager id traverse up
	 * unitil the manger id is null(CEO doesn't have manager).
	 */
	public static void validateHierarchyDepth(
			Map<Integer, Employee> employeeMap) {
		for (Employee employee : employeeMap.values()) {
			int depth = 0;
			Integer managerId = employee.managerId;
			while (managerId != null) {
				depth++;
				managerId = employeeMap.get(managerId).managerId;
				if (depth > 4) {
					System.out.printf(
							"Employee %s has long reporting line by %d levels.\n",
							employee.firstName + " " + employee.lastName,
							depth - 4);
					break;
				}
			}
		}
	}
}
