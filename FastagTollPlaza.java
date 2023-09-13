package com.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.time.Duration;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import java.util.Properties;


class SendMail{

    // In this method if some error occurs while sending mail then the program will not terminate
    // It will just print the error message and the program will continue
    // As if we are sending transaction details to the vehicle owner and if the mail is not sent due to some reasons
    // then the the TollPlaza should not terminate it should only display the error message and then continue with the next vehicle
    public boolean sendEmail(String to, String from, String subject, String text) 
    {
        boolean flag = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        final String username = "hardik23555";
        final String password = "wnetpfvovgnsmqjw";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try 
        {
            jakarta.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            flag = true;
            System.out.println("Mail sent successfully");
        } 
            catch (Exception e) 
        {
            System.err.println("An error occurred: " + e.getMessage());

        }

        return flag;
    }

    // only difference is _ in the method name
    // and if their is an error in sending mail then the program will terminate
    // as if we are sending OTP and Email ID is invalid then the program should terminate
    public boolean sendEmail_(String to, String from, String subject, String text) 
    {
        boolean flag = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        final String username = "hardik23555";
        final String password = "wnetpfvovgnsmqjw";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try 
        {
            jakarta.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            flag = true;
            System.out.println("OTP sent successfully! ");
        } 
            catch (Exception e) 
        {
            System.err.println("An error occurred: " + e.getMessage());
            System.exit(-1);

        }

        return flag;
    }


}

public class FastagTollPlaza 
{  
    // These are kept private as they are highly sensitive information
    // They are kept final as they are not supposed to be changed
    // These are used to encrypt and decrypt the password
    // static is used so that they can be accessed in the method without creating an object of the class
    private static final String ALGORITHM = "AES";
    private static final String keyT = "HkZGE9WPDeX2Tr5Q";
    private static final String keyV = "HqZPE3WPDeXrTr5s";

    // this encrypt method is used to encrypt the password entered by the user
    // the password entered by the user is encrypted using the AES algorithm
    // the encrypted password is returned by the encrypt method
    // the encrypted password is stored in the database
    // the encrypted password is compared with the password stored in the database
    // if the encrypted password matches with the password stored in the database then the password entered by the user is correct
    // so the user will be allowed to login
    public static byte[] encrypt(String input) throws Exception 
    {
        SecretKeySpec keySpec = new SecretKeySpec(keyT.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input.getBytes());
    }

