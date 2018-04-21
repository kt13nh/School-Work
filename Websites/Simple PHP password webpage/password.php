#!/usr/bin/php-cgi
<?php

//determine which button is pressed
if(isset($_POST["login"])){
 	login();
}
if(isset($_POST["register"])){
	register();
}

//register function for register button
function register(){
	//initialize variables, and load into array, then count amount of arrays
	$file ="users.txt";
	$countFileInt=explode("::",file_get_contents("$file"));
	$fileInt=count($countFileInt);
	$init ="::";
	//post username and passwrd data, set timezones and date
	$user = $_POST["username"];
	$pass = $_POST["passwrd"];
	date_default_timezone_set('Canada/Eastern');
	$date = date("d/m/Y");
	$dateLine = $date;
	$time = date("h:i:s a");
	//conditions for textboxes 
	if($_POST["username"] !='' & $_POST["passwrd"] != '' ){
		if(doesUserExist()==true){
			echo nl2br("Username already in Use! \r\n Please choose another !");
		}
		else if(doesUserExist()==false){
				file_put_contents($file, crypt($user,'st'),FILE_APPEND);
				file_put_contents($file, $init,FILE_APPEND);
				file_put_contents($file, crypt($pass,'st'),FILE_APPEND);
				file_put_contents($file, $init,FILE_APPEND);
				file_put_contents($file, $dateLine,FILE_APPEND);
				file_put_contents($file, $init,FILE_APPEND);
				file_put_contents($file, $time,FILE_APPEND);
				file_put_contents($file, $init,FILE_APPEND);
				$countfile = explode("::",file_get_contents("$file"));
				echo "Thank you for registering !";
				echo nl2br( "\r\n Username: $user \r\n Password: $pass \r\n Registration Time and Date: $date, $time");
		}
		
	}
	else{
		echo nl2br("Error ! \r\n Please Fill in both Username and Password !");
	}
	exit;
}

//function for login, similar to the register function
function login(){
	$file = "users.txt";
	$countFileInt = explode("::", file_get_contents("$file"));
	$fileInt=count($countFileInt);
	if($_POST["username"] !='' & $_POST["passwrd"] != ''){
		if(doesUserExist()==true){
			if(doesPasswordExist()==true){
				echo nl2br("Login Successful! ");
			}
			else if(doesPasswordExist()==false){
				echo "Incorrect Password!";
			}
		}
		else if(doesUserExist()==false){
			echo "Username does not exist!";
		}
	}
	else if($_POST["username"] =='' & $_POST["passwrd"] != ''){
		echo "Please enter your username!";
	}
	else if($_POST["username"] !='' & $_POST["passwrd"] == ''){
		echo "Please enter a password!";
	}
	else{
		echo "Please fill in the missing information!";
	}
}

//function to check is username already exists
function doesUserExist(){
	$file ="users.txt";
	$countFileInt=explode("::",file_get_contents("$file"),-1);
	$fileInt=count($countFileInt);
	$user = $_POST["username"];
	$value=0;

	for($i=0;$i<$fileInt;$i+=4){
		if($countFileInt[$i] == crypt($user,'st')){
			return true;
		}
	}
	return false;	
}


//username to check if username exists, if it does, then also check if password exists
function doesPasswordExist(){
	$file ="users.txt";
	$countFileInt=explode("::",file_get_contents("$file"));
	$fileInt=count($countFileInt);
	$user = $_POST["username"];
	$pass = $_POST["passwrd"];
	for($i=0; $i < $fileInt; $i+=4){
		if($countFileInt[$i]==crypt($user,'st')){
			if($countFileInt[$i+=1]==crypt($pass,'st')){
				return true;
			}
		}
	}
	return false;
}

?>
