#!/usr/bin/php-cgi

<?php
    //access database
    $servername = 'localhost';
    $username = 'kt13nh';
    $password = '5459854';

    $data=array();

    $db = mysql_connect($servername,$username,$password);
    $dbname = mysql_select_db('kt13nh',$db);

    //show suggestions based on substr's
    if ($dbname){
        $SQL = "SELECT * FROM Album";
        $result = mysql_query($SQL);
        while ( $db_field = mysql_fetch_assoc($result) ) {
            array_push($data,$db_field['Album_Name']);
        }
    }    
    $q = $_REQUEST["q"];

    $hint = "";

    if ($q !== "") {
        $q = strtolower($q);
        $len=strlen($q);
        foreach($data as $name) {
            if (stristr($q, substr($name, 0, $len))) {
                if ($hint === "") {
                    $hint = $name;
                } else {
                    $hint .= ", $name";
                }
            }
        }
    }


    echo $hint === "" ? "no suggestion" : $hint;

?>