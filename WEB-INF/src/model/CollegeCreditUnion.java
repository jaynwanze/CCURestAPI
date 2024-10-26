package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "collegecreditunion")
@Entity
public class CollegeCreditUnion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Student> students;

	public CollegeCreditUnion(List<Student> students) {
		this.students = students;
	}

	public CollegeCreditUnion() {

	}

	@XmlElement
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlElement
	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public String toString() {
		return "CollegeCreditUnion [students=" + students + "]";
	}

}
