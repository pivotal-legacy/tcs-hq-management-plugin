/*
 * Copyright (C) 2009-2015  Pivotal Software, Inc
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

{
  jdbcDrivers : [
    {
      name : "",
      className : "[select a driver]",
      url : "[select a driver]"
    },
    
    {
      name : "JDBC ODBC Bridge",
      className : "sun.jdbc.odbc.JdbcOdbcDriver",
      url : "jdbc:odbc:<alias>"
    },
    {
      name : "IBM DB2 App Driver",
      className : "com.ibm.db2.jdbc.app.DB2Driver",
      url : "jdbc:db2:<dbname>"
    },
    {
      name : "IBM DB2 Net Driver",
      className : "com.ibm.db2.jdbc.net.DB2Driver",
      url : "jdbc:db2://<server>:<6789>/<db-name>"
    },
    {
      name : "Informix",
      className : "com.informix.jdbc.IfxDriver",
      url : "jdbc:informix-sqli://<host_name>:<port_number>/<database_name>:INFORMIXSERVER=<server_name>"
    },
    {
      name : "Microsoft MSSQL Server JDBC Driver",
      className : "com.microsoft.sqlserver.jdbc.SQLServerDriver",
      url : "jdbc:microsoft:sqlserver://<server_name>:<1433>"
    },
    {
      name : "MySQL Driver",
      className : "com.mysql.jdbc.Driver",
      url : "jdbc:mysql://<hostname>[,<failoverhost>][<:3306>]/<dbname>"
    },
    {
      name : "Oracle OCI Driver",
      className : "oracle.jdbc.OracleDriver",
      url : "jdbc:oracle:oci8:@<database_name>"
    },
    {
      name : "Oracle Thin Driver",
      className : "oracle.jdbc.OracleDriver",
      url : "jdbc:oracle:thin:@<server>[:<1521>]:<database_name>"
    },
    
    {
      name : "",
      className : "[select a driver]",
      url : "[select a driver]"
    },
    
    {
      name : "Apache Derby Client",
      className : "org.apache.derby.jdbc.ClientDriver",
      url : "jdbc:derby://<server>[:<port>]/<databaseName>"
    },
    {
      name : "Apache Derby Embedded",
      className : "org.apache.derby.jdbc.EmbeddedDriver",
      url : "jdbc:derby:<database>[;create=true]"
    },
    {
      name : "Axion",
      className : "org.axiondb.jdbc.AxionDriver",
      url : "jdbc:axiondb:<database-name>[:<database-directory>]"
    },
    {
      name : "Firebird JayBird",
      className : "org.firebirdsql.jdbc.FBDriver",
      url : "jdbc:firebirdsql:[//host[:port]/]<database>"
    },
    {
      name : "FrontBase",
      className : "jdbc.FrontBase.FBJDriver",
      url : "jdbc:FrontBase://<server>/<db-name>"
    },
    {
      name : "H2",
      className : "org.h2.Driver",
      url : "jdbc:h2://<server>:<9092>/<db-name>"
    },
    {
      name : "H2 Embedded",
      className : "org.h2.Driver",
      url : "jdbc:h2://<db-name>"
    },
    {
      name : "H2 In-Memory",
      className : "org.h2.Driver",
      url : "jdbc:h2:mem:"
    },
    {
      name : "HSQLDB In-Memory",
      className : "org.hsqldb.jdbcDriver",
      url : "jdbc:hsqldb:."
    },
    {
      name : "HSQLDB Server",
      className : "org.hsqldb.jdbcDriver",
      url : "jdbc:hsqldb:hsql://<server>[:<1476>]"
    },
    {
      name : "HSQLDB Standalone",
      className : "org.hsqldb.jdbcDriver",
      url : "jdbc:hsqldb:<databaseName>"
    },
    {
      name : "HSQLDB Web Server",
      className : "org.hsqldb.jdbcDriver",
      url : "jdbc:hsqldb:http://<server>[:<1476>]"
    },
    {
      name : "HXTT Access Client",
      className : "com.hxtt.sql.access.AccessDriver",
      url : "jdbc:access://<server:port>/<databaseName>"
    },
    {
      name : "HXTT Access Embedded",
      className : "com.hxtt.sql.access.AccessDriver",
      url : "jdbc:access:///<databaseName>"
    },
    {
      name : "HXTT CSV Client",
      className : "com.hxtt.sql.text.TextDriver",
      url : "jdbc:csv://<server:port>/<databaseName>"
    },
    {
      name : "HXTT CSV Embedded",
      className : "com.hxtt.sql.text.TextDriver",
      url : "jdbc:csv:///<databaseName>"
    },
    {
      name : "HXTT DBF Client",
      className : "com.hxtt.sql.dbf.DBFDriver",
      url : "jdbc:dbf://<server:port>/<databaseName>"
    },
    {
      name : "HXTT DBF Embedded",
      className : "com.hxtt.sql.dbf.DBFDriver",
      url : "jdbc:dbf:///<databaseName>"
    },
    {
      name : "HXTT Excel Client",
      className : "com.hxtt.sql.excel.ExcelDriver",
      url : "jdbc:excel://<server:port>/<databaseName>"
    },
    {
      name : "HXTT Excel Embedded",
      className : "com.hxtt.sql.excel.ExcelDriver",
      url : "jdbc:excel:///<databaseName>"
    },
    {
      name : "HXTT Paradox Client",
      className : "com.hxtt.sql.paradox.ParadoxDriver",
      url : "jdbc:paradox://<server:port>/<databaseName>"
    },
    {
      name : "HXTT Paradox Embedded",
      className : "com.hxtt.sql.paradox.ParadoxDriver",
      url : "jdbc:paradox:///<databaseName>"
    },
    {
      name : "HXTT Text Client",
      className : "com.hxtt.sql.text.TextDriver",
      url : "jdbc:text://<server:port>/<databaseName>"
    },
    {
      name : "HXTT Text Embedded",
      className : "com.hxtt.sql.text.TextDriver",
      url : "jdbc:text:///<databaseName>"
    },
    {
      name : "IBM DB2 App Driver",
      className : "com.ibm.db2.jdbc.app.DB2Driver",
      url : "jdbc:db2:<dbname>"
    },
    {
      name : "IBM DB2 Net Driver",
      className : "com.ibm.db2.jdbc.net.DB2Driver",
      url : "jdbc:db2://<server>:<6789>/<db-name>"
    },
    {
      name : "Informix",
      className : "com.informix.jdbc.IfxDriver",
      url : "jdbc:informix-sqli://<host_name>:<port_number>/<database_name>:INFORMIXSERVER=<server_name>"
    },
    {
      name : "InstantDB",
      className : "org.enhydra.instantdb.jdbc.idbDriver",
      url : "jdbc:idb:<pathname>"
    },
    {
      name : "InterClient",
      className : "interbase.interclient.Driver",
      url : "jdbc:interbase://<server>/<full_db_path>"
    },
    {
      name : "Intersystems Cache",
      className : "com.intersys.jdbc.CacheDriver",
      url : "jdbc:Cache://<host>:1972/<database>"
    },
    {
      name : "JDBC ODBC Bridge",
      className : "sun.jdbc.odbc.JdbcOdbcDriver",
      url : "jdbc:odbc:<alias>"
    },
    {
      name : "jTDS",
      className : "com.internetcds.jdbc.tds.Driver",
      url : "jdbc:freetds:sqlserver://<hostname>[:<4100>]/<dbname>"
    },
    {
      name : "jTDS Microsoft SQL",
      className : "net.sourceforge.jtds.jdbc.Driver",
      url : "jdbc:jtds:sqlserver://<hostname>[:<1433>]/<dbname>"
    },
    {
      name : "jTDS Sybase",
      className : "net.sourceforge.jtds.jdbc.Driver",
      url : "jdbc:jtds:sybase://<hostname>[:<4100>]/<dbname>"
    },
    {
      name : "JTOpen(AS/400)",
      className : "com.ibm.as400.access.AS400JDBCDriver",
      url : "jdbc:as400://<host_name>/<default-schema>"
    },
    {
      name : "Mckoi",
      className : "com.mckoi.JDBCDriver",
      url : "jdbc:mckoi://<host>[:9157][/<schema>]/"
    },
    {
      name : "Microsoft MSSQL Server JDBC Driver",
      className : "com.microsoft.sqlserver.jdbc.SQLServerDriver",
      url : "jdbc:microsoft:sqlserver://<server_name>:<1433>"
    },
    {
      name : "Mimer SQL",
      className : "com.mimer.jdbc.Driver",
      url : "jdbc:mimer:[//[<user>[:<password>]@]<server>[:<1360>]][/<dbname>]"
    },
    {
      name : "MMMySQL Driver",
      className : "org.gjt.mm.mysql.Driver",
      url : "jdbc:mysql://<hostname>[<:3306>]/<dbname>"
    },
    {
      name : "MySQL Driver",
      className : "com.mysql.jdbc.Driver",
      url : "jdbc:mysql://<hostname>[,<failoverhost>][<:3306>]/<dbname>"
    },
    {
      name : "Oracle OCI Driver",
      className : "oracle.jdbc.OracleDriver",
      url : "jdbc:oracle:oci8:@<database_name>"
    },
    {
      name : "Oracle Thin Driver",
      className : "oracle.jdbc.OracleDriver",
      url : "jdbc:oracle:thin:@<server>[:<1521>]:<database_name>"
    },
    {
      name : "Pointbase Embedded",
      className : "com.pointbase.net.netJDBCDriver",
      url : "jdbc:pointbase:embedded:<dbname>"
    },
    {
      name : "Pointbase Server",
      className : "com.pointbase.net.netJDBCDriver",
      url : "jdbc:pointbase:server://<server_name>/<dbname>"
    },
    {
      name : "PostgreSQL",
      className : "org.postgresql.Driver",
      url : "jdbc:postgresql:[<//host>[:<5432>/]]<database>"
    },
    {
      name : "SAPDB",
      className : "com.sap.dbtech.jdbc.DriverSapDB",
      url : "jdbc:sapdb:[//host/]dbname"
    },
    {
      name : "Sybase Adaptive Server Anywhere",
      className : "com.sybase.jdbc2.jdbc.SybDriver",
      url : "jdbc:sybase:Tds:<host>:<port>?ServiceName=<DBNAME>"
    },
    {
      name : "Sunopsis XML",
      className : "com.sunopsis.jdbc.driver.xml.SnpsXmlDriver",
      url : "jdbc:snps:xml?f=<file-name>&s=<schema-name>"
    },
    {
      name : "Sybase Adaptive Server Enterprise",
      className : "jdbc:sybase:Tds:<host>:<port>/<DBNAME>",
      url : "com.sybase.jdbc2.jdbc.SybDriver"
    },
    {
      name : "ThinkSQL",
      className : "uk.co.thinksql.ThinkSQLDriver",
      url : "jdbc:thinksql://<server>:<9075>"
    }
  ]
}
