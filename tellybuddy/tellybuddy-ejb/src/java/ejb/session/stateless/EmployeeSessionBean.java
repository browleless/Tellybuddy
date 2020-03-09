package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author tjle2
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Override
    public Long createNewEmployee(Employee newEmployee) throws EmployeeUsernameExistException {

        Query query = entityManager.createQuery("SELECT e FROM Employee e WHERE e.username = :inUsername");
        query.setParameter("inUsername", newEmployee.getUsername());

        if (query.getSingleResult() != null) {
            throw new EmployeeUsernameExistException("Chosen username for employee already exists! Try another username.");
        } else {
            entityManager.persist(newEmployee);
            entityManager.flush();

            return newEmployee.getEmployeeId();
        }
    }

    @Override
    public List<Employee> retrieveAllEmployees() {

        Query query = entityManager.createQuery("SELECT e FROM Employee e");

        return query.getResultList();
    }

    @Override
    public Employee retrieveEmployeeById(Long employeeId) throws EmployeeNotFoundException {

        Employee employee = entityManager.find(Employee.class, employeeId);

        if (employee != null) {
            return employee;
        } else {
            throw new EmployeeNotFoundException("Employee ID " + employeeId + " does not exist!");
        }
    }

    @Override
    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException {

        Query query = entityManager.createQuery("SELECT e FROM Employee e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);

        try {
            return (Employee) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Employee Username " + username + " does not exist!");
        }
    }

    @Override
    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            Employee employee = retrieveEmployeeByUsername(username);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + employee.getSalt()));

            if (employee.getPassword().equals(passwordHash)) {
                return employee;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (EmployeeNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    @Override
    public void updateEmployee(Employee employee) throws EmployeeNotFoundException {

        if (employee != null && employee.getEmployeeId() != null) {
            Employee employeeToUpdate = retrieveEmployeeById(employee.getEmployeeId());
            if (employeeToUpdate.getUsername().equals(employee.getUsername())) {
                employeeToUpdate.setAccessRightEnum(employee.getAccessRightEnum());
                employeeToUpdate.setFirstName(employee.getFirstName());
                employeeToUpdate.setLastName(employee.getLastName());
            }
        } else {
            throw new EmployeeNotFoundException("Employee ID not provided for staff to be updated");
        }
    }
    
    @Override
    public void deleteEmployee(Employee employee) throws EmployeeNotFoundException {        
    
        Employee employeeToDelete = retrieveEmployeeById(employee.getEmployeeId());

        // currently can just delete since there is no association yet
        entityManager.remove(employeeToDelete);
    }
}
