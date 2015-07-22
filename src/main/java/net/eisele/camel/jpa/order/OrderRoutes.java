package net.eisele.camel.jpa.order;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;

/**
 * Camel Route Builder for the Order Demo
 *
 * @author Markus Eisele <markus@jboss.org>
 */
@ApplicationScoped
@Startup
@ContextName("cdi-context")
public class OrderRoutes extends RouteBuilder {

    @Inject
    OrderService orderService;

    /**
     * Configure the Routes
     *
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).maximumRedeliveries(1).to("direct:handleFail");

        //    from("ftp://localhost:21/?username=test&password=test&move=.done")
        //          .routeId("importFtp").to("file://{{sys:jboss.server.data.dir}}/inbox");
        from("file://{{sys:jboss.server.data.dir}}/inbox?delete=true").routeId("loadLocalFile")
                .split(xpath("//orders/order"))
                .to("direct:processOrder");

        from("direct:processOrder").routeId("processOrder").startupOrder(1)
                .setHeader("order", xpath("/order/@id", String.class))
                .setHeader("dealer", xpath("/order/info/dealerid/text()", String.class))
                .choice()
                .when().xpath("//order/info/customer/@ref")
                .setHeader("customerid", xpath("/order/info/customer/@ref", Long.class))
                .to("direct:loadCustomer")
                .endChoice()
                .otherwise()
                .to("direct:createCustomer")
                .to("direct:handleOrder")
                .to("log:net.eisele.camel.jpa.order?showAll=true")
                .end();

        from("direct:loadCustomer").routeId("loadCustomer").bean(orderService, "loadCustomer").to("direct:handleOrder");

        from("direct:createCustomer").routeId("createCustomer").bean(orderService, "createCustomer").to("direct:handleOrder");

        from("direct:handleOrder").routeId("handleOrder")
                .split(xpath("/order/isbn"))
                .setBody(xpath("/isbn/text()"))
                .bean(orderService, "handleOrder").to("log:net.eisele.camel.jpa.order?showAll=true");

        /**
         * Handles all route failures and logs them into
         * %JBOSS_HOME%/standalone/data/errordir
         */
        from("direct:handleFail")
                .routeId("failedRoute")
                .log("ERROR => ${header.CamelExceptionCaught}")
                .setBody().simple("${header.transacontent}\n"
                        + "         * &lt;exceptionInfo&gt;${header.CamelExceptionCaught}&lt;/exceptionInfo&gt;")
                .to("file://{{sys:jboss.server.data.dir}}/errordir?fileName=error-$simple{date:now:yyyyMMddhhmmss}.xml");
    }
}
