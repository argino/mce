package com.cognizant.mce.dao;


import java.util.List;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.cognizant.mce.entity.Customer;



public class CustomerDAO {

	private static final String PERSISTENCE_UNIT_NAME = "cstmpu";
	private static EntityManagerFactory factory;
	private static EntityTransaction tx;

	public Customer getCustomerById(int customerId) {
		Customer customer = new Customer();
		try {
			factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
			EntityManager entityManager = factory.createEntityManager();
			//customer = entityManager.find(Customer.class, customerId);
			// funktioniert auch

			 Query q = entityManager.createQuery("SELECT c FROM Customer c WHERE c.id = :id");
			 q.setParameter("id", customerId);
			 customer=(Customer) q.getSingleResult();
		} catch (Exception ex) {
			ex.getMessage();
			System.out.println("Fehler bei der Abfrage des Kunden: " + ex.getLocalizedMessage());
			System.out.println("Stacktrace: "+ex.getStackTrace()+"  "+ex.getMessage());
			ex.getStackTrace();
		}
		return customer;
	}

	public List<Customer> getCustomerByLastName(String lastName) {
		List<Customer> customers = new ArrayList<Customer>();
		try {
			factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
			EntityManager entityManager = factory.createEntityManager();
			customers = entityManager.createNamedQuery("Customer.findByLastName", Customer.class)
					.setParameter("lastName", lastName).getResultList();
		} catch (Exception ex) {
			System.out.println("" + ex.getLocalizedMessage());
			ex.getStackTrace();
		}
		return customers;
	}

	public int updateCustomer(Customer customer, int id) {
		int result = 0;
		try {
			factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
			EntityManager entityManager = factory.createEntityManager();
			//transaction start
			tx=entityManager.getTransaction();
			tx.begin();
			result = entityManager.createNamedQuery("Customer.updateCustomerById", Customer.class)
					.setParameter("id", customer.getId())
					.setParameter("firstName", customer.getFirstName())
					.setParameter("lastName", customer.getLastName())
					.setParameter("email", customer.getEmail())
					.setParameter("phone", customer.getPhoneNo())
					.executeUpdate();
			tx.commit();
			entityManager.close();
			// transaction end
			if (result !=0){
			System.out.println("Anzahl der geupdaten Kunden: "+result);
			System.out.println("Kunde wurde geändert zu : "+customer);
			}else
				System.out.println("Error! Kunde nicht vorhanden");
		} catch (Exception ex) {
			System.out.println("Error: Kunde konnte nicht aktualisiert werden, weil .... "+ex.getMessage());
			ex.getStackTrace();
		}
		return result;
	}
}
