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

import java.io.ByteArrayInputStream
import java.util.Map

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.ServletContext

import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.easymock.Capture
import static org.easymock.classextension.EasyMock.*
import org.hyperic.hq.authz.server.session.AuthzSubject
import org.hyperic.hq.authz.shared.ResourceManager
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.control.shared.ControlManager 
import org.hyperic.hq.hqu.rendit.RequestInvocationBindings
import org.junit.Before
import org.junit.Ignore 
import static org.mockito.Mockito.*

/**
 * 
 */
class TcServerAppMgmtControllerTestCase {
    
    TomcatappmgmtController controller
    RequestInvocationBindings bindings
    AuthzSubject mockUser
    
    HttpServletRequest mockRequest
    HttpServletResponse mockResponse
    ServletContext mockServletContext
//    DiskFileItemFactory mockDiskFileItemFactory 
//    ServletFileUpload mockServletFileUpload
    ControlManager mockControlManager
    ResourceManager mockResourceManager
    
    @Before
    @Ignore
    public void setUp() throws Exception {
        
        // Begin Spring 
//        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest()
//        request.addFile(new MockMultipartFile("demo.war", "demo.war", "multipart/form-data; boundary=xxx", "demo.war".getBytes()))
        // End Spring 
        
        
        // Begin Mockito 
//        mockUser = mock(AuthzSubject)
//        mockRequest = mock(HttpServletRequest)
//        mockResponse = mock(HttpServletResponse)
//        mockServletContext = mock(ServletContext)
        
//        Answer requestAnswer = new RequestAnswer()
        // How to capture an arg and feed it to the RequestAnswer#answer method? 
//        when(mockRequest.getParameter(anyString())).thenAnswer(/* TODO */)
        
        // Groovy 1.6.7 can't handle anonymous inner classes 
//        when(mockRequest.getParameter(anyString())).thenAnswer(new Answer {
//          public Object answer(InvocationOnMock invocation) {
//	        String key = (String) invocation.getArguments()[0]
//	        return params.get(key)
//          }  
//        })
        
//        mockDiskFileItemFactory = mock(DiskFileItemFactory)
//        Answer diskFileItemAnswer = new DiskFileItemFactoryAnswer()
        // How to capture an arg and feed it to the DiskFileItemFactoryAnswer#answer method? 
//        when(mockRequest.getParameter(anyString())).thenAnswer(/* TODO */)
        
        // Groovy 1.6.7 can't handle anonymous inner classes 
//        when(mockRequest.getParameter(anyString())).thenAnswer(new Answer {
//            public Object answer(InvocationOnMock invocation) {
//		        return new ArrayList<Object>(params.values())
//            }
//        })
        
        // Not needed 
//        mockDiskFileItemFactory = mock(DiskFileItemFactory)
//        when(mockDiskFileItemFactory.getFileItemFactory()).thenAnswer(new Answer {
//            public Object answer(InvocationOnMock invocation) {
//                return mockDiskFileItemFactory
//            }
//        })
        
//        mockServletFileUpload = mock(ServletFileFileUpload)
//        Answer servletUploadAnswer = new ServletUploadAnswer()
        // How to capture an arg and feed it to the ServletUploadAnswer#answer method? 
//        expect(mockServletFileUpload.parseRequest(mockRequest)).andReturn(new ArrayList<Object>(params.values()))
        
        // Groovy 1.6.7 can't handle anonymous inner classes 
//        when(mockServletFileUpload.parseRequest(mockRequest)).thenAnswer(new Answer {
//            public Object answer(InvocationOnMock invocation) {
//                return new ArrayList<Object>(params.values())
//            }
//        })
        // End Mockito 
        
        
        // Begin EasyMock 
        mockUser = createMock(AuthzSubject)
        mockRequest = createMock(HttpServletRequest)
        mockResponse = createMock(HttpServletResponse)
        mockServletContext = createMock(ServletContext)
//        mockServletFileUpload = createMock(ServletFileUpload)
        mockControlManager = createMock(ControlManager)
        
        Bootstrap.setBean(ControlManager.class, mockControlManager)
        
        Capture <String> capturedArgString = new Capture <String>();
        expect(mockRequest.getParameter(and(capture(capturedArgString), isA(String)))).andReturn(params.get(capturedArgString))
        // Not needed 
//        mockDiskFileItemFactory = createMock(DiskFileItemFactory)
//        expect(mockServletFileUpload.getFileItemFactory()).andReturn(mockDiskFileItemFactory)
//        mockServletFileUpload = createMock(ServletFileFileUpload)
//        expect(mockServletFileUpload.parseRequest(mockRequest)).andReturn(new ArrayList<Object>(params.values()))
        
        replay(mockUser)
        replay(mockResponse)
        replay(mockServletContext)
        replay(mockRequest)
//        replay(mockServletFileUpload)
        // End EasyMock 
    
        String mockRequestURI = "mockRequestURI"
        String mockCtxPath ="mockCtxPath"
        String mockPathInfo = "mockPathInfo"
        String mockServletPath = "mockServletPath"
        String mockQueryStr = "mockQueryStr"
        
        bindings = new RequestInvocationBindings(
            mockRequestURI,
            mockCtxPath,
            mockPathInfo,
            mockServletPath,
            mockQueryStr,
            mockUser,
            mockRequest,
            mockResponse,
            mockServletContext
        )
        
        controller = new TomcatappmgmtController()
//        controller.fileUpload = mockServletFileUpload
        controller.setInvokeArgs(bindings)
    }
    
    public Map getParams() {
        
	    def params = ['fileName':'demo.war',
	        'streamText':new ByteArrayInputStream('demo.war'.getBytes()),
	        'contextPath':'contextPath',
	        'hostName':'localhost',
	        'coldDeployValue':'off']
    }
    
}
