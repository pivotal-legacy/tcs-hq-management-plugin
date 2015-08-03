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

import org.hyperic.hq.control.ControlEvent

import javax.jms.Destination 
import javax.jms.JMSException 
import javax.jms.ObjectMessage


class StubObjectMessage implements ObjectMessage {
    
    private final ControlEvent object;
    
    StubObjectMessage(ControlEvent controlEvent) {
        this.object = controlEvent
    }
    
    public void acknowledge() throws JMSException {
    }
    
    public void clearBody() throws JMSException {
    }
    
    public void clearProperties() throws JMSException {
    }
    
    /** 
     * {@inheritDoc}
     */
    public boolean getBooleanProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }
    
    /** 
     * {@inheritDoc}
     */
    public byte getByteProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public double getDoubleProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public float getFloatProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public int getIntProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public String getJMSCorrelationID() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public int getJMSDeliveryMode() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public Destination getJMSDestination() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public long getJMSExpiration() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public String getJMSMessageID() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public int getJMSPriority() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public boolean getJMSRedelivered() throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }
    
    /** 
     * {@inheritDoc}
     */
    public Destination getJMSReplyTo() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public long getJMSTimestamp() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public String getJMSType() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public long getLongProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public Object getObjectProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public Enumeration getPropertyNames() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public short getShortProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * {@inheritDoc}
     */
    public String getStringProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public boolean propertyExists(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setBooleanProperty(String arg0, boolean arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setByteProperty(String arg0, byte arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setDoubleProperty(String arg0, double arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setFloatProperty(String arg0, float arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setIntProperty(String arg0, int arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSCorrelationID(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSCorrelationIDAsBytes(byte[] bytes) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSDeliveryMode(int arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSDestination(Destination arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSExpiration(long arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSMessageID(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSPriority(int arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSRedelivered(boolean arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSReplyTo(Destination arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSTimestamp(long arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setJMSType(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setLongProperty(String arg0, long arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setObjectProperty(String arg0, Object arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setShortProperty(String arg0, short arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setStringProperty(String arg0, String arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public Serializable getObject() throws JMSException {
        return this.object;
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setObject(Serializable arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }
}
