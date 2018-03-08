import java.util.HashMap;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;


public class ProjectsMailer {
	
	private final String FROM = "cmehta@yodlee.com";
	private final String TO_List ="IAE-Projects@yodlee.com";
	private final String CC_List = "tgarg@yodlee.com,manjunathg@yodlee.com,SBansal@yodlee.com,AMalur@yodlee.com";
	
	private final static String SUBJECT = "PC and BAC stats for Document Download( Automated Stats ) - Projects Team";
	
	private Session getSessionInstance(Properties props) {
		
		final String username = "";
		final String password = "";
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		return session;
	}
	
	public void sendMail(HashMap<String, double[]> mailData) { 
		
		/*Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "outlook.office365.com");
		props.put("mail.smtp.port", "587");
		Session session = this.getSessionInstance(props);*/
		
		Properties props = System.getProperties();
        props.put("mail.smtp.host","192.168.211.175");
        Session session = Session.getInstance(props, null);
		
		try {
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(FROM));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(TO_List));
			message.addRecipients(Message.RecipientType.CC,InternetAddress.parse(CC_List));
			message.setSubject(SUBJECT);
			//message.setText("Sorry for SPAM !");
			
			BodyPart messageBodyPart = new MimeBodyPart();
//			String bodyText = "Hi";
//			messageBodyPart.setText(bodyText);
			String bodyText = this.getMessageBody(mailData);
			messageBodyPart.setContent(bodyText, "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			messageBodyPart = new MimeBodyPart();
			String filename = ExcelHelper.getFilePath() + "\\Document Download Stats.xlsx";
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename.substring(filename.lastIndexOf("\\")));
			multipart.addBodyPart(messageBodyPart);
			
			message.setContent(multipart);
			
			Transport.send(message);
			
			System.out.println("Done");
			//JOptionPane.showMessageDialog(null, "Mail has been sent", null, JOptionPane.INFORMATION_MESSAGE);
			//JOptionPane.showMessageDialog(null, "Mail has been sent");
			
			
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getMessageBody(HashMap<String, double[]> mailData) {
		String bodyText;
		
		bodyText = "Hi Team,<br font='Calibri'>";
		
		bodyText += "<br font='Calibri'> Please find last 1 day doc Download stats for PC and BAC in the attached sheet.<br></br>";
		
		
		bodyText += "<table cellpadding='0.5' cellspacing='0.5' border='1' bordercolor='black' width='80%' align='center' font='Calibri'> ";
		bodyText += "<tr bgcolor = '#66CD00' fontsize = '90%' font='Calibri'><td>&nbsp; COBRAND &nbsp;</td><td>&nbsp; TOTAL_REQUEST &nbsp;</td><td>&nbsp; SUCCESS% &nbsp;</td><td>&nbsp; (SUCCESS + 570 + PARTIAL SUCCESS)% &nbsp;</td><td>&nbsp; PARTIAL SUCCESS% &nbsp;</td><td>&nbsp; 570% &nbsp;</td><td>&nbsp; AGENT_ERROR% &nbsp;</td><td>&nbsp; SITE_ERROR% &nbsp;</td><td>&nbsp; UAR_ERROR% &nbsp;</td></tr>";
		
		String tr = "<br>"; String bgColor="grey";
		for(String key : mailData.keySet()){
			
			//comment
			/*Object[] objArr = mailData.get(key);
			if((Integer) objArr[objArr.length-1] == (Integer) 0){
				bgColor = "#FF0000";
				alertFlag = true;
			} else {
				bgColor = "#00FFFFFF";//transparent
			}
			tr += "<tr bgcolor='"+bgColor+"'>";
			for(Object obj : objArr){
				if(obj instanceof Double)
					tr += "<td>"+ExcelHelper.round((Double)obj, 2)+"</td>";
				else
					tr += "<td>"+obj.toString()+"</td>";
			}
			tr += "</tr>";*/
			//comment
			
			double[] totalStats=mailData.get(key);
			tr += "<tr bgcolor='"+bgColor+"' font='Calibri'>";	
			tr += "<td>"+key+"</td>";	
			if(totalStats!=null && totalStats.length!=0){
				double total_success=totalStats[1]+totalStats[2]+totalStats[3];	
				total_success=ExcelHelper.round((total_success/totalStats[0])*100, 2);
				for(int i=0;i<totalStats.length;i++){
					if(i==0)
						tr += "<td>"+new Double(totalStats[i]).intValue()+"</td>";
					else{
						double val=(((Double)totalStats[i])/totalStats[0])*100;
						System.out.println("val::"+val);
						tr += "<td>"+ExcelHelper.round(val, 2)+"</td>";
					}
					if(i==1){
						tr += "<td>"+ExcelHelper.round((Double)total_success, 2)+"</td>";
					}
				}
			}else{
				for(int i=0;i<9;i++){
					tr += "<td>"+0+"</td>";
				}
			}
			bgColor="white";
		}
		tr += "</tr>";
		bodyText += tr;
		bodyText += "</table>";
		bodyText += "<br font='Calibri'>This is an automated stats taken from siteP.It might have some discrepancies against production stats.";
		bodyText += "<br><br font='Calibri' >Note:"
				+ "<br font='Calibri' >1) Success include error code 0 and success with 570."
				+ "<br font='Calibri'>2) Partial Success includes success with 811."
				+ "<br><br>Please reach out to cmehta for any concerns.";
		bodyText += "<br><br font='Calibri'> On Behalf Of,<br>Projects Team<br>";
		
		//commnet
		/*if(tr.equals("<br>"))
			bodyText += "<tr><td colspan='9' align='left' color='#FF0000'>&emsp;&emsp;&emsp;&emsp;No Failing Refresh(s) Data Found for input Agent(s)</td></tr>";
		else
			bodyText += tr;
		
		bodyText += "</table>";
		
		bodyText += "<br>";
		
		String p = "<p style='text-align:left; color:red; font-size:110%;'>";
		if(alertFlag)
			p += "<b>IAV Flag disabled for some of the agents.</b><br>Please Verify the attached Data.";
		else
			p += "All agent(s) have IAV flag ENABLED.";
		p += "</p>";
		
		bodyText += p;
		
		bodyText += "<br><br> On Behalf Of,<br><br/>Projects Team<br>Manager: Manjunath G";
		bodyText += "";*/
		//comment
		
		
		return bodyText;
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//new ProjectsMailer().sendMail(null, false);
	}

}
