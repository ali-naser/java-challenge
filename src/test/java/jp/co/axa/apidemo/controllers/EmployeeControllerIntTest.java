package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.ApiDemoApplication;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import jp.co.axa.apidemo.services.EmployeeService;
import jp.co.axa.apidemo.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Test class for the Employee REST controller.
 *
 * @see EmployeeController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiDemoApplication.class)
public class EmployeeControllerIntTest {

    public static final String DEFAULT_NAME = "AAAAAAAAAA";
    public static final String UPDATED_NAME = "BBBBBBBBBB";
    public static final String DEFAULT_DEPARTMENT = "CCCCCCCCCC";
    public static final String UPDATED_DEPARTMENT = "DDDDDDDDDD";
    public static final Double DEFAULT_SALARY = Double.valueOf(123456);
    public static final Double UPDATED_SALARY = Double.valueOf(654321);
    public static final String GREATERTHAN50 = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private String BASE_URL_TEMPLATE = "/api/v1/employees";

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EntityManager em;

    private MockMvc restEmployeeMockMvc;

    private Employee employee;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;
    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Before
    public void setup() {
        final EmployeeController employeeController = new EmployeeController();
        employeeController.setEmployeeService(employeeService);

        this.restEmployeeMockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                //.setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     */
    public static Employee createEntity() {
        return Employee.builder().name(DEFAULT_NAME).department(DEFAULT_DEPARTMENT).salary(DEFAULT_SALARY).build();
    }

    @Before
    public void initTest() {
        employee = createEntity();
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
    @Transactional
    public void createEmployee() throws Exception {
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();

        // Create the Employee
        restEmployeeMockMvc.perform(post(BASE_URL_TEMPLATE)
                        .contentType(TestUtils.APPLICATION_JSON_UTF8)
                        .content(TestUtils.convertObjectToJsonBytes(employee)))
                .andExpect(status().isCreated());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate + 1);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEmployee.getDepartment()).isEqualTo(DEFAULT_DEPARTMENT);
        assertThat(testEmployee.getSalary()).isEqualTo(DEFAULT_SALARY);
    }

