<?php
    #$con = mysqli_connect("mysql10.000webhost.com", "a3288368_user", "abcd1234", "a3288368_data");
    $con = pg_connect("host=localhost port=5432 dbname=hvislar_db user=postgres password=admin");
    $username = $_POST["email"];
    $password = $_POST["password"];
    
    #$statement = mysqli_prepare($con, "SELECT * FROM user WHERE username = ? AND password = ?");
    $statement = pg_prepare($con, "find_user", "SELECT * FROM account WHERE email = $1 AND passwd = $2");
    $result = pg_execute($con, "find_user", array($username, $password));
    
    #mysqli_stmt_store_result($statement);
    #mysqli_stmt_bind_result($statement, $userID, $name, $age, $username, $password);
    $response = array();
    $response["success"] = false;  
    
    while($row = pg_fetch_array($result)){
        $response["success"] = true;  
        $response["name"] = $row["username"];
        $response["email"] = $row["email"];
    #    $response["password"] = $password;
    }
    
    echo json_encode($response);
?>