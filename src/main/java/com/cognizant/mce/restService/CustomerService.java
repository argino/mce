package com.cognizant.mce.restService;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import org.springframework.stereotype.Service;

import com.cognizant.mce.dao.CustomerDAO;
import com.cognizant.mce.entity.Customer;
//import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Service
@Path("/customers")
public class CustomerService {

	private CustomerDAO customerDAO;

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/serviceIsAlive")
	public Response serviceIsAlive() {
		return Response.ok("Hallo Service wurde aufgerufen!!").build();
	}
	
	// JPA Solution
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/list/{id}")
	public Response getById(@PathParam("id") int id) {
		// CustomerDAO dao= new CustomerDAO();
		Customer customer = new Customer();
		try {
			customer = customerDAO.getCustomerById(id);
			if (customer == null)
				return Response.status(Status.NOT_FOUND).entity("Error: Kunde wurde nicht gefunden").build();
			else
				return Response.ok(customer).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/findByLastName/{lastName}")
	public Response findCustomersByLastName(@PathParam("lastName") String lastName) {
		XStream xStream = new XStream(new DomDriver());
		String XMLList = null;
		try {
			List<Customer> customersList = new ArrayList<Customer>();
			customersList = customerDAO.getCustomerByLastName(lastName);
			System.out.println(customersList);
			// from customer object into XML string
			XMLList = new XStream().toXML(customersList);
			xStream.alias("customers", Customer.class);
			if (XMLList.contains("entity.Customer"))
				return Response.ok(XMLList).build();
			else
				return Response.status(Status.NOT_FOUND).entity("Error: Kunde wurde nicht gefunden").build();
		} catch (Exception ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(ex.getMessage()).build();
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	@Path("/updateCustomerById/{id}")
	public Response updateCustomerById(String XMLstring, @PathParam("id") int id) {
		Customer customer = new Customer();
		int result = 0;
		try {
			// From XML String into customer object
			StringReader sr = new StringReader(XMLstring);
			JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			customer = (Customer) unmarshaller.unmarshal(sr);
			result = customerDAO.updateCustomer(customer, id);
		} catch (Exception ex) {
			System.out.println("" + ex.getMessage());
			ex.getStackTrace();
			Response.status(Status.BAD_REQUEST).entity(ex.getLocalizedMessage()).build();
		}
		if (result != 0)
			return Response.ok(customer + " wurde aktualisiert").build();
		else
			return Response.status(Status.NOT_FOUND).entity("Error: " + result + " Kunde wurde gefunden").build();
	}
}