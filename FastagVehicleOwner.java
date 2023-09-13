package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Random;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public class FastagVehicleOwner 
{
    private static final String ALGORITHM = "AES";
    private static final String keyV = "HqZPE3WPDeXrTr5s";

    public static byte[] encrypt(String input) throws Exception 
    {
        SecretKeySpec keySpec = new SecretKeySpec(keyV.getBytes(), ALGORITHM);
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

        System.out.println("Enter you Vehicle Number: ");
        Scanner sc = new Scanner(System.in);
        String vehicle_number = sc.next();
        System.out.println("Enter password(If forgot password Enter 0): ");
        String passEntered = sc.next();

        // flag is used to count the number of wrong attempts
        int flag=0;
        // loop will run until the user enters the correct captcha
        // if the user enters wrong captcha 5 times then the program will terminate
        // if the user enters correct captcha then the program will continue
        while(true)
        {
            // alphabets O and I are not included in captcha to avoid confusion with digits 0 and 1
            // digits 0 and 1 are not included in captcha to avoid confusion with alphabets O and I
            // captcha is case sensitive
            // captcha is generated randomly
            // captcha is generated using Random class
            // captcha is generated using nextInt() method of Random class
            // nextInt() method of Random class takes an integer as argument
            String s = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
            int lenCaptcha = 4;
            Random rand = new Random();
            // StringBuilder is used to create a mutable string which can be modified and is used to store captcha
            StringBuilder randomCaptcha = new StringBuilder();

            // loop will run 4 times to generate 4 random characters
            for(int i=0;i<lenCaptcha;i++)
            {
                int randomDigits = rand.nextInt(s.length());
                randomCaptcha.append(s.charAt(randomDigits));
            }
            System.out.println("Captcha: "+randomCaptcha);
            System.out.println("Enter Captcha");
            String captchaEnteredByUser = sc.next();
            
            // if the user enters wrong captcha then the flag will be incremented
            // if the flag becomes 5 then the program will terminate
            // if the user enters correct captcha then the program will continue
            if(!captchaEnteredByUser.equals(randomCaptcha.toString()))
            {
                System.out.println("Wrong Captcha");
                flag++;
                if(flag==5)
                {
                    System.out.println("5 wrong attempts");
                    break;
                }
            }
            else
            {
                try
                {
                    String url = "jdbc:mysql://localhost:3306/fastag";
                    String userName = "root";
                    String password = "root";

                    
                    Connection connection = DriverManager.getConnection(url, userName, password);

                    if(passEntered.equals("0"))
                    {
                        System.out.println("Enter your mail id: ");
                        String enteredMailID = sc.next();
        
                        byte[] encryptedEnteredMailID = encrypt(enteredMailID);
                        String encryptedMailID = Base64.getEncoder().encodeToString(encryptedEnteredMailID);


                        String sqlQuery = "SELECT mail_id FROM vehicle_details WHERE Vehicle_Number ='"+ vehicle_number+"'";
                        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                        ResultSet resultSet = preparedStatement.executeQuery();
                        
                        String mailId = null;
                        if (resultSet.next()) 
                        {
                            mailId = resultSet.getString("mail_id");
                        }

                        if(encryptedMailID.equals(mailId))
                        {


                            // now we will generate a random 4 digit OTP
                            // OTP is generated randomly
                            
                            Random random = new Random();
                            
                            int otpRandom = random.nextInt(900000)+100000;

                            // now we will send the password to the user on his registered mail ID
                            String to = enteredMailID;
                            String from = "hardik23555@gmail.com";
                            String subject = "FASTMAN PASSWORD RECOVERY";
                            String text = null;

                            text = "Dear FastMAN User,\n\nVehicle Number:  "+vehicle_number+"\nYour OTP to change password is: "+otpRandom+"\nReport back if not requested by you\n\nRegards,\nFASTMAN";
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
                                System.out.println("Enter new password(Don't use space):: ");
                                String newPass = sc.next();
                                System.out.println("Confirm new password:: ");
                                String confirmNewPass = sc.next();
                                if(newPass.equals(confirmNewPass))
                                {
                                    byte[] encrypted = encrypt(newPass);
                                    String newEncryptedPassByUserString = Base64.getEncoder().encodeToString(encrypted);

                                    String sqlQuery1 = "UPDATE vehicle_details SET password ='"+newEncryptedPassByUserString+"' WHERE Vehicle_Number ='"+ vehicle_number+"'";
                                    PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery1);
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
                            System.out.println("Wrong Mail ID");
                            System.exit(-1);
                        }
    
                        System.exit(-1);
                    }
                    else
                    {

                        String sqlQuery = "SELECT password FROM vehicle_details WHERE Vehicle_Number ='"+ vehicle_number+"'";
                        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                        ResultSet resultSet = preparedStatement.executeQuery();

                        String correctpass = null;

                        // if the vehicle number is not present in the database then the program will terminate
                        if (resultSet.next()) 
                        {
                            correctpass = resultSet.getString("password");
                        }

                        // now we will convert the password entered by the user into encrypted form 
                        // and then we will compare it with the password stored in the database
                        byte[] encrypted = encrypt(passEntered);
                        String encryptedPassByUserString = Base64.getEncoder().encodeToString(encrypted);

                        // if the password entered by the user is correct then the program will continue
                        if (encryptedPassByUserString.equals(correctpass) )
                        {
                            String sqlQuery_1 = "SELECT Account_balance,Category_id,mail_id FROM vehicle_details WHERE Vehicle_Number ='"+vehicle_number+"'";
                            PreparedStatement preparedStatement_1 = connection.prepareStatement(sqlQuery_1);
                            ResultSet resultSet_1 = preparedStatement_1.executeQuery();

                            String mailId = null;
                            String decryptedMailID = null;

                            while (resultSet_1.next()) 
                            {
                                int accountBalance = resultSet_1.getInt("Account_balance");
                                int categoryID = resultSet_1.getInt("Category_id");
                                mailId = resultSet_1.getString("mail_id");

                                decryptedMailID = decrypt(Base64.getDecoder().decode(mailId));

                                String sqlQuery_4 = "SELECT CategoryName FROM category WHERE CategoryID ="+categoryID;
                                PreparedStatement preparedStatement_4 = connection.prepareStatement(sqlQuery_4);
                                ResultSet resultSet_4 = preparedStatement_4.executeQuery();

                                while(resultSet_4.next())
                                {
                                    String category = resultSet_4.getString("CategoryName");
                                    System.out.println();
                                    System.out.println("Vehicle Number: " + vehicle_number+"\nEMail ID::"+ decryptedMailID + "\nVehicle Type: "+ category + "\nAccount Balance: " + accountBalance);
                                }

                            }

                            System.out.println("\n\nEnter 1 to view transaction history\nEnter 2 to change EmailID \nEnter 0 to exit");

                            int choice = sc.nextInt();

                            if(choice==0)
                            {
                                System.exit(-1);
                            }
                            else if(choice==1)
                            {
                                System.out.println("\nTRANSACTION HISTORY\n");

                                String sqlQuery_2 = "SELECT Transaction_Id,toll_plaza_id,Date_Time,Transaction_amount,Closing_Account_balance,Journey_Type FROM transactions WHERE vehicle_number ='"+vehicle_number+"'";
                                PreparedStatement preparedStatement_2 = connection.prepareStatement(sqlQuery_2);
                                ResultSet resultSet_2 = preparedStatement_2.executeQuery();

                                while (resultSet_2.next()) 
                                {
                                    int transactionID = resultSet_2.getInt("Transaction_Id");
                                    int tollPlazaId = resultSet_2.getInt("toll_plaza_id");
                                    java.sql.Timestamp DateTime = resultSet_2.getTimestamp("Date_Time");
                                    // Date class is used to convert the timestamp into normal date time format
                                    Date date = new Date(DateTime.getTime());
                                    // SimpleDateFormat class is used to format the date
                                    SimpleDateFormat sdf = new SimpleDateFormat("EEE YYYY MMM dd HH:mm:ss  ");
                                    // formattedDate is used to store the date in the format specified in the constructor of SimpleDateFormat class
                                    // formattedDate is of type String
                                    String formattedDate = sdf.format(date);
                                    int transactionAmount = resultSet_2.getInt("Transaction_amount");
                                    int closingAccountBalance = resultSet_2.getInt("Closing_Account_balance");
                                    String journeyType = resultSet_2.getString("Journey_Type");
                                        
                                    String sqlQuery_3 = "SELECT toll_plaza_name,state FROM toll_list WHERE id ="+tollPlazaId;
                                    PreparedStatement preparedStatement_3 = connection.prepareStatement(sqlQuery_3);
                                    ResultSet resultSet_3 = preparedStatement_3.executeQuery();

                                    while (resultSet_3.next())
                                    {
                                        String tollPlazaName = resultSet_3.getString("toll_plaza_name");
                                        String State = resultSet_3.getString("state");

                                        System.out.println("TRANSACTION ID: "+transactionID);
                                        System.out.print("Date Time: "+formattedDate+"     ");
                                        System.out.println(tollPlazaName.toUpperCase()+" TOLL PLAZA  " + " STATE: "+State.toUpperCase());
                                        System.out.println("Journey Type: "+journeyType+"       Charges: "+transactionAmount+"      Closing Account Balance: "+closingAccountBalance);
                                        System.out.println();
                                    }
                                }
                            }
                            else if(choice==2)
                            {

                                Random random = new Random();
                            
                                int otpRandom = random.nextInt(900000)+100000;

                    
                                String to = decryptedMailID;
                                String from = "hardik23555@gmail.com";
                                String subject = "FASTMAN EMAIL ID UPDATE";
                                String text = null;

                                text = "Dear FastMAN User,\n\nVehicle Number:  "+vehicle_number+"\nYour OTP to change your EmailID is: "+otpRandom+"\nReport back if not requested by you\n\nRegards,\nFASTMAN";
                                System.out.println("Sending OTP to your registered Email ID");

                                // OTP on original mail ID is required to prevent unauthorized access to the account and to prevent misuse of the account if somehow someone gets access to the password
                                // he should not be able to change the mail ID without OTP on original mail ID
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

                                if(otpEntered==otpRandom)
                                {

                                    System.out.println("Enter new Email ID: ");
                                    String newMailID = sc.next();

                                    Random random2 = new Random();
                                
                                    int otpRandom2 = random2.nextInt(900000)+100000;

                        
                                    to = newMailID;
                                    // String from = "hardik23555@gmail.com";
                                    //String subject = "FASTMAN EMAIL ID UPDATE";
                                    text = null;

                                    text = "Dear FastMAN User,\n\nVehicle Number:  "+vehicle_number+"\nVerify your email to set as EmailID for this vehicle Number\nUse the following verification code: "+otpRandom2+"\n\nRegards,\nFASTMAN";
                                    System.out.println("Sending OTP to new Email ID");
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
                                    otpEntered = sc.nextInt();

                                    if(otpEntered==otpRandom2)
                                    {
                                        byte[] encryptedNewMailID = encrypt(newMailID);
                                        String newEncryptedMailID = Base64.getEncoder().encodeToString(encryptedNewMailID);

                                        String sqlQuery1 = "UPDATE vehicle_details SET mail_id ='"+newEncryptedMailID+"' WHERE Vehicle_Number ='"+ vehicle_number+"'";
                                        PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery1);
                                        preparedStatement1.execute();
                                        System.out.println("Email ID changed successfully!\n Please login again ");
                                        System.exit(-1);
                                    }
                                    else
                                    {
                                        System.out.println("Wrong OTP");
                                        System.exit(-1);
                                    }
                                }
                                else
                                {
                                    System.out.println("Wrong OTP");
                                    System.exit(-1);
                                }

                            }
                            else
                            {
                                System.out.println("Invalid input");
                                System.exit(-1);
                            }
                        }
                        // if the password entered by the user is wrong then the program will terminate
                        else
                        {
                            System.out.println("Invalid credentials ");
                            System.exit(-1);
                        }
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                } 
                break;
            }
        }
        // closing the scanner object
        sc.close();
   }
}
