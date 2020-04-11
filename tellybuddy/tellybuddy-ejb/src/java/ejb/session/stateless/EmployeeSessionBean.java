package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.security.CryptographicHelper;

/**
 *
 * @author tjle2
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    private final ValidatorFactory validatorFactory;

    private final Validator validator;
    private String salt;

    public EmployeeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewEmployee(Employee newEmployee) throws EmployeeUsernameExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(newEmployee);

        if (constraintViolations.isEmpty()) {
            try {
                entityManager.persist(newEmployee);
                entityManager.flush();

                return newEmployee.getEmployeeId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new EmployeeUsernameExistException("Employee cannot be created as there is already an employee created with the same username!");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

    }

    @Override
    public List<Employee> retrieveAllEmployees() {

        Query query = entityManager.createQuery("SELECT e FROM Employee e");

        return query.getResultList();
    }

    @Override
    public Employee retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException {

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
            System.out.println(passwordHash);
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
    public void updateStickyNotes(List<String> newNote, Employee employee) throws EmployeeNotFoundException{
        Employee employeeToUpdate = retrieveEmployeeByEmployeeId(employee.getEmployeeId());
        employeeToUpdate.setStickyNotes(newNote);
    }
    
    @Override
    public void updateEmployee(Employee employee) throws EmployeeNotFoundException {

        if (employee != null && employee.getEmployeeId() != null) {
            Employee employeeToUpdate = retrieveEmployeeByEmployeeId(employee.getEmployeeId());
            if (employeeToUpdate.getUsername().equals(employee.getUsername())) {
                employeeToUpdate.setAccessRightEnum(employee.getAccessRightEnum());
                employeeToUpdate.setFirstName(employee.getFirstName());
                employeeToUpdate.setLastName(employee.getLastName());
                employeeToUpdate.setPhotoPath(employee.getPhotoPath());
                employeeToUpdate.setUpdatedPassword(CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(employee.getPassword() + employeeToUpdate.getSalt())));
                entityManager.flush();

            }
        } else {
            throw new EmployeeNotFoundException("Employee ID not provided for staff to be updated");
        }
    }

    @Override
    public void deleteEmployee(Employee employee) throws EmployeeNotFoundException {

        Employee employeeToDelete = retrieveEmployeeByEmployeeId(employee.getEmployeeId());

        // currently can just delete since there is no association yet
        entityManager.remove(employeeToDelete);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Employee>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
