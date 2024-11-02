
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
import dao.CollegeCreditUnionDAO;
import model.CollegeCreditUnion;

@Path("/collegecreditunionservice")
public class CollegeCreditUnionService {

    private static CollegeCreditUnion collegeCreditUnion;

    static {
        try {
            CollegeCreditUnionDAO collegeCreditUnionDAO = new CollegeCreditUnionDAO();
            collegeCreditUnion = collegeCreditUnionDAO.getCollegeCreditUnion();
            if (collegeCreditUnion == null) {
                collegeCreditUnion = new CollegeCreditUnion();
                collegeCreditUnion.setStudents(new ArrayList<Student>());
                collegeCreditUnionDAO.persist(collegeCreditUnion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    @POST
    @Path("/newStudent")
    @Consumes("application/json")
    @Produces("text/plain")
    public String addStudentToDBJSON(Student student) {
        // do unique validation
        StudentDAO studentDAO = new StudentDAO();
        CollegeCreditUnionDAO collegeCreditUnionDAO = new CollegeCreditUnionDAO();
        Student studentExists = studentDAO.getStudentByStudentNumber(student.getStudentNumber());
        if (studentExists != null) {
            return "Student with student number " + student.getStudentNumber() + " already exists";
        }
        // persist student and update college credit union
        studentDAO.persist(student);
        collegeCreditUnion.getStudents().add(student);
        collegeCreditUnion = collegeCreditUnionDAO.merge(collegeCreditUnion);
        return "Student " + student.getName() + " with student number " + student.getStudentNumber()
                + " has been created" +
                "\nStudent Details: " + student.toString();
    }

    @DELETE
    @Path("/deleteStudent/{studentNumber}")
    @Produces("text/plain")
    public String deleteStudent(@PathParam("studentNumber") String studentNumber) {
        // get student
        StudentDAO studentDAO = new StudentDAO();
        CollegeCreditUnionDAO collegeCreditUnionDAO = new CollegeCreditUnionDAO();
        Student student = studentDAO.getStudentByStudentNumber(studentNumber);
        if (student == null || collegeCreditUnion.getStudents() == null) {
            return "Student with student number " + studentNumber + " does not exist";
        } else if (student.getLoan() != null) {
            return "Cannot delete student with student number " + studentNumber + " while still has a loan";
        }
        // remove student and update college credit union
        for (int i = 0; i < collegeCreditUnion.getStudents().size(); i++) {
            if (collegeCreditUnion.getStudents().get(i).getStudentNumber().equals(studentNumber)) {
                collegeCreditUnion.getStudents().remove(i);
                break;
            }
        }
        collegeCreditUnion = collegeCreditUnionDAO.merge(collegeCreditUnion);
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

        // DAO Variables
        LoanDAO loanDao = new LoanDAO();

        // Update student with bew loan within db
        loan.setLoanDeposits(new ArrayList<LoanDeposit>());
        loanDao.persist(loan);
        student.setLoan(loan);
        studentDAO.merge(student);

        // Update student with loan within code
        for (int i = 0; i < collegeCreditUnion.getStudents().size(); i++) {
            if (collegeCreditUnion.getStudents().get(i).getStudentNumber().equals(studentNumber)) {
                collegeCreditUnion.getStudents().get(i).setLoan(loan);
                break;
            }
        }

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
        if (student == null || student.getLoan() == null) {
            return "Student with student number " + studentNumber + " does not exist or does not have a loan";
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
        double loanAmount = student.getLoan().getAmount();

        // Calculate total loan deposits
        double totalLoanDeposits = 0;
        if (loan.getLoanDeposits() != null) {
            for (LoanDeposit ld : student.getLoan().getLoanDeposits()) {
                totalLoanDeposits += ld.getAmount();
            }
        }

        // Check if total loan deposits is greater than 0
        if (totalLoanDeposits < loanAmount) {
            return "Balance has't been paid off!\nTotal loan deposits must be equal to loan amount.\n"
                    + "Loan amount: " + loanAmount + "\nTotal loan deposits: "
                    + totalLoanDeposits + "\nBalance remaining: " + (loanAmount - totalLoanDeposits);
        } // Check if total loan deposits is equal to loan amount and remove loan
        else if (totalLoanDeposits == loanAmount) {
            // Dao Variables
            LoanDAO loanDao = new LoanDAO();

            // Remove student loan from db
            student.setLoan(null);
            studentDao.merge(student);
            loanDao.remove(loan);

            // Remove student loan within code
            for (int i = 0; i < collegeCreditUnion.getStudents().size(); i++) {
                if (collegeCreditUnion.getStudents().get(i).getStudentNumber().equals(studentNumber)) {
                    collegeCreditUnion.getStudents().get(i).setLoan(null);
                    break;
                }
            }
            return "Loan has already fully paid by student: " + student.getName()
                    + " with student number " + student.getStudentNumber()
                    + "\nRemoving loan from the system!";
        }
        // return loan details
        return "Error performing operation to delete loan";

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
        double loanAmount = student.getLoan().getAmount();

        // calculate total loan deposits
        double totalLoanDeposits = 0;
        if (loan.getLoanDeposits() != null) {
            for (LoanDeposit ld : student.getLoan().getLoanDeposits()) {
                totalLoanDeposits += ld.getAmount();
            }
        }
        // check if total loan deposits + amount is equal to loan amount and remove loan
        if (totalLoanDeposits == loanAmount) {
            return "Loan has already fully paid by student: " + student.getName()
                    + " with student number " + student.getStudentNumber()
                    + "\nYou are now eligible to remove loan from the system!";
        }
        // check if total loan deposits + amount is greater balanace remaining
        else if (totalLoanDeposits + amount > loanAmount) {
            return "Amount cannot be more than balance remaining.\n"
                    + "Loan amount: " + loan.getAmount() + "\nTotal loan deposits: "
                    + totalLoanDeposits + "\nBalance remaining: " + (loanAmount - totalLoanDeposits);
        }

        // Add loan deposit to relavent loan in db
        loanDepositDAO.persist(loanDeposit);
        loan.getLoanDeposits().add(loanDeposit);
        LoanDAO loanDAO = new LoanDAO();
        loanDAO.merge(loan);

        // Updated loan with new new deposit within code
        for (int i = 0; i < collegeCreditUnion.getStudents().size(); i++) {
            if (collegeCreditUnion.getStudents().get(i).getStudentNumber().equals(studentNumber)) {
                collegeCreditUnion.getStudents().get(i).setLoan(loan);
                break;
            }
        }

        if (totalLoanDeposits + amount == loanAmount) {
            totalLoanDeposits += amount;
            return "Loan Deposit added by student " + student.getName()
                    + " with student number " + student.getStudentNumber()
                    + "\nTotal loan deposits: " + totalLoanDeposits
                    + "\nLoan Deposit Details: " + loanDeposit.toString()
                    + "\nLoan has already fully paid by student\nYou are now eligible to remove loan from the system!";
        }

        return "Loan Deposit added by student " + student.getName() + " with student number "
                + student.getStudentNumber()
                + "\nLoan Deposit Details " + loanDeposit.toString() + "\nTotal loan deposits: "
                + totalLoanDeposits + "\nBalance remaining: " + (loanAmount - totalLoanDeposits);
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
        if (loan.getLoanDeposits() == null || loan.getLoanDeposits().isEmpty()) {
            return "Student with student number " + studentNumber + " does not have any loan deposits";
        }
        // return loan deposits
        return "Loan Deposits: " + loan.getLoanDeposits();
    }

}
