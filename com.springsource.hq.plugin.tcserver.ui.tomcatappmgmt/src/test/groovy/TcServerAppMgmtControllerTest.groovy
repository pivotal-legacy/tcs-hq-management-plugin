/*
 * Copyright (C) 2010-2015  Pivotal Software, Inc
 *
 * This program is is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Map

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.ServletContext

import org.apache.commons.fileupload.disk.DiskFileItem
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import static org.easymock.classextension.EasyMock.*
import org.hyperic.hq.authz.server.session.AuthzSubject
import org.hyperic.hq.authz.shared.ResourceManager
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.control.shared.ControlManager 
import org.hyperic.hq.hqu.rendit.RequestInvocationBindings
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import org.junit.Before 
import org.junit.Ignore 
import org.junit.Test
import static org.mockito.Mockito.*
import org.springframework.web.context.support.StaticWebApplicationContext
import org.springframework.mock.web.MockServletConfig
import org.springframework.web.context.WebApplicationContext
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

/**
 *
 */
class TcServerAppMgmtControllerTest extends TcServerAppMgmtControllerTestCase {
    
    private byte[] buffer
    
    @Before
    public void setUp() throws Exception {
        InputStream input = getClass().getResourceAsStream("demo.war")
        buffer = new byte[input.available()]
        input.read( buffer )
        input.close()
    }
    
    @Test
    @Ignore
    public void testManageApplications() throws Exception {
        MockServletConfig config = new MockServletConfig()
        config.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, 
            new StaticWebApplicationContext())
        HttpServletRequest request = new MockHttpServletRequest("POST", "https://locallhost")
        request.addParameter("eid", "1234567")
        HttpServletResponse response = new MockHttpServletResponse()
        
        ByteArrayOutputStream output = new ByteArrayOutputStream()
        
        try {
            output.write(String.format("-----1234\r\n" +
                    "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n" +
                    "Content-Type: %s\r\n" +
                    "\r\n",
                    "textField",
                    "demo.war",
                    "application/x-zip").getBytes())
            
            output.write(buffer)
            output.write(new String( "\r\n-----1234" ).getBytes())
            output.flush()
        } finally {
//            output.close
        }
        
        String mockRequestURI = "mockRequestURI"
        String mockCtxPath ="mockCtxPath"
        String mockPathInfo = "mockPathInfo"
        String mockServletPath = "mockServletPath"
        String mockQueryStr = "mockQueryStr"
        
	    mockUser = createMock(AuthzSubject)
        mockControlManager = createMock(ControlManager)
        mockResourceManager = createMock(ResourceManager)
        
        Bootstrap.setBean(ControlManager.class, mockControlManager)
        Bootstrap.setBean(ResourceManager, mockResourceManager)
        
        bindings = new RequestInvocationBindings(
            mockRequestURI,
            mockCtxPath,
            mockPathInfo,
            mockServletPath,
            mockQueryStr,
            mockUser,
            (HttpServletRequest)request,
            (HttpServletResponse)response,
            config.getServletContext())
        
        controller = new TomcatappmgmtController()
        controller.setInvokeArgs(bindings)
        controller.manageApplications(params)
    }
    
    public Map getParams() {
        ['fileName':'demo.war',
         'streamText':new ByteArrayInputStream('demo.war'.getBytes()),
         'contextPath':'contextPath',
         'hostName':'localhost',
         'coldDeployValue':'off']
    }
    
}
