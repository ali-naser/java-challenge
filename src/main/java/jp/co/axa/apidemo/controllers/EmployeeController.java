package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import jp.co.axa.apidemo.util.HeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    private static final String ENTITY_NAME = "employee";

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * GET  /employees : get all the employees with pagination.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of employees in body
     */
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getEmployees(Pageable pageable) {
        Page<Employee> employees = employeeService.findAllEmployee(pageable);
        return ResponseEntity.ok().headers(HeaderUtil.generatePaginationHeaders(employees)).body(employees.getContent());
    }

    /**
     * GET  /employees/:id : get the "id" employee.
     *
     * @param employeeId the id of the employee to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employee, or with status 404 (Not Found)
     */
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployee(@PathVariable(name = "employeeId") Long employeeId) {
        //Read employee
        Employee employee = employeeService.getEmployee(employeeId);
        return  null != employee ? ResponseEntity.ok().body(employee) : ResponseEntity.notFound().headers(HeaderUtil.createEntityNotFoundHeader(ENTITY_NAME)).build();
    }

    /**
     * POST  /employees : Create a new employee.
     *
     * @param employee the employee to create
     * @return the ResponseEntity with status 201 (Created) and with body the new employee, or with status 400 (Bad Request) if the ID already exist
     */
    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        try {
            if (null != employee.getId() && null != employeeService.getEmployee(employee.getId())) {
                return ResponseEntity.badRequest()
                        .headers(HeaderUtil.createFailureHeaders(ENTITY_NAME, "idExist", "A new employee already have an ID")).body(null);
            }
            Employee result = employeeService.saveEmployee(employee);
            return ResponseEntity.created(new URI("/api/v1/employees/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationHeaders(ENTITY_NAME, result.getName()))
                    .body(result);
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureHeaders(ENTITY_NAME, ex.getMessage(), "Cannot create employee")).body(null);
        }
    }

    /**
     * DELETE  /employees/:id : delete the "id" employee.
     *
     * @param employeeId the id of the employee to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable(name = "employeeId") Long employeeId){
        Employee employee = employeeService.getEmployee(employeeId);
        if (null == employee) {
            return ResponseEntity.notFound().headers(HeaderUtil.createEntityNotFoundHeader(ENTITY_NAME)).build();
        }
        try {
            employeeService.deleteEmployee(employeeId);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeleteHeaders(ENTITY_NAME, employee.getName())).build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureHeaders(ENTITY_NAME, ex.getMessage(), "Cannot delete employee")).body(null);
        }
    }

    /**
     * PUT  /employees/:id : update the "id" employee.
     *
     * @param employee the employee to update
     * @return the ResponseEntity with status 200 (OK)and with body the updated employee, or with status 400 (Bad Request) if the employee not exist.
     */
    @PutMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(@Valid @RequestBody Employee employee,
                               @PathVariable(name="employeeId") Long employeeId){
        Employee emp = employeeService.getEmployee(employeeId);
        if (null == emp) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureHeaders(ENTITY_NAME, "notExist", "Employee not found")).body(null);
        }

        try {
            employee.setId(employeeId);
            Employee result = employeeService.updateEmployee(employee);
            return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateHeaders(ENTITY_NAME, result.getName()))
                    .body(result);
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureHeaders(ENTITY_NAME, ex.getMessage(), "Cannot update employee")).body(null);
        }
    }

}
