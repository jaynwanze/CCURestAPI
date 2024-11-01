
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import model.Loan;
import model.LoanDeposit;
import model.Student;
import dao.LoanDAO;
import dao.LoanDepositDAO;
import dao.StudentDAO;

@Path("/collegecreditunionservice")
public class CollegeCreditUnionService {
    @POST
    @Path("/newStudent")
    @Consumes("application/json")
    @Produces("text/plain")
    public String addStudentToDBJSON(Student student) {
        // do unique validation
        StudentDAO studentDAO = new StudentDAO();
        Student studentExists = studentDAO.getStudentByStudentNumber(student.getStudentNumber());
        if (studentExists != null) {
            return "Student with student number " + student.getStudentNumber() + " already exists";
        }
        // persist student
        studentDAO.persist(student);
        return "Student added to DB from JSON Param " + student.getName();
    }

    @DELETE
    @Path("/deleteStudent/{studentNumber}")
    @Produces("text/plain")
    public String deleteStudent(@PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() != null) {
            return "Cannot delete student with student number " + studentNumber + " while still has a loan, ";
        }

        // remove student
        studentDAO.remove(student);

        return "Student with student number " + studentNumber + " has been deleted";
    }

    @GET
    @Path("/viewStudentDetails/{studentNumber}")
    @Produces("text/plain")
    public String viewStudentDetails(@PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        }
        return "Student Details: " + student.toString();
    }

    @PUT
    @Path("/newLoan/{studentNumber}")
    @Consumes("application/xml")
    @Produces("text/plain")
    public String newLoan(Loan loan, @PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() != null) {
            return "Student with student number " + studentNumber + " already has a loan";
        }

        // Update student with new loan within db
        LoanDAO loanDao = new LoanDAO();
        loan.setLoanDeposits(new ArrayList<LoanDeposit>());
        loanDao.persist(loan);
        student.setLoan(loan);
        studentDAO.merge(student);

        return "Loan added to student " + student.getName() + " with student number " + student.getStudentNumber()
                + "\nLoan Details:" + student.getLoan().toString();
    }

    @GET
    @Path("/viewLoanDetails/{studentNumber}")
    @Produces("text/plain")
    public String viewLoanDetails(@PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO studentDao = new StudentDAO();
        Student student = studentDao.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() == null) {
            return "Student with student number " + studentNumber + " does not have a loan";
        }

        // Variables
        Loan loan = student.getLoan();

        // calculate total loan deposits
        double totalLoanDeposits = 0;
        if (loan.getLoanDeposits() != null) {
            for (LoanDeposit ld : student.getLoan().getLoanDeposits()) {
                totalLoanDeposits += ld.getAmount();
            }
        }

        // return loan details
        return "Total loan deposits must be less than or equal to loan amount.\n"
                + "Loan amount: " + loan.getAmount() + "\nTotal loan deposits: "
                + totalLoanDeposits + "\nRepayment Balance remaining: " + (loan.getAmount() - totalLoanDeposits
                        + "\nLoan Deposits: " + loan.getLoanDeposits());
    }

    @DELETE
    @Path("/deleteLoan/{studentNumber}")
    @Produces("text/plain")
    public String deleteLoan(@PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO studentDao = new StudentDAO();
        Student student = studentDao.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() == null) {
            return "Student with student number " + studentNumber + " does not have a loan";
        }
        // Variables
        Loan loan = student.getLoan();

        // Calculate total loan deposits
        double totalLoanDeposits = 0;
        if (loan.getLoanDeposits() != null) {
            for (LoanDeposit ld : student.getLoan().getLoanDeposits()) {
                totalLoanDeposits += ld.getAmount();
            }
        }

        if (totalLoanDeposits == student.getLoan().getAmount()) {
            return "Loan has already fully paid by student: " + student.getName()
                    + " with student number " + student.getStudentNumber()
                    + "\nYou are now eligible to remove loan from the system!";
        }
        // Remove student loan from db
        student.setLoan(null);
        studentDao.merge(student);
        LoanDAO loanDao = new LoanDAO();
        loanDao.remove(loan);

        return "Loan removed from student " + student.getName() + " with student number " + student.getStudentNumber();
    }

    @POST
    @Path("/payLoanDeposit/{studentNumber}")
    @Consumes("application/json")
    @Produces("text/plain")
    public String payLoanDeposit(LoanDeposit loanDeposit, @PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO studentDao = new StudentDAO();
        Student student = studentDao.getStudentByStudentNumber(studentNumber);
        double amount = loanDeposit.getAmount();
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() == null) {
            return "Student with student number " + studentNumber + " does not have a loan";
        } else if (amount <= 0) {
            return "Amount must be greater than 0";
        }

        // Variables
        LoanDepositDAO loanDepositDAO = new LoanDepositDAO();
        Loan loan = student.getLoan();

        // calculate total loan deposits
        double totalLoanDeposits = 0;
        if (loan.getLoanDeposits() != null) {
            for (LoanDeposit ld : student.getLoan().getLoanDeposits()) {
                totalLoanDeposits += ld.getAmount();
            }
        }

        // check if total loan deposits + amount is greater than loan amount
        if (totalLoanDeposits + amount > student.getLoan().getAmount()) {
            return "Total loan deposits must be less than or equal to loan amount.\n"
                    + "Loan amount: " + loan.getAmount() + "Total loan deposits: "
                    + totalLoanDeposits + " Balance remaining: " + (loan.getAmount() - totalLoanDeposits);
        } // check if total loan deposits + amount is equal to loan amount and remove loan
        else if (totalLoanDeposits + amount == student.getLoan().getAmount()) {
            return "Loan has already fully paid by student:" + student.getName()
                    + " with student number " + student.getStudentNumber()
                    + "\nYou are now eligible to remove loan from the system!";
        }

        // Add loan deposit to relavent loan in db
        loanDepositDAO.persist(loanDeposit);
        loan.getLoanDeposits().add(loanDeposit);
        LoanDAO loanDAO = new LoanDAO();
        loanDAO.merge(loan);

        // return success message
        return "Loan Deposit added to student " + student.getName() + " with student number "
                + student.getStudentNumber()
                + "\nLoan Deposit Details " + loanDeposit.toString();
    }

    @GET
    @Path("/viewLoanDeposits/{studentNumber}")
    @Produces("text/plain")
    public String viewLoanDeposits(@PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO studentDao = new StudentDAO();
        Student student = studentDao.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() == null) {
            return "Student with student number " + studentNumber + " does not have a loan";
        } else if (student.getLoan().getLoanDeposits() == null) {
            return "Student with student number " + studentNumber + " does not have any loan deposits";
        }
        // Variables
        Loan loan = student.getLoan();
        // return loan deposits
        return "Loan Deposits: " + loan.getLoanDeposits();
    }

}
