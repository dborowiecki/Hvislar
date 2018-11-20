<?php
    #$con = mysqli_connect("mysql10.000webhost.com", "a3288368_user", "abcd1234", "a3288368_data");
    $con = pg_connect("host=localhost port=5432 dbname=hvislar_db user=postgres password=admin");
    
    $result = pg_query($con, "SELECT * FROM account");
    $response = array();
    $response["success"] = false;  
    $response["users"] = array();
    while($row = pg_fetch_array($result)){
        $response["success"] = true;  
        array_push($response["users"], $row["username"]);
    }
    
    echo json_encode($response);
?>