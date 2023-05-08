package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.ApiDemoApplication;
import jp.co.axa.apidemo.controllers.EmployeeControllerIntTest;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiDemoApplication.class)
public class EmployeeServiceImplTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EntityManager em;

    private Employee employee;

    @Before
    public void initTest() {
        employee = EmployeeControllerIntTest.createEntity();;
    }

    @After
    public void clear() {
        try {
            employeeRepository.deleteAll();
        } catch (Exception ex) {
            // Do nothing
        }
    }

    @Test
    public void createEmployee() {
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
        Employee result = employeeService.saveEmployee(employee);
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate + 1);

        assertThat(result.getName()).isEqualTo(EmployeeControllerIntTest.DEFAULT_NAME);
        assertThat(result.getDepartment()).isEqualTo(EmployeeControllerIntTest.DEFAULT_DEPARTMENT);
        assertThat(result.getSalary()).isEqualTo(EmployeeControllerIntTest.DEFAULT_SALARY);
    }

    @Test(expected = Exception.class)
    public void createEmployeeNameNull() {
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
        employee.setName(null);
        Employee result = employeeService.saveEmployee(employee);
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test(expected = Exception.class)
    public void createEmployeeNameLessThan2() {
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
        employee.setName("A");
        Employee result = employeeService.saveEmployee(employee);
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test(expected = Exception.class)
    public void createEmployeeNameGreaterThan50() {
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
        employee.setName(EmployeeControllerIntTest.GREATERTHAN50);
        Employee result = employeeService.saveEmployee(employee);
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void updateEmployee() {
        // Initialize employee
        employeeRepository.saveAndFlush(employee);
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();

        // Disconnect from session.
        em.detach(employee);

        //Update employee
        employee.setName(EmployeeControllerIntTest.UPDATED_NAME);
        employee.setDepartment(EmployeeControllerIntTest.UPDATED_DEPARTMENT);
        employee.setSalary(EmployeeControllerIntTest.UPDATED_SALARY);
        Employee result = employeeService.updateEmployee(employee);

        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate);

        assertThat(result.getName()).isEqualTo(EmployeeControllerIntTest.UPDATED_NAME);
        assertThat(result.getDepartment()).isEqualTo(EmployeeControllerIntTest.UPDATED_DEPARTMENT);
        assertThat(result.getSalary()).isEqualTo(EmployeeControllerIntTest.UPDATED_SALARY);
    }

    @Test
    public void deleteEmployee() {
        // Initialize employee
        employeeRepository.saveAndFlush(employee);
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
        //Delete employee
        employeeService.deleteEmployee(employee.getId());

        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate - 1);
    }

    @Test(expected = Exception.class)
    public void deleteEmployeeNotFound() {
        // Initialize employee
        employeeRepository.saveAndFlush(employee);
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
        //Delete employee
        employeeService.deleteEmployee(Long.MAX_VALUE);

        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void findEmployee() {
        // Initialize employee
        employeeRepository.saveAndFlush(employee);

        Employee result = employeeService.getEmployee(employee.getId());
        assertNotNull(result);
        assertEquals(result.getId(), employee.getId());
    }

    @Test
    public void findEmployeeNotFound() {
        employeeRepository.saveAndFlush(employee);
        Employee result = employeeService.getEmployee(Long.MAX_VALUE);
        assertNull(result);
    }

    @Test
    public void findAllEmployee() {
        // Initialize employee1
        employeeRepository.saveAndFlush(employee);
        // Initialize employee2
        Employee employee2 = Employee.builder()
                .name(EmployeeControllerIntTest.UPDATED_NAME)
                .department(EmployeeControllerIntTest.UPDATED_DEPARTMENT)
                .salary(EmployeeControllerIntTest.UPDATED_SALARY).build();
        employeeRepository.saveAndFlush(employee2);

        //Find all employees.
        List<Employee> employees = employeeService.retrieveEmployees();
        assertNotNull(employees);
        assertEquals(employees.size(), 2);

        //Find employees for 1st page with page size 10
        Page<Employee> employeePage = employeeService.findAllEmployee(PageRequest.of(0, 10));
        assertNotNull(employeePage);
        assertEquals(employeePage.getContent().size(), 2);

        //Find employees for 1st page with page size 1
        employeePage = employeeService.findAllEmployee(PageRequest.of(0, 1));
        assertNotNull(employeePage);
        assertEquals(employeePage.getContent().size(), 1);

        //Find employees for 2nd page with page size 10
        employeePage = employeeService.findAllEmployee(PageRequest.of(1, 10));
        assertNotNull(employeePage);
        assertEquals(employeePage.getContent().size(), 0);
    }

}
