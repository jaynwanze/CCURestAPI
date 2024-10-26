package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
    @NamedQuery(name = "Student.findByName", query = "SELECT s FROM Student s WHERE s.studentNumber = :studentNumber")
})


@XmlRootElement(name = "student")
@Entity
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

    private String name;
    private String studentNumber;
    private String phoneNumber;
    private String address;
    private String courseCode;
    private Loan loan;

	public Student(String name, String studentNumber, String phoneNumber, String address, String courseCode, Loan loan) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.courseCode = courseCode;
        this.loan = loan;
    }

	public Student() {

	}

    @XmlElement
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    @XmlElement
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @XmlElement
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlElement
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    @XmlElement
    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public String toString() {
        return "Student [name=" + name + ", studentNumber=" + studentNumber + ", phoneNumber=" + phoneNumber + ", address=" + address + ", courseCode=" + courseCode + ", loan=" + loan + "]";
    }

}
