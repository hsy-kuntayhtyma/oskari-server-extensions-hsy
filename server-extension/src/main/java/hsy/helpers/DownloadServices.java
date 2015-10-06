package hsy.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpSession;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.MediaType;

import livi.dataObjects.ErrorReportDetails;
import livi.dataObjects.LoadZipDetails;
import livi.schemas.wfsReader111;
import livi.utils.MapHelpers;
import livi.utils.Utilities;

import org.apache.commons.mail.MultiPartEmail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Stopwatch;
import com.liferay.util.portlet.PortletProps;

/**
 * Download services.
 * @author Marko Kuosmanen
 * @copyright Dimenteq Oy
 *
 */
public class DownloadServices {
	public static final String WFS_GETCAPABILITIES_XML_JSON_VALUE ="WFSGetCapabilitiesXML";
	public static final String WFS_METADATA_URL_JSON_VALUE ="linkForMetadata";
	public static final String WFS_USED_URL ="url";
	public static final String WFS_FEATURETYPES ="featureTypes";
	private int serviceType = 0;
	Properties properties;
	
	/**
	 * Default Constructor.
	 * @param type
	 * @param properties 
	 */
	public DownloadServices(int type, Properties properties){
		this.serviceType = type;
		this.properties = properties;
	}
	
	/**
	 * Enum of Http methods.
	 * @author Marko Kuosmanen
	 *
	 */
	public enum HttpMetodes {
		   POST("POST"), GET("GET");
		   private final String stringValue;
		   private HttpMetodes(final String s) { stringValue = s; }
		   public String toString() { return stringValue; }
		}
	
	/**
	 * Get Spatial Data Set.
	 * @return JSONObject of spatial dataset
	 */
	public JSONObject GetSpatialDataSet(){
		JSONObject job = new JSONObject();
		return job;
	}
	
