package de.mpg.mpdl.doi;

import java.io.InputStream;
import java.net.URI;
import java.util.EnumSet;
import java.util.Scanner;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.spring.SpringWebApplicationInitializer;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.init.JerseyServletContainerInitializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.DelegatingFilterProxy;

import de.mpg.mpdl.doi.rest.JerseyApplicationConfig;

public class SimpleTest { 

	private Logger logger = LogManager.getLogger(SimpleTest.class);

	private String testDoi = "10.15771/doxi7";
	private String url = "http://qa-pubman.mpdl.mpg.de/pubman/item/escidoc:2123284";
	
	private InputStream updatedMetadata = SimpleTest.class
			.getResourceAsStream("/doi_update_metadata.xml");

	private WebTarget target;
	private HttpServer server;
	
	/*
	@Override
	protected Application configure() {
		
		
		return new JerseyApplicationConfig();
		
		
		//return new ResourceConfig(DOIResource.class, DoxiExceptionMapper.class, DoiAlreadyExistsMapper.class, ExceptionMapper.class, SecurityConfig.class, SecurityWebApplicationInitializer.class);
	}
	*/
	/*
	@Override
    protected DeploymentContext configureDeployment(){
        ServletDeploymentContext context = ServletDeploymentContext
                .builder(new JerseyApplicationConfig())
                //.addFilter(DelegatingFilterProxy.class, "springSecurityFilterChain")
                
                //.contextParam("contextConfigLocation", "classpath:applicationContext.xml")
                .build();
        
        return context;
        
    }
    
*/
	@Before
	public void setUp() throws Exception
	{
		
		//HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8123"), new JerseyApplicationConfig());
		
		
		server = new HttpServer();
		NetworkListener listener = new NetworkListener("grizzly2", "localhost", 8123);
		server.addListener(listener);
		
		
		WebappContext ctx = new WebappContext("ctx","/");       
		
		
		//If Java-config should be used, create a class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer
		//and a config and use the following method:
		//SecurityWebApplicationInitializer initializer = new SecurityWebApplicationInitializer();
		//initializer.onStartup(ctx);
		
		
		
//		 If XML-Config should be used use SpringWebApplicationInitializer from package jersey-spring 3, which does the following:
//		 ctx.addContextInitParameter("contextConfigLocation", "classpath:applicationContext.xml");
//		 ctx.addListener(ContextLoaderListener.class);
//		 ctx.addListener(RequestContextListener.class);
		new SpringWebApplicationInitializer().onStartup(ctx);

		
		//Register Jersey Servlet
		ctx.addServlet("de.mpg.mpdl.doi.rest.JerseyApplicationConfig", new ServletContainer(new JerseyApplicationConfig())).addMapping("/*");

		//Register security filter
		FilterRegistration reg = ctx.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain"));
		reg.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");;

		ctx.deploy(server);
		
		
		server.start();
		
		
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(new CsrfProtectionFilter("doxi test"));
		
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
			    .nonPreemptive().credentials("user", "password").build();
		clientConfig.register(feature);
		
		this.target = ClientBuilder.newClient(clientConfig).target("http://localhost:8123/doi");
	}
	
	@After
	public void tearDown() throws Exception
	{
		server.shutdown();
		
	}
	
	
    

	@Test
	public void testGetDoiList() throws Exception {

		logger.info("--------------------- STARTING get DOI-List test ---------------------");
		Response result = target.request().get();
		logger.info("Status: " + result.getStatus() + " expected 200");
		logger.info("Message: " + result.getEntityTag().getValue());
		logger.info("List: " + result.readEntity(String.class));
		Assert.assertEquals(200, result.getStatus());

		// new DataciteAPIController().updateUrl(testDoi, url);
		logger.info("--------------------- FINISHED get DOI-List test ---------------------");
	}

	@Test
	public void testCreateDoi() throws Exception {
		logger.info("--------------------- STARTING create DOI test ---------------------");
		
		
		String metadata = streamToString(SimpleTest.class.getResourceAsStream("/doi_metadata.xml"));
		
		Response result = target.path(testDoi).queryParam("url", url)
				.request(MediaType.TEXT_PLAIN_TYPE).put(Entity.xml(metadata));
		logger.info("Status: " + result.getStatus() + " expected 201");
		logger.info("Message: " + result.readEntity(String.class));
		Assert.assertEquals(201, result.getStatus());

		// new DataciteAPIController().updateUrl(testDoi, url);
		logger.info("--------------------- FINISHED create DOI test ---------------------");
	}

	@Test
	public void testUpdateMd() throws Exception {
		logger.info("--------------------- STARTING update DOI test ---------------------");
		Response result = target.path(testDoi).queryParam("url", url).request()
				.post(Entity.xml(updatedMetadata));
		logger.info("Status: " + result.getStatus() + " expected 200\nEntity: " + result.readEntity(String.class));
		Assert.assertEquals(201, result.getStatus());

		// new DataciteAPIController().updateUrl(testDoi, url);
		logger.info("--------------------- FINISHED update DOI test ---------------------");
	}

	@Test
	public void testGetDoi() throws Exception {
		logger.info("--------------------- STARTING get DOI test ---------------------");
		Response result = target.path(testDoi).queryParam("url", url)
				.request().get();
		logger.info("Status: " + result.getStatus() + " expected 200");
		logger.info("Message: " + result.readEntity(String.class));
		Assert.assertEquals(200, result.getStatus());

		// new DataciteAPIController().updateUrl(testDoi, url);
		logger.info("--------------------- FINISHED get DOI test ---------------------");
	}
	
	@Test
	public void testException()
	{
		Response result = target.path("test").request().get();
		logger.info("Exception " + result.readEntity(String.class) +result.getStatus());
		
		Response result2 = target.path("test2").request().get();
		logger.info("Exception " + result2.readEntity(String.class) +result2.getStatus());
	}
	
	
	private static String streamToString(InputStream is)
	{
		String inputStreamString = new Scanner(is,"UTF-8").useDelimiter("\\A").next();
        return inputStreamString;
	}
}
