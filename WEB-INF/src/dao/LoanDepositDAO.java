package dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.LoanDeposit;

public class LoanDepositDAO {

	protected static EntityManagerFactory emf = Persistence.createEntityManagerFactory("mydb");

	public LoanDepositDAO() {
		// TODO Auto-generated constructor stub
	}

	public void persist(LoanDeposit loanDeposit) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(loanDeposit);
		em.getTransaction().commit();
		em.close();
	}

	public void remove(LoanDeposit loanDeposit) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.remove(em.merge(loanDeposit));
		em.getTransaction().commit();
		em.close();
	}

	public LoanDeposit merge(LoanDeposit loanDeposit) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		LoanDeposit updatedLoanDeposit = em.merge(loanDeposit);
		em.getTransaction().commit();
		em.close();
		return updatedLoanDeposit;
	}

	public List<LoanDeposit> getAllLoanDeposits() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		List<LoanDeposit> loanDeposits = new ArrayList<LoanDeposit>();
		loanDeposits = em.createQuery("from LoanDeposit").getResultList();
		em.getTransaction().commit();
		em.close();
		return loanDeposits;
	}

}
