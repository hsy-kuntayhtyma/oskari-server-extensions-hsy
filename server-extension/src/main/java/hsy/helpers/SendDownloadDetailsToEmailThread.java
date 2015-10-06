package hsy.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBException;

import org.apache.commons.mail.HtmlEmail;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.mail.smtp.SMTPTransport;

/**
 * Send download details email service (thread).
 * @author Marko Kuosmanen
 * @copyright Dimenteq Oy
 *
 */
public class SendDownloadDetailsToEmailThread extends Thread{
	JSONArray downLoadDetails;
	JSONObject userDetails;
	String language = "fi";
	
	/**
	 * Constructor.
	 * @param downLoadDetails download details
	 * @param userDetails user details
	 */
	public SendDownloadDetailsToEmailThread(JSONArray downLoadDetails, JSONObject userDetails){
		this.downLoadDetails = downLoadDetails;
		this.userDetails = userDetails;
	}
	
	/**
	 * 
	 * Overrides the run method.
	 * Collects the download materials and sends them using the variables given in constructor.
	 * 
	 */
    @Override
    public void run() {
    	ArrayList<String> digiroadFiles = new ArrayList<String>();
    	
    	//Stopwatch timer = Stopwatch.createStarted();
    	
    	boolean isOtherThanDigiroadAlso = false;
    	
    	WFSLayers wfsDd = (WFSLayers)httpsession.getAttribute("livi_map_view_download_wfs_details");
    	
    	try {
    		LoggerService.LiviLogger logger = new LoggerService.LiviLogger();
    		Utilities utils = new Utilities();
			OGCServices ogcs = new OGCServices();
			DownloadServices ds = new DownloadServices(0, properties);			
			ArrayList<ZipDownloadDetails> mergeThese = new ArrayList<ZipDownloadDetails>();
			String strTempDir = properties.getProperty("livi.download.folder.name");
			String rajoitettuTunnus = properties.getProperty("mapdata.dowload.service.default.username");
			String rajoitettuSalasana = properties.getProperty("mapdata.dowload.service.default.password");
			
			int smtpPort = Integer.parseInt(properties.getProperty("livi.email.smtp.port"));
			String hostname = properties.getProperty("livi.email.smtp.hostname");
			String emailfrom = properties.getProperty("livi.email.from");
			String errorReportSubject = properties.getProperty("error.report.subject");			
			String sendErrorReportEmail = properties.getProperty("send.error.report.to");
			
			String normalWayDownload = properties.getProperty("download.normal.way.this.croppings");
			String[] temp = normalWayDownload.split(",");
			NormalWayDownloads normalDownloads = new NormalWayDownloads();
			for (int i = 0; i < temp.length; i++) {
				normalDownloads.addDownload(temp[i]);
			}
			
			String openLicenceFile = properties.getProperty("mapdata.open.data.licence.file");
			if(openLicenceFile==null){
				openLicenceFile = "./LICENCE.txt";
			}
			String limitedLicenceFile = properties.getProperty("mapdata.limited.data.licence.file");
			if(limitedLicenceFile==null){
				limitedLicenceFile = "./HYDROGRAPHIC_LICENCE.txt";
			}
			
			// TODO estä käyttäjän samanlaisten latausten käyttö?
			
			for(int i=0;i<downLoadDetails.size();i++){
				//Boolean isMultipleSessionLoad = false;
				DownloadDetail dd = downLoadDetails.get(i);
								
				String property = null;
				
				String fileLocation = "";								
				if (!dd.getCroppingMode().equals("digiroad")) {
					String wfsUrl = wfsDd.getWfsUrl(MapHelpers.getLayerNameWithoutNameSpace(dd.getLayer()), dd.getTransport());
					LoadZipDetails ldz = new LoadZipDetails();
					ldz.setContext(context);
					ldz.setEmailFrom(emailfrom);
					ldz.setErrorReportSubject(errorReportSubject);
					ldz.setErrorReportToEmail(sendErrorReportEmail);
					
					ldz.setHostname(hostname);
					ldz.setLimitedAccessPassword(rajoitettuSalasana);
					ldz.setLimitedAccesUsername(rajoitettuTunnus);
					ldz.setSendErrorReportEmail(sendErrorReportEmail);
					ldz.setSMTPPort(smtpPort);
					ldz.setTemporaryDirectory(strTempDir);
					ldz.setUserEmail(userDetails.getString("email"));
					ldz.setLanguage(this.language);
					
					
					// Tarkistetaan lataus, ladataanko normaalisti vai pluginin kautta
					ldz.setDownloadNormalWay(normalDownloads.isNormalWayDownload(dd.getCroppingMode(), dd.getCroppingLayer()));
					
						
					if(ldz.isDownloadNormalWay()) {
						ldz.setGetFeatureInfoRequest(ogcs.getFilter(dd, session, true));
						ldz.setWFSUrl(ogcs.doGetFeatureUrl_1_1_0(wfsUrl, dd, session, false));
					} else {
						ldz.setGetFeatureInfoRequest(ogcs.getPluginFilter(dd, true, true));
						ldz.setWFSUrl(ogcs.doGetFeatureUrl_1_1_0(properties.getProperty("mapdata.viewing.service.transport.wfs"), dd, session, true));
					}
					
					/************************************************************
					 * Load ZIP
					 ************************************************************/
					fileLocation = ds.loadZip(ldz);
				} else {
					fileLocation = "digiroad";
				}								
				
				//LOGITUS POISTETTU
				
		        if(fileLocation!=null) {		        
					ZipDownloadDetails zdd = new ZipDownloadDetails();
					zdd.setFileName(fileLocation);
					String sLayer = dd.getLayer();
					
					sLayer = MapHelpers.getLayerNameWithoutNameSpace(sLayer);
					
					zdd.setLimited(dd.getIsSpecialConditions());
					zdd.setLayerName(sLayer);
					mergeThese.add(zdd);
		        }
			}		
			
			ZipOutputStream out = null;
			String strZipFileName = UUID.randomUUID().toString() + ".zip";
			try {							
				File f = new File(strTempDir);
				f.mkdirs();
				out = new ZipOutputStream(new FileOutputStream(strTempDir + "/" + strZipFileName));
				
				Hashtable<String, Integer> indeksit = new Hashtable<String, Integer>(); 
				byte[] buffer = new byte[1024];
				
				for(int i=0;i<mergeThese.size();i++){
					ZipInputStream in = null;
					
					try{						
						ZipDownloadDetails zdd = mergeThese.get(i);
						
						// Tavalliset lataukset (ei digiroad)
						if(!zdd.getFileName().equals("digiroad")){
							isOtherThanDigiroadAlso = true;							
							String strTempFile = zdd.getFileName();
							
							Integer index = indeksit.get(zdd.getLayerName());
							if(index==null){
								index = 0;								
							} else {
								index++;
								indeksit.remove(zdd.getLayerName());
							}
							
							indeksit.put(zdd.getLayerName(), index);
							
							String folderName = zdd.getLayerName() + "_"+index+"/";
							out.putNextEntry(new ZipEntry(folderName));
							
							in = new ZipInputStream(new FileInputStream(strTempFile));
							ZipEntry ze = in.getNextEntry();
							while(ze!=null){
								String fileName = ze.getName();             
								out.putNextEntry(new ZipEntry(folderName+fileName));
					            int len;
					            while ((len = in.read(buffer)) > 0) {
					            	out.write(buffer, 0, len);
					            }
					            ze = in.getNextEntry();
							}
							
							try {
								// ADD LICENCE FILE					
								// Limited licence
					            if(zdd.isLimited()){
					            	out.putNextEntry(new ZipEntry(folderName + "HYDROGRAPHIC_LICENCE.txt"));
					            	File limitedFile = new File(limitedLicenceFile);
					        		FileInputStream fis = new FileInputStream(limitedFile);
					        		byte[] bytes = new byte[1024];
					        		int length;
					        		while ((length = fis.read(bytes)) >= 0) {
					        			out.write(bytes, 0, length);
					        		}
					        		fis.close();
					            } 
					            // Open licence
					            else {					           
						            out.putNextEntry(new ZipEntry(folderName + "LICENCE.txt"));
						            File openFile = new File(openLicenceFile);
					        		FileInputStream fis = new FileInputStream(openFile);
					        		byte[] bytes = new byte[1024];
					        		int length;
					        		while ((length = fis.read(bytes)) >= 0) {
					        			out.write(bytes, 0, length);
					        		}
					        		fis.close();
					            }
							} catch(Exception e) {
								logger.error("Licence file not found");
							}
				            
							out.closeEntry();
							in.close();
							deleteFile(strTempFile);
						} 
						// digiroad zipit
						else {
							String digiroadKansio = properties.getProperty("digiroad.aineistot.kansio");
							if(zdd.getLayerName().equals("full-finland")){
								String[] kokoSuomi = fullFinlandFiles;
								for (String koko : kokoSuomi) {
									if(!digiroadFiles.contains(koko)){
										digiroadFiles.add(koko);
									}
								}
							} else {
								final File folder = new File(digiroadKansio);
								
								ArrayList<String> files = getDigiroadFilesLoc(folder,zdd.getLayerName());
								for (String file : files) {							
									if(!digiroadFiles.contains(file)){
										digiroadFiles.add(file);
									}
								}
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally{
						if(in!=null) in.close();
					}
				}
				
				
			} catch(FileNotFoundException fe){
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			finally {
				if (out != null) {
					try {
						out.close();
					}
					catch (Exception ex) {}
				}
			}
			//System.out.println("Download took: " + timer.stop());
			sendZipFile(strZipFileName, digiroadFiles,isOtherThanDigiroadAlso);
    	}
    	catch (Exception ex) {
			ex.printStackTrace();
    	}
    }
    
    /**
	 * 
	 * Sends the zip file to current user's email address.
	 * @param strZipFileName zip file name
     * @param digiroadFiles digiroad files
     * @param isOtherThanDigiroadAlso  is there also other data than digiroad
	 * @param intUserId user id
	 */
	public void sendZipFile(String strZipFileName, ArrayList<String> digiroadFiles, boolean isOtherThanDigiroadAlso) {

		try {
			HtmlEmail email = new HtmlEmail();
			
			int smtpPort = Integer.parseInt(properties.getProperty("livi.email.smtp.port"));
			email.setSmtpPort(smtpPort);
			email.setHostName(properties.getProperty("livi.email.smtp.hostname"));
			email.setFrom(properties.getProperty("livi.email.from"));
			if("en".equals(this.language)) {
				email.setSubject(properties.getProperty("livi.email.subject.en"));
			} else if("sv".equals(this.language)) {
				email.setSubject(properties.getProperty("livi.email.subject.sv"));
			} else {
				email.setSubject(properties.getProperty("livi.email.subject"));
			}			
			email.setCharset("UTF-8");			
			
			StringBuilder htmlHeader = new StringBuilder();
			StringBuilder htmlMsg = new StringBuilder();
			StringBuilder htmlFooter = new StringBuilder();
			
			StringBuilder txtHeader = new StringBuilder();
			StringBuilder txtMsg = new StringBuilder();
			StringBuilder txtFooter = new StringBuilder();
			
			
			
			if("en".equals(this.language)) {
				htmlHeader.append(properties.getProperty("livi.email.header.en"));
				txtHeader.append(properties.getProperty("livi.email.header.en"));
			} else if("sv".equals(this.language)) {
				htmlHeader.append(properties.getProperty("livi.email.header.sv"));
				txtHeader.append(properties.getProperty("livi.email.header.sv"));
			} else {
				htmlHeader.append(properties.getProperty("livi.email.header"));
				txtHeader.append(properties.getProperty("livi.email.header"));
			}
			
			htmlHeader.append("<br/><br/>");
			txtHeader.append("\n\n");
			
			
			if(isOtherThanDigiroadAlso){
				
				if("en".equals(this.language)) {
					htmlMsg.append(properties.getProperty("livi.email.message.en"));
					txtMsg.append(properties.getProperty("livi.email.message.en"));
				} else if("sv".equals(this.language)) {
					htmlMsg.append(properties.getProperty("livi.email.message.sv"));
					txtMsg.append(properties.getProperty("livi.email.message.sv"));
				} else {
					htmlMsg.append(properties.getProperty("livi.email.message"));
					txtMsg.append(properties.getProperty("livi.email.message"));
				}
				htmlMsg.append("<br/>");
				txtMsg.append("\n");
				String url = properties.getProperty("livi.download.link.url.prefix")+strZipFileName;
				htmlMsg.append("<a href=\"" + url +"\">"+url+"</a>");
				txtMsg.append(url);
				if(digiroadFiles.size()>0){
					htmlMsg.append("<br/><br/>");
					txtMsg.append("\n\n");
				}
			}
			
			if(digiroadFiles.size()>0){
				if(digiroadFiles.size() == 1){
					if("en".equals(this.language)) {
						htmlMsg.append(properties.getProperty("livi.email.message.digiroad_one.en"));
						txtMsg.append(properties.getProperty("livi.email.message.digiroad_one.en"));
					} else if("sv".equals(this.language)) {
						htmlMsg.append(properties.getProperty("livi.email.message.digiroad_one.sv"));
						txtMsg.append(properties.getProperty("livi.email.message.digiroad_one.sv"));
					} else {
						htmlMsg.append(properties.getProperty("livi.email.message.digiroad_one"));
						txtMsg.append(properties.getProperty("livi.email.message.digiroad_one"));
					}
					htmlMsg.append("<br/>");
					txtMsg.append("\n");
				} else {
					if("en".equals(this.language)) {
						htmlMsg.append(properties.getProperty("livi.email.message.digiroad_multi.en"));
						txtMsg.append(properties.getProperty("livi.email.message.digiroad_multi.en"));
					} else if("sv".equals(this.language)) {
						htmlMsg.append(properties.getProperty("livi.email.message.digiroad_multi.sv"));
						txtMsg.append(properties.getProperty("livi.email.message.digiroad_multi.sv"));
					} else {
						htmlMsg.append(properties.getProperty("livi.email.message.digiroad_multi"));
						txtMsg.append(properties.getProperty("livi.email.message.digiroad_multi"));
					}
					htmlMsg.append("<br/>");
					txtMsg.append("\n");
				}
				
				for (String fileName : digiroadFiles) {
					htmlMsg.append(properties.getProperty("livi.download.link.url.prefix.digiroad")+fileName);
					txtMsg.append(properties.getProperty("livi.download.link.url.prefix.digiroad")+fileName);
					txtMsg.append("\n");
					htmlMsg.append("<br/>");
				}
			}
			
			
			if("en".equals(this.language)) {
				htmlFooter.append("<br/><br/>");
				txtFooter.append("\n\n");
				String f = properties.containsKey("livi.email.footer.en") ? properties.getProperty("livi.email.footer.en") : "";
				String ff = f.replaceAll("\\{RIVINVAIHTO\\}", "\n");
				f = f.replaceAll("\\{RIVINVAIHTO\\}", "<br/>");
				htmlFooter.append(f);
				txtFooter.append(ff);
				String d = properties.containsKey("livi.email.message.datadescription.en") ? properties.getProperty("livi.email.message.datadescription.en") : "";
				String dd =  d.replaceAll("\\{RIVINVAIHTO\\}", "\n");
				d = d.replaceAll("\\{RIVINVAIHTO\\}", "<br/>");
				htmlFooter.append(d);
				txtFooter.append(dd);
				htmlFooter.append(properties.containsKey("livi.email.datadescription_link.en") ? properties.getProperty("livi.email.datadescription_link.en") : "");
				txtFooter.append(properties.containsKey("livi.email.datadescription_link.en") ? properties.getProperty("livi.email.datadescription_link.en") : "");
			} else if("sv".equals(this.language)) {
				htmlFooter.append("<br/><br/>");
				txtFooter.append("\n\n");
				String f = properties.containsKey("livi.email.footer.sv") ? properties.getProperty("livi.email.footer.sv") : "";
				String ff = f.replaceAll("\\{RIVINVAIHTO\\}", "\n");
				f = f.replaceAll("\\{RIVINVAIHTO\\}", "<br/>");
				htmlFooter.append(f);
				txtFooter.append(ff);
				String d = properties.containsKey("livi.email.message.datadescription.sv") ? properties.getProperty("livi.email.message.datadescription.sv") : "";
				String dd =  d.replaceAll("\\{RIVINVAIHTO\\}", "\n");
				d = d.replaceAll("\\{RIVINVAIHTO\\}", "<br/>");
				htmlFooter.append(d);
				txtFooter.append(dd);
				htmlFooter.append(properties.containsKey("livi.email.datadescription_link.sv") ? properties.getProperty("livi.email.datadescription_link.sv") : "");
				txtFooter.append(properties.containsKey("livi.email.datadescription_link.en") ? properties.getProperty("livi.email.datadescription_link.en") : "");
			} else {
				htmlFooter.append("<br/><br/>");
				txtFooter.append("\n\n");
				String f = properties.containsKey("livi.email.footer") ? properties.getProperty("livi.email.footer") : "";
				String ff = f.replaceAll("\\{RIVINVAIHTO\\}", "\n");
				f = f.replaceAll("\\{RIVINVAIHTO\\}", "<br/>");
				htmlFooter.append(f);
				txtFooter.append(ff);
				String d = properties.containsKey("livi.email.message.datadescription") ? properties.getProperty("livi.email.message.datadescription") : "";
				String dd =  d.replaceAll("\\{RIVINVAIHTO\\}", "\n");
				d = d.replaceAll("\\{RIVINVAIHTO\\}", "<br/>");
				htmlFooter.append(d);
				txtFooter.append(dd);	
				htmlFooter.append(properties.containsKey("livi.email.datadescription_link") ? properties.getProperty("livi.email.datadescription_link") : "");
				txtFooter.append(properties.containsKey("livi.email.datadescription_link.en") ? properties.getProperty("livi.email.datadescription_link.en") : "");
			}
			if(digiroadFiles.size()>0){
				if("en".equals(this.language)) {
					String f = properties.containsKey("livi.email.message.datadescription_digiroad.en") ? properties.getProperty("livi.email.message.datadescription_digiroad.en") : "";
					String ff = f.replaceAll("\\{RIVINVAIHTO\\}", "\n");
					f = f.replaceAll("\\{RIVINVAIHTO\\}", "<br/>");
					htmlFooter.append(f);
					txtFooter.append(ff);
				} else if("sv".equals(this.language)) {
					String f = properties.containsKey("livi.email.message.datadescription_digiroad.sv") ? properties.getProperty("livi.email.message.datadescription_digiroad.sv") : "";
					String ff = f.replaceAll("\\{RIVINVAIHTO\\}", "\n");
					f = f.replaceAll("\\{RIVINVAIHTO\\}", "<br/>");
					htmlFooter.append(f);
					txtFooter.append(ff);
				} else {
					String f = properties.containsKey("livi.email.message.datadescription_digiroad") ? properties.getProperty("livi.email.message.datadescription_digiroad") : "";
					String ff = f.replaceAll("\\{RIVINVAIHTO\\}", "\n");
					f = f.replaceAll("\\{RIVINVAIHTO\\}", "<br/>");
					htmlFooter.append(f);
					txtFooter.append(ff);
				}
			}
			String htmlFullMessage = "<html>" + htmlHeader.toString() 
					+ htmlMsg.toString() 
					+ htmlFooter.toString() 
			+ "</html>";
			
			String txtFullMessage = txtHeader.toString() 
					+ txtMsg.toString() 
					+ txtFooter.toString();
			
			email.setHtmlMsg(htmlFullMessage);
			email.setTextMsg(txtFullMessage);			
			email.addTo(userDetails.getString("email"));			
			email.send();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
	/**
	 * Delete temp files.
	 * @param strFilePath temp path
	 */
    private void deleteFile(String strFilePath) {

		File f = new File(strFilePath);
		if (f.exists() && f.canWrite()) {
			f.delete();
		}
	}
	
    /**
     * Get digiroad files.
     * @param folder digiroad folder path
     * @param maakunta maakunta name
     * @return founded digiroad files
     */
	public String getDigiroadFiles(final File folder, String maakunta) {
		StringBuilder sb = new StringBuilder();
		maakunta = maakunta.replace(" ", "");
		String encodingOverChar = properties.getProperty("digiroad.filename.over.encoding.char");
		
		String[] kansiot = digiroadFolders;
		  for (String kansio : kansiot) {
	        final File folder1 = new File(kansio);
        	for (final File fileEntry : folder1.listFiles()) {
		        if (!fileEntry.isDirectory()){        	
		            String fileName = fileEntry.getName();
		            
		            String maakunta2 = maakunta;
		            
		            boolean isSpecial = false;
		            if(fileName.contains(encodingOverChar)){
		            	maakunta2 = Utilities.ReplaceWithSpecialChars(maakunta);
		            	isSpecial = true;
		            }

		            if(fileName.equalsIgnoreCase(maakunta2+".zip")){
		              	if(isSpecial){
	            			sb.append(maakunta.toUpperCase() + ",");
		            	} else {
		            		sb.append(fileName + ",");
		            	}
		            	
		            }
		        }
        	}
	    }

	    String files = sb.toString();
	    if(files.length()>1){
	    	files = files.substring(0,files.length()-1);
	    }
	    return files;
	}
	
	/**
	 * Get arraylist of selected digiroad files.
	 * @param folder digiroad folder path
	 * @param maakunta maakunta name
	 * @return founded files in ArrayList
	 */
	public ArrayList<String> getDigiroadFilesLoc(final File folder, String maakunta) {
		ArrayList<String> files = new ArrayList<String>();
		String encodingOverChar = properties.getProperty("digiroad.filename.over.encoding.char");
		maakunta = maakunta.replace(" ", "");
		String[] kansiot = digiroadFolders;
        for (String kansio : kansiot) {
        	final File folder1 = new File(kansio);
        	for (final File fileEntry : folder1.listFiles()) {
		        if (!fileEntry.isDirectory()){        	
		            String fileName = fileEntry.getName();
		            
		            String maakunta2 = maakunta;
		            boolean isSpecial = false;
		            if(fileName.contains(encodingOverChar)){
		            	maakunta2 = Utilities.ReplaceWithSpecialChars(maakunta);
		            	isSpecial = true;
		            }
		            
		            if(fileName.equalsIgnoreCase(maakunta2+".zip")){
		            	String fullFileName = fileEntry.getName();
		            	
		            	if(isSpecial){
		            		if(fileName.equalsIgnoreCase(maakunta2+".zip")){
		            			fullFileName = maakunta.toUpperCase()+".zip";
		            		}
		            	}	            	
		            			            	
		            	if(kansio.contains("_K")){
		            		fullFileName = folder1.getName()+"/"+fullFileName;
		            	}else if(kansio.contains("_R")){
		            		fullFileName = folder1.getName()+"/"+fullFileName;
		            	}
		            	
		            	if(!files.contains(fullFileName)){
		            		files.add(fullFileName);
		            	}
		            }
		        }
	    }
	}
	    return files;
	}
}
