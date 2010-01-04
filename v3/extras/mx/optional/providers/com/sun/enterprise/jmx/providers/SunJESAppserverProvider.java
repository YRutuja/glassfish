/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2003-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * $Header: /m/jws/jmxcmd/optional/providers/com/sun/enterprise/jmx/providers/SunJESAppserverProvider.java,v 1.7 2005/11/15 20:59:51 llc Exp $
 * $Revision: 1.7 $
 * $Date: 2005/11/15 20:59:51 $
 */

package com.sun.enterprise.jmx.providers;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;

import java.io.IOException;
import java.io.File;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HandshakeCompletedListener;

import com.sun.cli.jmxcmd.spi.JMXConnectorProviderInfo;
import com.sun.cli.jmxcmd.spi.JMXConnectorProviderBase;
import com.sun.cli.jmxcmd.spi.RMIDefaultConnectorProvider;

//import com.sun.enterprise.admin.jmx.remote.SunOneHttpJmxConnectorFactory;

import com.sun.appserv.management.client.AppserverConnectionSource;
import com.sun.appserv.management.client.TLSParams;


import com.sun.cli.jcmd.util.misc.GSetUtil;

/**
	Supports connectivity to Sun Jave Enterprise System Appserver via its
	RMI connector.
	Supported protocols are:
	<ul>
	<li>sun-as-rmi</li>
	</ul>
 */
public class SunJESAppserverProvider
	extends JMXConnectorProviderBase
{
	    public
	SunJESAppserverProvider()
	{
	    Package.getPackage( "com.sun.appserv.management" );
	}
	
	static class Info implements JMXConnectorProviderInfo
	{
		private static final String	DESCRIPTION	=
			"Implements the SunOne Appserver 8.0 PE connector.";
		private static final String	USAGE	=
			"connect --host <host> --port port --protocol sun-as-rmi " +
			"--user <user> --password <pass> --truststore <file> --truststore-password <pw> [connection-name]";
		
			public String
		getDescription()
		{
			return( DESCRIPTION );
		}
			public String
		getUsage()
		{
			return( USAGE );
		}
	}
	
		public static JMXConnectorProviderInfo
	getInfo()
	{
		return( new Info() );
	}
		
	/*
	
		public JMXConnector
	connectHTTP( final Map m) throws IOException
	{
		final String user		= getUser( m );
		final String password	= getPassword( m );
		
		requireParam( user, "user" );
		requireParam( password, "password" );
		
		final JMXServiceURL url	= envToJMXServiceUrl(m);
		
		try
		{
			final Class c	= 
			Thread.currentThread().getContextClassLoader().loadClass(
				"com.sun.enterprise.admin.jmx.remote.SunOneHttpJmxConnectorFactory" );
			
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return ( SunOneHttpJmxConnectorFactory.connect(url, user, password) );
	}
	
	
		private JMXServiceURL
	envToJMXServiceUrl( final Map m)
		throws java.net.MalformedURLException
	{
		assert( HTTP_PROTOCOL.equals( getProtocol( m ) ) );
		
		final String protocol	= "s1ashttp";
		final String host		= getHost( m );
		final int port			= getPortInt( m );
		return ( new JMXServiceURL( protocol, host, port) );
	}
	
	public static final String	APPSERVER_JNDI_NAME	= "/management/rmi-jmx-connector";
		public JMXConnector
	connectRMINonSecure( final Map m) throws IOException
	{
		final RMIDefaultConnectorProvider	provider	= new RMIDefaultConnectorProvider();
		
		final HashMap	params	= new HashMap();
		params.putAll( m );
		params.put( JNDI_NAME, APPSERVER_JNDI_NAME );
		params.put( PROTOCOL, provider.RMI_PROTOCOL );
		
		return( provider.connect( params ) );
	}
	*/
	
		public JMXConnector
	connectRMI( final Map m) throws IOException
	{
		final String	host		= getHost( m );
		final int		port		= getPortInt( m );
		final String	user		= getUser( m );
		final String	password	= getPassword( m );
		final String	trustStore	= getTruststore( m );
		final String	trustStorePassword	= getTruststorePassword( m );
		
		final TLSParams	tlsParams	= trustStore == null ?
			null : new TLSParams( new File( trustStore ), trustStorePassword.toCharArray(), true,
			    (HandshakeCompletedListener)null );
			
		final AppserverConnectionSource	connSource	= 
			new AppserverConnectionSource( AppserverConnectionSource.PROTOCOL_RMI,
				host, port, user, password, tlsParams, null);
		
		final JMXConnector	conn	= connSource.getJMXConnector( false );
		
		return( conn );
	}
	
		public JMXConnector
	connect( final Map m ) throws IOException
	{
		final String protocol	= getProtocol( m );
		JMXConnector	jmxConnector	= null;
		
		final String	truststoreFile	= getTruststore( m );
		
		/*
		if ( protocol.equals( HTTP_PROTOCOL ) )
		{
			jmxConnector	= connectHTTP( m );
		}
		else
		*/
		if ( protocol.equals( RMI_PROTOCOL ) )
		{
			jmxConnector	=  connectRMI( m );
		}
		else
		{
			throw new IllegalArgumentException( "" + protocol );
		}
		
		return( jmxConnector );
	}
	
	
	//public final static String	HTTP_PROTOCOL	= "sun-as-http";
	public final static String	RMI_PROTOCOL	= "sun-as-rmi";
	public static final Set	SUPPORTED_PROTOCOLS	=
		GSetUtil.newUnmodifiableStringSet( RMI_PROTOCOL );
	
		protected Set
	getSupportedProtocols()
	{
		return( SUPPORTED_PROTOCOLS );
	}
}





