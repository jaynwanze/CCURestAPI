
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import model.CollegeCreditUnion;
import model.Loan;
import model.LoanDeposit;
import model.Student;
import dao.CollegeCreditUnionDAO;
import dao.LoanDAO;
import dao.LoanDepositDAO;
import dao.StudentDAO;

@Path("/collegecreditunionservice")
public class CollegeCreditUnionService {

    private static CollegeCreditUnion collegeCreditUnion = new CollegeCreditUnion();

    static {
        collegeCreditUnion.setStudents(new ArrayList<Student>());
        CollegeCreditUnionDAO dao = new CollegeCreditUnionDAO();
        dao.persist(collegeCreditUnion);
    }

    @GET
    @Path("/hello")
    @Produces("text/plain")
    public String hello() {
        return "Hello, welcome to the College Credit Union Service";
    }

    @POST
    @Path("/newStudent")
    @Consumes("application/json")
    public String addStudentToDBJSON(Student student) {
        // do unique validation
        StudentDAO dao = new StudentDAO();
        Student studentExists = dao.getStudentByStudentNumber(student.getStudentNumber());
        if (studentExists != null) {
            return "Student with student number " + student.getStudentNumber() + " already exists";
        }
        dao.persist(student);
        return "Student added to DB from JSON Param " + student.getName();
    }

    @DELETE
    @Path("/deleteEmployee/{studentNumber}")
    @Produces("text/plain")
    public String deleteStudent(@PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO dao = new StudentDAO();
        Student student = dao.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() != null) {
            return "Cannot delete student with student number " + studentNumber + " while still has a loan, ";
        }
        // remove student
        dao.remove(student);
        return "Student " + student + " deleted";
    }

    @PUT
    @Path("/newLoan/{studentNumber}")
    @Consumes("application/json")
    @Produces("text/plain")
    public String newLoan(Loan loan, @PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO dao = new StudentDAO();
        Student student = dao.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() != null) {
            return "Student with student number " + studentNumber + " already has a loan";
        }

        // Initalize empty loan deposits list and persist loan
        LoanDAO loanDao = new LoanDAO();
        loan.setLoanDeposits(new ArrayList<LoanDeposit>());
        loanDao.persist(loan);

        // add loan to student and merge
        student.setLoan(loan);
        dao.merge(student);

        return "Loan added to student " + student.getName() + " with student number " + student.getStudentNumber()
                + " and Loan Details " + student.getLoan().toString();
    }

    @PUT
    @Path("/payLoanDeposit/{studentNumber}/{amount}")
    @Consumes("application/json")
    @Produces("text/plain")
    public String payLoanDeposit(LoanDeposit loanDeposit, @PathParam("studentNumber") String studentNumber,
            @PathParam("amount") double amount) {
        // get student
        StudentDAO studentDao = new StudentDAO();
        Student student = studentDao.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() == null) {
            return "Student with student number " + studentNumber + " does not have a loan";
        } else if (amount <= 0) {
            return "Amount must be greater than 0";
        }

        // Variables
        LoanDepositDAO loanDepositDAO = new LoanDepositDAO();
        LoanDAO loanDAO = new LoanDAO();
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
            return "Total loan deposits must be less than or equal to loan amount.\n "
                    + "Loan amount: " + loan.getAmount() + "Total loan deposits: "
                    + totalLoanDeposits + " Balance remaining: " + (loan.getAmount() - totalLoanDeposits);
        } // check if total loan deposits + amount is equal to loan amount and remove loan
        else if (totalLoanDeposits + amount == student.getLoan().getAmount()) {
            loanDAO.remove(loan);
            student.setLoan(null);
            studentDao.merge(student);
            return "Loan fully paid by student and removed from the system:" + student.getName()
                    + " with student number " + student.getStudentNumber();
        }

        // persist loan deposit
        loanDepositDAO.persist(loanDeposit);
        // add loan deposit to students loan
        loan.getLoanDeposits().add(loanDeposit);
        loanDAO.merge(loan);

        // return success message
        return "Loan Deposit added to student " + student.getName() + " with student number "
                + student.getStudentNumber()
                + " and Loan Deposit Details " + loanDeposit.toString();
    }

    @GET
    @Path("/viewLoanDetails/{studentNumber}")
    @Consumes("application/json")
    @Produces("text/plain")
    public String viewLoanDetails(LoanDeposit loanDeposit, @PathParam("studentNumber") String studentNumber) {
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
        return "Total loan deposits must be less than or equal to loan amount.\n "
                + "Loan amount: " + loan.getAmount() + "Total loan deposits: "
                + totalLoanDeposits + "Repayment Balance remaining: " + (loan.getAmount() - totalLoanDeposits
                        + "Loan Deposits: " + loan.getLoanDeposits());
    }
}
