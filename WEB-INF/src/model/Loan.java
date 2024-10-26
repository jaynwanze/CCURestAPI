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

@XmlRootElement(name = "loan")
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double amount;
    private String description;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<LoanDeposit> loanDeposits;

    public Loan(double amount, String description, List<LoanDeposit> loanDeposits) {
        this.amount = amount;
        this.description = description;
        this.loanDeposits = loanDeposits;
    }

    public Loan() {

    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public List<LoanDeposit> getLoanDeposits() {
        return loanDeposits;
    }

    public void setLoanDeposits(List<LoanDeposit> loanDeposits) {
        this.loanDeposits = loanDeposits;
    }

    public String toString() {
        return "Loan [amount=" + amount + ", description=" + description + ", loanDeposits=" + loanDeposits + "]\n";
    }
}
