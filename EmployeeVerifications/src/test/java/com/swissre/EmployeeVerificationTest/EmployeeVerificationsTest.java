package com.swissre.EmployeeVerificationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.swissre.EmployeeVerifications.Employee;
import com.swissre.EmployeeVerifications.EmployeeVerification;

class EmployeeVerificationsTest {

	@Test
	void testReadEmployees() throws IOException {
		List<Employee> list = EmployeeVerification
				.populateEmployeeData("emp.csv");
		assertEquals(list.size(), 20);
	}
}
