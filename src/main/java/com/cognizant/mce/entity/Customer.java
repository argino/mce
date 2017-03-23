package com.cognizant.mce.entity;


import java.io.Serializable;

import javax.inject.Named;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@NamedQueries({
	@NamedQuery(name = "Customer.findAll",
				query= "SELECT c FROM Customer c"),
	@NamedQuery(name = "Customer.findById",
				query= "SELECT c FROM Customer c WHERE c.id = :id"),
	@NamedQuery(name = "Customer.findByLastName",
				query= "SELECT c FROM Customer c WHERE c.lastName = :lastName"),
	@NamedQuery(name = "Customer.updateCustomerById",
				query= "UPDATE Customer c SET c.firstName =:firstName, c.lastName =:lastName , c.email=:email, c.phoneNo=:phone WHERE c.id = :id")
})
@Named
@Access(AccessType.FIELD)
@Table(name = "customers")
@XStreamAlias("customers")
@XmlRootElement(name = "customer")
//@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "firstName", "lastName", "email", "phoneNo" })
@Entity
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "personId")
	private int id = 0;
	@Column(name = "firstName")
	private String firstName = null;
	@Column(name = "lastName")
	private String lastName = null;
	@Column(name = "email")
	private String email = null;
	@Column(name = "phone")
	private String phoneNo = null;

	public Customer(){}
	
	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	@XmlAttribute
	public void setId(int id) {
		this.id = id;
	}

	// @XmlElement
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	// @XmlElement
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// @XmlElement
	public void setEmail(String email) {
		this.email = email;
	}

	// @XmlElement
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", phoneNo=" + phoneNo + "]";
	}

}
