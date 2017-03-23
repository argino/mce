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
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.cognizant.mce.dao.CustomerDAO;
import com.cognizant.mce.entity.Customer;
//import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


//@Stateless
//@LocalBean
@Service("customerService")
@Controller
@Provider
@Path("/customers")
public class CustomerService {
	
	private CustomerDAO dao;
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/serviceIsAlive")
	public Response serviceIsAlive() {
		return Response.ok("Hallo Service wurde aufgerufen!!").build();
	}
	// DTO Solution
//	@GET
//	@Path("/list")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response listAllCustomers() {
//		try {
//			List<Customer> listCustomer = new ArrayList<Customer>();
//			AccessManager access = new AccessManager();
//			listCustomer = access.getAllCustomers();
//			new Gson().toJson(listCustomer);
//			return Response.ok(listCustomer).build();
//		} catch (Exception ex) {
//			String result = "{\"Message\":" + ex.getMessage() + "\"}";
//			ex.getLocalizedMessage();
//			return Response.status(Response.Status.NOT_FOUND.getStatusCode()).entity(result).build();
//		}
//	}

//	@GET
//	@Path("/listXML")
//	@Produces({ MediaType.APPLICATION_XML })
//	public Response listAllCustomersXML() {
//		String XMLList = null;
//		try {
//			XStream xStream = new XStream(new DomDriver());
//			List<Customer> listCustomer = new ArrayList<Customer>();
//			AccessManager access = new AccessManager();
//			listCustomer = access.getAllCustomers();
//			System.out.println(listCustomer);
//			XMLList = new XStream().toXML(listCustomer);
//			xStream.alias("customer", Customer.class);
//			System.out.println(XMLList);
//			return Response.ok(XMLList).build();
//		} catch (Exception ex) {
//			String result = "{\"Message\":" + ex.getMessage() + "\"}";
//			ex.printStackTrace();
//			ex.getLocalizedMessage();
//			return Response.status(Response.Status.NOT_FOUND.getStatusCode()).entity(result).build();
//		}
//	}

	
	public void setDao(CustomerDAO dao) {
		this.dao = dao;
	}

	// JPA Solution
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/list/{id}")
	public Response getById(@PathParam("id") int id) {
		CustomerDAO dao = new CustomerDAO();
		Customer customer = new Customer();
		try {
			customer = dao.getCustomerById(id);
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
		CustomerDAO dao = new CustomerDAO();
		XStream xStream = new XStream(new DomDriver());
		String XMLList = null;
		try {
			List<Customer> customersList = new ArrayList<Customer>();
			customersList = dao.getCustomerByLastName(lastName);
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
		//CustomerDAO dao = new CustomerDAO();
		int result = 0;
		try {
			// From XML String into customer object
			StringReader sr = new StringReader(XMLstring);
			JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			customer = (Customer) unmarshaller.unmarshal(sr);
			result = dao.updateCustomer(customer, id);
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
