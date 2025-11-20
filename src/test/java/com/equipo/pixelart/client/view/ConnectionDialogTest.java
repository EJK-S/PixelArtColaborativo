package com.equipo.pixelart.client.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionDialogTest {

    private ConnectionDialog dialog;

    @BeforeEach
    void setUp() {
  
        dialog = new ConnectionDialog(null);
    }

    @Test
    void testValoresPorDefecto() {
      
        assertEquals("localhost", dialog.getServer());
        assertEquals(12345, dialog.getPort());
        assertFalse(dialog.isAccepted());
    }

    @Test
    void testGetPortConBasura() throws Exception {
       
        setPrivateField("portField", new JTextField("TEXTO_NO_VALIDO"));

        assertEquals(12345, dialog.getPort());
    }

    @Test
    void testLoginExitoso() throws Exception {
        
        JTextField userField = (JTextField) getPrivateField("loginUsernameField");
        JPasswordField passField = (JPasswordField) getPrivateField("loginPasswordField");
        
        userField.setText("UsuarioTest");
        passField.setText("123456");

        clickPrivateButton("loginButton");

        assertTrue(dialog.isAccepted(), "Debe aceptar el diálogo");
        assertFalse(dialog.isRegister(), "No es registro, es login");
        assertEquals("UsuarioTest", dialog.getUsername());
        assertEquals("123456", dialog.getPassword());
    }

    @Test
    void testLoginVacio() throws Exception {
        
        clickPrivateButton("loginButton");

        assertFalse(dialog.isAccepted());
        JLabel status = (JLabel) getPrivateField("statusLabel");
        assertTrue(status.getText().contains("completa usuario"), "Debe mostrar error de campos vacíos");
    }

    @Test
    void testNavegacionEntrePaneles() throws Exception {
      
        clickPrivateButton("goToRegisterButton");
        JLabel status = (JLabel) getPrivateField("statusLabel");
        assertTrue(status.getText().contains("Crea tu cuenta"), "Debe cambiar el mensaje de estado");

        clickPrivateButton("backToLoginButton");
        assertEquals(" ", status.getText(), "Debe limpiar el estado al volver");
    }

    @Test
    void testRegistroExitoso() throws Exception {
       
        clickPrivateButton("goToRegisterButton");

        setRegisterFields("NuevoUser", "pass123", "pass123");

        clickPrivateButton("registerButton");

        assertTrue(dialog.isAccepted());
        assertTrue(dialog.isRegister());
        assertEquals("NuevoUser", dialog.getUsername());
        assertEquals("pass123", dialog.getPassword());
    }

    @Test
    void testRegistroUsuarioVacio() throws Exception {
        clickPrivateButton("goToRegisterButton");
        setRegisterFields("", "pass", "pass");
        
        clickPrivateButton("registerButton");
        assertFalse(dialog.isAccepted());
        verificarError("usuario esta vacio");
    }

    @Test
    void testRegistroPasswordVacio() throws Exception {
        clickPrivateButton("goToRegisterButton");
        setRegisterFields("User", "", "");
        
        clickPrivateButton("registerButton");
        verificarError("contrasena esta vacio");
    }

    @Test
    void testRegistroConfirmacionVacia() throws Exception {
        clickPrivateButton("goToRegisterButton");
        setRegisterFields("User", "pass", "");
        
        clickPrivateButton("registerButton");
        verificarError("confirmar tu contrasena");
    }

    @Test
    void testRegistroPasswordCorto() throws Exception {
        clickPrivateButton("goToRegisterButton");
        setRegisterFields("User", "12", "12"); 
        
        clickPrivateButton("registerButton");
        verificarError("al menos 3 caracteres");
    }

    @Test
    void testRegistroPasswordNoCoincide() throws Exception {
        clickPrivateButton("goToRegisterButton");
        setRegisterFields("User", "password123", "passwordXXX");
        
        clickPrivateButton("registerButton");
        verificarError("no coinciden");

        JPasswordField confirm = (JPasswordField) getPrivateField("registerConfirmPasswordField");
        assertEquals("", new String(confirm.getPassword()));
    }
    
    @Test
    void testBotonCancelar() throws Exception {

        dialog.dispose();
        assertFalse(dialog.isAccepted());
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = ConnectionDialog.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(dialog, value);
    }

    private Object getPrivateField(String fieldName) throws Exception {
        Field field = ConnectionDialog.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(dialog);
    }

    private void clickPrivateButton(String buttonName) throws Exception {
        JButton btn = (JButton) getPrivateField(buttonName);
        btn.doClick();
    }

    private void setRegisterFields(String user, String pass, String confirm) throws Exception {
        ((JTextField) getPrivateField("registerUsernameField")).setText(user);
        ((JPasswordField) getPrivateField("registerPasswordField")).setText(pass);
        ((JPasswordField) getPrivateField("registerConfirmPasswordField")).setText(confirm);
    }

    private void verificarError(String parteDelMensaje) throws Exception {
        JLabel status = (JLabel) getPrivateField("statusLabel");
        String texto = status.getText().toLowerCase();
        assertTrue(texto.contains(parteDelMensaje.toLowerCase()), 
                   "El mensaje de error debería contener: " + parteDelMensaje + ", pero fue: " + texto);
    }
}