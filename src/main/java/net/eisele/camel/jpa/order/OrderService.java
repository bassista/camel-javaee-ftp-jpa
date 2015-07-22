/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eisele.camel.jpa.order;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.eisele.camel.jpa.Customer;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.camel.OutHeaders;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author myfear
 */
@Named
public class OrderService {

    @PersistenceContext(unitName = "customer-pu", type = PersistenceContextType.EXTENDED)
    EntityManager em;

    private final static Logger LOGGER = Logger.getLogger(OrderService.class.getName());

    public void log(Exchange oldExchange, Exchange newExchange) throws Exception {

        LOGGER.log(Level.INFO, ">> Import {0}", newExchange.getIn().getBody(String.class));
    }

    // @Transactional
    public Object handleOrder(@Headers Map<?, ?> in, @Body String payload, @OutHeaders Map<String, Object> out) {

        LOGGER.log(Level.INFO, ">> handleOrder {0}", payload);

        return payload;
        /**
         * Customer customer = newExchange.getIn().getBody(Customer.class); try
         * { //em.merge(customerStatus); newExchange.getIn().setBody(customer,
         * Customer.class); } catch (Exception e) { throw new
         * Exception("CUSTOMER NOT FOUND", e); }
         */
    }

    public Object loadCustomer(@Headers Map<?, ?> in, @Body String payload, @OutHeaders Map<String, Object> out) {

        LOGGER.log(Level.INFO, ">> loadCustomer {0}", in.get("customerid"));
        Long customerid = (Long) in.get("customerid");

        Customer customer = new Customer();
        try {
            TypedQuery<Customer> query
                    = em.createNamedQuery("Customer.findByID", Customer.class);
            query.setParameter("customerID", customerid);
            customer = query.getSingleResult();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Customer not found", e);
        }

        return updateMessageWithCustomer(payload, customer);

    }

    private String getStringFromDoc(org.w3c.dom.Document doc) {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        return lsSerializer.writeToString(doc);
    }

    @Transactional
    public Object createCustomer(@Headers Map<?, ?> in, @Body String payload, @OutHeaders Map<String, Object> out) {

        LOGGER.log(Level.FINEST, ">> createCustomer {0}", payload);
        LOGGER.log(Level.FINEST, ">> DealerId {0}", in.get("dealer").toString());
        Customer customer = new Customer();

        try {
            JAXBContext jaxbc = JAXBContext.newInstance(Customer.class);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(new InputSource(new StringReader(payload)));

            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("//order/info/customer");
            Element nodeFound = (Element) expr.evaluate(doc, XPathConstants.NODE);

            Unmarshaller u = jaxbc.createUnmarshaller();
            StringReader sr = new StringReader(payload);
            customer = (Customer) u.unmarshal(nodeFound);
            customer.setDealerId(in.get("dealer").toString());

            em.persist(customer);

            LOGGER.log(Level.INFO, ">> Persisted Customer Entity {0}", customer);

            payload = updateMessageWithCustomer(payload, customer);

        } catch (JAXBException | IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return payload;

    }

    public void logCustomers(Exchange oldExchange, Exchange newExchange) throws Exception {
        try {
            TypedQuery<Customer> query
                    = em.createNamedQuery("Customer.findAll", Customer.class);
            List<Customer> customers = query.getResultList();
            customers.stream().forEach((c) -> {
                LOGGER.log(Level.INFO, "Customer: {0}", c.toString());
            });

        } catch (Exception e) {
            throw new Exception("NO CUSTOMERS FOUND");
        }
    }

    private String updateMessageWithCustomer(String payload, Customer customer) {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(new InputSource(new StringReader(payload)));

            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("//order/info/customer");
            Element nodeFound = (Element) expr.evaluate(doc, XPathConstants.NODE);

            JAXBContext jaxbc = JAXBContext.newInstance(Customer.class);
            Marshaller m = jaxbc.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter outputWriter = new StringWriter();
            m.marshal(customer, outputWriter);

            Document fragmentDoc = docBuilder.parse(new InputSource(new StringReader(outputWriter.toString())));
            Node injectedNode = doc.adoptNode(fragmentDoc.getFirstChild());

            Node parentNode = nodeFound.getParentNode();
            parentNode.replaceChild(injectedNode, nodeFound);

            payload = getStringFromDoc(doc);

            //  LOGGER.log(Level.INFO, ">> T E S T {0}", getStringFromDoc(doc));
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException | JAXBException ex) {
            Logger.getLogger(OrderService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return payload;
    }

}
