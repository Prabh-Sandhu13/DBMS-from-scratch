package login;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Login {
	static String fileUserName;
	static String filePassword;

	public String verification(String userName, String password) {
		Boolean flag = false;
		String path = "credentials.txt";

		try {
			Scanner sc = new Scanner(new File(path));
			sc.useDelimiter("[,\n]");
			while (sc.hasNext()) {
				fileUserName = sc.next();
				filePassword = sc.next();

				if (fileUserName.trim().equals(userName.trim()) && filePassword.trim().equals(password.trim())) {
					flag = true;
				}
			}
			sc.close();
			if (flag) {
				System.out.println("Login Successful !!");
				return userName;
			} else {
				System.out.println("Invalid Credentials !!");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;

	}
}
