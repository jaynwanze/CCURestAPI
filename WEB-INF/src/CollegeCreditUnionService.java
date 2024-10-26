
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import model.CollegeCreditUnion;
import model.Loan;
import model.Student;
import dao.CollegeCreditUnionDAO;
import dao.StudentDAO;


@Path("/collegecreditunionservice")
public class CollegeCreditUnionService {

    private static CollegeCreditUnion collegeCreditUnion = new CollegeCreditUnion();

    static {
        collegeCreditUnion.setStudents(new ArrayList<Student>());
        CollegeCreditUnionDAO dao = new CollegeCreditUnionDAO();
        dao.persist(collegeCreditUnion);
    }

    @POST
    @Path("/newStudent")
    @Consumes("application/json")
    public String addStudentToDBJSON(Student student) {
        //do unique validation
        StudentDAO dao = new StudentDAO();
        Student studentExists = dao.getStudentByStudentNumber(student.getStudentNumber());
        if (studentExists != null) {
            return "Student with student number " + student.getStudentNumber() + " already exists";
        }
        dao.persist(student);
        return "Student added to DB from JSON Param " + student.getName();
    }

    @PUT
    @Path("/updateStudent/{studentNumber}")
    @Produces("application/json")
    public Student updateStudent(@PathParam("studentNumber") String studentNumber) {
        StudentDAO dao = new StudentDAO();
        Student student = dao.getStudentByStudentNumber(studentNumber);
        return dao.merge(student);
    }

    @DELETE
    @Path("/deleteEmployee/{studentNumber}")
    @Produces("text/plain")
    public String deleteStudent(@PathParam("studentNumber") String studentNumber) {
        StudentDAO dao = new StudentDAO();
        Student student = dao.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        }
        //do unique validation 
        // if student stilll exists, delete it
        dao.remove(student);
        return "Student " + student + " deleted";
    }

    @PUT
    @Path("/newLoan/{studentNumber}")
    @Consumes("application/json")
    @Produces("text/plain")
    public String newLoan(@PathParam("studentNumber") String studentNumber) {
        StudentDAO dao = new StudentDAO();
        Student student = dao.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return "Student with student number " + studentNumber + " does not exist";
        }
        else if (student.getLoan() != null) {
            return "Student with student number " + studentNumber + " already has a loan";
        }
        student.setLoan(new Loan());

        return "Loan added to student " + student.getName() + " with student number " + student.getStudentNumber() 
                + " and Loan Details " + student.getLoan().toString();
    }

}
