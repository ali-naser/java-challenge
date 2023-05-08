package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Get all employees
     *
     * @return a List of  all employees
     */
    @Transactional(readOnly = true)
    public List<Employee> retrieveEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }

    /**
     * Get an employee by employeeId
     *
     * @param employeeId the ID of the employee
     * @return employee if found otherwise return null
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "employee", key="#p0", condition="#p0!=null", unless="#result == null")
    public Employee getEmployee(Long employeeId) {
        // Return the value if present, otherwise return null.
        return employeeRepository.findById(employeeId).orElse(null);
    }

    /**
     * Save an employee
     *
     * @param employee
     * @return the saved employee with all attributes
     */
    public Employee saveEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    /**
     * Delete an employee
     *
     * @param employeeId
     *
     */
    @CacheEvict(value = "employee", allEntries = true)
    public void deleteEmployee(Long employeeId){
        employeeRepository.deleteById(employeeId);
    }

    /**
     * Update an employee with cache update
     *
     * @param employee
     * @return the saved employee with all attributes
     */
    @CachePut(value = "employee", key = "#p0.id")
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * Get all employees with pagination
     *
     * @param pageable
     * @return a page of employees
     */
    @Transactional(readOnly = true)
    public Page<Employee> findAllEmployee(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }
}