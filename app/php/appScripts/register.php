<?php
    #$con = mysqli_connect("localhost", "a3288368_user", "abcd1234", "a3288368_data");

    $con = pg_connect("host=localhost port=5432 dbname=hvislar_db user=postgres password=admin");

    $name = $_POST["name"];
    $email = $_POST["email"];
    $password = $_POST["password"];
    // $name = 'karol1';
    // $email = 'test@test.test1';
    // $password = 'tajnehaszlo1';
    $statement = pg_prepare($con, "create_user", "INSERT INTO account (username,passwd, email) VALUES ($1, $2, $3)");
    pg_execute($con, "create_user", array($name,  $password, $email));
    #mysqli_stmt_bind_param($statement, "siss", $name, $username, $age, $password);
    #mysqli_stmt_execute($statement);
    
    $response = array();
    $response["success"] = true;  
    
    echo json_encode($response);
?>