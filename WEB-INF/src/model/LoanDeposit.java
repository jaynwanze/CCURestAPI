package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "loandeposits")
@Entity
public class LoanDeposit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String amount;
	private String date;

	public LoanDeposit(String amount, String date) {
		this.amount = amount;
		this.date = date;
	}

	public LoanDeposit() {

	}

	@XmlElement
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@XmlElement
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	@XmlElement
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String toString() {
		return "LoanDeposit [amount=" + amount + ", date=" + date + "]";
	}
}