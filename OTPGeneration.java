package Email;

import Database.DatabaseConstants;
import Database.Hashing;
import GUI.AlertMessage;
import com.sun.mail.smtp.SMTPMessage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

public class OTPGeneration implements Email, DatabaseConstants {

    private static int[] OTPList = {168715, 145784, 125364, 474145, 414156, 741459, 142512};

    private String to;
    private Properties properties;
    private Session session;

    public OTPGeneration(String to, int otp) {
        this.to = to;

       properties = new Properties();
       properties.put("mail.smtp.host", "smtp.gmail.com");
       properties.put("mail.smtp.socketFactory.port", "465");
       properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
       properties.put("mail.smtp.auth", "true");;
       properties.put("mail.smtp.port", "805");

       session = Session.getDefaultInstance(properties, new Authenticator() {
           @Override
           protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(from, Email.password);
           }
       });

       try {
           SMTPMessage message = new SMTPMessage(session);
           message.setFrom(new InternetAddress(from));
           message.setRecipients(Message.RecipientType.TO, InternetAddress.parse( to ));
           message.setSubject("Password Recovery");
           message.setText("It seems that you have forgotten your password.\nYou can reset it by using the OTP: " + otp);
           message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
           Transport.send(message);
           System.out.println("Email Sent");
       } catch (MessagingException ex) {
           AlertMessage.getAlert("Error. Cannot send Email. Please try again after sometime.");
           throw new RuntimeException("Email not sent.");
       }
    }

    public static void getOTP(String username) throws IllegalArgumentException {
        String email = getEmail(username);
        if(email == null)
            throw new IllegalArgumentException("Invalid username");
        new OTPGeneration(email, OTPList[new Random().nextInt(8)]);
    }

    public static boolean validate(int otp) {
        for(int i = 0; i < OTPList.length; i++)
            if(OTPList[i] == otp)
                return true;
        return false;
    }

    private static String getEmail(String username) {
        try {
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, user, DatabaseConstants.password);
            Statement statement = connection.createStatement();

            statement.executeUpdate("use mysql");
            statement.executeUpdate("use javabook");
            ResultSet set = statement.executeQuery("select email from users where username = '" + username + "'");

            if(set == null) {
                return null;
            } else {
               set.next();
               return set.getString("email");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            AlertMessage.getAlert("Error. Cannot connect to the SQL database.");
        }
        return null;
    }

    public static void updatePassword(String username, String password) {
        try {
            String hashVal = Hashing.getHash(password);

            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, user, DatabaseConstants.password);
            Statement statement = connection.createStatement();

            statement.executeUpdate("use mysql");
            statement.executeUpdate("use javabook");
            statement.executeUpdate("update users set password = '" + hashVal + "'" + " where username = '" + username + "'");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException | ClassNotFoundException e) {
            AlertMessage.getAlert("Error. Cannot send E-mail. Please try again later.");
        }
    }

}