    public static String decrypt(byte[] encrypted) throws Exception 
    {
        SecretKeySpec keySpec = new SecretKeySpec(keyV.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(encrypted);
        return new String(decryptedBytes);
    }

    public static void main(String[] args) 
    {
        System.out.println("Enter you toll plaza id: ");
        Scanner sc = new Scanner(System.in);
        // we need to initialize tollID with -1 here as we are taking input in the try block so without initializing it here system will throw compile time error
        // as then the scope of tollID will be limited to the try block only
        int tollID = -1;
        // this try catch block is used to give proper feedback to the user if the user enters invalid toll plaza id
        // without this try catch block the program will throw error if the user enters invalid toll plaza id
        try
        {
            tollID = sc.nextInt();
        }
        catch(Exception e)
        {
            System.out.println("Invalid Toll Plaza ID");
            sc.close();
            System.exit(-1);
        }
        System.out.println("Enter high security password(To reset password Enter 0 ): ");
        String passEntered = sc.next();

        // this flag variable is used to count the number of wrong attempts of captcha
        // this logout variable is used to logout from the program if user wish to logout and enters 0
        int flag=0;
        int logout =1;

        // this infinite loop is used to generate captcha and ask the user to enter the captcha
        // if the captcha is entered correctly then the user will be asked to enter the Vehicle Number
        // if the captcha is entered incorrectly then the user will be asked to enter the captcha again
        // if the number of wrong attempts is 5 then the program will terminate
        while(true)
        {
            // this is the string which contains all the characters which are used to generate captcha
            String s = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
            // this lenCaptcha variable is used to store the length of the captcha
            int lenCaptcha = 4;
            // this random object is used to generate random numbers
            Random rand = new Random();
            // this randomCaptcha object is used to store the generated captcha
            StringBuilder randomCaptcha = new StringBuilder();
            // this for loop is used to generate captcha
            // the characters are selected randomly from the string s
            for(int i=0;i<lenCaptcha;i++)
            {
                // this line is used to generate random numbers from 0 to length of the string s
                int randomDigits = rand.nextInt(s.length());
                // this line is used to append the randomly selected character to the randomCaptcha object
                // the character is selected from the string s at the index randomDigits
                randomCaptcha.append(s.charAt(randomDigits));
            }
            System.out.println("Captcha: "+randomCaptcha);
            System.out.println("Enter Captcha");
            String captchaEnteredByUser = sc.next();
            
            // this if condition is used to check whether the captcha entered by the user is correct or not
            // if the captcha entered by the user is correct then the program will proceed further
            // if the captcha entered by the user is incorrect then the program will ask the user to enter the captcha again
            // if the number of wrong attempts is 5 then the program will terminate
            // and the infinite loop will break;
            if(!captchaEnteredByUser.equals(randomCaptcha.toString()))
            {
                System.out.println("Wrong Captcha");
                flag++;
                if(flag==5)
                {
                    System.out.println("5 wrong attempts");
                    System.exit(-1);
                    //break;
                }
            }
            // if the captcha entered by the user is correct then the program will proceed further and the infinite loop will break
            else
            {
                break;
            }
        }

        // this url variable is used to store the url of the database
        String url = "jdbc:mysql://localhost:3306/fastag";
        // this userName variable is used to store the username of the database
        String userName = "root";
        // this password variable is used to store the password of the database
        String password = "root";
    
        // this try block is used to connect to the database
        try
        {
            // this conn object is used to connect to the database
            Connection conn = DriverManager.getConnection(url, userName, password);


            if(passEntered.equals("0"))
            {
                System.out.println("Enter your mail id: ");
                String enteredMailID = sc.next();

                byte[] encryptedEnteredMailID = encrypt(enteredMailID);
                String encryptedMailID = Base64.getEncoder().encodeToString(encryptedEnteredMailID);

                String sqlQuery_6 = "SELECT State,toll_plaza_name,mailID FROM toll_list WHERE id ="+tollID;
                PreparedStatement preparedStatement_6 = conn.prepareStatement(sqlQuery_6);
                ResultSet resultSet_6 = preparedStatement_6.executeQuery();

                String mailID=null;
                String tollPlazaName=null;
                String state = null;
                if(resultSet_6.next())
                {
                    mailID = resultSet_6.getString("mailID");
                    tollPlazaName = resultSet_6.getString("toll_plaza_name");
                    state = resultSet_6.getString("State");

                }
                if(encryptedMailID.equals(mailID))
                {
                    Random random = new Random();
                        
                    int otpRandom = random.nextInt(900000)+100000;

                    // now we will send the password to the user on his registered mail ID
                    String to = enteredMailID;
                    String from = "hardik23555@gmail.com";
                    String subject = "FASTMAN TOLL PLAZA PASSWORD RECOVERY";
                    String text = null;

                    text = "Dear Toll Plaza Owner,\n\nToll Plaza ID:"+tollID +"\nToll Plaza: "+tollPlazaName+"\nState: "+state+"\nYour OTP to change password is: "+otpRandom+"\nReport back immediately to help@fas_man.com if not requested by you\n\nRegards,\nFASTMAN";
                    System.out.println("Sending OTP to your registered Email ID");
                    try
                    {
                        SendMail sm = new SendMail();
                        sm.sendEmail_(to, from, subject, text);
                    }
                    catch(Exception e)
                    {
                        System.out.println("Error");
                        System.exit(-1);
                    }

                    System.out.println("Enter OTP:: ");
                    int otpEntered = sc.nextInt();
                    if(otpEntered!=otpRandom)
                    {
                        System.out.println("Wrong OTP");
                        System.exit(-1);
                    }
                    else if(otpEntered==otpRandom)
                    {
                        System.out.println("Enter new password(Don't use space and enter strong 8 digit password and use both characters and numbers):: ");
                        String newPass = sc.next();
                        System.out.println("Confirm new password:: ");
                        String confirmNewPass = sc.next();
                        if(newPass.equals(confirmNewPass))
                        {
                            byte[] encrypted = encrypt(newPass);
                            String newEncryptedPassByUserString = Base64.getEncoder().encodeToString(encrypted);

                            String sqlQuery1 = "UPDATE toll_list SET password ='"+newEncryptedPassByUserString+"' WHERE id ="+ tollID;
                            PreparedStatement preparedStatement1 = conn.prepareStatement(sqlQuery1);
                            preparedStatement1.execute();
                            System.out.println("Password changed successfully!\n Please login again ");
                            System.exit(-1);
                        }
                        else
                        {
                            System.out.println("Passwords do not match");
                            System.exit(-1);
                        }
                    }

                }
                else
                {
                    System.out.println("Invalid Mail ID");
                    System.exit(-1);
                }

                 System.exit(-1);
            }
            else{
            // this sqlQuery variable is used to store the sql query
            String sqlQuery = "SELECT password FROM toll_list WHERE id = "+ tollID;
            // this preparedStatement object is used to prepare the sql query
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            // this resultSet object is used to execute the sql query and store the result of the sql query
            ResultSet resultSet = preparedStatement.executeQuery();

            // this correctpass variable is used to store the correct password of the toll plaza
            String correctpass = null;

            // this if conditon is used to store the correct password of the toll plaza in the correctpass variable
            if (resultSet.next()) 
            {
                correctpass = resultSet.getString("password");
            }

            // now we will create hash code of password entered by user to verify it with has code stored in the database
            // this encryptedEnteredPassword variable is used to store the encrypted password entered by the user in the byte array format
            // the password entered by the user is encrypted using the AES algorithm
            byte[] encryptedEnteredPassword = encrypt(passEntered);
            // this encryptedUserEnteredString variable is used to store the encrypted password entered by the user in the string format and compare it 
            // with the password stored in the database
            String encryptedUserEnteredString = Base64.getEncoder().encodeToString(encryptedEnteredPassword);

            String tollPlazaName = null;
            String state = null;

            // this if condition is used to check whether the password entered by the user is correct or not
            // if the password entered by the user is correct then the program will proceed further
            if (encryptedUserEnteredString.equals(correctpass) )
            {
                String sqlQuery_1 = "SELECT state,toll_plaza_name FROM toll_list WHERE id = "+ tollID;
                PreparedStatement preparedStatement_1 = conn.prepareStatement(sqlQuery_1);
                ResultSet resultSet_1 = preparedStatement_1.executeQuery();


                // this while loop is used to print the state and toll plaza name of the toll plaza
                // the state and toll plaza name of the toll plaza is fetched from the resultSet_1 object
                // this while loop will run only once
                // because there is only one toll plaza with the given tollID
                while (resultSet_1.next()) 
                {
                    state = resultSet_1.getString("state");
                    tollPlazaName = resultSet_1.getString("toll_plaza_name");
                    System.out.println("State: " + state + ", Toll Plaza Name: " + tollPlazaName);
                }
            }
            // if the password entered by the user is incorrect then the program will terminate
            else
            {
                System.out.println("Invalid credentials ");
                System.exit(-1);
            }
            // this infinite loop is used to ask the user to enter the vehicle number
            while(true)
            {
                String LightColor = "Red";
                System.out.println("Light: "+LightColor);
                System.out.println("Enter the vehicle number: ");
                String vehicle_number = sc.next();
                System.out.println("Vehicle number is: "+vehicle_number);

                if(vehicle_number.equals("0"))
                {
                    System.exit(-1);
                }

                int categoryID=0;
                int accountBalance=0;
                String MailID = null;
                // this roundJourneyFlag variable is used to check whether the vehicle is on round journey or not
                // if the vehicle is on round journey then the roundJourneyFlag variable will be 1
                // if the vehicle is not on round journey then the roundJourneyFlag variable will be 0
                int roundJourneyFlag = 0;


                String sqlQuery_2 = "SELECT Category_id,owner_name,Account_balance,mail_id FROM vehicle_details WHERE Vehicle_Number = '"+vehicle_number+"'";
                PreparedStatement preparedStatement_2 = conn.prepareStatement(sqlQuery_2);
                // this resultSet_2 object is used to execute the sql query and store the result of the sql query
                ResultSet resultSet_2 = preparedStatement_2.executeQuery();

                // used to fetch the category id and account balance of the vehicle from the resultSet_2 object
                if (resultSet_2.next()) 
                {
                    categoryID = resultSet_2.getInt("Category_id");
                    accountBalance = resultSet_2.getInt("Account_balance");
                    MailID = resultSet_2.getString("mail_id");

                }
                // if the vehicle number entered by the user is incorrect then the program will terminate
                // because there is no vehicle with the given vehicle number
                // and the loop will not execute further for this iteration
                if(categoryID==0)
                {
                    System.out.println("Invalid Vehicle Number");
                    System.out.println();
                    continue;
                }

                // this sqlQuery_31 variable is used to store the sql query
                // this sql query is used to fetch the timestamp and transaction amount of the previous transaction
                // the timestamp and JourneyType is fetched from the resultSet_31 object
                // this query will fetch only one transaction in descending time order because we want the latest transaction
                String sqlQuery_31 = "SELECT Date_Time,Journey_Type FROM transactions WHERE toll_plaza_id ="+ tollID +" AND vehicle_number = '"+vehicle_number+"' ORDER BY TransactionID DESC LIMIT 1";
                PreparedStatement preparedStatement_31 = conn.prepareStatement(sqlQuery_31);
                ResultSet resultSet_31 = preparedStatement_31.executeQuery();

                // this previouTimestamp variable is used to store the timestamp of the previous transaction
                java.sql.Timestamp previouTimestamp = new java.sql.Timestamp(0);
                String previousJourneyType = null;

                // this while loop is used to fetch the timestamp and transaction amount of the previous transaction
                // the timestamp and transaction amount of the previous transaction is fetched from the resultSet_31 object
                // this while loop will run only once
                // because there is only one previous transaction with the given tollID and vehicle number
                while (resultSet_31.next()) 
                {
                    previouTimestamp = resultSet_31.getTimestamp("Date_Time");
                    previousJourneyType = resultSet_31.getString("Journey_Type");
                }
            
                // this seconds variable is used to store the number of seconds between the current time and the previous transaction time
                long seconds=0;

                // this if condition is used to check whether the previous transaction time is 0 or not
                // if the previous transaction time is 0 then the seconds variable will be 0 that means that vehicle is entering the toll plaza for the first time
                if(!previouTimestamp.equals(new java.sql.Timestamp(0)))
                {
                    // this currentDate1 variable is used to store the current date and time
                    java.util.Date currentDate1 = new java.util.Date();
                
                    // this timestamp1 object of Timestamp class is used to store the current date and time in the timestamp format
                    // time method is used to get the current time in milliseconds
            
                    java.sql.Timestamp timestamp1 = new java.sql.Timestamp(currentDate1.getTime());

                    // bigger timestamp - smaller timestamp = duration
                    // 2 nd is bigger 1 st is smaller
                    // this duration object is used to store the difference between the current time and the previous transaction time
                    Duration duration = Duration.between(previouTimestamp.toInstant(), timestamp1.toInstant());

                    // this seconds variable is used to store the number of seconds between the current time and the previous transaction time
                    seconds = duration.toSeconds();
                }

                String sqlQuery_3 = "SELECT Single,Round FROM tariff WHERE CategoryID ="+ categoryID +" AND TollID ="+ tollID;
                PreparedStatement preparedStatement_3 = conn.prepareStatement(sqlQuery_3);
                ResultSet resultSet_3 = preparedStatement_3.executeQuery();

                int applicableTariff=0;
                int singleJourneyTariff=0;

                // this while loop is used to fetch the category name and applicable tariff of the vehicle
                // the category name and applicable tariff and round journey tariff of the vehicle is fetched from the resultSet_3 object
                // this while loop will run only once
                // because there is only one category with the given categoryID
                while (resultSet_3.next()) 
                {
                    applicableTariff = resultSet_3.getInt("Single");
                    singleJourneyTariff = applicableTariff;
                    int roundJourneyTariff = resultSet_3.getInt("Round");

                    // this if condition is used to check whether the vehicle is on round journey or not
                    // if the vehicle is on round journey then the roundJourneyFlag variable will be set as 1
                    // if the vehicle is not on round journey then the roundJourneyFlag variable will not be changed
                    // if the vehicle is on round journey then the applicable tariff will be changed to round journey tariff-applicable tariff
                    // if seconds>0  is used to check whether the vehicle is entering the toll plaza for the first time or not
                    // as if the vehicle is entering the toll plaza for the first time then seconds will be 0
                    // if seconds<43200 is used to check whether the vehicle is entering the toll plaza within 12 hours of the previous transaction or not
                    // as if the vehicle is entering the toll plaza within 12 hours of the previous transaction then seconds will be less than 43200
                    // previousJourneyType.equals("SINGLE") is used to check whether the previous transaction was a single journey or not
                    // even if the previous journey was single but if the vehicle is entering the toll plaza after 12 hours of the previous transaction then the vehicle will be considered as a single journey
        
                    if(seconds>0 && seconds<43200 && previousJourneyType.equals("SINGLE"))
                    {
                        applicableTariff = roundJourneyTariff-applicableTariff;
                        roundJourneyFlag = 1;
                    }
                }
          
                // this sql query is used to fetch the category name of the vehicle
                String sqlQuery_311 = "SELECT CategoryName FROM category WHERE CategoryID ="+ categoryID;
                PreparedStatement preparedStatement_311 = conn.prepareStatement(sqlQuery_311);
                ResultSet resultSet_311 = preparedStatement_311.executeQuery();

                String categoryName = null;

                // this while loop is used to fetch the category name of the vehicle
                // the category name of the vehicle is fetched from the resultSet_311 object
                while (resultSet_311.next()) 
                {
                    categoryName = resultSet_311.getString("CategoryName");
                }

                // remainingBalance of vehicle is calculated by subtracting the applicable tariff from the account balance
                int remainingBalance = accountBalance-applicableTariff;

                // this if condition is used to check whether the remaining balance of the vehicle is greater than or equal to 0 or not
                // as if remainingBalance is less than zero that means the vehicle does not have sufficient balance to pay the toll
                // if the vehicle does not have sufficient balance to pay the toll then the vehicle will not be allowed to pass through the toll plaza
                if(remainingBalance>=0)
                {
                    LightColor = "Green";
                    // this sqlQuery_4 variable is used to store the sql query
                    // this sql query is used to update the account balance of the vehicle in the database
                    // the account balance of the vehicle is updated to remainingBalance
                    // the account balance of the vehicle is updated in the vehicle_details table of the database

                    String sqlQuery_4 = "Update vehicle_details SET Account_balance = " + remainingBalance +" WHERE Vehicle_Number ='"+vehicle_number+"'";
                    // this p object is used to prepare the sql query
                    PreparedStatement p = conn.prepareStatement(sqlQuery_4);
                    // this execute method is used to execute the sql query
                    p.execute();

                    // this currentDate variable is used to store the current date and time
                    java.util.Date currentDate = new java.util.Date();
                    // this timestamp object of Timestamp class is used to store the current date and time in the timestamp format
                    // time method is used to get the current time in milliseconds
                    java.sql.Timestamp timestamp = new java.sql.Timestamp(currentDate.getTime());

                    // this sqlQuery_5 variable is used to store the sql query
                    // the transaction details of the vehicle is inserted in the transactions table of the database
                    // the transaction details of the vehicle includes the date and time of the transaction, vehicle number, toll plaza id, closing account balance and transaction amount
                    // the closing account balance is the remaining balance of the vehicle after paying the toll
                    // the transaction amount is the applicable tariff of the vehicle
                    // the transaction details of the vehicle is inserted in the transactions table of the database

                    if(roundJourneyFlag==0)
                    {
                        String sqlQuery_5 = "INSERT INTO transactions (Date_Time,vehicle_number,toll_plaza_id,Closing_Account_balance,Transaction_amount,Journey_Type) VALUES ('"+timestamp+"', '"+vehicle_number+"', '"+tollID+"','"+remainingBalance+"','"+applicableTariff+"','SINGLE')";
                        PreparedStatement p_5 = conn.prepareStatement(sqlQuery_5);
                        p_5.execute();
                    }
                    else if(roundJourneyFlag==1)
                    {
                        String sqlQuery_99 = "INSERT INTO transactions (Date_Time,vehicle_number,toll_plaza_id,Closing_Account_balance,Transaction_amount,Journey_Type) VALUES ('"+timestamp+"', '"+vehicle_number+"', '"+tollID+"','"+remainingBalance+"','"+applicableTariff+"','RETURN')";
                        PreparedStatement p_99 = conn.prepareStatement(sqlQuery_99);
                        p_99.execute();   
                    }

                    System.out.println("Light: "+LightColor);
                    System.out.println("Vehicle Type: "+categoryName);
                    // this if condition is used to check whether the vehicle is on round journey or not
                    if(roundJourneyFlag==1)
                    {
                        System.out.println("Return Journey");
                    }
                    else if(roundJourneyFlag==0)
                    {
                        System.out.println("Single Journey");
                    }
                    System.out.println("Charges: "+applicableTariff+ " Account Balance: "+remainingBalance);
                    // this if condition is used to check whether the remaining balance of the vehicle is less than or equal to 2 times the single journey tariff or not
                    // if the remaining balance of the vehicle is less than or equal to 2 times the single journey tariff then the user will be alerted to recharge the account
                    // as the vehicle will not be able to pay the toll for the next 2 toll plazas

                    Date date = new Date(timestamp.getTime());
                    // SimpleDateFormat class is used to format the date
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd YYYY   HH:mm:ss  ");
                    // formattedDate is used to store the date in the format specified in the constructor of SimpleDateFormat class
                    // formattedDate is of type String
                    String formattedDate = sdf.format(date);

                    StringBuilder text = new StringBuilder();
                    text = text.append("Dear FASTMAN USER,\n\n");

                    if(remainingBalance<=(2*singleJourneyTariff))
                    {
                        System.out.println("ALERT! Low Remaining Balance for next toll plazas! Please Recharge your account immediately to avoid any future interruption!!");
                        System.out.println();
                        text = text.append("ALERT! Low Remaining Balance for next toll plazas! Please Recharge your fastag account immediately to avoid any future interruption!!\n\n");
                    }

                    System.out.println("Thank You");
                    System.out.println("Happy Journey");
                    

                    if(roundJourneyFlag==0)
                    {
                        text = text.append("Your vehicle with vehicle number\n"+vehicle_number+"\nhas crossed \nToll plaza:: "+tollPlazaName+"\nSTATE:: "+state+ "\nat "+formattedDate+"\nApplicable Charges: "+applicableTariff + "\nJourney Type: SINGLE\nUpdated Account Balance:: "+remainingBalance+" \n\nThank You\nFASTMAN ");
                    }
                    else if(roundJourneyFlag==1)
                    {
                        text = text.append("Your vehicle with vehicle number\n"+vehicle_number+"\nhas crossed \nToll plaza:: "+tollPlazaName+"\nSTATE:: "+state+ "\nat "+formattedDate+"\nApplicable Charges: "+applicableTariff + "\nJourney Type: RETURN\nUpdated Account Balance:: "+remainingBalance+" \n\nThank You\nFASTMAN ");
                    }

                    String finalText = text.toString();
                    System.out.println("Sending Mail");

                    String decryptedMailID = decrypt(Base64.getDecoder().decode(MailID));

                    String to = decryptedMailID;
                    String from = "hardik23555@gmail.com";
                    String subject = "FASTAG";

                    try
                    {
                        SendMail sm = new SendMail();
                        sm.sendEmail(to, from, subject, finalText);
                    }
                    catch(Exception e)
                    {
                        System.out.println("Mail Error");
                    }


                }
                // if the vehicle does not have sufficient balance to pay the toll then the vehicle will not be allowed to pass through the toll plaza
                // and the user will be alerted to recharge the account
                else if(remainingBalance<0)
                {
                    LightColor = "Red";
                    System.out.println("Light: "+LightColor);
                    System.out.println("Low Account Balance! Can not Pay the toll! Please Recharge your account immediately!!");
                    System.out.println();
                }
                
                System.out.println("To logout Enter 0 ,to continue enter 1 ,to generate transaction report Enter 2 :: ");
                // this try catch block is used to give proper feedback to the user if the user enters invalid input
                // without this try catch block the program will throw error if the user enters invalid input that user can not understand
                try
                {
                    logout = sc.nextInt();
                }
                catch(Exception e)
                {
                    System.out.println("Invalid Input");
                    System.exit(-1);
                }

                // is users enters 2 then the transaction history of the toll plaza will be printed
                // the transaction history of the toll plaza includes the transaction id, date and time of the transaction, vehicle number and transaction amount
                if(logout==2)
                {
                    System.out.println("\nTRANSACTION HISTORY\n");

                    String sqlQuery_10 = "SELECT Transaction_Id,vehicle_number,Date_Time,Transaction_amount,Journey_Type FROM transactions WHERE toll_plaza_id ="+tollID;
                    PreparedStatement preparedStatement_10 = conn.prepareStatement(sqlQuery_10);
                    ResultSet resultSet_10 = preparedStatement_10.executeQuery();

                    // this while loop is used to print the transaction history of the toll plaza
                    // the transaction history of the toll plaza includes the transaction id, date and time of the transaction, vehicle number and transaction amount
                    // the transaction history of the toll plaza is fetched from the resultSet_10 object
                    // this while loop will run for all the transactions of the toll plaza
                    // as there can be multiple transactions at a toll plaza
                
                    while (resultSet_10.next()) 
                    {             
                        int transactionID = resultSet_10.getInt("Transaction_Id");
                        // this DateTime variable is used to store the date and time of the transaction in the timestamp format
                        java.sql.Timestamp DateTime = resultSet_10.getTimestamp("Date_Time");
                        String vehicleNumber = resultSet_10.getString("vehicle_number");
                        int transactionAmount = resultSet_10.getInt("Transaction_amount");
                        String journeyType = resultSet_10.getString("Journey_Type");
                    
                        String sqlQuery_101 = "SELECT Category_id FROM vehicle_details WHERE Vehicle_Number ='"+vehicleNumber+"'";
                        PreparedStatement preparedStatement_101 = conn.prepareStatement(sqlQuery_101);
                        ResultSet resultSet_101 = preparedStatement_101.executeQuery();

                        String categoryName_102 = null;

                        while (resultSet_101.next())
                        {
                            int categoryID_101 = resultSet_101.getInt("Category_id");
                            String sqlQuery_102 = "SELECT CategoryName FROM category WHERE CategoryID ="+ categoryID_101;
                            PreparedStatement preparedStatement_102 = conn.prepareStatement(sqlQuery_102);
                            ResultSet resultSet_102 = preparedStatement_102.executeQuery();

                            while (resultSet_102.next()) 
                            {
                                categoryName_102 = resultSet_102.getString("CategoryName");                              
                            }
                        }
                                
                            System.out.print("TRANSACTION ID: "+transactionID);
                            System.out.print("      Date Time: "+DateTime+"  ");
                            System.out.println("      Vehicle Number: "+vehicleNumber);
                            System.out.print("Vehicle Type: "+categoryName_102);
                            System.out.print("      Charges: "+transactionAmount);
                            System.out.println("    Journey Type: "+journeyType);
                            System.out.println();                        
                    }

                    System.out.println("To logout Enter 0 ,to continue enter 1 :: ");
                    try
                    {
                        logout = sc.nextInt();
                    }
                    catch(Exception e)
                    {
                        System.out.println("Invalid Input");
                        System.exit(-1);
                    }
                    System.out.println();
                }
                // if the user enters 0 then the connection to the database will be closed and the loop to enter the vehicle number will break
                // if the user enters 1 then the loop to enter the vehicle number will continue
                
                if(logout==0)
                {
                    // this close method is used to close the connection to the database
                    conn.close();
                    // this break statement is used to break the infinite loop to enter the vehicle number
                    break;
                }
            }
        }
        }
        // if the connection to the database is not established then the program will terminate
        // and the exception will be printed
        catch (Exception e)
        {
            // this printStackTrace method is used to print the exception
            System.out.println("Server Down");
            e.printStackTrace();
        }
        // this close method is used to close the scanner object
        // as the scanner object is not required further
        // and it will save the memory
        sc.close();
    }   
}

