package dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.Student;

public class StudentDAO {

	protected static EntityManagerFactory emf = Persistence.createEntityManagerFactory("mydb");

	public StudentDAO() {
		// TODO Auto-generated constructor stub
	}

	public void persist(Student student) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(student);
		em.getTransaction().commit();
		em.close();
	}

	public void remove(Student student) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.remove(em.merge(student));
		em.getTransaction().commit();
		em.close();
	}

	public Student merge(Student student) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Student updatedStudent = em.merge(student);
		em.getTransaction().commit();
		em.close();
		return updatedStudent;
	}

	public Student getStudentByStudentNumber(String studentNumber) {
		EntityManager em = emf.createEntityManager();
		Student student = (Student) em.createNamedQuery("Student.findByStudentNumber").setParameter("studentNumber", studentNumber).getSingleResult();
		em.close();
		//Do whatever you want with the subscriber(s) with that username
		//Here we just return the first one
		return student;
	}

}
