package com.equipo.pixelart.client.app;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ClientAppTest{
	
	private ClientApp app;
	
	@BeforeEach
	public void setUp() {
		
		app = new ClientApp();
	}


    @Test
    void testAppInicializaComponentes() {
    	
    	assertNotNull(app, "La aplicacion no debe ser null despues de construirse");
    }
    
    @Test 
    void testCanvasModelInicializado() throws Exception {
    	
    	var field = ClientApp.class.getDeclaredField("canvasModel");
    	field.setAccessible(true);
    	Object canvasModel = field.get(app);
    	
    	assertNotNull(canvasModel, "El modelo debe estar inicializado");
    }
    
    @Test
    void testControladoresConfigurados() throws Exception {
    	var networkField = ClientApp.class.getDeclaredField("networkController");
    	var canvasField = ClientApp.class.getDeclaredField("canvasController");
    	var toolboxField = ClientApp.class.getDeclaredField("toolboxController");
    	
    	networkField.setAccessible(true);
    	canvasField.setAccessible(true);
    	toolboxField.setAccessible(true);
    	
    	assertNotNull(networkField, "El networkController debe estar inicializado");
        assertNotNull(canvasField, "El canvasController debe estar inicializado");
        assertNotNull(toolboxField, "El toolboxController debe estar inicializado");
    	
    }
    
    @Test
    void testVentanaPrincipalConfigurada() throws Exception{
    	var field = ClientApp.class.getDeclaredField("mainFrame");
    	field.setAccessible(true);
    	Object mainFrame = field.get(app);
    	
    	assertNotNull(mainFrame,"La ventana principal debe estar configurada");
    	
    }
  
}
