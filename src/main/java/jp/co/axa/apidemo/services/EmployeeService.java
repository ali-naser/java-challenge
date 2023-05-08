package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    /**
     * Get all employees
     *
     * @return a List of  all employees
     */
    public List<Employee> retrieveEmployees();

    /**
     * Get an employee by employeeId
     *
     * @param employeeId the ID of the employee
     * @return employee if found otherwise return null
     */
    public Employee getEmployee(Long employeeId);

    /**
     * Save an employee
     *
     * @param employee
     * @return the saved employee with all attributes
     */
    public Employee saveEmployee(Employee employee);

    /**
     * Delete an employee
     *
     * @param employeeId
     *
     */
    public void deleteEmployee(Long employeeId);

    /**
     * Update an employee with cache update
     *
     * @param employee
     * @return the saved employee with all attributes
     */
    public Employee updateEmployee(Employee employee);

    /**
     * Get all employees with pagination
     *
     * @param pageable
     * @return a page of employees
     */
    public Page<Employee> findAllEmployee(Pageable pageable);
}