	/**
	 * Describe Spatial Data Set.
	 * @return JSONArray of describe data sets
	 */
	public JSONArray DescribeSpatialDataSet(){
		String strWfsUrl = new String();
		JSONArray featureTypes = new JSONArray();
		try{
			// road
			if(serviceType==0){
				strWfsUrl = PortletProps.get("mapdata.service.transport.road");
			}
			// railway line
			else if(serviceType==1){
				strWfsUrl = PortletProps.get("mapdata.service.transport.railway_line");
			}
			// sea
			else if(serviceType==2){
				strWfsUrl = PortletProps.get("mapdata.service.transport.sea");
			}
			
			featureTypes = wfsReader111.getFeatureTypes(strWfsUrl);
			//featureTypes = wfsReader111.getFeatureTypes(strWfsUrl,"katselupalvelu_Ajoradan leveys");
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return featureTypes;
	}
	
	/**
	 * Link Download Service.
	 * @return JSONObject of link download details
	 */
	public JSONObject LinkDownloadService(){
		JSONObject job = new JSONObject();
		try {
			job.put(WFS_METADATA_URL_JSON_VALUE, "");
		} catch (JSONException e) {
		}
		return job;
	}
	
	/**
	 * Get Spatial Object.
	 * @return JSONObject of spatial object
	 */
	public JSONObject GetSpatialObject(){
		JSONObject job = new JSONObject();
		return job;
	}
	
	/**
	 * Describe Spatial Object Type.
	 * @return JSONObject of describe object dataset
	 */
	public JSONObject DescribeSpatialObjectType(){
		JSONObject job = new JSONObject();
		return job;
	}

	/**
	 * Load shape-ZIP from Geoserver.
	 * @return succeeds downloads
	 * @throws IOException
	 * @deprecated
	 */
	public boolean loadZip(String gfr) throws IOException{
		
		boolean ready = false;
		Utilities util = new Utilities();
		final URL url = new URL("http://194.28.2.158/livigeoserver/wfs");
		
		HttpURLConnection conn = util.getConnection(url, HttpMetodes.POST, MediaType.APPLICATION_XML_TYPE);
		
		OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
		try{
			writer.write(gfr);
			writer.flush();
		
			String filename = UUID.randomUUID().toString();
			InputStream istream = conn.getInputStream();
			OutputStream ostream = new FileOutputStream("c:/sheipit/" + filename + ".zip");

			final byte[] buffer = new byte[8*1024];
		
			while (true)
			    {
					int len = istream.read(buffer);
	
			    if (len <= 0)
			        { 
			    		ready = true;
			    		break; }
	
					ostream.write(buffer, 0, len);
	
			    }
		
			ostream.close();
		} catch(Exception ex){
			
		} finally{
			writer.close();
		}
		
		return ready;
	}
	
	/**
	 * Load shape-ZIP from Geoserver.
	 * @param ldz load zip details
	 * @return filename file name
	 * @throws IOException 
	 */
	public String loadZip(LoadZipDetails ldz) throws IOException{	
		Stopwatch timer = Stopwatch.createStarted();
		Stopwatch connectionTimer = Stopwatch.createStarted();
		String realFileName = "";
		String returnFileName = "";
		Utilities util = new Utilities();		
		OutputStreamWriter writer = null;
		HttpURLConnection conn = null;
		InputStream istream = null;
		OutputStream ostream = null;
		
		try{
			System.out.println(ldz.getWFSUrl());
			
			
			if(ldz.isDownloadNormalWay()) {
				System.out.println("Download normal way");
			} else {
				System.out.println("Download plugin way");
			}
			
			if(ldz.getGetFeatureInfoRequest().isEmpty()){
				return null;
			}
			
			System.out.println("WFS URL: " + ldz.getWFSUrl());
			System.out.println("-- filtter: " + ldz.getGetFeatureInfoRequest());
			final URL url = new URL(ldz.getWFSUrl() + ldz.getGetFeatureInfoRequest());
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(600000);
			
			
			// FIXME use helpers
			if(MapHelpers.hasProtected(ldz.getWFSUrl()) || !ldz.isDownloadNormalWay()){
				if(ldz.isLimitedValid()){
					String tunnusSalasana = ldz.getLimitedAccesUsername() + ":" + ldz.getLimitedAccessPassword();
					String encoding = new sun.misc.BASE64Encoder().encode(tunnusSalasana.getBytes());			
					conn.setRequestProperty("Authorization", "Basic " + encoding);
				}
			}
			
			conn.connect();
		
			String filename = UUID.randomUUID().toString();
			istream = conn.getInputStream();
			
			String strTempDir = ldz.getTemporaryDirectory();
			
			File dir0 = new File(strTempDir);
			dir0.mkdirs();
			
			String slashType = "";
			String myFullFileName = dir0.getName();
			if (myFullFileName.lastIndexOf("\\")> 0) {
				slashType = "\\";
			} else {
				slashType = "/";
			}
			
			realFileName = strTempDir + slashType + filename + ".zip";
			
			returnFileName = strTempDir + slashType + filename + ".zip";
			ostream = new FileOutputStream(realFileName);
			

			final byte[] buffer = new byte[8*1024];
		
			while (true)
		    {
				int len = istream.read(buffer);
				if (len <= 0)
		        { 
		    		break; 
		    	}
				ostream.write(buffer, 0, len);
		    }			
			
			if(!isValid(new File(realFileName)) && ldz.getSendErrorReportEmail()!=null){
				ErrorReportDetails erd = new ErrorReportDetails();
				erd.setEmailfrom(ldz.getEmailFrom());
				erd.setErrorFileLocation(realFileName);
				erd.setHostname(ldz.getHostname());
				erd.setSendToEmail(ldz.getErrorReportToEmail());
				erd.setSmtpPort(ldz.getSMTPPort());
				erd.setSubject(ldz.getErrorReportSubject());
				erd.setWfsUrl(ldz.getWFSUrl());
				erd.setXmlRequest(ldz.getGetFeatureInfoRequest());
				erd.setUserEmail(ldz.getUserEmail());
				erd.setLanguage(ldz.getLanguage());
				sendErrorReportToEmail(erd);
				sendErrorReportToUserEmail(erd);
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
		} finally{
			if(writer!=null){
				writer.close();
			}
			if(conn!=null){
				conn.disconnect();
			}
			if(istream!=null){
				istream.close();
			}
			if(ostream!=null){
				ostream.close();
			}
		}
		System.out.println("Download took: " + timer.stop());
		return returnFileName;
	}
	
	/**
	 * Check at is zipfile valid.
	 * @param file zip file
	 * @return
	 */
	static boolean isValid(final File file) {
	    ZipFile zipfile = null;
	    
	    try {
	        zipfile = new ZipFile(file);
	        return true;
	    } catch (ZipException e) {
	        return false;
	    } catch (IOException e) {
	        return false;
	    } finally {
	        try {
	            if (zipfile != null) {
	                zipfile.close();
	                zipfile = null;
	            }
	        } catch (IOException e) {
	        }
	    }
	}
	
	/***
	 * Send error report to support email.
	 * @param xmlRequest xml request
	 * @param wfsUrl wfs url
	 */
	
	/**
	 * Send error report to support email.
	 * @param errorDetails error report details
	 */
	private void sendErrorReportToEmail(ErrorReportDetails errorDetails) {
		try {
			String msg = "";			
			msg += "<b>Tapahtui virhe WFS-latauksessa</b><br/><br/>WFS-url:<br/>" + errorDetails.getWfsUrl() + "<br/><br/>"+
					"WFS-pyyntö ja GeoServerin vastaus liitetiedostona.<br/><br/><b>HUOM!</b> Tämä on järjestelmän generoima virheilmoitus, älä vastaa tähän viestiin.";			

			
			//Using Multipart because HtmlEmail doesn't handle attachments very well.
			MultiPartEmail email = new MultiPartEmail();
			MimeBodyPart messageBodyPart = new MimeBodyPart();  
			messageBodyPart.setContent(msg, "text/html; charset=UTF-8");
			MimeMultipart multipart = new MimeMultipart();  
			multipart.addBodyPart(messageBodyPart);
			
			byte[] bytes = errorDetails.getXmlRequest().getBytes();            

            DataSource dataSource = new ByteArrayDataSource(bytes, "application/xml");
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setDataHandler(new DataHandler(dataSource));
            bodyPart.setFileName("wfs_request.xml");
            multipart.addBodyPart(bodyPart);
            
            if(errorDetails.getErrorFileLocation() != null){
            	  DataSource source = new FileDataSource(errorDetails.getErrorFileLocation());
            	  MimeBodyPart part = new MimeBodyPart();
            	  part.setDataHandler(new DataHandler(source));
            	  part.setFileName("geoserver_wfs_response.xml");
                  multipart.addBodyPart(part);
            } 
            
			email.setSmtpPort(errorDetails.getSmtpPort());
			email.setCharset("UTF-8");
					   
			email.setContent(multipart);  
			email.setHostName(errorDetails.getHostname());
			email.setFrom(errorDetails.getEmailfrom());
			email.setSubject(errorDetails.getSubject());
			email.addTo(errorDetails.getSendToEmail());
			email.send();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Send error report to user email.
	 * @param errorDetails error report details
	 */
	private void sendErrorReportToUserEmail(ErrorReportDetails errorDetails) {
		try {
			String msg = "";
			String topic = "";

			if("en".equals(errorDetails.getLanguage())) {
				topic = properties.getProperty("livi.email.error.user.topic.en");
				msg = "<b>"+properties.getProperty("livi.email.error.user.topic.en")+"</b><br/><br/>" +
						properties.getProperty("livi.email.error.user.message.en") + "<br/><br/>" + properties.getProperty("livi.email.error.user.automatic.en");
			}else if("sv".equals(errorDetails.getLanguage())) {
				topic = properties.getProperty("livi.email.error.user.topic.sv");
				msg = "<b>"+properties.getProperty("livi.email.error.user.topic.sv")+"</b><br/><br/>" +
						properties.getProperty("livi.email.error.user.message.sv") + "<br/><br/>" + properties.getProperty("livi.email.error.user.automatic.sv");
			}else{
				topic = properties.getProperty("livi.email.error.user.topic.fi");
				msg = "<b>"+properties.getProperty("livi.email.error.user.topic.fi")+"</b><br/><br/>" +
						properties.getProperty("livi.email.error.user.message.fi") + "<br/><br/>" + properties.getProperty("livi.email.error.user.automatic.fi");
			}
			
			//Using Multipart because HtmlEmail doesn't handle attachments very well.
			MultiPartEmail email = new MultiPartEmail();
			MimeBodyPart messageBodyPart = new MimeBodyPart();  
			messageBodyPart.setContent(msg, "text/html; charset=UTF-8");
			MimeMultipart multipart = new MimeMultipart();  
			multipart.addBodyPart(messageBodyPart);
			
			byte[] bytes = errorDetails.getXmlRequest().getBytes();            

            DataSource dataSource = new ByteArrayDataSource(bytes, "application/xml");
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setDataHandler(new DataHandler(dataSource));
            
			email.setSmtpPort(errorDetails.getSmtpPort());
			email.setCharset("UTF-8");
					   
			email.setContent(multipart);  
			email.setHostName(errorDetails.getHostname());
			email.setFrom(errorDetails.getEmailfrom());
			email.setSubject(topic);
			email.addTo(errorDetails.getUserEmail());
			email.send();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
}