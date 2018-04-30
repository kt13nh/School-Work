#!/usr/bin/php-cgi
<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
<body>

    <head>
        <link rel="stylesheet" type="text/css" href="../CSS/css1.css">
        <link rel="stylesheet" type="text/css" href="../CSS/css2.css">
        <title>Result</title>
    </head>
    <h1>
        The Beatles
        <img src="../pictures/record.png" alt="picture" width ="50" height="50">
    </h1>
    <p align='center'>
        <a href ="../assign2.html"> Home </a> |
        <a href="../sub1.html"> John Lennon</a> |
        <a href="../sub2.html"> George Harrison</a> |
        <a href="../sub3.html"> Ringo Starr</a> |
        <a href="../sub4.html"> Paul McCartney</a> |
        <a href="../sub5.html"> Discography</a> |
        <a href="../sub6.html"> Other Beatles members</a> |
        <a href="../sub7.html"> Discography Selections</a>  
    </p>
    <hr />

    <fieldset>
    <?php
    //connect to database
        $servername = 'localhost';
        $username = 'kt13nh';
        $password = '5459854';
        
        $db = mysql_connect('localhost','kt13nh','5459854');
        $dbname = mysql_select_db('kt13nh',$db);

        if (!$db){
            die("Connection failed: " .mysql_error());
        }
        //conditions for text and radio buttons
        $text = $_POST["txtField"];
        if($text != ''){
            if($_POST['radiobutton']=="albumButton" ){
                $int =0;
                $db = mysql_connect('localhost','kt13nh','5459854');
                $dbname = mysql_select_db('kt13nh',$db);
                if($dbname){
                    $SQLalbum = "SELECT * FROM Album";
                    $resultAlbum = mysql_query($SQLalbum);
                    while ( $db_field = mysql_fetch_assoc($resultAlbum) ) {
                        $index =$db_field['Album_Name'];
                        if($index==$text){
                            $int++;
                            selectAlbum($text);
                            break;
                        }
                    }
                    if($int ==0){
                        echo "Please enter exactly as it is in the Suggestions box!";
                    }  
                }
            }
            else if($_POST['radiobutton']=="songButton"){
                $int=0;
                $db = mysql_connect('localhost','kt13nh','5459854');
                $dbname = mysql_select_db('kt13nh',$db);
                if($dbname){
                    $SQLalbum = "SELECT * FROM Song";
                    $resultAlbum = mysql_query($SQLalbum);
                    while ( $db_field = mysql_fetch_assoc($resultAlbum) ) {
                        $index =$db_field['Name'];
                        if($index==$text){
                            $int++;
                            selectSongAlbum($text);
                            break;
                        }
                    }
                    if($int==0){
                        echo "Please enter exactly as it is in the Suggestions box!";
                    }
                }
            }
            else if($_POST['radiobutton']=="songComposerButton"){
                $db = mysql_connect('localhost','kt13nh','5459854');
                $dbname = mysql_select_db('kt13nh',$db);
                if($dbname){
                    $int=0;
                    $SQLalbum = "SELECT * FROM Song";
                    $resultAlbum = mysql_query($SQLalbum);
                    while ( $db_field = mysql_fetch_assoc($resultAlbum) ) {
                        $index =$db_field['Name'];
                        if($index==$text){
                            $int++;
                            selectSongComposer($text);
                            break;
                        }
                    }
                    if($int==0){
                        echo "Please enter exactly as it is in the Suggestions box!";
                    }
                }
            }
            else if($_POST['radiobutton']=="songSearchButton"){
                songSearch($text);
            }
        }
        else if($text ==''){
            echo "Please Fill in the text field to display information!";
        }
        //function for if radio button 1 is selected
        function selectAlbum($text){
            $db = mysql_connect('localhost','kt13nh','5459854');
            $dbname = mysql_select_db('kt13nh',$db);
            if ($dbname){
                $SQLalbum = "SELECT * FROM Album";
                $resultAlbum = mysql_query($SQLalbum);
                while ( $db_field = mysql_fetch_assoc($resultAlbum) ) {
                    $index =$db_field['Album_Name'];
                    if($index==$text){
                        $idresult = $db_field['Id'];
                        
                        $albumName=$db_field['Album_Name'];
                        $albumYear = $db_field['Year'];
                        echo "Album Name: ";
                        echo "$albumName";
                        echo "<br>";
                        echo "<br>";
                        echo "Year of release: ";
                        echo "$albumYear";
                        echo "<br>";
                        echo "<br>";
                        echo "List of Songs in this album: ";
                        echo "<br>";
                        echo "________________________________";
                        echo "<br>";
                        echo "<br>";


                         break;
                    }
                }
                $SQLsong ="SELECT * FROM Album_Songs";
                $resultSong=mysql_query($SQLsong);
                while($db_field=mysql_fetch_assoc($resultSong)){
                    if($db_field['Album_Id']=="$idresult"){
                        $songID = $db_field['Song_Id'];
                        $SQLsong2="SELECT * FROM Song";
                        $resultSong2=mysql_query($SQLsong2);
                        while($db_field=mysql_fetch_assoc($resultSong2)){
                            if($db_field['Id']=="$songID"){
                                print $db_field['Name'];
                                echo "<br>";
                                // echo "Lyrics:";
                                // print $db_field['Lyrics'];
                                // echo "<br>";
                                //echo "---------------------------------------";
                                echo "<br>";
                            }
                        }
                    }
                }
            }
        }
        //function for if radio button 2 is selected
        function selectSongAlbum($text){
            $db = mysql_connect('localhost','kt13nh','5459854');
            $dbname = mysql_select_db('kt13nh',$db);
            if ($dbname){
                $SQLalbum = "SELECT * FROM Song";
                $resultAlbum = mysql_query($SQLalbum);
                while ( $db_field = mysql_fetch_assoc($resultAlbum) ) {
                    $index =$db_field['Name'];
                    if($index==$text){
                        $idresult = $db_field['Id'];
                        
                        $songName=$db_field['Name'];
                        $songLyrics = $db_field['Lyrics'];
                        echo "Name of Song: ";
                        echo "$songName";
                        echo "<br>";
                        echo "<br>";
                        echo "Song Lyrics: ";
                        echo "<br>";
                        echo "___________";
                        echo "<br>";
                        echo "$songLyrics";
                        echo "<br>";
                        echo "<br>";

                         break;
                    }
                }
                echo "Name of the Album the song is in: ";
                $SQLsong ="SELECT * FROM Album_Songs";
                $resultSong=mysql_query($SQLsong);
                while($db_field=mysql_fetch_assoc($resultSong)){
                    if($db_field['Song_Id']=="$idresult"){
                        $albumID = $db_field['Album_Id'];
                        $SQLsong2="SELECT * FROM Album";
                        $resultSong2=mysql_query($SQLsong2);
                        while($db_field=mysql_fetch_assoc($resultSong2)){
                            if($db_field['Id']=="$albumID"){
                                print $db_field['Album_Name'];
                                echo "<br>";
                                echo "---------------------------------------";
                                echo "<br>";
                                
                            }
                        }

                    }
                }
                echo "Name(s) of the composer(s): ";
                echo "<br>";
                $SQLcompose = "SELECT * FROM Composer_Songs";
                $resultCompose=mysql_query($SQLcompose);
                
                while($db_field=mysql_fetch_assoc($resultCompose)){
                    if($db_field['Song_Id']=="$idresult"){
                        $composerId=$db_field['Composer_Id'];
                        $SQLCompose2="SELECT * FROM Composer";
                        $resultCompose2=mysql_query($SQLCompose2);
                        while($db_field=mysql_fetch_assoc($resultCompose2)){
                            if($db_field['Id']=="$composerId"){
                                print $db_field['Name'];
                                echo "<br>";
                            }
                        }
                    }
                }
            }
        }
        //function for if radio button 3 is selected
        function selectSongComposer($text){
            $db = mysql_connect('localhost','kt13nh','5459854');
            $dbname = mysql_select_db('kt13nh',$db);
            if ($dbname){
                $SQLalbum = "SELECT * FROM Song";
                $resultAlbum = mysql_query($SQLalbum);
                while ( $db_field = mysql_fetch_assoc($resultAlbum) ) {
                    $index =$db_field['Name'];
                    if($index==$text){
                        $idresult = $db_field['Id'];
                        
                        $songName=$db_field['Name'];
                        $songLyrics = $db_field['Lyrics'];
                        echo "Name of Song: ";
                        echo "$songName";
                        echo "<br>";
                        echo "<br>";
                        echo "Song Lyrics: ";
                        echo "<br>";
                        echo "___________";
                        echo "<br>";
                        echo "$songLyrics";
                        echo "<br>";

                         break;
                    }
                }
                $SQLcompose = "SELECT * FROM Composer_Songs";
                $resultCompose=mysql_query($SQLcompose);
                
                while($db_field=mysql_fetch_assoc($resultCompose)){
                    if($db_field['Song_Id']=="$idresult"){
                        $composerId=$db_field['Composer_Id'];
                        $SQLCompose2="SELECT * FROM Composer";
                        $resultCompose2=mysql_query($SQLCompose2);
                        while($db_field=mysql_fetch_assoc($resultCompose2)){
                            if($db_field['Id']=="$composerId"){
                                echo "<br>";
                                echo "____________________";
                                echo "<br>";
                                echo "Composer Name: ";
                                print $db_field['Name'];
                                
                                echo "<br>";
                                echo "Birthdate: ";
                                print $db_field['Birthdate'];
                                echo "<br>";
                                echo "Deathdate: ";
                                print $db_field['Deathdate'];
                                echo "<br>";
                            }
                        }
                    }
                }
            }
        }
        //function for if radio button 4 is selected
        function songSearch($text){
            $db = mysql_connect('localhost','kt13nh','5459854');
            $dbname = mysql_select_db('kt13nh',$db);
            if ($dbname){
                $SQLalbum = "SELECT * FROM Song WHERE Name LIKE '%$text%'";
                $resultAlbum = mysql_query($SQLalbum);
                echo "Songs that have similar characters to $text :";
                echo "<br>";
                echo "<br>";
                while ( $db_field = mysql_fetch_assoc($resultAlbum) ) {
                    print $db_field['Name'];
                    echo " ";
                    echo "<br>";
                    echo "_____________";
                    echo "<br>";
                 }

            }
        }


    ?>
</fieldset>
</body>
</html>