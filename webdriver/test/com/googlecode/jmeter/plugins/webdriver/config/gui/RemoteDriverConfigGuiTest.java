package com.googlecode.jmeter.plugins.webdriver.config.gui;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.awt.event.FocusEvent;

import javax.swing.JOptionPane;

import kg.apc.emulators.TestJMeterUtils;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockito.internal.matchers.Any;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.googlecode.jmeter.plugins.webdriver.config.RemoteDriverConfig;

public class RemoteDriverConfigGuiTest {

    private RemoteDriverConfigGui gui;

    @BeforeClass
    public static void setupJMeterEnv() {
        TestJMeterUtils.createJmeterEnv();
    }

    @Before
    public void createConfig() {
        gui = new RemoteDriverConfigGui();
    }

    @Test
    public void shouldReturnStaticLabel() {
        assertThat(gui.getStaticLabel(), containsString("Remote Driver Config"));
    }

    @Test
    public void shouldReturnWikiPage() {
        assertThat(gui.getWikiPage(), is("RemoteDriverConfig"));
    }

    @Test
    public void shouldReturnCanonicalClassNameAsLabelResource() {
        assertThat(gui.getLabelResource(), is(gui.getClass().getCanonicalName()));
    }

    @Test
    public void shouldReturnRemoteDriverConfig() {
        assertThat(gui.createTestElement(), is(instanceOf(RemoteDriverConfig.class)));
    }

    @Test
    public void shouldOverrideUserAgent() {
        gui.userAgentOverrideCheckbox.setSelected(true);
        gui.userAgentOverrideText.setText("some user agent");
        final RemoteDriverConfig testElement = (RemoteDriverConfig) gui.createTestElement();
        assertThat(testElement.getUserAgentOverride(), is("some user agent"));
    }
    
    @Test
	public void shouldSetTheSeleniumNodeUrl() throws Exception {
		gui.remoteSeleniumGridText.setText("http://my.awesomegrid.com");
		final RemoteDriverConfig testElement = (RemoteDriverConfig) gui.createTestElement();
        assertThat(testElement.getSeleniumGridUrl(), is("http://my.awesomegrid.com"));
	}

    @Test
    public void shouldNotOverrideUserAgent() {
        gui.userAgentOverrideCheckbox.setSelected(false);
        gui.userAgentOverrideText.setText("some user agent");
        
        final RemoteDriverConfig testElement = (RemoteDriverConfig) gui.createTestElement();
        assertThat(testElement.getUserAgentOverride(), is(not("some user agent")));
    }

    @Test
    public void shouldResetValuesOnClearGui() {
        gui.userAgentOverrideText.setText("user agent");
        gui.userAgentOverrideCheckbox.setSelected(true);
        gui.remoteSeleniumGridText.setText("http://my.awesomegrid.com");
        
        gui.clearGui();

        assertThat(gui.remoteSeleniumGridText.getText(), is(StringUtils.EMPTY));
        assertThat(gui.userAgentOverrideText.getText(), is(RemoteDriverConfigGui.OVERRIDEN_USER_AGENT));
        assertThat(gui.userAgentOverrideCheckbox.isSelected(), is(false));
    }

    @Test
    public void shouldSetRemoteDriverConfigOnConfigure() {
        RemoteDriverConfig config = new RemoteDriverConfig();
        config.setUserAgentOverride("user-agent");
        config.setUserAgentOverridden(true);
        gui.configure(config);

        assertThat(gui.userAgentOverrideText.getText(), is(config.getUserAgentOverride()));
        assertThat(gui.userAgentOverrideCheckbox.isSelected(), is(config.isUserAgentOverridden()));
    }
    
    @Test
	public void shouldFireAMessageWindowWhenTheFocusIsLost() throws Exception {
    	gui.remoteSeleniumGridText.setText("badURL");
    	FocusEvent focusEvent = new FocusEvent(gui.remoteSeleniumGridText, 1);
    	gui.messageDialog = Mockito.mock(MessageDialog.class);
    	gui.focusLost(focusEvent);
    	Mockito.verify(gui.messageDialog).show(gui, "The selenium grid URL is malformed", "Error", JOptionPane.ERROR_MESSAGE);
	}
    
    @Test
	public void shouldNotFireAMessageWindowWhenTheURLIsCorrect() throws Exception {
    	
    	gui.remoteSeleniumGridText.setText("http://my.awesomegrid.com");
    	FocusEvent focusEvent = new FocusEvent(gui.remoteSeleniumGridText, 1);
    	gui.messageDialog = Mockito.mock(MessageDialog.class);
    	gui.focusLost(focusEvent);
    	Mockito.verify(gui.messageDialog, Mockito.never()).show(gui, "The selenium grid URL is malformed", "Error", JOptionPane.ERROR_MESSAGE);
	}
    
    @Test
	public void shouldNotFireAMessageWindowWhenTheFocusLostIsNotFromSeleniumGridComponent() throws Exception {
    	PowerMockito.mockStatic(JOptionPane.class);
    	gui.remoteSeleniumGridText.setText("badURL");
    	FocusEvent focusEvent = new FocusEvent(gui.capabilitiesComboBox, 1);
    	gui.messageDialog = Mockito.mock(MessageDialog.class);
    	gui.focusLost(focusEvent);
    	Mockito.verify(gui.messageDialog, Mockito.never()).show(gui, "The selenium grid URL is malformed", "Error", JOptionPane.ERROR_MESSAGE);
	}
    
    @Test
	public void shouldNotFireAMessageWindowWhenTheFocusLostIsNotFromSeleniumGridComponentAndURLIsCorrect() throws Exception {
    	PowerMockito.mockStatic(JOptionPane.class);
    	gui.remoteSeleniumGridText.setText("http://my.awesomegrid.com");
    	FocusEvent focusEvent = new FocusEvent(gui.capabilitiesComboBox, 1);
    	gui.messageDialog = Mockito.mock(MessageDialog.class);
    	gui.focusLost(focusEvent);
    	Mockito.verify(gui.messageDialog, Mockito.never()).show(gui, "The selenium grid URL is malformed", "Error", JOptionPane.ERROR_MESSAGE);
	}
}