    @Test
    @Transactional
    public void createEmployeeWithExistingId() throws Exception {
        // Create the Employee
        employeeRepository.save(employee);
        em.flush();
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();

        // Initialize another employee
        Employee employee2 = new Employee(employee.getId(), DEFAULT_NAME,DEFAULT_SALARY, DEFAULT_DEPARTMENT);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeMockMvc.perform(post(BASE_URL_TEMPLATE)
                        .contentType(TestUtils.APPLICATION_JSON_UTF8)
                        .content(TestUtils.convertObjectToJsonBytes(employee2)))
                .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeRepository.findAll().size();
        // set the required name field null
        employee.setName(null);

        // Create the Employee, which fails.
        restEmployeeMockMvc.perform(post(BASE_URL_TEMPLATE)
                        .contentType(TestUtils.APPLICATION_JSON_UTF8)
                        .content(TestUtils.convertObjectToJsonBytes(employee)))
                .andExpect(status().isBadRequest());

        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameLengthIsBetween2to50() throws Exception {
        int databaseSizeBeforeTest = employeeRepository.findAll().size();
        // set the name field single character
        employee.setName("a");

        // Create the Employee, which fails.
        restEmployeeMockMvc.perform(post(BASE_URL_TEMPLATE)
                        .contentType(TestUtils.APPLICATION_JSON_UTF8)
                        .content(TestUtils.convertObjectToJsonBytes(employee)))
                .andExpect(status().isBadRequest());

        assertThat(employeeRepository.findAll()).hasSize(databaseSizeBeforeTest);
        // set the name field more than character
        employee.setName(GREATERTHAN50);
        assertThat(employee.getName().length()).isGreaterThan(50);

        // Create the Employee, which fails.
        restEmployeeMockMvc.perform(post(BASE_URL_TEMPLATE)
                        .contentType(TestUtils.APPLICATION_JSON_UTF8)
                        .content(TestUtils.convertObjectToJsonBytes(employee)))
                .andExpect(status().isBadRequest());

        assertThat(employeeRepository.findAll()).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDepartmentLengthLessThan50() throws Exception {
        int databaseSizeBeforeTest = employeeRepository.findAll().size();
        // set the name field single character
        employee.setDepartment(GREATERTHAN50);
        assertThat(employee.getDepartment().length()).isGreaterThan(50);

        // Create the Employee, which fails.
        restEmployeeMockMvc.perform(post(BASE_URL_TEMPLATE)
                        .contentType(TestUtils.APPLICATION_JSON_UTF8)
                        .content(TestUtils.convertObjectToJsonBytes(employee)))
                .andExpect(status().isBadRequest());

        assertThat(employeeRepository.findAll()).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void updateEmployee() throws Exception {
        // Initialize the database
        employeeService.saveEmployee(employee);

        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Update the employee
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).orElse(null);
        // Disconnect from session so that the updates on updatedEmployee are not directly saved in db
        em.detach(updatedEmployee);
        updatedEmployee.setName(UPDATED_NAME);
        updatedEmployee.setDepartment(UPDATED_DEPARTMENT);
        updatedEmployee.setSalary(UPDATED_SALARY);

        restEmployeeMockMvc.perform(put(BASE_URL_TEMPLATE + "/" + employee.getId())
                        .contentType(TestUtils.APPLICATION_JSON_UTF8)
                        .content(TestUtils.convertObjectToJsonBytes(updatedEmployee)))
                .andExpect(status().isOk());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployee.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
        assertThat(testEmployee.getSalary()).isEqualTo(UPDATED_SALARY);
    }

    @Test
    @Transactional
    public void updateNonExistingEmployee() throws Exception {
        // Initialize the database
        employeeService.saveEmployee(employee);
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Create the Employee
        // If the entity have a non-existing ID, it will be then employee not .
        restEmployeeMockMvc.perform(put(BASE_URL_TEMPLATE + "/" + employee.getId() + 1)
                        .contentType(TestUtils.APPLICATION_JSON_UTF8)
                        .content(TestUtils.convertObjectToJsonBytes(employee)))
                .andExpect(status().isBadRequest());
        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEmployee() throws Exception {
        // Initialize the database
        employeeService.saveEmployee(employee);
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Get the employee
        restEmployeeMockMvc.perform(delete(BASE_URL_TEMPLATE + "/{id}",employee.getId())
                        .accept(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate - 1);
    }

    @Test
    @Transactional
    public void deleteEmployeeIdNotFound() throws Exception {
        // Get the employee
        restEmployeeMockMvc.perform(delete(BASE_URL_TEMPLATE + "/{id}",1)
                        .accept(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getEmployee() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get the employee
        restEmployeeMockMvc.perform(get(BASE_URL_TEMPLATE + "/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value(employee.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.department").value(DEFAULT_DEPARTMENT))
                .andExpect(jsonPath("$.salary").value(DEFAULT_SALARY));
    }

    @Test
    @Transactional
    public void getNonExistingEmployee() throws Exception {
        // Get the employee
        restEmployeeMockMvc.perform(get(BASE_URL_TEMPLATE + "/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getAllEmployeesWithPagination() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);
        Employee employee2 = Employee.builder().name(UPDATED_NAME).department(UPDATED_DEPARTMENT).salary(UPDATED_SALARY).build();
        employeeRepository.saveAndFlush(employee2);

        assertThat(employeeRepository.findAll()).hasSize(2);

        // Get all the employeeList
        restEmployeeMockMvc.perform(get(BASE_URL_TEMPLATE + "?page=0&size=10&sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].department").value(hasItem(DEFAULT_DEPARTMENT)))
                .andExpect(jsonPath("$.[*].salary").value(hasItem(DEFAULT_SALARY)));

        //1st Page and page size 1; expect 1 entity
        restEmployeeMockMvc.perform(get(BASE_URL_TEMPLATE + "?page=0&size=1&sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)));

        //2nd Page and page size 10; expect 0 entity
        restEmployeeMockMvc.perform(get(BASE_URL_TEMPLATE + "?page=1&size=10&sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
